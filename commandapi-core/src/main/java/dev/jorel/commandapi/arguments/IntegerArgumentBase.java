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

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.jorel.commandapi.exceptions.InvalidRangeException;
import dev.jorel.commandapi.nms.NMS;

/**
 * An argument that represents primitive Java ints
 */
public interface IntegerArgumentBase<ImplementedSender> extends IArgumentBase<Integer, ImplementedSender> {

	public static final Function<Integer, String> MAPPER = String::valueOf;

	@Override
	public default Class<Integer> getPrimitiveType() {
		return int.class;
	}

	@Override
	public default CommandAPIArgumentType getArgumentType() {
		return CommandAPIArgumentType.PRIMITIVE_INTEGER;
	}

	/**
	 * Not to be confused with the non-static method getRawType
	 */
	static ArgumentType<?> getRawType() {
		return IntegerArgumentType.integer();
	}

	public static ArgumentType<?> getRawType(int min) {
		return IntegerArgumentType.integer(min);
	}

	public static ArgumentType<?> getRawType(int min, int max) {
		if (max < min) {
			throw new InvalidRangeException();
		}
		return IntegerArgumentType.integer(min, max);
	}

	@Override
	public default <CommandListenerWrapper> Integer parseArgument(NMS<CommandListenerWrapper, ImplementedSender> nms,
			CommandContext<CommandListenerWrapper> cmdCtx, String key) throws CommandSyntaxException {
		return cmdCtx.getArgument(key, getPrimitiveType());
	}
}
