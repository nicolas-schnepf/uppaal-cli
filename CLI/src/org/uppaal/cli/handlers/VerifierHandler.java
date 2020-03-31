package org.uppaal.cli.handlers;

import org.uppaal.cli.commands.CommandResult;
import org.uppaal.cli.commands.Command;
import org.uppaal.cli.commands.Context;
import java.io.IOException;
import java.net.MalformedURLException;

/**
* concrete class implementing a verifier handler
* supporting all commands for the verifier mode
*/

public class VerifierHandler extends AbstractHandler {
// array of accepted command codes

private static Command.CommandCode[] verifier_commands = {
Command.CommandCode.CHECK,
Command.CommandCode.IMPORT,
Command.CommandCode.ADD,
Command.CommandCode.SET,
Command.CommandCode.SHOW,
Command.CommandCode.REMOVE,
Command.CommandCode.SELECT,
Command.CommandCode.UNSELECT
};

// array of accepted objects for a verifier handler

private static Command.ObjectCode[] verifier_objects = {
Command.ObjectCode.QUERIES, 
Command.ObjectCode.QUERY, 
Command.ObjectCode.OPTION,
Command.ObjectCode.OPTIONS
};

/**
* public constructor of a verifier handler
* initializing it from a reference to the uppaal context
* @param context the reference to the uppaal context for this handler
*/

public VerifierHandler (Context context) {
	super(context);
	this.accepted_commands = verifier_commands;
	this.accepted_objects = verifier_objects;
}

@Override
public CommandResult handle (Command command) throws MalformedURLException, IOException {
	return this.command_result;
}

@Override
public Handler.HandlerCode getMode() {
	return Handler.HandlerCode.VERIFIER;
}
}
