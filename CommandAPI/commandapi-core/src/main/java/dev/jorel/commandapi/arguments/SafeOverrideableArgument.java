/*******************************************************************************
 * Copyright 2018, 2020 Jorel Ali (Skepter) - MIT License
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

import com.mojang.brigadier.arguments.ArgumentType;

import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.SuggestionInfo;
import dev.jorel.commandapi.Tooltip;

/**
 * An interface declaring methods required to override argument suggestions
 * 
 * @param <S> A custom type which is represented by this argument. For example, a {@link LocationArgument} will have a custom type <code>Location</code>
 */
public abstract class SafeOverrideableArgument<S> extends Argument {
	
	private final Function<S, String> mapper;

	/**
	 * Instantiates this argument and assigns the mapper to the provided mapper
	 * 
	 * @param nodeName the node name of this argument
	 * @param rawType  the NMS raw argument type of this argument
	 * @param mapper   the mapping function that maps this argument type to a string
	 *                 for suggestions
	 */
	protected SafeOverrideableArgument(String nodeName, ArgumentType<?> rawType, Function<S, String> mapper) {
		super(nodeName, rawType);
		this.mapper = mapper;
	}
	
	/**
	 * Replaces the suggestions of this argument with an array of suggestions.
	 * 
	 * @param suggestions a function that takes in {@link SuggestionInfo} and
	 *                    returns a {@link S} array of suggestions, where S is your custom
	 *                    type
	 * @return the current argument
	 */
	public final Argument replaceWithSafeSuggestions(Function<SuggestionInfo, S[]> suggestions) {
		return super.replaceSuggestions(suggestionsInfo -> {
			S[] sArr = suggestions.apply(suggestionsInfo);
			String[] result = new String[sArr.length];
			for(int i = 0; i < sArr.length; i++) {
				result[i] = mapper.apply(sArr[i]);
			}
			return result;
		});
	}

	/**
	 * Replaces the suggestions of this argument with an array of suggestions.
	 * 
	 * @param suggestions a function that takes in {@link SuggestionInfo} and
	 *                    returns a {@link Tooltip} array of suggestions,
	 *                    parameterized over {@link S} where S is your custom type
	 * @return the current argument
	 */
	public final Argument replaceWithSafeSuggestionsT(Function<SuggestionInfo, Tooltip<S>[]> suggestions) {
		return super.includeSuggestionsT(suggestionsInfo -> {
			Tooltip<S>[] tArr = suggestions.apply(suggestionsInfo);
			IStringTooltip[] result = new IStringTooltip[tArr.length];
			for(int i = 0; i < tArr.length; i++) {
				result[i] = Tooltip.build(mapper).apply(tArr[i]);
			}
			return result;
		});
	}
	
	/**
	 * Include suggestions to add to the list of default suggestions represented by
	 * this argument.
	 * 
	 * @param suggestions a function that takes in {@link SuggestionInfo} which
	 *                    includes information about the current state at the time
	 *                    the suggestions are run and returns a {@link S} array of
	 *                    suggestions to add, where S is your custom type
	 * @return the current argument
	 */
	public final Argument includeWithSafeSuggestions(Function<SuggestionInfo, S[]> suggestions) {
		return super.replaceSuggestions(suggestionsInfo -> {
			S[] sArr = suggestions.apply(suggestionsInfo);
			String[] result = new String[sArr.length];
			for(int i = 0; i < sArr.length; i++) {
				result[i] = mapper.apply(sArr[i]);
			}
			return result;
		});
	}

	/**
	 * Include suggestions to add to the list of default suggestions represented by
	 * this argument.
	 * 
	 * @param suggestions a function that takes in {@link SuggestionInfo} which
	 *                    includes information about the current state at the time
	 *                    the suggestions are run and returns a {@link Tooltip}
	 *                    array of suggestions to add, parameterized over {@link S}
	 *                    where S is your custom type
	 * @return the current argument
	 */
	public final Argument includeWithSafeSuggestionsT(Function<SuggestionInfo, Tooltip<S>[]> suggestions) {
		return super.includeSuggestionsT(suggestionsInfo -> {
			Tooltip<S>[] tArr = suggestions.apply(suggestionsInfo);
			IStringTooltip[] result = new IStringTooltip[tArr.length];
			for(int i = 0; i < tArr.length; i++) {
				result[i] = Tooltip.build(mapper).apply(tArr[i]);
			}
			return result;
		});
	}
	
	/**
	 * Composes a <code>S</code> to a <code>NamespacedKey</code> mapping function to convert <code>S</code> to a <code>String</code>
	 * @param mapper the mapping function from <code>S</code> to <code>NamespacedKey</code>
	 * @return a composed function that converts <code>S</code> to <code>String</code>
	 */
	static <S> Function<S, String> fromKey(Function<S, NamespacedKey> mapper) {
		return mapper.andThen(NamespacedKey::toString);
	}
	
}
