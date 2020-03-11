package org.uppaal.cli.handlers;

import com.uppaal.model.core2.QueryList;
import com.uppaal.model.core2.Query;
import org.uppaal.cli.CommandResult;
import org.uppaal.cli.Command;
import org.uppaal.cli.Context;

import java.net.MalformedURLException;
import java.io.IOException;

/**
* concrete class implementing a editor handler
* supporting all commands for the editor mode
*/

public class EditorHandler extends AbstractHandler {
// array of accepted command codes

private static Command.CommandCode[] editor_commands = {
Command.CommandCode.DECLARE,
Command.CommandCode.IMPORT,
Command.CommandCode.CLEAR,
Command.CommandCode.REPLACE,
Command.CommandCode.SHOW,
Command.CommandCode.REMOVE,
};

// array of accepted object codes

private static Command.ObjectCode[] editor_objects = {
Command.ObjectCode.DOCUMENT, 
Command.ObjectCode.QUERIES, 
Command.ObjectCode.TEMPLATES, 
Command.ObjectCode.QUERY, 
Command.ObjectCode.TEMPLATE, 
Command.ObjectCode.CHANNELS,
Command.ObjectCode.URGENT_CHANNELS,
Command.ObjectCode.TYPE,
Command.ObjectCode.VARIABLE,
Command.ObjectCode.VARIABLES,
Command.ObjectCode.CONSTANT,
Command.ObjectCode.CONSTANTS,
Command.ObjectCode.FUNCTION,
Command.ObjectCode.SYSTEM
};

/**
* public constructor of a editor handler
* initializing it from a reference to the uppaal context
* @param context the reference to the uppaal context for this handler
*/

public EditorHandler (Context context) {
	super(context);
	this.accepted_commands = editor_commands;
	this.accepted_objects = editor_objects;
}

@Override
public CommandResult handle (Command command) throws MalformedURLException, IOException {

// redirect the command to the appropriated method depending on its command code

	switch (command.getCommandCode()) {
		case IMPORT:
		return this.handleImport(command);

		case CLEAR:
		return this.handleClear(command);

		case SHOW:
		return this.handleShow(command);
	}

	return this.command_result;
}

@Override
public Handler.HandlerCode getMode () {
	return Handler.HandlerCode.EDITOR;
}

/**
* handle import commands
* @param command the command to handle
* @return the command result corresponding to this command
* @exception an exception describing the type of error which was encountered
*/
private CommandResult handleImport(Command command) throws MalformedURLException, IOException {

// check that the command contains exactly one argument

	Command.CommandCode command_code= command.getCommandCode();
	int argument_number = command.getArgumentNumber();

	if (argument_number<1)
		this.throwMissingArgumentException(command_code, 1, argument_number);
	else if (argument_number>1)
		this.throwExtraArgumentException (command_code, 1, argument_number);

// process the command depending on its object code

	Command.ObjectCode object_code = command.getObjectCode();
	String filename = command.getArgumentAt(0);
	int index = filename.length()-1;
	while (filename.charAt(index)!='.' && index>0) index --;
	String extension = filename.substring(index+1);

	switch (command.getObjectCode()) {
		case DOCUMENT:
		if (!extension.equals("xta") && !extension.equals("xml")) 
			this.throwWrongExtensionException (command_code, object_code, extension);
		this.context.loadDocument(filename);
		break;

		case QUERIES:
		if (!extension.equals("q")) 
			this.throwWrongExtensionException (command_code, object_code, extension);
		this.context.loadQueries(filename);
		break;
	}

	return this.command_result;
}

/**
* handle clear commands
* @param command the command to handle
* @return the command result corresponding to this command
*/
private CommandResult handleClear(Command command) {

// check that the command contains exactly one argument

	Command.CommandCode command_code= command.getCommandCode();
	Command.ObjectCode object_code = command.getObjectCode();
	int argument_number = command.getArgumentNumber();

 if (argument_number>0)
		this.throwExtraArgumentException (command_code, 0, argument_number);

// process the command depending on its object code

	switch (command.getObjectCode()) {
		case DOCUMENT:
		this.context.clearDocument();
		break;

		case QUERIES:
		this.context.clearQueries();
		break;
	}

	return this.command_result;
}

/**
* handle show commands
* @param command the command to handle
* @return the command result corresponding to this command
* @exception an exception describing the type of error which was encountered
*/
private CommandResult handleShow(Command command) {

// process the command depending on its object code

	Command.ObjectCode object_code = command.getObjectCode();
	this.command_result.clear();

	switch (object_code) {
		case QUERIES:
		QueryList queries = this.context.getQueryList();
		for (Query query:queries) {
			this.command_result.addArgument(query.getComment());
			this.command_result.addArgument(query.getFormula());
		}
		break;

	}

	this.command_result.setResultCode(CommandResult.ResultCode.OK);
	return this.command_result;
}
}