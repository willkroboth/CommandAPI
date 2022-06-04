package dev.jorel.commandapi.arguments;

import org.bukkit.command.CommandSender;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.jorel.commandapi.nms.BukkitNMS;
import dev.jorel.commandapi.nms.NMS;

public abstract class Argument<T> extends ArgumentBase<T, CommandSender, Argument<T>> {

	protected Argument(String nodeName, ArgumentType<?> rawType) {
		super(nodeName, rawType);
	}
	
	@Override
	public <CommandListenerWrapper> T parseArgument(NMS<CommandListenerWrapper, CommandSender> nms,
			CommandContext<CommandListenerWrapper> cmdCtx, String key) throws CommandSyntaxException {
		return (T) parseArgument((BukkitNMS<CommandListenerWrapper>) nms, cmdCtx, key);
	}
	
	public abstract <CommandListenerWrapper> T parseArgument(BukkitNMS<CommandListenerWrapper> nms,
			CommandContext<CommandListenerWrapper> cmdCtx, String key) throws CommandSyntaxException;
}
