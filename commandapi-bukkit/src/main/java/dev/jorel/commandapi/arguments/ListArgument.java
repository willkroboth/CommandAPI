/*******************************************************************************
 * Copyright 2022 Jorel Ali (Skepter) - MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package dev.jorel.commandapi.arguments;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.bukkit.command.CommandSender;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.nms.BukkitNMS;

/**
 * An argument that accepts a list of objects
 * @param <T> the type that this list argument generates a list of.
 */
@SuppressWarnings("rawtypes")
public class ListArgument<T> extends Argument<List> implements IGreedyArgument, ListArgumentBase<T, CommandSender> {

	private final String delimiter;
	private final boolean allowDuplicates;
	private final Function<CommandSender, Collection<T>> supplier;
	private final Function<T, IStringTooltip> mapper;

	ListArgument(String nodeName, String delimiter, boolean allowDuplicates, Function<CommandSender, Collection<T>> supplier, Function<T, IStringTooltip> mapper) {
		super(nodeName, StringArgumentType.greedyString());
		this.delimiter = delimiter;
		this.allowDuplicates = allowDuplicates;
		this.supplier = supplier;
		this.mapper = mapper;

		this.replaceSuggestions(ListArgumentBase.super.applySuggestions(delimiter, allowDuplicates, supplier, mapper));
	}

	@Override
	public <CommandSourceStack> List<T> parseArgument(BukkitNMS<CommandSourceStack> nms,
			CommandContext<CommandSourceStack> cmdCtx, String key) throws CommandSyntaxException {
		return ListArgumentBase.super.parseArgument(nms, cmdCtx, key, delimiter, allowDuplicates, supplier, mapper);
	}
}
