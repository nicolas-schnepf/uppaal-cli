package org.uppaal.cli.commands;

/**
* command launcher, receives the name of a command and returns the corresponding handler
*/

import org.uppaal.cli.context.Context;
import java.util.HashMap;
import java.util.HashSet;

public class CommandLauncher {

// default handler of this command launcher
private DefaultHandler default_handler;

// Operation map of this command launcher
private HashMap<String, Handler> command_map;

// context of this command launcher
private Context context;

public CommandLauncher (Context context) {
	this.context = context;
	this.default_handler = new DefaultHandler(context);
	this.command_map = new HashMap<String, Handler>();
	this.command_map.put("load", new LoadHandler(context));
	this.command_map.put("save", new SaveHandler(context));
	this.command_map.put("show", new ShowHandler(context));
	this.command_map.put("reset", new ResetHandler(context));
	this.command_map.put("set", new SetHandler(context));
	this.command_map.put("unset", new UnsetHandler(context));
	this.command_map.put("check", new CheckHandler(context));
	this.command_map.put("help", new HelpHandler(context));
	HelpHandler help_handler = (HelpHandler)this.command_map.get("help");

// setup the help handler with the help for all commands

	for (String command: this.command_map.keySet()) {
		Handler handler = this.command_map.get(command);
		help_handler.setHelpMessage(command, handler.getHelpMessage());
		help_handler.setSyntaxMessage(command, handler.getSyntax());
	}

// finally add the help about all commands of the default handler

	for (String command: this.default_handler) {
		String 		message = this.default_handler.getHelpMessage(command);
		help_handler.setHelpMessage(command, message);
		message = this.default_handler.getSyntax(command);
		help_handler.setSyntaxMessage(command, message);
	}
}

/**
* @return the set of commands currently accepted by this command launcher
*/

public HashSet<String> getAcceptedCommands () {
	HashSet<String> accepted_commands = this.default_handler.getAcceptedCommands();
			for(String command:this.command_map.keySet()) {
		if (this.command_map.get(command).acceptMode(this.context.getMode()))
			accepted_commands.add(command);
	}

	return accepted_commands;
}

/**
* get a command handler based on the name of the command to handle
* @param command the name of the command to handle
* @return the handler for a specific command name
*/
public Handler getCommandHandler(String command) {
	if (this.command_map.keySet().contains(command))
		return this.command_map.get(command);
	else
		return this.default_handler;
}
}
