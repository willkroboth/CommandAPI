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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import dev.jorel.commandapi.arguments.ArgumentBase;
import dev.jorel.commandapi.arguments.IGreedyArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgumentBase;
import dev.jorel.commandapi.exceptions.GreedyArgumentException;
import dev.jorel.commandapi.exceptions.InvalidCommandNameException;

/**
 * A builder used to create commands to be registered by the CommandAPI.
 */
public abstract class CommandAPICommandBase<T extends CommandAPICommandBase<T, ImplementedSender, ArgumentImpl>, ImplementedSender, ArgumentImpl extends ArgumentBase<?, ImplementedSender, ArgumentImpl>>
		extends
		ExecutableCommand<CommandAPICommandBase<T, ImplementedSender, ArgumentImpl>, ImplementedSender, ArgumentImpl> {

	List<ArgumentImpl> args = new ArrayList<>();
	List<CommandAPICommandBase<T, ImplementedSender, ArgumentImpl>> subcommands = new ArrayList<>();
	boolean isConverted;
	
	/**
	 * Creates a new command builder
	 * @param commandName The name of the command to create
	 */
	public CommandAPICommandBase(String commandName) {
		super(commandName);
		this.isConverted = false;
	}

	public CommandAPICommandBase(CommandMetaData<ImplementedSender> metaData) {
		super(metaData);
		this.isConverted = false;
	}
	
	/**
	 * Appends the arguments to the current command builder
	 * @param args A <code>List</code> that represents the arguments that this command can accept
	 * @return this command builder
	 */
	@SuppressWarnings("unchecked")
	public T withArguments(List<ArgumentImpl> args) {
		this.args.addAll(args);
		return (T) this;
	}
	
	/**
	 * Appends the argument(s) to the current command builder
	 * @param args Arguments that this command can accept
	 * @return this command builder
	 */
	@SuppressWarnings("unchecked")
	@SafeVarargs
	public final T withArguments(ArgumentImpl... args) {
		this.args.addAll(Arrays.asList(args));
		return (T) this;
	}
	
	/**
	 * Adds a subcommand to this command builder
	 * @param subcommand the subcommand to add as a child of this command 
	 * @return this command builder
	 */
	@SuppressWarnings("unchecked")
	public T withSubcommand(T subcommand) {
		this.subcommands.add(subcommand);
		return (T) this;
	}

	/**
	 * Returns the list of arguments that this command has
	 * @return the list of arguments that this command has
	 */
	public List<ArgumentImpl> getArguments() {
		return args;
	}

	/**
	 * Sets the arguments that this command has
	 * @param args the arguments that this command has
	 */
	public void setArguments(List<ArgumentImpl> args) {
		this.args = args;
	}

	/**
	 * Returns the list of subcommands that this command has
	 * @return the list of subcommands that this command has
	 */
	public List<CommandAPICommandBase<T, ImplementedSender, ArgumentImpl>> getSubcommands() {
		return subcommands;
	}

	/**
	 * Sets the list of subcommands that this command has
	 * @param subcommands the list of subcommands that this command has
	 */
	public void setSubcommands(List<CommandAPICommandBase<T, ImplementedSender, ArgumentImpl>> subcommands) {
		this.subcommands = subcommands;
	}

	/**
	 * Returns whether this command is an automatically converted command
	 * @return whether this command is an automatically converted command
	 */
	public boolean isConverted() {
		return isConverted;
	}

	/**
	 * Sets a command as "converted". This tells the CommandAPI that this command
	 * was converted by the CommandAPI's Converter. This should not be used outside
	 * of the CommandAPI's internal API
	 * @param isConverted whether this command is converted or not
	 * @return this command builder
	 */
	@SuppressWarnings("unchecked")
	T setConverted(boolean isConverted) {
		this.isConverted = isConverted;
		return (T) this;
	}
	
	//Expand subcommands into arguments
	private void flatten(CommandAPICommandBase<T, ImplementedSender, ArgumentImpl> rootCommand, List<ArgumentImpl> prevArguments, CommandAPICommandBase<T, ImplementedSender, ArgumentImpl> subcommand) {
		
		String[] literals = new String[subcommand.meta.aliases.length + 1];
		literals[0] = subcommand.meta.commandName;
		System.arraycopy(subcommand.meta.aliases, 0, literals, 1, subcommand.meta.aliases.length);
		MultiLiteralArgumentBase<ImplementedSender> literal = new MultiLiteralArgumentBase.MultiLiteralArgumentBaseImpl<ImplementedSender>(literals)
			.withPermission(subcommand.meta.permission)
			.withRequirement(subcommand.meta.requirements)
			.setListed(false);
		
		prevArguments.add((ArgumentImpl) literal);
		
		if(subcommand.executor.hasAnyExecutors()) {	
			rootCommand.args = prevArguments;
			rootCommand.withArguments(subcommand.args);
			rootCommand.executor = subcommand.executor;
			
			rootCommand.subcommands = new ArrayList<>();
			rootCommand.register();
		}
		
		for(CommandAPICommandBase<T, ImplementedSender, ArgumentImpl> subsubcommand : new ArrayList<>(subcommand.subcommands)) {
			flatten(rootCommand, new ArrayList<>(prevArguments), subsubcommand);
		}
	}
	
	/**
	 * Registers the command
	 */
	public void register() {
		if(!CommandAPIBase.canRegister()) {
			CommandAPIBase.logWarning("Command /" + meta.commandName + " is being registered after the server had loaded. Undefined behavior ahead!");
		}
		try {
			ArgumentBase<?, ImplementedSender, ArgumentImpl>[] argumentsArr = args == null ? new ArgumentBase[0] : args.toArray(new ArgumentBase[0]);
			
			// Check IGreedyArgument constraints 
			for(int i = 0, numGreedyArgs = 0; i < argumentsArr.length; i++) {
				if(argumentsArr[i] instanceof IGreedyArgument) {
					if(++numGreedyArgs > 1 || i != argumentsArr.length - 1) {
						throw new GreedyArgumentException(argumentsArr);
					}
				}
			}
			
			//Assign the command's permissions to arguments if the arguments don't already have one
			for(ArgumentBase<?, ImplementedSender, ?> argument : argumentsArr) {
				if(argument.getArgumentPermission() == null) {
					argument.withPermission(meta.permission);
				}
			}
			
			if(executor.hasAnyExecutors()) {
				register(meta, argumentsArr, executor, isConverted);
			}
			
			for(CommandAPICommandBase<T, ImplementedSender, ArgumentImpl> subcommand : this.subcommands) {
				flatten(this, new ArrayList<>(), subcommand);
			}
		} catch (InvalidCommandNameException | GreedyArgumentException | CommandSyntaxException | IOException e) {
			e.printStackTrace();
		}
		
	}

	abstract void register(CommandMetaData<ImplementedSender> meta, ArgumentBase<?, ImplementedSender, ArgumentImpl>[] argumentsArr,
			CustomCommandExecutor<ImplementedSender> executor, boolean isConverted)
			throws CommandSyntaxException, IOException;
	
}
