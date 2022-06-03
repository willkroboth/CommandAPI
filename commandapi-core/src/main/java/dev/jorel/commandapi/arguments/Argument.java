/*******************************************************************************
 * Copyright 2018, 2021 Jorel Ali (Skepter) - MIT License
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.mojang.brigadier.arguments.ArgumentType;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.CommandPermission;

/**
 * The core abstract class for Command API arguments
 * 
 * @param <T> The type of the underlying object that this argument casts to
 */
public abstract class Argument<T, ImplementedSender, ArgumentImpl extends Argument<T, ImplementedSender, ArgumentImpl>>
		extends ArgumentTree<ImplementedSender, ArgumentImpl> implements IArgumentBase<T, ImplementedSender> {

	////////////////////////
	// Raw Argument Types //
	////////////////////////

	private final String nodeName;
	private final ArgumentType<?> rawType;

	/**
	 * Constructs an argument with a given NMS/brigadier type.
	 * 
	 * @param nodeName the name to assign to this argument node 
	 * @param rawType the NMS or brigadier type to be used for this argument
	 */
	protected Argument(String nodeName, ArgumentType<?> rawType) {
		this.nodeName = nodeName;
		this.rawType = rawType;
	}

	/**
	 * Returns the NMS or brigadier type for this argument.
	 * 
	 * @return the NMS or brigadier type for this argument
	 */
	public final ArgumentType<?> getRawType() {
		return this.rawType;
	}
	
	/**
	 * Returns the name of this argument's node
	 * @return the name of this argument's node
	 */
	public final String getNodeName() {
		return this.nodeName;
	}

	/////////////////
	// Suggestions //
	/////////////////

	private Optional<ArgumentSuggestions<ImplementedSender>> suggestions = Optional.empty();
	private Optional<ArgumentSuggestions<ImplementedSender>> addedSuggestions = Optional.empty();

	/**
	 * Include suggestions to add to the list of default suggestions represented by this argument.
	 *
	 * @param suggestions An {@link ArgumentSuggestions} object representing the suggestions. Use the
	 * Static methods on ArgumentSuggestions to create these.
	 *
	 * @return the current argument
	 */
	@SuppressWarnings("unchecked")
	public ArgumentImpl includeSuggestions(ArgumentSuggestions<ImplementedSender> suggestions) {
		this.addedSuggestions = Optional.of(suggestions);
		return (ArgumentImpl) this;
	}

	/**
	 * Returns an optional function which produces an array of suggestions which should be added
	 * to existing suggestions.
	 * @return An Optional containing a function which generates suggestions
	 */
	public Optional<ArgumentSuggestions<ImplementedSender>> getIncludedSuggestions() {
		return addedSuggestions;
	}


	/**
	 * Replace the suggestions of this argument.
	 * @param suggestions An {@link ArgumentSuggestions} object representing the suggestions. Use the static methods in
	 * ArgumentSuggestions to create these.
	 * @return the current argument
	 */
	@SuppressWarnings("unchecked")
	public ArgumentImpl replaceSuggestions(ArgumentSuggestions<ImplementedSender> suggestions) {
		this.suggestions = Optional.of(suggestions);
		return (ArgumentImpl) this;
	}

	/**
	 * Returns an optional function that maps the command sender to an IStringTooltip array of
	 * suggestions for the current command
	 * 
	 * @return a function that provides suggestions, or <code>Optional.empty()</code> if there
	 *         are no overridden suggestions.
	 */
	public final Optional<ArgumentSuggestions<ImplementedSender>> getOverriddenSuggestions() {
		return suggestions;
	}

	/////////////////
	// Permissions //
	/////////////////

	private CommandPermission permission = CommandPermission.NONE;

	/**
	 * Assigns the given permission as a requirement to execute this command.
	 * 
	 * @param permission the permission required to execute this command
	 * @return this current argument
	 */
	@SuppressWarnings("unchecked")
	public final ArgumentImpl withPermission(CommandPermission permission) {
		this.permission = permission;
		return (ArgumentImpl) this;
	}
	
	/**
	 * Assigns the given permission as a requirement to execute this command.
	 * 
	 * @param permission the permission required to execute this command
	 * @return this current argument
	 */
	@SuppressWarnings("unchecked")
	public final ArgumentImpl withPermission(String permission) {
		this.permission = CommandPermission.fromString(permission);
		return (ArgumentImpl) this;
	}

	/**
	 * Returns the permission required to run this command
	 * @return the permission required to run this command
	 */
	public final CommandPermission getArgumentPermission() {
		return permission;
	}
	
	//////////////////
	// Requirements //
	//////////////////
	
	private Predicate<ImplementedSender> requirements = s -> true;
		
	/**
	 * Returns the requirements required to run this command
	 * @return the requirements required to run this command
	 */
	public final Predicate<ImplementedSender> getRequirements() {
		return this.requirements;
	}
	
	/**
	 * Adds a requirement that has to be satisfied to use this argument. This method
	 * can be used multiple times and each use of this method will AND its
	 * requirement with the previously declared ones
	 * 
	 * @param requirement the predicate that must be satisfied to use this argument
	 * @return this current argument
	 */
	@SuppressWarnings("unchecked")
	public final ArgumentImpl withRequirement(Predicate<ImplementedSender> requirement) {
		this.requirements = this.requirements.and(requirement);
		return (ArgumentImpl) this;
	}
	
	/////////////////
	// Listability //
	/////////////////
	
	private boolean isListed = true;
	
	/**
	 * Returns true if this argument will be listed in the Object args[] of the command executor
	 * @return true if this argument will be listed in the Object args[] of the command executor
	 */
	public boolean isListed() {
		return this.isListed;
	}
	
	/**
	 * Sets whether this argument will be listed in the Object args[] of the command executor
	 * @param listed if true, this argument will be included in the Object args[] of the command executor
	 * @return this current argument
	 */
	@SuppressWarnings("unchecked")
	public ArgumentImpl setListed(boolean listed) {
		this.isListed = listed;
		return (ArgumentImpl) this;
	}

	////////////////////////////
	// Platform-specific code //
	////////////////////////////

	public List<String> getEntityNames(Object args) {
		return Arrays.asList(new String[] { null });
	}

}