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

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import org.bukkit.Axis;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.help.HelpTopic;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.loot.LootTable;
import org.bukkit.potion.PotionEffectType;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import de.tr7zw.nbtapi.NBTContainer;
import dev.jorel.commandapi.CommandAPIBase;
import dev.jorel.commandapi.enums.EntitySelector;
import dev.jorel.commandapi.wrappers.FloatRange;
import dev.jorel.commandapi.wrappers.FunctionWrapper;
import dev.jorel.commandapi.wrappers.IntegerRange;
import dev.jorel.commandapi.wrappers.Location2D;
import dev.jorel.commandapi.wrappers.MathOperation;
import dev.jorel.commandapi.wrappers.ParticleData;
import dev.jorel.commandapi.wrappers.Rotation;
import dev.jorel.commandapi.wrappers.ScoreboardSlot;
import dev.jorel.commandapi.wrappers.SimpleFunctionWrapper;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;

public interface BukkitNMS<CommandListenerWrapper> extends NMS<CommandListenerWrapper, CommandSender> {
	
	static BukkitNMS<?> get() {
		return (BukkitNMS<?>) BukkitNMS.get();
	}
	
	@Override
	default void checkAndWarnRegisteredPluginCommand(java.lang.String commandName) {
		if (Bukkit.getPluginCommand(commandName) != null) {
			CommandAPIBase.logWarning("Plugin command /" + commandName + " is registered by Bukkit ("
					+ Bukkit.getPluginCommand(commandName).getPlugin().getName()
					+ "). Did you forget to remove this from your plugin.yml file?");
		}
	}

	String convert(ItemStack is);

	String convert(ParticleData<?> particle);

	String convert(PotionEffectType potion);

	String convert(Sound sound);

	Advancement getAdvancement(CommandContext<CommandListenerWrapper> cmdCtx, String key) throws CommandSyntaxException;

	Component getAdventureChat(CommandContext<CommandListenerWrapper> cmdCtx, String key) throws CommandSyntaxException;

	Component getAdventureChatComponent(CommandContext<CommandListenerWrapper> cmdCtx, String key);

	float getAngle(CommandContext<CommandListenerWrapper> cmdCtx, String key);

	EnumSet<Axis> getAxis(CommandContext<CommandListenerWrapper> cmdCtx, String key);

	Biome getBiome(CommandContext<CommandListenerWrapper> cmdCtx, String key) throws CommandSyntaxException;

