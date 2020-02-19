package org.uppaal.cli.handlers;

import org.uppaal.cli.exceptions.UnknownModeException;
import org.uppaal.cli.CommandResult;
import org.uppaal.cli.Command;
import org.uppaal.cli.Context;

/**
* concrete class implementing a command handler
* implementing the design pattern facade
* to hide the process of command execution to client classes
*/

public class CommandHandler extends AbstractHandler {

// static array of accepted commands
private static Command.CommandCode[] default_commands = 
{Command.CommandCode.START,
Command.CommandCode.EXIT,
Command.CommandCode.EXPORT,
Command.CommandCode.HELP
};

// current active handler of this command handler
private Handler active_handler;

// editor handler of this command handler
private EditorHandler editor_handler;

// private simulator handler of this handler
private SimulatorHandler simulator_handler;

// private VerifierHandler of this handler
private VerifierHandler verifier_handler;

// private unknown mode exception to throw when receiving an unknown mode
private UnknownModeException unknown_mode_exception;

/**
* public constructor of a command handler
* initializing it from a given context
* @param context the uppaal context for this handler
*/

public CommandHandler (Context context) {
	super(context);
	this.accepted_commands = default_commands;
	this.editor_handler = new EditorHandler(context);
	this.simulator_handler = new SimulatorHandler(context);
	this.verifier_handler = new VerifierHandler(context);
	this.active_handler = this.editor_handler;
	this.unknown_mode_exception = new UnknownModeException();
}

@Override
public CommandResult handle (Command command) {

// if the command is accepted by this handler simply execute it

	switch (command.getCommandCode()) {

// if start command was entered start the corresponding mode and return the corresponding code

		case START:
		HandlerCode mode = command.getMode();
		switch (mode) {

// start editor handler if requested

			case EDITOR:
			this.active_handler = this.editor_handler;
			break;

// start simulator handler if requested

			case SIMULATOR:
			this.active_handler = this.simulator_handler;
			break;

// start verifier handler if requested

			case VERIFIER:
			this.active_handler = this.verifier_handler;
			break;

// if the mode is not known throw an unknown mode exception

			case UNKNOWN:
				String unknown_mode = command.getArgumentAt(0);
				this.unknown_mode_exception.setMode(unknown_mode);
				throw this.unknown_mode_exception;
		}

		this.command_result.setResultCode(CommandResult.ResultCode.MODE_CHANGED);
		return this.command_result;

// if exit command was entered simply set the code of the command result and return it

		case EXIT:
		this.command_result.setResultCode(CommandResult.ResultCode.EXIT);
		return this.command_result;

// otherwise if it is accepted by the current active handler execute it

		default:
		if (this.active_handler.acceptCommand(command))
			return this.active_handler.handle(command);
		break;
	}

// otherwise throw a wrong mode exception

	return this.command_result;
}

/**
* @return the list of active command codes
*/
public Command.CommandCode[] getActiveCommands() {
	Command.CommandCode[] handler_commands = this.active_handler.getAcceptedCommands();
	int n = handler_commands.length+this.accepted_commands.length;
	Command.CommandCode[] active_commands = new Command.CommandCode [n];

	for (int i = 0 ; i <handler_commands.length;i++)
		active_commands[i] = handler_commands[i];

	for (int i = 0; i < this.accepted_commands.length;i++)
		active_commands[handler_commands.length + i] = this.accepted_commands[i];

	return active_commands;
}

@Override
public Command.ObjectCode[] getAcceptedObjects() {
	return this.active_handler.getAcceptedObjects();
}

@Override
public Handler.HandlerCode getMode() {
	return this.active_handler.getMode();
}
}