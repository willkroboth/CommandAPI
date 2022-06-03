package dev.jorel.commandapi.arguments;

import org.bukkit.command.CommandSender;

import com.mojang.brigadier.arguments.ArgumentType;

public abstract class Argument<T> extends BukkitArgument<T, CommandSender> {

	protected Argument(String nodeName, ArgumentType<?> rawType) {
		super(nodeName, rawType);
	}
}
