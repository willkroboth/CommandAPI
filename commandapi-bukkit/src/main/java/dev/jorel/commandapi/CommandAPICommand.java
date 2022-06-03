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

import java.io.IOException;

import org.bukkit.command.CommandSender;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.jorel.commandapi.arguments.ArgumentBase;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandBlockCommandExecutor;
import dev.jorel.commandapi.executors.CommandBlockResultingCommandExecutor;
import dev.jorel.commandapi.executors.CommandExecutor;
import dev.jorel.commandapi.executors.ConsoleCommandExecutor;
import dev.jorel.commandapi.executors.ConsoleResultingCommandExecutor;
import dev.jorel.commandapi.executors.EntityCommandExecutor;
import dev.jorel.commandapi.executors.EntityResultingCommandExecutor;
import dev.jorel.commandapi.executors.ExecutorType;
import dev.jorel.commandapi.executors.NativeCommandExecutor;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import dev.jorel.commandapi.executors.PlayerResultingCommandExecutor;
import dev.jorel.commandapi.executors.ProxyCommandExecutor;
import dev.jorel.commandapi.executors.ProxyResultingCommandExecutor;
import dev.jorel.commandapi.executors.ResultingCommandExecutor;

/**
 * A builder used to create commands to be registered by the CommandAPI.
 */
public class CommandAPICommand extends CommandAPICommandBase<CommandAPICommand, CommandSender, Argument<?>> {
	
	/**
	 * Creates a new command builder
	 * @param commandName The name of the command to create
	 */
	public CommandAPICommand(String commandName) {
		super(commandName);
		super.isConverted = false;
	}

	public CommandAPICommand(CommandMetaData<CommandSender> metaData) {
		super(metaData);
		super.isConverted = false;
	}

	@Override
	void register(CommandMetaData<CommandSender> meta, ArgumentBase<?, CommandSender, Argument<?>>[] argumentsArr,
			CustomCommandExecutor<CommandSender> executor, boolean isConverted)
			throws CommandSyntaxException, IOException {
		BukkitCommandAPIHandler.getInstance().register(meta, argumentsArr, executor, isConverted);
	}

	// Regular command executor

	/**
	 * Adds an executor to the current command builder
	 * @param executor A lambda of type <code>(CommandSender, Object[]) -&gt; ()</code> that will be executed when the command is run
	 * @return this command builder
	 */
	@SuppressWarnings("unchecked")
	public CommandAPICommand executes(CommandExecutor executor, ExecutorType... types) {
		if(types == null || types.length == 0) {
			this.executor.addNormalExecutor(executor);
		} else {
			for(ExecutorType type : types) {
				this.executor.addNormalExecutor(new CommandExecutor() {

					@Override
					public void run(CommandSender sender, Object[] args) throws WrapperCommandSyntaxException {
						executor.executeWith(sender, args);
					}
					
					@Override
					public ExecutorType getType() {
						return type;
					}
				});
			}
		}
		return this;
	}

	/**
	 * Adds an executor to the current command builder
	 * @param executor A lambda of type <code>(CommandSender, Object[]) -&gt; int</code> that will be executed when the command is run
	 * @return this command builder
	 */
	@SuppressWarnings("unchecked")
	public CommandAPICommand executes(ResultingCommandExecutor executor, ExecutorType... types) {
		if(types == null || types.length == 0) {
			this.executor.addResultingExecutor(executor);
		} else {
			for(ExecutorType type : types) {
				this.executor.addResultingExecutor(new ResultingCommandExecutor() {

					@Override
					public int run(CommandSender sender, Object[] args) throws WrapperCommandSyntaxException {
						return executor.executeWith(sender, args);
					}
					
					@Override
					public ExecutorType getType() {
						return type;
					}
				});
			}
		}
		return this;
	}

	// Player command executor

	/**
	 * Adds an executor to the current command builder
	 * @param executor A lambda of type <code>(Player, Object[]) -&gt; ()</code> that will be executed when the command is run
	 * @return this command builder
	 */
	@SuppressWarnings("unchecked")
	public CommandAPICommand executesPlayer(PlayerCommandExecutor executor) {
		this.executor.addNormalExecutor(executor);
		return this;
	}

