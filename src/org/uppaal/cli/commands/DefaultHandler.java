package org.uppaal.cli.commands;

import com.uppaal.engine.EngineException;
import com.uppaal.engine.CannotEvaluateException;
import org.uppaal.cli.exceptions.ConsoleException;
import org.uppaal.cli.exceptions.UnknownCommandException;


import org.uppaal.cli.enumerations.ResultCode;
import org.uppaal.cli.commands.CommandResult;

import org.uppaal.cli.context.ModeCode;
import org.uppaal.cli.context.Context;

import java.io.IOException;
import java.util.LinkedList;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

/**
* concrete class implementing a default handler
* supporting all methods to handle the commands available in all modes
* to hide the process of command execution to client classes
*/

public class DefaultHandler extends AbstractHandler implements Iterable<String> {


// private unknown command exception to throw when receiving an unknown command
private UnknownCommandException unknown_command_exception;

// private command of this default handler
private String command;

// command map of this default handler

private HashMap <String, Method> command_map;

/**
* public constructor of a command handler
* initializing it from a given context
* @param context the uppaal context for this handler
*/

public DefaultHandler (Context context) {
	super(context, null);
	this.unknown_command_exception = new UnknownCommandException ();
	try {
	this.command_map = new HashMap<String, Method>();
	this.command_map.put("start", this.getClass().getMethod("handleStart"));
	this.command_map.put("exit", this.getClass().getMethod("handleExit"));
	this.command_map.put("compile", this.getClass().getMethod("handleCompile"));
	this.command_map.put("connect", this.getClass().getMethod("handleConnect"));
	this.command_map.put("disconnect", this.getClass().getMethod("handleDisconnect"));
	} catch (Exception e) {
	System.out.println(e.getMessage());
	e.printStackTrace();
	System.exit(1);
	}
}

@Override
public Iterator<String> iterator() {
	return this.command_map.keySet().iterator();
}

/**
* set the command of this default handler
* @param command the new command for this default handler
*/
public void setCommand (String command) {
	this.command = command;
}

public HashSet<String> getAcceptedCommands () {
	HashSet<String> accepted_operations = new HashSet<String>();
	accepted_operations.addAll(this.command_map.keySet());
	return accepted_operations;
}

@Override
public CommandResult handle () {
	this.command_result.clear();
	this.command_result.setResultCode(ResultCode.OK);

	if (!this.command_map.keySet().contains(this.command)) {
this.unknown_command_exception.setCommand(this.command);
		this.unknown_command_exception.setStackTrace(Thread.currentThread().getStackTrace());
		throw this.unknown_command_exception;
	}

	try {
		this.command_map.get(this.command).invoke(this);
	} catch (IllegalAccessException e) {
		System.out.println(e.getMessage());
		e.printStackTrace();
		System.exit(1);
	} catch (InvocationTargetException e) {
		if (e.getTargetException() instanceof ConsoleException)
			throw (ConsoleException)e.getTargetException();

		System.out.println(e.getMessage());
		e.printStackTrace();
		System.exit(1);
	}

	return this.command_result;
}

public void handleStart() {
		ModeCode mode = this.context.getMode(this.arguments.get(0));
		switch (mode) {

// start editor handler if requested

			case EDITOR:
			this.context.setMode(this.getArgumentAt(0));
			this.command_result.addArgument("editor");
			break;

// start simulator handler if requested

		case SIMULATOR:
		case VERIFIER:

		try {
		if (this.context.getMode()==ModeCode.EDITOR) {
			LinkedList<String> problems = this.context.getModelExpert().compileDocument();
			for (String problem: problems) this.command_result.addArgument(problem);
			if (this.context.getSystem()==null) {
				this.command_result.setResultCode(ResultCode.COMPILATION_ERROR);
				return;
			}
		}

			this.context.setMode(this.getArgumentAt(0));
			this.command_result.addArgument(this.arguments.get(0));

			if (this.getArgumentAt(0).equals("simulator") && 
			(this.context.getTrace().isEmpty() || (this.arguments.size()>1))) {
				if (this.arguments.size()>1)
					this.context.getTraceExpert().setTrace(this.arguments.get(1));
				else
					this.context.getTraceExpert().setTrace();
			}
		} catch (EngineException e) {
		this.command_result.setResultCode(ResultCode.ENGINE_ERROR);
		this.command_result.addArgument(e.getMessage());
		return;
		} catch (CannotEvaluateException e) {
		this.command_result.setResultCode(ResultCode.ENGINE_ERROR);
		this.command_result.addArgument(e.getMessage());
		return;
		} catch (IOException e) {
			this.command_result.setResultCode(ResultCode.IO_ERROR);
			return;
		}

			break;

// if the mode is not known throw an unknown mode exception

		}
		this.command_result.setResultCode(ResultCode.MODE_CHANGED);
}

public void handleExit() {
		this.command_result.setResultCode(ResultCode.EXIT);
}

public void handleCompile () {
		try {
			LinkedList<String> problems = this.context.getModelExpert().compileDocument();
			for (String problem: problems) this.command_result.addArgument(problem);
			if (this.context.getSystem()!=null) this.command_result.setResultCode(ResultCode.OK);
			else this.command_result.setResultCode(ResultCode.COMPILATION_ERROR);
		} catch (EngineException e) {
		this.command_result.setResultCode(ResultCode.ENGINE_ERROR);
		} catch (IOException e) {
			this.command_result.setResultCode(ResultCode.IO_ERROR);
		}
}

public void handleConnect () {
		try {
			this.context.getEngineExpert().connectEngine();
			this.command_result.setResultCode(ResultCode.OK);
		} catch (EngineException e) {
		this.command_result.setResultCode(ResultCode.ENGINE_ERROR);
		} catch (IOException e) {
			this.command_result.setResultCode(ResultCode.IO_ERROR);
		}
}



public void handleDisconnect () {
			this.context.getEngineExpert().disconnectEngine();
			this.command_result.setResultCode(ResultCode.OK);
}

@Override
public void clear() {
	super.clear();
	this.command = null;
}

@Override
public ModeCode getMode() {
	return this.context.getMode();
}

@Override
public boolean acceptMode (ModeCode mode) {
	return true;
}

@Override
public String getHelpMessage () {
	return null;
}

@Override
public String getSyntax() {
	return null;
}

/**
* return the help message for a specified command
* @param command the command to document
* @return the intended help message
*/
public String getHelpMessage (String command) {

	switch (command) {
		case "start":
		return "Start a specified mode.";

		case "exit":
		return "Exit the uppaal command line interface";

		case "compile":
		return "compile the uppaal model.";

		case "connect":
		return "Connect the uppaal engine.";

		case "disconnect":
		return "Disconnect the uppaal engine.";
	}

	return null;
}

/**
* return the syntax of a specified command
* @param command the command to document
* @return the regex matching the syntax for the provided command
*/
public String getSyntax(String command) {

	switch (command) {
		case "start":
		return "\"start\" ( \"editor\" | \"simulator\" | \"verifier\" )";

		default:
		return String.format("\"%s\"", command);
	}
}
}
