package dev.jorel.commandapi;

import java.util.Optional;

public record CommandHelp(String commandName, Optional<String> shortDescription, Optional<String> fullDescription,
		String[] aliases, CommandPermission permission) {
};