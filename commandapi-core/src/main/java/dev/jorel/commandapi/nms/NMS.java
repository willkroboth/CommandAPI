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
package dev.jorel.commandapi.nms;

import com.mojang.brigadier.arguments.ArgumentType;

import dev.jorel.commandapi.enums.EntitySelector;

public interface NMS<CommandListenerWrapper> {

	/* Argument types */
	ArgumentType<?> _ArgumentAngle();

	ArgumentType<?> _ArgumentAxis();

	ArgumentType<?> _ArgumentBlockPredicate();

	ArgumentType<?> _ArgumentBlockState();

	ArgumentType<?> _ArgumentChat();

	ArgumentType<?> _ArgumentChatComponent();

	ArgumentType<?> _ArgumentChatFormat();

	ArgumentType<?> _ArgumentDimension();

	ArgumentType<?> _ArgumentEnchantment();

	ArgumentType<?> _ArgumentEntity(EntitySelector selector);

	ArgumentType<?> _ArgumentEntitySummon();

	ArgumentType<?> _ArgumentFloatRange();

	ArgumentType<?> _ArgumentIntRange();

	ArgumentType<?> _ArgumentItemPredicate();

	ArgumentType<?> _ArgumentItemStack();

	ArgumentType<?> _ArgumentMathOperation();

	ArgumentType<?> _ArgumentMinecraftKeyRegistered();

	ArgumentType<?> _ArgumentMobEffect();

	ArgumentType<?> _ArgumentNBTCompound();

	ArgumentType<?> _ArgumentParticle();

	ArgumentType<?> _ArgumentPosition();

	ArgumentType<?> _ArgumentPosition2D();

	ArgumentType<?> _ArgumentProfile();

	ArgumentType<?> _ArgumentRotation();

	ArgumentType<?> _ArgumentScoreboardCriteria();

	ArgumentType<?> _ArgumentScoreboardObjective();

	ArgumentType<?> _ArgumentScoreboardSlot();

	ArgumentType<?> _ArgumentScoreboardTeam();

	ArgumentType<?> _ArgumentScoreholder(boolean single);

	ArgumentType<?> _ArgumentTag();

	ArgumentType<?> _ArgumentTime();

	ArgumentType<?> _ArgumentUUID();

	ArgumentType<?> _ArgumentVec2();

	ArgumentType<?> _ArgumentVec3();
	
	/*
	 * Synthetic arguments - arguments that don't actually exist, but have
	 * version-specific implementations, so we can switch their implementation
	 * as needed. For example, the BiomeArgument is both a _ArgumentMinecraftKeyRegistered
	 * and a _ArgumentResourceOrTag, but we'll refer to it as an _ArgumentSyntheticBiome
	 */
	
	ArgumentType<?> _ArgumentSyntheticBiome();

	/**
	 * A String array of Minecraft versions that this NMS implementation is
	 * compatible with. For example, ["1.14", "1.14.1", "1.14.2", "1.14.3"]. This
	 * can be found by opening a Minecraft jar file, viewing the version.json file
	 * and reading the object "name".
	 * 
	 * @return A String array of compatible Minecraft versions
	 */
	String[] compatibleVersions();
	
	/**
	 * Returns a CommandSender of a given CommandListenerWrapper object
	 * 
	 * @param clw The CommandListenerWrapper object
	 * @return A CommandSender (not proxied) from the command listener wrapper
	 */
	<T> T getImplementedSenderFromCSS(CommandListenerWrapper clw);
}
