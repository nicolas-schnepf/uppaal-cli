package org.uppaal.cli.commands;

import org.uppaal.cli.context.ModeCode;
import org.uppaal.cli.enumerations.ResultCode;
import org.uppaal.cli.context.Context;
import java.util.HashMap;

/**
* concrete class implementing a help handler
* supporting all possible help commands under mode control
*/

public class HelpHandler extends AbstractHandler {

// hash map of help messages
private HashMap <String, String> command_messages;

// hash map of syntax help messages
private HashMap <String, String> syntax_messages;

// hash map of environment help messages
private HashMap<String, String> environment_messages;

public HelpHandler (Context context) {
	super(context, "help");
	this.command_messages = new HashMap<String, String>();
	this.syntax_messages = new HashMap<String, String>();
	this.environment_messages = new HashMap<String, String>();

// setup the help on available regex 

	String syntax = "REFERENCE := PROPERTY | NAME[\"[\"INDEX\"]\"][\".\"PROPERTY]";
	this.syntax_messages.put("REFERENCE", syntax);

	syntax = "NAME := [a-zA-Z][a-zA-Z0-1]*";
	this.syntax_messages.put("NAME", syntax);

	syntax = "INDEX = NAME [ \"->\" NAME ] | [0-1]+";
	this.syntax_messages.put("INDEX", syntax);

	syntax = "Press tab to see the list of available properties for a given prefix.";
	this.syntax_messages.put("PROPERTIES", syntax);

	syntax = "VALUE := STR | \"{\"(PROPERTY \":\" STR (\";\" PROPERTY\":\"STR )*)? \"}\"";
	this.syntax_messages.put("VALUE", syntax);

	syntax = "STR := \"'\" .* \"'\"";
	this.syntax_messages.put("STR", syntax);

	syntax = "SAVEABLE := \"document\" | \"trace\" | \"queries\" | \"strategy\" | \"data\"";
	this.syntax_messages.put("SAVEABLE", syntax);

	syntax = "LOADABLE := \"document\" | \"templates\" | \"queries\" | \"strategy\" | \"data\"";
	this.syntax_messages.put("LOADABLE", syntax);

	syntax = "A standard filename in the syntax of your operating system.";
	this.syntax_messages.put("FILENAME", syntax);

// put the help messages about the available environments

	StringBuffer environment = new StringBuffer();
	environment.append ("The shell environment is the default environment\n");
	environment.append("in this environment you can type a command and its arguments,\n");
	environment.append("press tab to complete a command or one of its arguments,\n");
	environment.append("undo commands with ctrl + u,\n");
	environment.append("or redo a command with ctrl + r.");
	this.environment_messages.put("shell", environment.toString());

environment = new StringBuffer();
	environment.append ("The nano environment emulates a nano editor\n");
	environment.append("it is started by the command set with no value for a property\n");
	environment.append("See ");
	environment.append("https://github.com/jline/jline3/blob/master/builtins/src/main/resources/org/jline/builtins/nano-main-help.txt\n");
	environment.append("for more help about this environment.");
	this.environment_messages.put("nano", environment.toString());

environment = new StringBuffer();
	environment.append ("The selection environment is started when you need to select items.\n");
	environment.append("In this environment you can change the item with left and right arrows,\n");
	environment.append("change the view with up and down arrows,\n");
	environment.append("select an item with the space bar,\n");
	environment.append("validate the selection with enter,\n");
	environment.append("or cancel the selection with ctrl + d.");
	this.environment_messages.put("selection", environment.toString());
	try {
	this.operation_map.put("commands", this.getClass().getMethod("helpCommands"));
	this.operation_map.put("environments", this.getClass().getMethod("helpEnvironments"));
	this.operation_map.put("default", this.getClass().getMethod("helpDefault"));
	} catch (Exception e) {
	System.out.println(e.getMessage());
	e.printStackTrace();
	System.exit(1);
	}
}

@Override
public CommandResult handle () {

// try to match all available maps of help

	if (this.command_messages.keySet().contains(this.getObjectType())) this.helpCommand();
	else if (this.syntax_messages.keySet().contains(this.getObjectType())) this.helpSyntax();
	else if (this.environment_messages.keySet().contains(this.getObjectType())) this.helpEnvironment();
	else return super.handle();

	return this.command_result;
}

/**
* return the help on a command specified as object type
*/
public void helpCommand() {
	this.command_result.addArgument(this.syntax_messages.get(this.getObjectType()));
	this.command_result.addArgument(this.command_messages.get(this.getObjectType()));
	this.command_result.addArgument("Type \"help\" REGEX for more information on a REGEX.");
}

/**
* return the help on a syntax specified as object type
*/
public void helpSyntax() {
	this.command_result.addArgument(this.syntax_messages.get(this.getObjectType()));
	this.command_result.addArgument("Type \"help\" REGEX for more information on a REGEX.");
}

/**
* return the help on a environment specified as object type
*/
public void helpEnvironment() {
	this.command_result.addArgument(this.environment_messages.get(this.getObjectType()));
}

/**
* return the help on all commands
*/
public void helpCommands() {
	this.command_result.addArgument("Available commands are:\n");
	for (String command: this.command_messages.keySet())
		this.command_result.addArgument(command);
	this.command_result.addArgument("\nType \"help\" COMMAND for more information.\n");
}

/**
* return the help on all environments
*/
public void helpEnvironments() {
	this.command_result.addArgument("Available environments are:\n");
	for (String environment: this.environment_messages.keySet())
		this.command_result.addArgument(environment);
	this.command_result.addArgument("\nType \"help\" ENVIRONMENT for more information.\n");
}

/**
* return the help on everything
*/
public void helpDefault() {
	this.helpCommands();
	this.command_result.addArgument("");
	this.helpEnvironments();
}

/**
* add the help message for a given command
* @param command the command to document
* @param message the corresponding help message
*/
public void setHelpMessage (String command, String message) {
	this.command_messages.put(command, message);
}

/**
* add the syntax message for a given command
* @param command the command to document
* @param message the corresponding help message
*/
public void setSyntaxMessage (String command, String message) {
	this.syntax_messages.put(command, message);
}

@Override
public boolean acceptMode (ModeCode mode_code) {
	return true;
}

@Override
public String getHelpMessage() {
	return "Display the help about any element of the uppaal command line interface.";
}

@Override
public String getSyntax() {
	return "\"help\" ( \"commands\" | \"environments\" | NAME )";
}
}
