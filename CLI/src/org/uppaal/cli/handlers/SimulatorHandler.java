package org.uppaal.cli.handlers;

import org.uppaal.cli.CommandResult;
import org.uppaal.cli.Command;
import org.uppaal.cli.Context;

/**
* concrete class implementing a simulator handler
* supporting all commands for the simulator mode
*/

public class SimulatorHandler extends AbstractHandler {
// array of accepted command codes

private static Command.CommandCode[] simulator_commands = {
Command.CommandCode.CHECK
};

/**
* public constructor of a simulator handler
* initializing it from a reference to the uppaal context
* @param context the reference to the uppaal context for this handler
*/

public SimulatorHandler (Context context) {
	super(context);
	this.accepted_commands = simulator_commands;
}

@Override
public CommandResult handle (Command command) {
	return this.command_result;
}

@Override
public Handler.HandlerCode getMode() {
	return Handler.HandlerCode.SIMULATOR;
}
}