	/**
	 * Adds an executor to the current command builder
	 * @param executor A lambda of type <code>(Player, Object[]) -&gt; int</code> that will be executed when the command is run
	 * @return this command builder
	 */
	@SuppressWarnings("unchecked")
	public CommandAPICommand executesPlayer(PlayerResultingCommandExecutor executor) {
		this.executor.addResultingExecutor(executor);
		return this;
	}

	// Entity command executor

	/**
	 * Adds an executor to the current command builder
	 * @param executor A lambda of type <code>(Entity, Object[]) -&gt; ()</code> that will be executed when the command is run
	 * @return this command builder
	 */
	@SuppressWarnings("unchecked")
	public CommandAPICommand executesEntity(EntityCommandExecutor executor) {
		this.executor.addNormalExecutor(executor);
		return this;
	}

	/**
	 * Adds an executor to the current command builder
	 * @param executor A lambda of type <code>(Entity, Object[]) -&gt; int</code> that will be executed when the command is run
	 * @return this command builder
	 */
	@SuppressWarnings("unchecked")
	public CommandAPICommand executesEntity(EntityResultingCommandExecutor executor) {
		this.executor.addResultingExecutor(executor);
		return this;
	}

	// Proxy command executor

	/**
	 * Adds an executor to the current command builder
	 * @param executor A lambda of type <code>(Entity, Object[]) -&gt; ()</code> that will be executed when the command is run
	 * @return this command builder
	 */
	@SuppressWarnings("unchecked")
	public CommandAPICommand executesProxy(ProxyCommandExecutor executor) {
		this.executor.addNormalExecutor(executor);
		return this;
	}

	/**
	 * Adds an executor to the current command builder
	 * @param executor A lambda of type <code>(Entity, Object[]) -&gt; int</code> that will be executed when the command is run
	 * @return this command builder
	 */
	@SuppressWarnings("unchecked")
	public CommandAPICommand executesProxy(ProxyResultingCommandExecutor executor) {
		this.executor.addResultingExecutor(executor);
		return this;
	}

	// Command block command sender

	/**
	 * Adds an executor to the current command builder
	 * @param executor A lambda of type <code>(BlockCommandSender, Object[]) -&gt; ()</code> that will be executed when the command is run
	 * @return this command builder
	 */
	@SuppressWarnings("unchecked")
	public CommandAPICommand executesCommandBlock(CommandBlockCommandExecutor executor) {
		this.executor.addNormalExecutor(executor);
		return this;
	}

	/**
	 * Adds an executor to the current command builder
	 * @param executor A lambda of type <code>(BlockCommandSender, Object[]) -&gt; int</code> that will be executed when the command is run
	 * @return this command builder
	 */
	@SuppressWarnings("unchecked")
	public CommandAPICommand executesCommandBlock(CommandBlockResultingCommandExecutor executor) {
		this.executor.addResultingExecutor(executor);
		return this;
	}

	// Console command sender

	/**
	 * Adds an executor to the current command builder
	 * @param executor A lambda of type <code>(BlockCommandSender, Object[]) -&gt; ()</code> that will be executed when the command is run
	 * @return this command builder
	 */
	@SuppressWarnings("unchecked")
	public CommandAPICommand executesConsole(ConsoleCommandExecutor executor) {
		this.executor.addNormalExecutor(executor);
		return this;
	}

	/**
	 * Adds an executor to the current command builder
	 * @param executor A lambda of type <code>(BlockCommandSender, Object[]) -&gt; int</code> that will be executed when the command is run
	 * @return this command builder
	 */
	@SuppressWarnings("unchecked")
	public CommandAPICommand executesConsole(ConsoleResultingCommandExecutor executor) {
		this.executor.addResultingExecutor(executor);
		return this;
	}

	/**
	 * Adds an executor to the current command builder
	 * @param executor A lambda of type <code>(NativeCommandExecutor, Object[]) -&gt; ()</code> that will be executed when the command is run
	 * @return this command builder
	 */
	@SuppressWarnings("unchecked")
	public CommandAPICommand executesNative(NativeCommandExecutor executor) {
		this.executor.addNormalExecutor(executor);
		return this;
	}

	/**
	 * Adds an executor to the current command builder
	 * @param executor A lambda of type <code>(NativeCommandExecutor, Object[]) -&gt; int</code> that will be executed when the command is run
	 * @return this command builder
	 */
	@SuppressWarnings("unchecked")
	public CommandAPICommand executesNative(NativeResultingCommandExecutor executor) {
		this.executor.addResultingExecutor(executor);
		return this;
	}
}
