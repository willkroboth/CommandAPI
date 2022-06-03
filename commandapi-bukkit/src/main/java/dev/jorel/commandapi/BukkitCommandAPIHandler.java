package dev.jorel.commandapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.help.HelpTopic;
import org.bukkit.permissions.Permission;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import dev.jorel.commandapi.arguments.BukkitArgument;
import dev.jorel.commandapi.nms.BukkitNMS;

public class BukkitCommandAPIHandler<CommandSourceStack> extends CommandAPIHandler<BukkitNMS<CommandSourceStack>, CommandSourceStack, CommandSender, BukkitArgument<?>> {

	final List<RegisteredCommand> registeredCommands; //Keep track of what has been registered for type checking
	private static BukkitCommandAPIHandler<?> instance;
	
	public static BukkitCommandAPIHandler<?> getInstance() {
		if(instance == null) {
			instance = new BukkitCommandAPIHandler<>();
		}
		return instance;
	}
	
	private BukkitCommandAPIHandler() {
		super(getNewNMS());
		super.DISPATCHER = NMS.getBrigadierDispatcher();
		registeredCommands = new ArrayList<>();
	}
	
	@SuppressWarnings("unchecked")
	private static <CommandSourceStack> BukkitNMS<CommandSourceStack> getNewNMS() {
		String bukkit = Bukkit.getServer().toString();
		return (BukkitNMS<CommandSourceStack>) CommandAPIVersionHandler.getNMS(bukkit.substring(bukkit.indexOf("minecraftVersion") + 17, bukkit.length() - 1));
	}

    /**
     * This permission generation setup ONLY works iff: 
     * <ul>
     * <li>You register the parent permission node FIRST.</li>
     * <li>Example:<br>/mycmd - permission node: <code>my.perm</code> <br>/mycmd &lt;arg> - permission node: <code>my.perm.other</code></li>
     * </ul>
     *
     * The <code>my.perm.other</code> permission node is revoked for the COMMAND REGISTRATION, however: 
     * <ul>
     * <li>The permission node IS REGISTERED.</li> 
     * <li>The permission node, if used for an argument (as in this case), 
     *      will be used for suggestions for said argument</li></ul>
     * @param requirements 
     */
	@Override
    Predicate<CommandSourceStack> generatePermissions(String commandName, CommandPermission permission, Predicate<CommandSender> requirements) {
        // If we've already registered a permission, set it to the "parent" permission.
        if (PERMISSIONS_TO_FIX.containsKey(commandName.toLowerCase())) {
            if (!PERMISSIONS_TO_FIX.get(commandName.toLowerCase()).equals(permission)) {
                permission = PERMISSIONS_TO_FIX.get(commandName.toLowerCase());
            }
        } else {
            // Add permission to a list to fix conflicts with minecraft:permissions
            PERMISSIONS_TO_FIX.put(commandName.toLowerCase(), permission);
        }

        final CommandPermission finalPermission = permission;

        // Register it to the Bukkit permissions registry
        if (finalPermission.getPermission().isPresent()) {
            try {
                Bukkit.getPluginManager().addPermission(new Permission(finalPermission.getPermission().get()));
            } catch (IllegalArgumentException e) {
                assert true; // nop, not an error.
            }
        }

        return (CommandSourceStack css) -> permissionCheck(NMS.getImplementedSenderFromCSS(css), finalPermission, requirements);
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

	public void updateHelpForCommands() {
		Map<String, HelpTopic> helpTopicsToAdd = new HashMap<>();

		for(CommandHelp command : super.commands) {
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

	@Override
	public CommandAPICommandBase<? extends CommandAPICommandBase<?, CommandSender, BukkitArgument<?>>, CommandSender, BukkitArgument<?>> createCommandBase(
			CommandMetaData<CommandSender> meta) {
			return new CommandAPICommand(meta);
	}
	
	
	
}
