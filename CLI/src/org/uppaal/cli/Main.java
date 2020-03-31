package org.uppaal.cli;

import org.uppaal.cli.commands.CommandResult;
import org.uppaal.cli.commands.Command;
import org.uppaal.cli.commands.Context;

/**
 * The main/entry class implementing the command line interface.
 * @author Marius Mikucionis <marius@cs.aau.dk>
 */
public class Main
{
public static void main(String[] args) {
	try {
		Context context = new Context();
		ConsoleManager console_manager = new ConsoleManager(context);
		console_manager.run();
	} catch (Exception e) {
		System.err.println("Unable to start the console interface.");
		e.printStackTrace();
		System.exit(1);
	}
}
}