	Predicate<Block> getBlockPredicate(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException;

	BlockData getBlockState(CommandContext<CommandListenerWrapper> cmdCtx, String key);

	/**
	 * Returns the Brigadier CommandDispatcher from the NMS CommandDispatcher
	 * 
	 * @return A Brigadier CommandDispatcher
	 */
	CommandDispatcher<CommandListenerWrapper> getBrigadierDispatcher();

	BaseComponent[] getChat(CommandContext<CommandListenerWrapper> cmdCtx, String key) throws CommandSyntaxException;

	ChatColor getChatColor(CommandContext<CommandListenerWrapper> cmdCtx, String key);

	BaseComponent[] getChatComponent(CommandContext<CommandListenerWrapper> cmdCtx, String key);

	/**
	 * Converts a CommandSender into a CLW
	 * 
	 * @param sender the command sender to convert
	 * @return a CLW.
	 */
	CommandListenerWrapper getCLWFromCommandSender(CommandSender sender);

	Environment getDimension(CommandContext<CommandListenerWrapper> cmdCtx, String key) throws CommandSyntaxException;

	Enchantment getEnchantment(CommandContext<CommandListenerWrapper> cmdCtx, String key);

	Object getEntitySelector(CommandContext<CommandListenerWrapper> cmdCtx, String key, EntitySelector selector)
			throws CommandSyntaxException;

	EntityType getEntityType(CommandContext<CommandListenerWrapper> cmdCtx, String key) throws CommandSyntaxException;

	FloatRange getFloatRange(CommandContext<CommandListenerWrapper> cmdCtx, String key);

	FunctionWrapper[] getFunction(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException;

	SimpleFunctionWrapper getFunction(NamespacedKey key);

	Set<NamespacedKey> getFunctions();

	IntegerRange getIntRange(CommandContext<CommandListenerWrapper> cmdCtx, String key);

	ItemStack getItemStack(CommandContext<CommandListenerWrapper> cmdCtx, String key) throws CommandSyntaxException;

	Predicate<ItemStack> getItemStackPredicate(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException;

	String getKeyedAsString(CommandContext<CommandListenerWrapper> cmdCtx, String key) throws CommandSyntaxException;

	Location2D getLocation2DBlock(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException;
	
	Location2D getLocation2DPrecise(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException;

	Location getLocationBlock(CommandContext<CommandListenerWrapper> cmdCtx, String str) throws CommandSyntaxException;

	Location getLocationPrecise(CommandContext<CommandListenerWrapper> cmdCtx, String str)
			throws CommandSyntaxException;

	LootTable getLootTable(CommandContext<CommandListenerWrapper> cmdCtx, String key);

	MathOperation getMathOperation(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException;

	NBTContainer getNBTCompound(CommandContext<CommandListenerWrapper> cmdCtx, String key);

	String getObjective(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws IllegalArgumentException, CommandSyntaxException;

	String getObjectiveCriteria(CommandContext<CommandListenerWrapper> cmdCtx, String key);

	ParticleData<?> getParticle(CommandContext<CommandListenerWrapper> cmdCtx, String key);

	Player getPlayer(CommandContext<CommandListenerWrapper> cmdCtx, String key) throws CommandSyntaxException;
	OfflinePlayer getOfflinePlayer(CommandContext<CommandListenerWrapper> cmdCtx, String key) throws CommandSyntaxException;

	PotionEffectType getPotionEffect(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException;

	Recipe getRecipe(CommandContext<CommandListenerWrapper> cmdCtx, String key) throws CommandSyntaxException;

	Rotation getRotation(CommandContext<CommandListenerWrapper> cmdCtx, String key);

	ScoreboardSlot getScoreboardSlot(CommandContext<CommandListenerWrapper> cmdCtx, String key);

	Collection<String> getScoreHolderMultiple(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException;

	String getScoreHolderSingle(CommandContext<CommandListenerWrapper> cmdCtx, String key)
			throws CommandSyntaxException;

	/**
	 * Returns the Server's internal (OBC) CommandMap
	 * 
	 * @return A SimpleCommandMap from the OBC server
	 */
	SimpleCommandMap getSimpleCommandMap();

	Sound getSound(CommandContext<CommandListenerWrapper> cmdCtx, String key);

	SimpleFunctionWrapper[] getTag(NamespacedKey key);

	Set<NamespacedKey> getTags();

	String getTeam(CommandContext<CommandListenerWrapper> cmdCtx, String key) throws CommandSyntaxException;

	int getTime(CommandContext<CommandListenerWrapper> cmdCtx, String key);

	UUID getUUID(CommandContext<CommandListenerWrapper> cmdCtx, String key);

	World getWorldForCSS(CommandListenerWrapper clw);

	/**
	 * Checks if a Command is an instance of the OBC VanillaCommandWrapper
	 * 
	 * @param command The Command to check
	 * @return true if Command is an instance of VanillaCommandWrapper
	 */
	boolean isVanillaCommandWrapper(Command command);

	/**
	 * Reloads the datapacks by using the updated the commandDispatcher tree
	 * 
	 * @throws SecurityException        reflection exception
	 * @throws NoSuchFieldException     reflection exception
	 * @throws IllegalAccessException   reflection exception
	 * @throws IllegalArgumentException reflection exception
	 */
	default void reloadDataPacks()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
	}

	/**
	 * Resends the command dispatcher's set of commands to a player.
	 * 
	 * @param player the player to send the command graph packet to
	 */
	void resendPackets(Player player);

	HelpTopic generateHelpTopic(String commandName, String shortDescription, String fullDescription, String permission);

	void addToHelpMap(Map<String, HelpTopic> helpTopicsToAdd);

}
