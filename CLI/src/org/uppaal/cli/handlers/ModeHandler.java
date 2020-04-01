package org.uppaal.cli.handlers;

/**
* abstract class implementing a mode handler
* with all protected methods and attributes
*/

import org.uppaal.cli.exceptions.WrongModeException;
import org.uppaal.cli.commands.Command.OperationCode;
import org.uppaal.cli.commands.Command.ObjectCode;
import org.uppaal.cli.commands.CommandResult;
import org.uppaal.cli.commands.Command;
import org.uppaal.cli.commands.Context;
import java.net.MalformedURLException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public abstract class ModeHandler extends AbstractHandler {

// hash map of accepted operations

protected HashMap<OperationCode, OperationHandler> operation_map;

// wrong mode exception to be thrown when the current mode does not support a command
protected WrongModeException wrong_mode_exception;


/**
* constructor of a mode handler
* @param context the context of this handler
*/

public ModeHandler (Context context) {
	super(context);
	this.operation_map = new HashMap<OperationCode, OperationHandler>();
	this.wrong_mode_exception = new WrongModeException();
}

@Override
public CommandResult handle (Command command) throws MalformedURLException, IOException {
	if (this.acceptCommand(command))
		return this.operation_map.get(command.getOperationCode()).handle(command);
	else
		this.throwWrongModeException(command.getOperationCode(), this.getMode());
	return null;
}

@Override
public HashSet<OperationCode> getAcceptedOperations() {
	return (HashSet<OperationCode>) this.operation_map.keySet();
}

@Override
public HashSet<ObjectCode> getAcceptedObjects() {
	HashSet<ObjectCode> object_codes = new HashSet<ObjectCode>();
	for (OperationCode operation_code:this.operation_map.keySet()) 
		object_codes.addAll(this.operation_map.get(operation_code).getAcceptedObjects());
	return object_codes;
}

@Override
public boolean acceptCommand (Command command) {
	return this.operation_map.keySet().contains(command.getOperationCode());
}

/**
* throws a wrong mode exception if the provided command operation is not supported by this handler
* @param operation_code the unsupported operation code
* @param mode the code of the handler for the current mode
* @exception an exception with all intended information
*/
protected void throwWrongModeException(OperationCode operation_code, HandlerCode mode) {
	this.wrong_mode_exception.setOperationCode(operation_code);
	this.wrong_mode_exception.setHandlerCode(mode);
	throw (this.wrong_mode_exception);
}
}
