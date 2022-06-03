package dev.jorel.commandapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.help.HelpTopic;

import dev.jorel.commandapi.nms.BukkitNMS;
import dev.jorel.commandapi.nms.NMS;

public class BukkitHelp {

	public void updateHelpForCommands(Collection<CommandHelp> commands) {
		Map<String, HelpTopic> helpTopicsToAdd = new HashMap<>();

		for(CommandHelp command : commands) {
			// Generate short description
			final String shortDescription;
			if(command.shortDescription().isPresent()) {
				shortDescription = command.shortDescription().get();
			} else if(command.fullDescription().isPresent()) {
				shortDescription = command.fullDescription().get();
			} else {
				shortDescription = "A Mojang provided command.";
			}

			// Generate full description
			StringBuilder sb = new StringBuilder();
			if(command.fullDescription().isPresent()) {
				sb.append(ChatColor.GOLD + "Description: " + ChatColor.WHITE + command.fullDescription().get() + "\n");
			}

			generateHelpUsage(sb, command);
			sb.append("\n");

			// Generate aliases. We make a copy of the StringBuilder because we
			// want to change the output when we register aliases
			StringBuilder aliasSb = new StringBuilder(sb.toString());
			if(command.aliases().length > 0) {
				sb.append(ChatColor.GOLD + "Aliases: " + ChatColor.WHITE + String.join(", ", command.aliases()));
			}

			// Must be empty string, not null as defined by OBC::CustomHelpTopic
			String permission = command.permission().getPermission().orElseGet(() -> "");

			// Don't override the plugin help topic
			String commandPrefix = generateCommandHelpPrefix(command.commandName());
			helpTopicsToAdd.put(commandPrefix, BukkitNMS.get().generateHelpTopic(commandPrefix, shortDescription, sb.toString().trim(), permission));

			for(String alias : command.aliases()) {
				StringBuilder currentAliasSb = new StringBuilder(aliasSb.toString());
				if(command.aliases().length > 0) {
					currentAliasSb.append(ChatColor.GOLD + "Aliases: " + ChatColor.WHITE);

					// We want to get all aliases (including the original command name),
					// except for the current alias
					List<String> aliases = new ArrayList<>(Arrays.asList(command.aliases()));
					aliases.add(command.commandName());
					aliases.remove(alias);

					currentAliasSb.append(ChatColor.WHITE + String.join(", ", aliases));
				}

				// Don't override the plugin help topic
				commandPrefix = generateCommandHelpPrefix(alias);
				helpTopicsToAdd.put(commandPrefix, BukkitNMS.get().generateHelpTopic(commandPrefix, shortDescription, currentAliasSb.toString().trim(), permission));
			}
		}

		BukkitNMS.get().addToHelpMap(helpTopicsToAdd);
	}

	private String generateCommandHelpPrefix(String command) {
		return (Bukkit.getPluginCommand(command) == null ? "/" : "/minecraft:") + command;
	}

	private void generateHelpUsage(StringBuilder sb, CommandHelp command) {
		sb.append(ChatColor.GOLD + "Usage: " + ChatColor.WHITE);

		// Generate usages
		List<String> usages = new ArrayList<>();
		for(RegisteredCommand rCommand : registeredCommands) {
			if(rCommand.command().equals(command.commandName())) {
				StringBuilder usageString = new StringBuilder();
				usageString.append("/" + command.commandName() + " ");
				for(String arg : rCommand.argsAsStr()) {
					usageString.append("<" + arg.split(":")[0] + "> ");
				}
				usages.add(usageString.toString());
			}
		}

		// If 1 usage, put it on the same line, otherwise format like a list
		if(usages.size() == 1) {
			sb.append(usages.get(0));
		} else if(usages.size() > 1) {
			for(String usage : usages) {
				sb.append("\n- " + usage);
			}
		}
	}
	
}
