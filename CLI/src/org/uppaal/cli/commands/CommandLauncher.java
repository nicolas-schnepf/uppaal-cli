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
	this.command_map.put("import", new ImportHandler(context));
	this.command_map.put("export", new ExportHandler(context));
	this.command_map.put("show", new ShowHandler(context));
	this.command_map.put("reset", new ResetHandler(context));
	this.command_map.put("set", new SetHandler(context));
	this.command_map.put("unset", new UnsetHandler(context));
	this.command_map.put("check", new CheckHandler(context));
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
