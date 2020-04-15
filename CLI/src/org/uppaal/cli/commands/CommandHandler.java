package org.uppaal.cli.commands;

import com.uppaal.engine.EngineException;
import com.uppaal.engine.CannotEvaluateException;
import org.uppaal.cli.exceptions.UnknownModeException;
import org.uppaal.cli.enumerations.OperationCode;
import org.uppaal.cli.enumerations.ObjectCode;
import org.uppaal.cli.enumerations.ResultCode;
import org.uppaal.cli.enumerations.ModeCode;
import org.uppaal.cli.commands.CommandResult;
import org.uppaal.cli.commands.Command;
import org.uppaal.cli.context.Context;

import java.io.IOException;
import java.util.LinkedList;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;

/**
* concrete class implementing a command handler
* implementing the design pattern facade
* to hide the process of command execution to client classes
*/

public class CommandHandler extends AbstractHandler {

// static array of accepted commands

// command map of this command handler
private HashMap<OperationCode, Handler> operation_map;

// private unknown mode exception to throw when receiving an unknown mode
private UnknownModeException unknown_mode_exception;

/**
* public constructor of a command handler
* initializing it from a given context
* @param context the uppaal context for this handler
*/

public CommandHandler (Context context) {
	super(context, null);
	this.operation_map = new HashMap<OperationCode, Handler>();
	this.unknown_mode_exception = new UnknownModeException();
	this.operation_map.put(OperationCode.IMPORT, new ImportHandler(context));
	this.operation_map.put(OperationCode.EXPORT, new ExportHandler(context));
	this.operation_map.put(OperationCode.SHOW, new ShowHandler(context));
	this.operation_map.put(OperationCode.RESET, new ResetHandler(context));
	this.operation_map.put(OperationCode.SET, new SetHandler(context));
	this.operation_map.put(OperationCode.UNSET, new UnsetHandler(context));
}

@Override
public HashSet<OperationCode> getAcceptedOperations () {
	HashSet<OperationCode> accepted_operations = new HashSet<OperationCode>();
	accepted_operations.add(OperationCode.EXIT);
	accepted_operations.add(OperationCode.START);
	accepted_operations.add(OperationCode.COMPILE);
	accepted_operations.add(OperationCode.CONNECT);
	accepted_operations.add(OperationCode.DISCONNECT);
	for(OperationCode operation_code:this.operation_map.keySet()) {
		if (this.operation_map.get(operation_code).acceptMode(this.context.getMode()))
			accepted_operations.add(operation_code);
	}
	return accepted_operations;
}

@Override
public CommandResult handle (Command command)  {

// if the command is accepted by this handler simply execute it

	switch (command.getOperationCode()) {

// if start command was entered start the corresponding mode and return the corresponding code

		case START:
		ModeCode mode = command.getMode();
		switch (mode) {

// start editor handler if requested

			case EDITOR:
			this.context.setMode(ModeCode.EDITOR);
			break;

// start simulator handler if requested

		case CONCRETE_SIMULATOR:
		case SYMBOLIC_SIMULATOR:

		this.checkArgumentNumber(command, 0);
		if (this.context.getMode()==ModeCode.EDITOR) {
		try {
			LinkedList<String> problems = this.context.getModelExpert().compileDocument();
			for (String problem: problems) this.command_result.addArgument(problem);
			if (this.context.getSystem()==null) {
				this.command_result.setResultCode(ResultCode.COMPILATION_ERROR);
				return this.command_result;
			}

			if (this.context.getTrace()==null) this.context.getTraceExpert().setTrace(mode);
			this.context.setMode(mode);
		} catch (EngineException e) {
		this.command_result.setResultCode(ResultCode.ENGINE_ERROR);
		return this.command_result;
		} catch (CannotEvaluateException e) {
		this.command_result.setResultCode(ResultCode.ENGINE_ERROR);
		return this.command_result;
		} catch (IOException e) {
			this.command_result.setResultCode(ResultCode.IO_ERROR);
			return this.command_result;
		}
		}

			break;

// if the verifier handler is requested first check that we have a valid system before changing mode

		case VERIFIER:

		this.checkArgumentNumber(command, 0);
		if (this.context.getMode()==ModeCode.EDITOR) {
		try {
			LinkedList<String> problems = this.context.getModelExpert().compileDocument();
			for (String problem: problems) this.command_result.addArgument(problem);
			if (this.context.getSystem()==null) {
				this.command_result.setResultCode(ResultCode.COMPILATION_ERROR);
				return this.command_result;
			}
		} catch (EngineException e) {
		this.command_result.setResultCode(ResultCode.ENGINE_ERROR);
		return this.command_result;
		} catch (IOException e) {
			this.command_result.setResultCode(ResultCode.IO_ERROR);
			return this.command_result;
		}
		}

		this.context.setMode(ModeCode.VERIFIER);
			break;

// if the mode is not known throw an unknown mode exception

		}
		this.command_result.setResultCode(ResultCode.MODE_CHANGED);
		return this.command_result;

// if exit command was entered simply set the code of the command result and return it

		case EXIT:
		this.command_result.setResultCode(ResultCode.EXIT);
		return this.command_result;

// for a compile command try to compile the current document

		case COMPILE:
		try {
			this.checkArgumentNumber(command, 0);
			LinkedList<String> problems = this.context.getModelExpert().compileDocument();
			for (String problem: problems) this.command_result.addArgument(problem);
			if (this.context.getSystem()!=null) this.command_result.setResultCode(ResultCode.OK);
			else this.command_result.setResultCode(ResultCode.COMPILATION_ERROR);
			return this.command_result;
		} catch (EngineException e) {
		this.command_result.setResultCode(ResultCode.ENGINE_ERROR);
		return this.command_result;
		} catch (IOException e) {
			this.command_result.setResultCode(ResultCode.IO_ERROR);
			return this.command_result;
		}

// for a connect command try to connect the engine of the context

		case CONNECT:
		try {
			this.checkArgumentNumber(command, 0);
			this.context.getEngineExpert().connectEngine();
			this.command_result.setResultCode(ResultCode.OK);
			return this.command_result;
		} catch (EngineException e) {
		this.command_result.setResultCode(ResultCode.ENGINE_ERROR);
		return this.command_result;
		} catch (IOException e) {
			this.command_result.setResultCode(ResultCode.IO_ERROR);
			return this.command_result;
		}

// for a disconnect command try to disconnect the engine of the context

		case DISCONNECT:
			this.checkArgumentNumber(command, 0);
			this.context.getEngineExpert().disconnectEngine();
			this.command_result.setResultCode(ResultCode.OK);
			return this.command_result;

// otherwise if it is accepted by the current active handler execute it

		default:
			return this.operation_map.get(command.getOperationCode()).handle(command);
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
public ModeCode getMode() {
	return this.context.getMode();
}

@Override
public boolean acceptMode (ModeCode mode) {
	return true;
}
}
