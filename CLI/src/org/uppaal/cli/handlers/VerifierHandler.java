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

public class VerifierHandler extends ModeHandler {
// array of accepted command codes

private static Command.OperationCode[] verifier_commands = {
Command.OperationCode.CHECK,
Command.OperationCode.IMPORT,
Command.OperationCode.ADD,
Command.OperationCode.SET,
Command.OperationCode.SHOW,
Command.OperationCode.REMOVE,
Command.OperationCode.SELECT,
Command.OperationCode.UNSELECT
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
}

@Override
public Handler.HandlerCode getMode() {
	return Handler.HandlerCode.VERIFIER;
}
}
