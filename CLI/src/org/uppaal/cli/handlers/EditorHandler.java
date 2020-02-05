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
Command.CommandCode.CHECK
};

/**
* public constructor of a editor handler
* initializing it from a reference to the uppaal context
* @param context the reference to the uppaal context for this handler
*/

public EditorHandler (Context context) {
	super(context);
	this.accepted_commands = editor_commands;
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