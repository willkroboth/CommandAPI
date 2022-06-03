package dev.jorel.commandapi;

import dev.jorel.commandapi.arguments.ArgumentBase;
import dev.jorel.commandapi.executors.IExecutorNormal;

/**
 * This class represents something that is executable. This is mostly, {@link CommandAPICommandBase} instances, or can also be {@link CommandTreeBase} nodes and even {@link ArgumentBase} nodes in a tree
 *
 * @param <T> return type for chain calls
 */
abstract class Executable<T extends Executable<T, ImplementedSender>, ImplementedSender> {

	protected CustomCommandExecutor<ImplementedSender> executor = new CustomCommandExecutor<>();

	/**
	 * Returns the executors that this command has
	 * @return the executors that this command has
	 */
	public CustomCommandExecutor<ImplementedSender> getExecutor() {
		return executor;
	}

	/**
	 * Sets the executors for this command
	 * @param executor the executors for this command
	 */
	public void setExecutor(CustomCommandExecutor<ImplementedSender> executor) {
		this.executor = executor;
	}

	/**
	 * Adds an executor to the current command builder
	 * @param executor A lambda of type <code>(Player, Object[]) -&gt; ()</code> that will be executed when the command is run
	 * @return this command builder
	 */
	@SuppressWarnings("unchecked")
	public abstract <K extends IExecutorNormal<L>, L extends ImplementedSender> T executesPlayer(K executor);

}
