package dev.jorel.commandapi;

import dev.jorel.commandapi.arguments.ArgumentBase;

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
}
