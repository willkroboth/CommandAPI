package dev.jorel.commandapi;

import org.bukkit.command.CommandSender;

/**
 * This is the root node for creating a command as a tree
 */
public class CommandTree extends CommandTreeBase<CommandSender> {

	public CommandTree(final String commandName) {
		super(commandName);
	}

}
