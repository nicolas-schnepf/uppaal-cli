package org.uppaal.cli.handlers;

import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.Template;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.QueryList;
import com.uppaal.model.core2.Query;
import org.uppaal.cli.CommandResult;
import org.uppaal.cli.Command;
import org.uppaal.cli.Context;

import java.net.MalformedURLException;
import java.util.LinkedList;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
* concrete class implementing a editor handler
* supporting all commands for the editor mode
*/

public class EditorHandler extends AbstractHandler {
// array of accepted command codes

private static Command.CommandCode[] editor_commands = {
Command.CommandCode.SET,
Command.CommandCode.IMPORT,
Command.CommandCode.CLEAR,
Command.CommandCode.ADD,
Command.CommandCode.SHOW,
Command.CommandCode.REMOVE,
};

// array of accepted object codes

private static Command.ObjectCode[] editor_objects = {
Command.ObjectCode.DOCUMENT, 
Command.ObjectCode.QUERIES, 
Command.ObjectCode.TEMPLATES, 
Command.ObjectCode.QUERY, 
Command.ObjectCode.TEMPLATE, 
Command.ObjectCode.DECLARATION,
Command.ObjectCode.LOCATIONS,
Command.ObjectCode.EDGE,
Command.ObjectCode.LOCATION,
Command.ObjectCode.EDGE,
Command.ObjectCode.SYSTEM
};

/**
* public constructor of a editor handler
* initializing it from a reference to the uppaal context
* @param context the reference to the uppaal context for this handler
*/

public EditorHandler (Context context) {
	super(context);
	this.accepted_commands = editor_commands;
	this.accepted_objects = editor_objects;
}

@Override
public CommandResult handle (Command command) throws MalformedURLException, IOException {

// redirect the command to the appropriated method depending on its command code

	switch (command.getCommandCode()) {
		case IMPORT:
		return this.handleImport(command);

		case CLEAR:
		return this.handleClear(command);

		case SHOW:
		return this.handleShow(command);

		case REMOVE:
		return this.handleRemove(command);

		case ADD:
		return this.handleAdd(command);

		case SET:
		return this.handleSet(command);
	}

	return this.command_result;
}

@Override
public Handler.HandlerCode getMode () {
	return Handler.HandlerCode.EDITOR;
}

/**
* handle import commands
* @param command the command to handle
* @return the command result corresponding to this command
* @exception an exception describing the type of error which was encountered
*/
private CommandResult handleImport(Command command) throws MalformedURLException, IOException {

// check that the command contains exactly one argument

	Command.CommandCode command_code= command.getCommandCode();
	Command.ObjectCode object_code = command.getObjectCode();
	int argument_number = command.getArgumentNumber();

	if (argument_number<1)
		this.throwMissingArgumentException(command_code, object_code, 1, argument_number);
	else if (argument_number>1)
		this.throwExtraArgumentException (command_code, object_code, 1, argument_number);

// process the command depending on its object code

	String filename = command.getArgumentAt(0);
	int index = filename.length()-1;
	while (filename.charAt(index)!='.' && index>0) index --;
	String extension = filename.substring(index+1);

	switch (command.getObjectCode()) {
		case DOCUMENT:
		if (!extension.equals("xta") && !extension.equals("xml")) 
			this.throwWrongExtensionException (command_code, object_code, extension);
		this.context.loadDocument(filename);
		break;

		case QUERIES:
		if (!extension.equals("q")) 
			this.throwWrongExtensionException (command_code, object_code, extension);
		this.context.loadQueries(filename);
		break;
	}

	return this.command_result;
}

/**
* handle clear commands
* @param command the command to handle
* @return the command result corresponding to this command
*/
private CommandResult handleClear(Command command) {

// check that the command contains exactly one argument

	Command.CommandCode command_code= command.getCommandCode();
	Command.ObjectCode object_code = command.getObjectCode();
	int argument_number = command.getArgumentNumber();

 if (argument_number>0)
		this.throwExtraArgumentException (command_code, object_code, 0, argument_number);

// process the command depending on its object code

	switch (command.getObjectCode()) {
		case DOCUMENT:
		this.context.clearDocument();
		break;

		case QUERIES:
		this.context.clearQueries();
		break;
	}

	return this.command_result;
}

/**
* handle show commands
* @param command the command to handle
* @return the command result corresponding to this command
* @exception an exception describing the type of error which was encountered
*/
private CommandResult handleShow(Command command) {

// process the command depending on its object code

	Command.CommandCode command_code = command.getCommandCode();
	Command.ObjectCode object_code = command.getObjectCode();
	int argument_number = command.getArgumentNumber();
	this.command_result.clear();
	String name = null;
	String template = null;
	String source = null;
	String target = null;

	switch (object_code) {
		case QUERIES:
		QueryList queries = this.context.getQueryList();
		for (Query query:queries) {
			this.command_result.addArgument(query.getComment());
			this.command_result.addArgument(query.getFormula());
		}
		break;

// for templates simply return the list of all templates names and parameters

		case TEMPLATES:
			if (argument_number>0)
				this.throwExtraArgumentException(command_code, object_code, 0, argument_number);
			LinkedList<String> headers= this.context.getTemplateHeaders();
			for (String header: headers) this.command_result.addArgument(header);
		break;

// for declarations check that we have a name and if not return the global ones

		case DECLARATION:
			if (argument_number>1)
				this.throwExtraArgumentException(command_code, object_code, 1, argument_number);
			else if (argument_number==1) {
				name = command.getArgumentAt(0);
				this.command_result.addArgument(this.context.getTemplateDeclaration(name));
			} else
				this.command_result.addArgument(this.context.getGlobalDeclaration());
		break;

// for a template check that we have its name

		case TEMPLATE:
			if (argument_number==0) 
				this.throwMissingArgumentException(command_code, object_code, 1, 0);
			else if (argument_number>1)
				this.throwExtraArgumentException(command_code, object_code, 1, argument_number);

		name = command.getArgumentAt(0);
			this.command_result.addArgument(this.context.getTemplateDescription(name));
		break;

// for a location check that we have its name and the name of its template

		case LOCATION:
		if (argument_number<2)
		this.throwMissingArgumentException(command_code, object_code, 2, argument_number);
		else if (argument_number>2)
			this.throwExtraArgumentException(command_code, object_code, 2, argument_number);

		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		this.command_result.addArgument(this.context.getLocationDescription(template, name));
		break;

// for an edge check that we have the name of its source, its target and its template

		case EDGE:
		if (argument_number<3)
		this.throwMissingArgumentException(command_code, object_code, 3, argument_number);
		else if (argument_number>3)
		this.throwExtraArgumentException(command_code, object_code, 3, argument_number);

		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		String description = this.context.getEdgeDescription(template, source, target);
		this.command_result.addArgument(description);
		break;

// for a system simply get the corresponding chain and return it

		case SYSTEM:
		if (argument_number>0) 
		this.throwExtraArgumentException(command_code, object_code, 0, argument_number);
		this.command_result.addArgument(this.context.getSystem());
		break;

// for any other object code throw a wrong object exception

		default:
			this.throwWrongObjectException (Command.CommandCode.SHOW, object_code);
	}

	this.command_result.setResultCode(CommandResult.ResultCode.OK);
	return this.command_result;
}

/**
* handle remove commands
* @param command the command to handle
* @return the command result corresponding to this command
* @exception an exception describing the type of error which was encountered
*/
private CommandResult handleRemove(Command command) {

// process the command depending on its object code

	Command.CommandCode command_code = command.getCommandCode();
	Command.ObjectCode object_code = command.getObjectCode();
	int argument_number = command.getArgumentNumber();
	this.command_result.clear();
	String name = null;
	String template = null;
	String source = null;
	String target = null;

	switch (object_code) {
		case QUERIES:
			this.context.clearQueries();
		break;

// for templates simply return the list of all templates names and parameters

		case TEMPLATES:
			if (argument_number>0)
				this.throwExtraArgumentException(command_code, object_code, 0, argument_number);
			this.context.clearTemplates();
		break;

// for declarations check that we have a name and if not return the global ones

		case DECLARATION:
			if (argument_number>1)
				this.throwExtraArgumentException(command_code, object_code, 1, argument_number);
			else if (argument_number==1) {
				name = command.getArgumentAt(0);
				this.context.setTemplateDeclaration(name, null);
			} else
				this.context.setGlobalDeclaration (null);
		break;

// for a template check that we have its name

		case TEMPLATE:
			if (argument_number==0) 
				this.throwMissingArgumentException(command_code, object_code, 1, 0);
			else if (argument_number>1)
				this.throwExtraArgumentException(command_code, object_code, 1, argument_number);

		name = command.getArgumentAt(0);
			this.context.removeTemplate(name);
		break;

// for a location check that we have its name and the name of its template

		case LOCATION:
		if (argument_number<2)
		this.throwMissingArgumentException(command_code, object_code, 2, argument_number);
		else if (argument_number>2)
			this.throwExtraArgumentException(command_code, object_code, 2, argument_number);

		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		this.context.removeLocation(template, name);
		break;

// for an edge check that we have the name of its source, its target and its template

		case EDGE:
		if (argument_number<3)
		this.throwMissingArgumentException(command_code, object_code, 3, argument_number);
		else if (argument_number>3)
		this.throwExtraArgumentException(command_code, object_code, 3, argument_number);

		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		this.context.removeEdge(template, source, target);
		break;

// for a system simply get the corresponding chain and return it

		case SYSTEM:
		if (argument_number>0) 
		this.throwExtraArgumentException(command_code, object_code, 0, argument_number);
		this.context.setSystem(null);
		break;

// for any other object code throw a wrong object exception

		default:
			this.throwWrongObjectException (Command.CommandCode.SHOW, object_code);
	}

	this.command_result.setResultCode(CommandResult.ResultCode.OK);
	return this.command_result;
}

/**
* handle add commands
* @param command the command to handle
* @return the command result corresponding to this command
* @exception an exception describing the type of error which was encountered
*/
private CommandResult handleAdd(Command command) {

// process the command depending on its object code

	Command.CommandCode command_code = command.getCommandCode();
	Command.ObjectCode object_code = command.getObjectCode();
	int argument_number = command.getArgumentNumber();
	this.command_result.clear();
	String name = null;
	String template = null;
	String declaration = null;
	String system = null;

	switch (object_code) {

// for declarations check that we have a name and if not return the global ones

		case DECLARATION:
		if (argument_number<1)
		this.throwMissingArgumentException(command_code, object_code, 1, argument_number);
		else if (argument_number>2)
		this.throwExtraArgumentException(command_code, object_code, 2, argument_number);
			else if (argument_number==2) {
				name = command.getArgumentAt(0);
				declaration = command.getArgumentAt(1);
				this.context.setTemplateDeclaration(name, declaration);
			} else {
				declaration = command.getArgumentAt(0);
				this.context.setGlobalDeclaration (declaration);
		}
		break;

// for a system simply set the corresponding chain

		case SYSTEM:

		if (argument_number<1) 
		this.throwMissingArgumentException(command_code, object_code, 1, argument_number);

		if (argument_number>1) 
		this.throwExtraArgumentException(command_code, object_code, 1, argument_number);

		system = command.getArgumentAt(0);
		this.context.setSystem(system);
		break;

// for any other object code throw a wrong object exception

		default:
			this.throwWrongObjectException (Command.CommandCode.SHOW, object_code);
	}

	this.command_result.setResultCode(CommandResult.ResultCode.OK);
	return this.command_result;
}

/**
* handle set commands
* @param command the command to handle
* @return the command result corresponding to this command
* @exception an exception describing the type of error which was encountered
*/
private CommandResult handleSet(Command command) {

// process the command depending on its object code

	Command.CommandCode command_code = command.getCommandCode();
	Command.ObjectCode object_code = command.getObjectCode();
	int argument_number = command.getArgumentNumber();
	this.command_result.clear();
	String name = null;
	String template = null;
	String declaration = null;
	String system = null;

	switch (object_code) {

// for declarations check that we have a name and if not return the global ones

		case DECLARATION:
		if (argument_number<1)
		this.throwMissingArgumentException(command_code, object_code, 1, argument_number);
		else if (argument_number>2)
		this.throwExtraArgumentException(command_code, object_code, 2, argument_number);
			else if (argument_number==2) {
				name = command.getArgumentAt(0);
				declaration = command.getArgumentAt(1);
				this.context.setTemplateDeclaration(name, declaration);
			} else {
				declaration = command.getArgumentAt(0);
				this.context.setGlobalDeclaration (declaration);
		}
		break;

// for a system simply set the corresponding chain

		case SYSTEM:

		if (argument_number<1) 
		this.throwMissingArgumentException(command_code, object_code, 1, argument_number);

		if (argument_number>1) 
		this.throwExtraArgumentException(command_code, object_code, 1, argument_number);

		system = command.getArgumentAt(0);
		this.context.setSystem(system);
		break;

// for any other object code throw a wrong object exception

		default:
			this.throwWrongObjectException (Command.CommandCode.SHOW, object_code);
	}

	this.command_result.setResultCode(CommandResult.ResultCode.OK);
	return this.command_result;
}
}