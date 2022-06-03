package dev.jorel.commandapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.jorel.commandapi.arguments.Argument;

/**
 * This is a base class for arguments, allowing them to behave as tree nodes in a {@link CommandTreeBase}
 */
public class ArgumentTree<ImplementedSender, ImplementedArgument extends Argument<?, ImplementedSender, ImplementedArgument>> extends Executable<ArgumentTree<ImplementedSender, ImplementedArgument>, ImplementedSender> {

	final List<ArgumentTree<ImplementedSender, ImplementedArgument>> arguments = new ArrayList<>();
	final Argument<?, ImplementedSender, ImplementedArgument> argument;

	protected ArgumentTree() {
		if(!(this instanceof Argument<?, ?, ?> argument)) {
			throw new IllegalArgumentException("Implicit inherited constructor must be from Argument");
		}
		this.argument = (Argument<?, ImplementedSender, ImplementedArgument>) argument;
	}

	public ArgumentTree(final Argument<?, ImplementedSender, ImplementedArgument> argument) {
		this.argument = argument;
		//Copy the executor in case any executions were defined on the argument
		this.executor = argument.executor;
	}

	/**
	 * Create a child branch on this node
	 * @param tree The child branch
	 * @return this tree node
	 */
	public ArgumentTree<ImplementedSender, ImplementedArgument> then(final ArgumentTree<ImplementedSender, ImplementedArgument> tree) {
		this.arguments.add(tree);
		return this;
	}

	List<Execution<ImplementedSender>> getExecutions() {
		List<Execution<ImplementedSender>> executions = new ArrayList<>();
		//If this is executable, add its execution
		if(this.executor.hasAnyExecutors()) {
			executions.add(new Execution<ImplementedSender>(Arrays.asList(this.argument), this.executor));
		}
		//Add all executions from all arguments
		for(ArgumentTree<ImplementedSender, ImplementedArgument> tree: arguments) {
			for(Execution<ImplementedSender> execution : tree.getExecutions()) {
				//Prepend this argument to the arguments of the executions
				executions.add(execution.prependedBy(this.argument));
			}
		}
		return executions;
	}

}
