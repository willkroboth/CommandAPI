package dev.jorel.commandapi;

import java.util.ArrayList;
import java.util.List;

import dev.jorel.commandapi.arguments.Argument;

/**
 * A list of arguments which results in an execution. This is used for building branches in a {@link CommandTree}
 */
record Execution<ImplementedSender>(List<Argument<?, ImplementedSender>> arguments, CustomCommandExecutor<ImplementedSender> executor) {

	/**
	 * Register a command with the given arguments and executor to brigadier, by converting it into a {@link CommandAPICommandBase}
	 * @param meta The metadata to register the command with
	 */
	public void register(CommandMetaData<ImplementedSender> meta) {
		CommandAPICommandBase<ImplementedSender> command = new CommandAPICommandBase<ImplementedSender>(meta).withArguments(arguments);
		command.setExecutor(executor);
		command.register();
	}

	public Execution<ImplementedSender> prependedBy(Argument<?, ImplementedSender> argument) {
		List<Argument<?, ImplementedSender>> arguments = new ArrayList<>();
		arguments.add(argument);
		arguments.addAll(arguments());
		return new Execution<ImplementedSender>(arguments, executor);
	}

}
