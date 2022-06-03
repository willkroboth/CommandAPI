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
package dev.jorel.commandapi;

/**
 * A class that represents information which you can use to generate
 * suggestions.
 */
public class SuggestionInfoBase {

	private final CommandAPICommandSender sender;
	private final Object[] previousArgs;
	private final String currentInput;
	private final String currentArg;

	/**
	 * Creates an instance of SuggestionInfo for suggestions
	 * 
	 * @param sender       - the CommandSender typing this command
	 * @param previousArgs - the list of previously declared (and parsed) arguments
	 * @param currentInput - a string representing the full current input (including
	 *                     /)
	 * @param currentArg   - the current partially typed argument. For example
	 *                     "/mycmd tes" will return "tes"
	 */
	public SuggestionInfoBase(CommandAPICommandSender sender, Object[] previousArgs, String currentInput,
			String currentArg) {
		this.sender = sender;
		this.previousArgs = previousArgs;
		this.currentInput = currentInput;
		this.currentArg = currentArg;
	}

	/**
	 * The CommandSender typing this command
	 */
	public CommandAPICommandSender sender() {
		return this.sender;
	}

	/**
	 * The list of previously declared (and parsed) arguments
	 */
	public Object[] previousArgs() {
		return this.previousArgs;
	}

	/**
	 * A string representing the full current input (including /)
	 */
	public String currentInput() {
		return this.currentInput;
	}

	/**
	 * The current partially typed argument. For example "/mycmd tes" will return
	 * "tes"
	 */
	public String currentArg() {
		return this.currentArg;
	}
}
