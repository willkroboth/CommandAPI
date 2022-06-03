package dev.jorel.commandapi.arguments;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import dev.jorel.commandapi.IStringTooltip;
import dev.jorel.commandapi.SuggestionInfoBase;
import dev.jorel.commandapi.Tooltip;

/**
 * This class represents safe suggestions. These are parameterized suggestions which can be converted
 * into a string under a mapping function.
 * {@link SafeSuggestions} instances are best created using the static methods as opposed to functionally.
 * @param <S> the type of the suggestions
 */
@FunctionalInterface
public interface SafeSuggestions<S, ImplementedSender> {

	/**
	 * Convert this {@link SafeSuggestions} object into an {@link ArgumentSuggestions} by mapping the values with a string
	 * mapping function.
	 * @param mapper a function which maps an instance of {@link S} to a string.
	 * @return an {@link ArgumentSuggestions} object resulting from this mapping.
	 */
	ArgumentSuggestions<ImplementedSender> toSuggestions(Function<S, String> mapper);

	/**
	 * Create an empty SafeSuggestions object.
	 * @param <T, ImplementedSender> type parameter of the SafeSuggestions object
	 * @return a SafeSuggestions object resulting in empty suggestions
	 */
	static <T, ImplementedSender> SafeSuggestions<T, ImplementedSender> empty() {
		return (mapper) -> ArgumentSuggestions.empty();
	}

	/**
	 * Hardcode values to suggest
	 * @param suggestions hardcoded values
	 * @param <T, ImplementedSender> type of the values
	 * @return a SafeSuggestions object suggesting the hardcoded suggestions
	 */
	@SafeVarargs
	static <T, ImplementedSender> SafeSuggestions<T, ImplementedSender> suggest(T... suggestions) {
		return (mapper) -> ArgumentSuggestions.strings(toStrings(mapper, suggestions));
	}

	/**
	 * Suggest values as the result of a function
	 * @param suggestions function providing the values
	 * @param <T, ImplementedSender> type of the values
	 * @return a SafeSuggestion object suggesting the result of the function
	 */
	static <T, ImplementedSender> SafeSuggestions<T, ImplementedSender> suggest(Function<SuggestionInfoBase<ImplementedSender>, T[]> suggestions) {
		return (mapper) -> ArgumentSuggestions.strings(info -> toStrings(mapper, suggestions.apply(info)));
	}

	/**
	 * Suggest values provided asynchronously
	 * @param suggestions function providing the values asynchronously
	 * @param <T, ImplementedSender> type of the values
	 * @return a SafeSuggestion object suggestion the result of the asynchronous function
	 */
	static <T, ImplementedSender> SafeSuggestions<T, ImplementedSender> suggestAsync(Function<SuggestionInfoBase<ImplementedSender>, CompletableFuture<T[]>> suggestions) {
		return (mapper) -> ArgumentSuggestions.stringsAsync(info -> suggestions
			.apply(info)
			.thenApply(items -> toStrings(mapper, items)));
	}

	/**
	 * Suggest hardcoded values with tooltips
	 * @param suggestions hardcoded values with their tooltips
	 * @param <T, ImplementedSender> type of the values
	 * @return a SafeSuggestion object suggesting the hardcoded values
	 */
	@SafeVarargs
	static <T, ImplementedSender> SafeSuggestions<T, ImplementedSender> tooltips(Tooltip<T>... suggestions) {
		return (mapper) -> ArgumentSuggestions.stringsWithTooltips(toStringsWithTooltips(mapper, suggestions));
	}

	/**
	 * Suggest values with tooltips as the result of a function
	 * @param suggestions function providing the values with tooltips
	 * @param <T, ImplementedSender> type of the values
	 * @return a SafeSuggestion object suggesting the result of the function
	 */
	static <T, ImplementedSender> SafeSuggestions<T, ImplementedSender> tooltips(Function<SuggestionInfoBase<ImplementedSender>, Tooltip<T>[]> suggestions) {
		return (mapper) -> ArgumentSuggestions.stringsWithTooltips(info -> toStringsWithTooltips(mapper,
			suggestions.apply(info)
		));
	}

	/**
	 * Suggest values with tooltips asynchronously
	 * @param suggestions function providing the values with tooltips
	 * @param <T, ImplementedSender> type of the values
	 * @return a SafeSuggestion suggesting the result of the asynchronous function
	 */
	static <T, ImplementedSender> SafeSuggestions<T, ImplementedSender> tooltipsAsync(Function<SuggestionInfoBase<ImplementedSender>, CompletableFuture<Tooltip<T>[]>> suggestions) {
		return (mapper) -> ArgumentSuggestions.stringsWithTooltipsAsync(info -> suggestions
			.apply(info)
			.thenApply(items -> toStringsWithTooltips(mapper, items)));
	}

	/**
	 * Convert an array of values into an array of strings using the mapping function
	 * @param mapper the mapping function
	 * @param suggestions the array of values
	 * @param <T, ImplementedSender> type of the values
	 * @return array of strings representing the array of values under the mapping function
	 */
	@SafeVarargs
	private static <T, ImplementedSender> String[] toStrings(Function<T, String> mapper, T... suggestions) {
		String[] strings = new String[suggestions.length];
		for(int i = 0; i < suggestions.length; i++) {
			strings[i] = mapper.apply(suggestions[i]);
		}
		return strings;
	}

	/**
	 * Convert an array of values with tooltips into an array of strings with tooltips using the mapping function
	 * @param mapper the mapping function
	 * @param suggestions the array of values with tooltips
	 * @param <T, ImplementedSender> type of the values
	 * @return array of strings with tooltips representing the array of values with tooltips under the mapping function
	 */
	@SafeVarargs
	private static <T, ImplementedSender> IStringTooltip[] toStringsWithTooltips(Function<T, String> mapper, Tooltip<T>... suggestions) {
		IStringTooltip[] stringsWithTooltips = new IStringTooltip[suggestions.length];
		for(int i = 0; i < suggestions.length; i++) {
			stringsWithTooltips[i] = Tooltip
				.build(mapper)
				.apply(suggestions[i]);
		}
		return stringsWithTooltips;
	}

}
