package dev.jorel.commandapi.arguments;

import org.bukkit.command.CommandSender;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.jorel.commandapi.nms.BukkitNMS;

public class GreedyStringArgument extends UnaryBukkitSafeOverrideableArgument<String> implements GreedyStringArgumentBase<CommandSender> {

	public GreedyStringArgument(String nodeName) {
		super(nodeName, GreedyStringArgumentBase.getRawType(), GreedyStringArgumentBase.MAPPER);
	}

	@Override
	public <CommandSourceStack> String parseArgument(BukkitNMS<CommandSourceStack> nms,
			CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException {
		return GreedyStringArgumentBase.super.parseArgument(nms, cmdCtx, key);
	}

}
