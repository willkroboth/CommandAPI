package dev.jorel.commandapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import dev.jorel.commandapi.arguments.ArgumentBase;

/**
 * This is a base class for arguments, allowing them to behave as tree nodes in a {@link CommandTreeBase}
 */
public class BukkitArgumentTree<ArgumentImpl extends ArgumentBase<?, CommandSender, ArgumentImpl>> extends BukkitExecutable implements ArgumentTree<CommandSender, ArgumentImpl> {

	final List<BukkitArgumentTree<ArgumentImpl>> arguments = new ArrayList<>();
	final ArgumentBase<?, CommandSender, ArgumentImpl> argument;

	protected BukkitArgumentTree() {
		if(!(this instanceof ArgumentBase<?, ?, ?> argument)) {
			throw new IllegalArgumentException("Implicit inherited constructor must be from Argument");
		}
		this.argument = (ArgumentBase<?, CommandSender, ArgumentImpl>) this;
	}

	public BukkitArgumentTree(final ArgumentBase<?, CommandSender, ArgumentImpl> argument) {
		this.argument = argument;
		//Copy the executor in case any executions were defined on the argument
		this.executor = argument.executor;
	}

	/**
	 * Create a child branch on this node
	 * @param tree The child branch
	 * @return this tree node
	 */
	public BukkitArgumentTree<ArgumentImpl> then(final BukkitArgumentTree<ArgumentImpl> tree) {
		this.arguments.add(tree);
		return this;
	}

	@Override
	public List<Execution<CommandSender, ArgumentImpl>> getExecutions() {
		List<Execution<CommandSender, ArgumentImpl>> executions = new ArrayList<>();
		//If this is executable, add its execution
		if(this.executor.hasAnyExecutors()) {
			executions.add(new Execution<CommandSender, ArgumentImpl>(Arrays.asList(this.argument), this.executor));
		}
		//Add all executions from all arguments
		for(BukkitArgumentTree<ArgumentImpl> tree: arguments) {
			for(Execution<CommandSender, ArgumentImpl> execution : tree.getExecutions()) {
				//Prepend this argument to the arguments of the executions
				executions.add(execution.prependedBy(this.argument));
			}
		}
		return executions;
	}

	@Override
	public BukkitArgumentTree<ArgumentImpl> then(ArgumentTree<CommandSender, ArgumentImpl> tree) {
		this.arguments.add((BukkitArgumentTree<ArgumentImpl>) tree);
		return this;
	}

}
