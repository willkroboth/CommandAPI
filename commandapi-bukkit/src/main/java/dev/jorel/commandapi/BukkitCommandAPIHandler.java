package dev.jorel.commandapi;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

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
	boolean permissionCheck(CommandAPICommandSender<CommandSender> sender, CommandPermission permission,
			Predicate<CommandAPICommandSender<CommandSender>> requirements) {
		boolean satisfiesPermissions;
		if (sender == null) {
			satisfiesPermissions = true;
		} else {
			if (permission.equals(CommandPermission.NONE)) {
				satisfiesPermissions = true;
			} else if (permission.equals(CommandPermission.OP)) {
				satisfiesPermissions = sender.getBase().isOp();
			} else {
				satisfiesPermissions = sender.getBase().hasPermission(permission.getPermission().get());
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
	LiteralArgumentBuilder<CommandSourceStack> getLiteralArgumentBuilderArgument(String commandName, CommandPermission permission, Predicate<CommandAPICommandSender> requirements) {
		LiteralArgumentBuilder<CommandSourceStack> builder = LiteralArgumentBuilder.literal(commandName);
		return builder.requires((CommandSourceStack css) -> permissionCheck(NMS.getCommandSenderFromCSS(css), permission, requirements));
	}
	
}
