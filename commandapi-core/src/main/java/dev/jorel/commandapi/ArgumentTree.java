package dev.jorel.commandapi;

import dev.jorel.commandapi.arguments.Argument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is a base class for arguments, allowing them to behave as tree nodes in a {@link CommandTreeBase}
 */
public class ArgumentTree<ImplementedSender> extends Executable<ArgumentTree<ImplementedSender>, ImplementedSender> {

	final List<ArgumentTree<ImplementedSender>> arguments = new ArrayList<>();
	final Argument<?, ImplementedSender> argument;

	protected ArgumentTree() {
		if(!(this instanceof Argument<?, ?> argument)) {
			throw new IllegalArgumentException("Implicit inherited constructor must be from Argument");
		}
		this.argument = (Argument<?, ImplementedSender>) argument;
	}

	public ArgumentTree(final Argument<?, ImplementedSender> argument) {
		this.argument = argument;
		//Copy the executor in case any executions were defined on the argument
		this.executor = argument.executor;
	}

	/**
	 * Create a child branch on this node
	 * @param tree The child branch
	 * @return this tree node
	 */
	public ArgumentTree<ImplementedSender> then(final ArgumentTree<ImplementedSender> tree) {
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
		for(ArgumentTree<ImplementedSender> tree: arguments) {
			for(Execution<ImplementedSender> execution : tree.getExecutions()) {
				//Prepend this argument to the arguments of the executions
				executions.add(execution.prependedBy(this.argument));
			}
		}
		return executions;
	}

}
