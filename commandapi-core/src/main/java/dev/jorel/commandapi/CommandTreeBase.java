package dev.jorel.commandapi;

import java.util.ArrayList;
import java.util.List;

import dev.jorel.commandapi.arguments.ArgumentBase;

/**
 * This is the root node for creating a command as a tree
 */
public abstract class CommandTreeBase<ImplementedSender, ArgumentImpl extends ArgumentBase<?, ImplementedSender, ArgumentImpl>>
		extends ExecutableCommand<CommandTreeBase<ImplementedSender, ArgumentImpl>, ImplementedSender> {

	private final List<ArgumentTree<ImplementedSender, ArgumentImpl>> arguments = new ArrayList<>();

	public CommandTreeBase(final String commandName) {
		super(commandName);
	}

	/**
	 * Create a child branch on the tree
	 * @param tree the child node
	 * @return this root node
	 */
	public CommandTreeBase<ImplementedSender, ArgumentImpl> then(final ArgumentTree<ImplementedSender, ArgumentImpl> tree) {
		this.arguments.add(tree);
		return this;
	}

	/**
	 * Registers the command
	 */
	public void register() {
		List<Execution<ImplementedSender, ArgumentImpl>> executions = new ArrayList<>();
		if(this.executor.hasAnyExecutors()) {
			executions.add(new Execution<ImplementedSender, ArgumentImpl>(new ArrayList<>(), this.executor));
		}
		for(ArgumentTree<ImplementedSender, ArgumentImpl> tree : arguments) {
			executions.addAll(tree.getExecutions());
		}
		for(Execution<ImplementedSender, ArgumentImpl> execution : executions) {
			execution.register(this.meta);
		}
	}

}
