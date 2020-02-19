package org.uppaal.cli.handlers;

import org.uppaal.cli.CommandResult;
import org.uppaal.cli.Command;
import org.uppaal.cli.Context;

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
public CommandResult handle (Command command) {
	return this.command_result;
}

@Override
public Handler.HandlerCode getMode () {
	return Handler.HandlerCode.EDITOR;
}
}