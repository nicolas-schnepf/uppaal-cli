package org.uppaal.cli.handlers;

import org.uppaal.cli.commands.CommandResult;
import org.uppaal.cli.commands.Command;
import org.uppaal.cli.commands.Context;
import java.io.IOException;
import java.net.MalformedURLException;

/**
* concrete class implementing a simulator handler
* supporting all commands for the simulator mode
*/

public class SimulatorHandler extends AbstractHandler {
// array of accepted command codes

private static Command.CommandCode[] simulator_commands = {
Command.CommandCode.SHOW,
Command.CommandCode.SELECT,
Command.CommandCode.PREVIEW,
Command.CommandCode.NEXT,
Command.CommandCode.FINISH
};

// private array of accepted object codes

private static Command.ObjectCode[] simulator_objects = {
Command.ObjectCode.TRACE,
Command.ObjectCode.TEMPLATES, 
Command.ObjectCode.TEMPLATE, 
Command.ObjectCode.VARIABLE,
Command.ObjectCode.VARIABLES,
Command.ObjectCode.CLOCKS,
Command.ObjectCode.CLOCK,
Command.ObjectCode.CONSTRAINT,
Command.ObjectCode.TRANSITION,
Command.ObjectCode.TRANSITIONS
};

/**
* public constructor of a simulator handler
* initializing it from a reference to the uppaal context
* @param context the reference to the uppaal context for this handler
*/

public SimulatorHandler (Context context) {
	super(context);
	this.accepted_commands = simulator_commands;
	this.accepted_objects = simulator_objects;
}

@Override
public CommandResult handle (Command command) throws MalformedURLException, IOException {
	return this.command_result;
}

@Override
public Handler.HandlerCode getMode() {
	return Handler.HandlerCode.SIMULATOR;
}
}
