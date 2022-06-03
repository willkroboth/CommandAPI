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


import java.util.function.Function;

import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;

import com.mojang.brigadier.arguments.ArgumentType;

/**
 * An interface declaring methods required to override argument suggestions
 * 
 * @param <T> The type of the underlying object that this argument casts to
 * @param <S> A custom type which is represented by this argument. For example, a {@link LocationArgument} will have a custom type <code>Location</code>
 */
public abstract class BukkitSafeOverrideableArgument<T, S, ArgumentImpl extends BukkitSafeOverrideableArgument<T, S, ArgumentImpl>> extends BukkitArgument<T> {

	private final Function<S, String> mapper;

	/**
	 * Instantiates this argument and assigns the mapper to the provided mapper
	 * 
	 * @param nodeName the node name of this argument
	 * @param rawType  the NMS raw argument type of this argument
	 * @param mapper   the mapping function that maps this argument type to a string
	 *                 for suggestions
	 */
	protected BukkitSafeOverrideableArgument(String nodeName, ArgumentType<?> rawType, Function<S, String> mapper) {
		super(nodeName, rawType);
		this.mapper = mapper;
	}

	@SuppressWarnings("unchecked")
	public final ArgumentImpl replaceSafeSuggestions(SafeSuggestions<S, CommandSender> suggestions) {
		replaceSuggestions(suggestions.toSuggestions(mapper));
		return (ArgumentImpl) this;
	}

	/**
	 * Replaces the suggestions with a safe {@link SafeSuggestions} object. Use the static methods in safe suggestions to create safe suggestions.
	 * @param suggestions The safe suggestions to use
	 * @return the current argument
	 */
	@SuppressWarnings("unchecked")
	public final ArgumentImpl includeSafeSuggestions(SafeSuggestions<S, CommandSender> suggestions) {
		return (ArgumentImpl) this.includeSuggestions(suggestions.toSuggestions(mapper));
	}

	static <S> Function<S, String> fromKey(Function<S, NamespacedKey> mapper) {
		return mapper.andThen(NamespacedKey::toString);
	}
}
