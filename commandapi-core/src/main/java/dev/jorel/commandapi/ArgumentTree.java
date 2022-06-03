package dev.jorel.commandapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.jorel.commandapi.arguments.ArgumentBase;

/**
 * This is a base class for arguments, allowing them to behave as tree nodes in a {@link CommandTreeBase}
 */
public class ArgumentTree<ImplementedSender, ArgumentImpl extends ArgumentBase<?, ImplementedSender, ArgumentImpl>> extends Executable<ArgumentTree<ImplementedSender, ArgumentImpl>, ImplementedSender> {

	final List<ArgumentTree<ImplementedSender, ArgumentImpl>> arguments = new ArrayList<>();
	final ArgumentBase<?, ImplementedSender, ArgumentImpl> argument;

	protected ArgumentTree() {
		if(!(this instanceof ArgumentBase<?, ?, ?> argument)) {
			throw new IllegalArgumentException("Implicit inherited constructor must be from Argument");
		}
		this.argument = (ArgumentBase<?, ImplementedSender, ArgumentImpl>) argument;
	}

	public ArgumentTree(final ArgumentBase<?, ImplementedSender, ArgumentImpl> argument) {
		this.argument = argument;
		//Copy the executor in case any executions were defined on the argument
		this.executor = argument.executor;
	}

	/**
	 * Create a child branch on this node
	 * @param tree The child branch
	 * @return this tree node
	 */
	public ArgumentTree<ImplementedSender, ArgumentImpl> then(final ArgumentTree<ImplementedSender, ArgumentImpl> tree) {
		this.arguments.add(tree);
		return this;
	}

	List<Execution<ImplementedSender, ArgumentImpl>> getExecutions() {
		List<Execution<ImplementedSender, ArgumentImpl>> executions = new ArrayList<>();
		//If this is executable, add its execution
		if(this.executor.hasAnyExecutors()) {
			executions.add(new Execution<ImplementedSender, ArgumentImpl>(Arrays.asList(this.argument), this.executor));
		}
		//Add all executions from all arguments
		for(ArgumentTree<ImplementedSender, ArgumentImpl> tree: arguments) {
			for(Execution<ImplementedSender, ArgumentImpl> execution : tree.getExecutions()) {
				//Prepend this argument to the arguments of the executions
				executions.add(execution.prependedBy(this.argument));
			}
		}
		return executions;
	}

}
