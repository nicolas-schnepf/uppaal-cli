package org.uppaal.cli.handlers;

import org.uppaal.cli.exceptions.UnknownModeException;
import org.uppaal.cli.commands.Command.OperationCode;
import org.uppaal.cli.commands.Command.ObjectCode;
import org.uppaal.cli.commands.CommandResult;
import org.uppaal.cli.commands.Command;
import org.uppaal.cli.commands.Context;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;

/**
* concrete class implementing a command handler
* implementing the design pattern facade
* to hide the process of command execution to client classes
*/

public class CommandHandler extends AbstractHandler {

// static array of accepted commands
private static Command.OperationCode[] default_commands = 
{Command.OperationCode.START,
Command.OperationCode.EXIT,
Command.OperationCode.EXPORT,
Command.OperationCode.HELP
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
	this.editor_handler = new EditorHandler(context);
	this.simulator_handler = new SimulatorHandler(context);
	this.verifier_handler = new VerifierHandler(context);
	this.active_handler = this.editor_handler;
	this.unknown_mode_exception = new UnknownModeException();
}

@Override
public HashSet<OperationCode> getAcceptedOperations () {
	HashSet<OperationCode> accepted_operations = new HashSet<OperationCode>(this.active_handler.getAcceptedOperations());
	accepted_operations.add(OperationCode.EXIT);
	accepted_operations.add(OperationCode.START);
	return accepted_operations;
}

@Override
public boolean acceptCommand (Command command) {
	OperationCode operation_code = command.getOperationCode();
	return (operation_code==OperationCode.EXIT) || (operation_code==OperationCode.START) || this.active_handler.acceptCommand(command);
}

@Override
public CommandResult handle (Command command) throws MalformedURLException, IOException {

// if the command is accepted by this handler simply execute it

	switch (command.getOperationCode()) {

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
		CommandResult command_result = new CommandResult();
		command_result.setResultCode(CommandResult.ResultCode.OK);
		return command_result;

// if exit command was entered simply set the code of the command result and return it

		case EXIT:
		 command_result = new CommandResult();
		command_result.setResultCode(CommandResult.ResultCode.EXIT);
		return command_result;

// otherwise if it is accepted by the current active handler execute it

		default:
			return this.active_handler.handle(command);
	}


}

/**
* @return the list of active command codes
*/
public HashSet<OperationCode> getActiveCommands() {
	HashSet<OperationCode> accepted_operations = new HashSet<OperationCode>();
	accepted_operations.add(OperationCode.EXIT);
	accepted_operations.add(OperationCode.START);
	return accepted_operations;
}

@Override
public HashSet<ObjectCode> getAcceptedObjects() {
	return this.active_handler.getAcceptedObjects();
}

@Override
public Handler.HandlerCode getMode() {
	return this.active_handler.getMode();
}
}
