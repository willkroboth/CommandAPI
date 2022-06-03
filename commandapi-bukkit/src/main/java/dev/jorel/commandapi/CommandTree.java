package dev.jorel.commandapi;

import org.bukkit.command.CommandSender;

import dev.jorel.commandapi.arguments.BukkitArgument;

/**
 * This is the root node for creating a command as a tree
 */
public class CommandTree extends CommandTreeBase<CommandSender, BukkitArgument<?>> {

	public CommandTree(final String commandName) {
		super(commandName);
	}

}
