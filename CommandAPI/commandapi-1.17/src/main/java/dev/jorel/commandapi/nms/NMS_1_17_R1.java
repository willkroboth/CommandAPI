/*******************************************************************************
 * Copyright 2018, 2021 Jorel Ali (Skepter) - MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package dev.jorel.commandapi.nms;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIHandler;
import dev.jorel.commandapi.preprocessor.RequireField;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.server.ServerFunctionLibrary;
import net.minecraft.server.ServerResources;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.Entity;

// Mojang-Mapped reflection
@RequireField(in = ServerResources.class, name = "functionLibrary", ofType = ServerFunctionLibrary.class)
@RequireField(in = ServerFunctionLibrary.class, name = "functionCompilationLevel", ofType = int.class)
@RequireField(in = EntitySelector.class, name = "usesSelector", ofType = boolean.class)
public class NMS_1_17_R1 extends Common {
	
	private static final VarHandle ServerFunctionLibrary_functionCompilationLevel;
	
	// Compute all var handles all in one go so we don't do this during main server runtime
	static {
		VarHandle sfl_fcl = null;
		 try {
			 sfl_fcl = MethodHandles.privateLookupIn(ServerFunctionLibrary.class, MethodHandles.lookup()).findVarHandle(ServerFunctionLibrary.class, "h", int.class);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
		 ServerFunctionLibrary_functionCompilationLevel = sfl_fcl;
	}

	@Override
	public String[] compatibleVersions() {
		return new String[] { "1.17" };
	}
	
	@Override
	public Object getEntitySelector(CommandContext<CommandSourceStack> cmdCtx, String str, dev.jorel.commandapi.arguments.EntitySelectorArgument.EntitySelector selector)
			throws CommandSyntaxException {
		
		// We override the rule whereby players need "minecraft.command.selector" and have to have
		// level 2 permissions in order to use entity selectors. We're trying to allow entity selectors
		// to be used by anyone that registers a command via the CommandAPI.
		EntitySelector argument = cmdCtx.getArgument(str, EntitySelector.class);
		try {
			CommandAPIHandler.getInstance().getField(EntitySelector.class, "o").set(argument, false);
		} catch (IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		
		return switch (selector) {
		case MANY_ENTITIES:
			try {
				List<org.bukkit.entity.Entity> result = new ArrayList<>();
				for(Entity entity : argument.findEntities(cmdCtx.getSource())) {
					result.add(entity.getBukkitEntity());
				}
				yield result;
			} catch (CommandSyntaxException e) {
				yield new ArrayList<org.bukkit.entity.Entity>();
			}
		case MANY_PLAYERS:
			try {
				List<Player> result = new ArrayList<>();
				for(ServerPlayer player : argument.findPlayers(cmdCtx.getSource())) {
					result.add(player.getBukkitEntity());
				}
				yield result;
			} catch (CommandSyntaxException e) {
				yield new ArrayList<Player>();
			}
		case ONE_ENTITY:
			yield (org.bukkit.entity.Entity) argument.findSingleEntity(cmdCtx.getSource()).getBukkitEntity();
		case ONE_PLAYER:
			yield (Player) argument.findSinglePlayer(cmdCtx.getSource()).getBukkitEntity();
		};
	}

	@Override
	public void reloadDataPacks()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		CommandAPI.logNormal("Reloading datapacks...");

		// Get previously declared recipes to be re-registered later
		Iterator<Recipe> recipes = Bukkit.recipeIterator();

		// Update the commandDispatcher with the current server's commandDispatcher
		ServerResources serverResources = MINECRAFT_SERVER.resources;
		serverResources.commands = MINECRAFT_SERVER.getCommands();

		// Update the ServerFunctionLibrary for the server resources which now has the new commandDispatcher
		try {
			ServerFunctionLibrary replacement = new ServerFunctionLibrary(
				(int) ServerFunctionLibrary_functionCompilationLevel.get(serverResources.getFunctionLibrary()),
				serverResources.commands.getDispatcher()
			);
			
			CommandAPIHandler.getInstance().getField(ServerResources.class, "j").set(serverResources, replacement);
		} catch (IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		
		// Construct the new CompletableFuture that now uses our updated serverResources
		CompletableFuture<?> unitCompletableFuture = ((ReloadableResourceManager) serverResources.getResourceManager()).reload(
			Util.backgroundExecutor(),
			Runnable::run, 
			MINECRAFT_SERVER.getPackRepository().openAllSelected(), 
			CompletableFuture.completedFuture(null)
		);		
		CompletableFuture<ServerResources> completablefuture = unitCompletableFuture
			.whenComplete((Object u, Throwable t) -> {
				if (t != null) {
					serverResources.close();
				}
			})
			.thenApply((Object u) -> serverResources);

		// Run the completableFuture and bind tags
		try {
			completablefuture.get().updateGlobals();

			// Register recipes again because reloading datapacks removes all non-vanilla recipes
			Recipe recipe;
			while(recipes.hasNext()) {
				recipe = recipes.next();
				try {
					Bukkit.addRecipe(recipe);
					if (recipe instanceof Keyed keyedRecipe) {
						CommandAPI.logInfo("Re-registering recipe: " + keyedRecipe.getKey());
					}
				} catch (Exception e) {
					continue; // Can't re-register registered recipes. Not an error.
				}
			}

			CommandAPI.logNormal("Finished reloading datapacks");
		} catch (Exception e) {
			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			e.printStackTrace(printWriter);
			
			CommandAPI.logError("Failed to load datapacks, can't proceed with normal server load procedure. Try fixing your datapacks?\n" + stringWriter.toString());
		}
	}

	@Override
	public void resendPackets(Player player) {
		MINECRAFT_SERVER.getCommands().sendCommands(((CraftPlayer) player).getHandle());
	}
}
