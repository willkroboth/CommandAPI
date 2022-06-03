package dev.jorel.commandapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import dev.jorel.commandapi.nms.BukkitNMS;

public class BukkitCommandAPIHandler<CommandSourceStack> extends CommandAPIHandler<CommandSourceStack, CommandSender> {

	final List<CommandHelp> commands;
	final BukkitNMS<CommandSourceStack> NMS;
	final CommandDispatcher<CommandSourceStack> DISPATCHER;
	final List<RegisteredCommand> registeredCommands; //Keep track of what has been registered for type checking

	private static BukkitCommandAPIHandler<?> instance;
	
	public static BukkitCommandAPIHandler<?> getInstance() {
		if(instance == null) {
			instance = new BukkitCommandAPIHandler<>();
		}
		return instance;
	}
	
	
	private BukkitCommandAPIHandler() {
		String bukkit = Bukkit.getServer().toString();
		NMS = (BukkitNMS<CommandSourceStack>) CommandAPIVersionHandler.getNMS(bukkit.substring(bukkit.indexOf("minecraftVersion") + 17, bukkit.length() - 1));
		DISPATCHER = NMS.getBrigadierDispatcher();
		registeredCommands = new ArrayList<>();
		commands = new ArrayList<>();
	}


	@Override
	public BukkitNMS<CommandSourceStack> getNMS() {
		return this.NMS;
	}

	/**
	 * Checks if a sender has a given permission.
	 * 
	 * @param sender     the sender to check permissions of
	 * @param permission the CommandAPI CommandPermission permission to check
	 * @return true if the sender satisfies the provided permission
	 */
	@Override
	boolean permissionCheck(CommandSender sender, CommandPermission permission,
			Predicate<CommandSender> requirements) {
		boolean satisfiesPermissions;
		if (sender == null) {
			satisfiesPermissions = true;
		} else {
			if (permission.equals(CommandPermission.NONE)) {
				satisfiesPermissions = true;
			} else if (permission.equals(CommandPermission.OP)) {
				satisfiesPermissions = sender.isOp();
			} else {
				satisfiesPermissions = sender.hasPermission(permission.getPermission().get());
			}
		}
		if(permission.isNegated()) {
			satisfiesPermissions = !satisfiesPermissions;
		}
		return satisfiesPermissions && requirements.test(sender);
	}

	/**
	 * Creates a literal for a given name that requires a specified permission.
	 * 
	 * @param commandName the name fo the literal to create
	 * @param permission  the permission required to use this literal
	 * @return a brigadier LiteralArgumentBuilder representing a literal
	 */
	LiteralArgumentBuilder<CommandSourceStack> getLiteralArgumentBuilderArgument(String commandName, CommandPermission permission, Predicate<CommandSender> requirements) {
		LiteralArgumentBuilder<CommandSourceStack> builder = LiteralArgumentBuilder.literal(commandName);
		return builder.requires((CommandSourceStack css) -> permissionCheck(NMS.getImplementedSenderFromCSS(css), permission, requirements));
	}


	@Override
	void fixPermissions() {
		// Get the command map to find registered commands
		SimpleCommandMap map = NMS.getSimpleCommandMap();

		if(!PERMISSIONS_TO_FIX.isEmpty()) {
			CommandAPI.logInfo("Linking permissions to commands:");
		}

		for(Entry<String, CommandPermission> entry : PERMISSIONS_TO_FIX.entrySet()) {
			String cmdName = entry.getKey();
			CommandPermission perm = entry.getValue();
			CommandAPI.logInfo(perm.toString() + " -> /" + cmdName);

			final String permNode;
			if(perm.isNegated() || perm.equals(CommandPermission.NONE) || perm.equals(CommandPermission.OP)) {
				permNode = "";
			} else if(perm.getPermission().isPresent()) {
				permNode = perm.getPermission().get();
			} else {
				// This case should never occur. Worth testing this with some assertion
				permNode = null;
			}

			/*
			 * Sets the permission. If you have to be OP to run this command,
			 * we set the permission to null. Doing so means that Bukkit's
			 * "testPermission" will always return true, however since the
			 * command's permission check occurs internally via the CommandAPI,
			 * this isn't a problem.
			 * 
			 * If anyone dares tries to use testPermission() on this command,
			 * seriously, what are you doing and why?
			 */
			if (NMS.isVanillaCommandWrapper(map.getCommand(cmdName))) {
				map.getCommand(cmdName).setPermission(permNode);
			}
			if (NMS.isVanillaCommandWrapper(map.getCommand("minecraft:" + cmdName))) {
				map.getCommand(cmdName).setPermission(permNode);
			}
		}
		CommandAPI.logNormal("Linked " + PERMISSIONS_TO_FIX.size() + " Bukkit permissions to commands");
	}
	
}
