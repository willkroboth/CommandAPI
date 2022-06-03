package dev.jorel.commandapi.arguments;

import org.bukkit.command.CommandSender;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.jorel.commandapi.nms.BukkitNMS;
import dev.jorel.commandapi.nms.NMS;

public abstract class BukkitArgument<T, A extends CommandSender> extends ArgumentBase<T, A, BukkitArgument<T, A>> {

	protected BukkitArgument(String nodeName, ArgumentType<?> rawType) {
		super(nodeName, rawType);
	}
	
	@Override
	public <CommandSourceStack> T parseArgument(NMS<CommandSourceStack, A> nms,
			CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException {
		return parseArgument(nms, cmdCtx, key);
	}

	public abstract <CommandSourceStack> T parseArgument(BukkitNMS<CommandSourceStack> nms,
			CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException;

}
