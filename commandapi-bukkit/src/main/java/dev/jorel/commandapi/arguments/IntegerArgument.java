package dev.jorel.commandapi.arguments;

import org.bukkit.command.CommandSender;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.jorel.commandapi.exceptions.InvalidRangeException;
import dev.jorel.commandapi.nms.BukkitNMS;

public class IntegerArgument extends UnaryBukkitSafeOverrideableArgument<Integer> implements IntegerArgumentBase<CommandSender> {

	/**
	 * An integer argument
	 * @param nodeName the name of the node for this argument
	 */
	public IntegerArgument(String nodeName) {
		super(nodeName, IntegerArgumentType.integer(), String::valueOf);
	}
	
	/**
	 * An integer argument with a minimum value
	 * @param nodeName the name of the node for this argument
	 * @param min The minimum value this argument can take (inclusive)
	 */
	public IntegerArgument(String nodeName, int min) {
		super(nodeName, IntegerArgumentType.integer(min), String::valueOf);
	}
	
	/**
	 * An integer argument with a minimum and maximum value
	 * @param nodeName the name of the node for this argument
	 * @param min The minimum value this argument can take (inclusive)
	 * @param max The maximum value this argument can take (inclusive)
	 */
	public IntegerArgument(String nodeName, int min, int max) {
		super(nodeName, IntegerArgumentType.integer(min, max), String::valueOf);
		if(max < min) {
			throw new InvalidRangeException();
		}
	}

	@Override
	public <CommandSourceStack> Integer parseArgument(BukkitNMS<CommandSourceStack> nms,
			CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException {
		return IntegerArgumentBase.super.parseArgument(nms, cmdCtx, key);
	}

}
