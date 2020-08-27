package org.uppaal.cli;

import org.uppaal.cli.frontend.ConsoleManager;
import org.uppaal.cli.commands.CommandResult;
import org.uppaal.cli.context.Context;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * The main/entry class implementing the command line interface.
 * @author Marius Mikucionis <marius@cs.aau.dk>
 */
public class Main
{
public static void main(String[] args) {
	try {
		Context context = new Context();
		FileInputStream input = null;
		FileOutputStream output = null;

		if (args.length > 0) {
			input = new FileInputStream (args[0]);
			output = new FileOutputStream("/dev/null");
		}


		ConsoleManager console_manager;
		if (args.length==0) console_manager = new ConsoleManager(context);
		else console_manager = new ConsoleManager(context, args[0]);
		console_manager.run();
	} catch (IOException e) {
		System.err.println("File "+args[0]+" does not exist.");
		System.exit(1);
	}catch (Exception e) {
		System.err.println("Unable to start the console interface.");
		e.printStackTrace();
		System.exit(1);
	}
}
}
