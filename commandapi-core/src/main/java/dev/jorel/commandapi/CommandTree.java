package dev.jorel.commandapi;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the root node for creating a command as a tree
 */
public class CommandTree<ImplementedSender> extends ExecutableCommand<CommandTree<ImplementedSender>, ImplementedSender> {

	private final List<ArgumentTree<ImplementedSender>> arguments = new ArrayList<>();

	public CommandTree(final String commandName) {
		super(commandName);
	}

	/**
	 * Create a child branch on the tree
	 * @param tree the child node
	 * @return this root node
	 */
	public CommandTree<ImplementedSender> then(final ArgumentTree<ImplementedSender> tree) {
		this.arguments.add(tree);
		return this;
	}

	/**
	 * Registers the command
	 */
	public void register() {
		List<Execution<ImplementedSender>> executions = new ArrayList<>();
		if(this.executor.hasAnyExecutors()) {
			executions.add(new Execution<ImplementedSender>(new ArrayList<>(), this.executor));
		}
		for(ArgumentTree<ImplementedSender> tree : arguments) {
			executions.addAll(tree.getExecutions());
		}
		for(Execution<ImplementedSender> execution : executions) {
			execution.register(this.meta);
		}
	}

}
