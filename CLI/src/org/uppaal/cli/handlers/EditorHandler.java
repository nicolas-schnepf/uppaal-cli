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
Command.CommandCode.UNSET,
Command.CommandCode.IMPORT,
Command.CommandCode.CLEAR,
Command.CommandCode.ADD,
Command.CommandCode.SHOW,
Command.CommandCode.REMOVE,
Command.CommandCode.UNDO,
Command.CommandCode.REDO
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
Command.ObjectCode.INVARIANT,
Command.ObjectCode.INIT,
Command.ObjectCode.COMMITTED,
Command.ObjectCode.EDGE,
Command.ObjectCode.SELECT,
Command.ObjectCode.GUARD,
Command.ObjectCode.SYNC,
Command.ObjectCode.ASSIGN,
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

		case RENAME:
		return this.handleRename(command);

		case SET:
		return this.handleSet(command);

		case UNSET:
		return this.handleUnset(command);

		case UNDO:
		return this.handleUndo(command);

		case REDO:
		return this.handleRedo(command);
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
	this.command_result.setCommandCode(command_code);
	this.command_result.setObjectCode(object_code);
	String name = null;
	String template = null;
	String source = null;
	String target = null;

	switch (object_code) {
		case QUERIES:
		if (argument_number>0)
		this.throwExtraArgumentException(command_code, object_code, 0, argument_number);
		LinkedList<String> names = this.context.showQueries();
		for (String arg: names) this.command_result.addArgument(arg);
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
				this.command_result.addArgument(this.context.getTemplateProperty(name, "declaration"));
			} else
				this.command_result.addArgument(this.context.getGlobalDeclaration());
		break;

// for a query check that we have its name

		case QUERY:
			if (argument_number==0) 
				this.throwMissingArgumentException(command_code, object_code, 1, 0);
			else if (argument_number>1)
				this.throwExtraArgumentException(command_code, object_code, 1, argument_number);

		name = command.getArgumentAt(0);
			this.command_result.addArgument(this.context.showQuery(name));
		break;

// for a template check that we have its name

		case TEMPLATE:
			if (argument_number==0) 
				this.throwMissingArgumentException(command_code, object_code, 1, 0);
			else if (argument_number>1)
				this.throwExtraArgumentException(command_code, object_code, 1, argument_number);

		name = command.getArgumentAt(0);
			this.command_result.addArgument(this.context.showTemplate(name));
		break;

// for a location check that we have its name and the name of its template

		case LOCATION:
		if (argument_number<2)
		this.throwMissingArgumentException(command_code, object_code, 2, argument_number);
		else if (argument_number>2)
			this.throwExtraArgumentException(command_code, object_code, 2, argument_number);

		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		this.command_result.addArgument(this.context.showLocation(template, name));
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
		String description = this.context.showEdge(template, source, target);
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
	this.command_result.setCommandCode(command_code);
	this.command_result.setObjectCode(object_code);
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
				this.context.setTemplateProperty(name, "declaration",  null);
				this.command_result.addArgument(name);
			} else
				this.context.setGlobalDeclaration (null);
		break;

// for a query check that we have its name

		case QUERY:
			if (argument_number==0) 
				this.throwMissingArgumentException(command_code, object_code, 1, 0);
			else if (argument_number>1)
				this.throwExtraArgumentException(command_code, object_code, 1, argument_number);

		name = command.getArgumentAt(0);
			this.context.removeQuery(name);
			this.command_result.addArgument(name);
		break;

// for a template check that we have its name

		case TEMPLATE:
			if (argument_number==0) 
				this.throwMissingArgumentException(command_code, object_code, 1, 0);
			else if (argument_number>1)
				this.throwExtraArgumentException(command_code, object_code, 1, argument_number);

		name = command.getArgumentAt(0);
			this.context.removeTemplate(name);
			this.command_result.addArgument(name);
		break;

// for a parameter check that we have the name of its template

		case PARAMETER:
			if (argument_number==0) 
				this.throwMissingArgumentException(command_code, object_code, 1, 0);
			else if (argument_number>1)
				this.throwExtraArgumentException(command_code, object_code, 1, argument_number);

		name = command.getArgumentAt(0);
			this.context.setTemplateProperty(name, "parameter", null);
			this.command_result.addArgument(name);
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
		this.command_result.addArgument(template);
			this.command_result.addArgument(name);
		break;

// for an invariant check that we have the name of its location and of its template

		case INVARIANT:
		if (argument_number<2)
		this.throwMissingArgumentException(command_code, object_code, 2, argument_number);
		else if (argument_number>2)
			this.throwExtraArgumentException(command_code, object_code, 2, argument_number);

		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		this.context.setLocationProperty(template, name, "invariant", null);
		this.command_result.addArgument(template);
			this.command_result.addArgument(name);
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
		this.command_result.addArgument(template);
			this.command_result.addArgument(source);
			this.command_result.addArgument(target);
		break;

// for a select check that all required information is present

		case SELECT:
		if (argument_number<3)
		this.throwMissingArgumentException(command_code, object_code, 3, argument_number);
		else if (argument_number>3)
		this.throwExtraArgumentException(command_code, object_code, 3, argument_number);

		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		this.context.setEdgeProperty(template, source, target, "select", null);
		this.command_result.addArgument(template);
			this.command_result.addArgument(source);
			this.command_result.addArgument(target);
		break;
		
// for a guard check that all required information is present

		case GUARD:
		if (argument_number<3)
		this.throwMissingArgumentException(command_code, object_code, 3, argument_number);
		else if (argument_number>3)
		this.throwExtraArgumentException(command_code, object_code, 3, argument_number);

		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		this.context.setEdgeProperty(template, source, target, "guard", null);
		this.command_result.addArgument(template);
			this.command_result.addArgument(source);
			this.command_result.addArgument(target);
		break;

// for a sync check that all required information is present

		case SYNC:
		if (argument_number<3)
		this.throwMissingArgumentException(command_code, object_code, 3, argument_number);
		else if (argument_number>3)
		this.throwExtraArgumentException(command_code, object_code, 3, argument_number);

		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		this.context.setEdgeProperty(template, source, target, "sync", null);
		this.command_result.addArgument(template);
			this.command_result.addArgument(source);
			this.command_result.addArgument(target);
		break;

// for an assign check that all required information is present

		case ASSIGN:
		if (argument_number<3)
		this.throwMissingArgumentException(command_code, object_code, 3, argument_number);
		else if (argument_number>3)
		this.throwExtraArgumentException(command_code, object_code, 3, argument_number);

		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		this.context.setEdgeProperty(template, source, target, "assign", null);
		this.command_result.addArgument(template);
			this.command_result.addArgument(source);
			this.command_result.addArgument(target);
		break;

// for a system simply get the corresponding chain and return it

		case SYSTEM:
		if (argument_number>0) 
		this.throwExtraArgumentException(command_code, object_code, 0, argument_number);
		this.context.setSystem(null);
		break;

// for any other object code throw a wrong object exception

		default:
			this.throwWrongObjectException (Command.CommandCode.REMOVE, object_code);
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
	this.command_result.setCommandCode(command_code);
	this.command_result.setObjectCode(object_code);
	String name = null;
	String formula = null;
	String comment = null;
	String parameter = null;
	String template = null;
	String source = null;
	String target = null;
	String declaration = null;
	String system = null;
	String value=null;

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
				this.context.setTemplateProperty(name, "declaration",  declaration);
				this.command_result.addArgument(name);
			} else {
				declaration = command.getArgumentAt(0);
				this.context.setGlobalDeclaration (declaration);
		}
		break;

// for a query check that we have its name, its formula and its comment

		case QUERY:
			if (argument_number<3) 
				this.throwMissingArgumentException(command_code, object_code, 3, argument_number);
			else if (argument_number>3)
				this.throwExtraArgumentException(command_code, object_code, 3, argument_number);

		name = command.getArgumentAt(0);
		formula = command.getArgumentAt(1);
		comment = command.getArgumentAt(2);
			this.context.addQuery(name, formula, comment);
this.command_result.addArgument(name);
		break;

// for a template check that we have its name

		case TEMPLATE:
			if (argument_number==0) 
				this.throwMissingArgumentException(command_code, object_code, 1, 0);
			else if (argument_number>2)
				this.throwExtraArgumentException(command_code, object_code, 2, argument_number);

		name = command.getArgumentAt(0);
		if (argument_number==2) parameter = command.getArgumentAt(1);
			this.context.addTemplate(name, parameter);
			this.command_result.addArgument(name);
		break;

// for a parameter check that we have the name of its template

		case PARAMETER:
			if (argument_number<2) 
				this.throwMissingArgumentException(command_code, object_code, 2, argument_number);
			else if (argument_number>2)
				this.throwExtraArgumentException(command_code, object_code, 2, argument_number);

		name = command.getArgumentAt(0);
		value = command.getArgumentAt(1);
			this.context.setTemplateProperty(name, "parameter", value);
			this.command_result.addArgument(name);
			this.command_result.addArgument(parameter);
		break;

// for a location check that we have its name and the name of its template

		case LOCATION:
		if (argument_number<2)
		this.throwMissingArgumentException(command_code, object_code, 2, argument_number);
		else if (argument_number>2)
			this.throwExtraArgumentException(command_code, object_code, 2, argument_number);

		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		this.context.addLocation(template, name);
		this.command_result.addArgument(template);
		this.command_result.addArgument(name);
		break;

// for an invariant check that we have the name of its location and of its template

		case INVARIANT:
		if (argument_number<3)
		this.throwMissingArgumentException(command_code, object_code, 3, argument_number);
		else if (argument_number>3)
			this.throwExtraArgumentException(command_code, object_code, 3, argument_number);

		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		value = command.getArgumentAt(2);
		this.context.setLocationProperty(template, name, "invariant", value);
		this.command_result.addArgument(template);
		this.command_result.addArgument(name);
		this.command_result.addArgument(value);
		break;


// for an init label check that we have the name of its location and of its template

		case INIT:
		if (argument_number<2)
		this.throwMissingArgumentException(command_code, object_code, 2, argument_number);
		else if (argument_number>2)
			this.throwExtraArgumentException(command_code, object_code, 2, argument_number);

		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		this.context.addLocation(template, name);
		this.context.setLocationProperty(template, name, "init", true);
		this.command_result.addArgument(template);
		this.command_result.addArgument(name);
		break;
		
// for a committed label check that we have the name of its location and of its template

		case COMMITTED:
		if (argument_number<2)
		this.throwMissingArgumentException(command_code, object_code, 2, argument_number);
		else if (argument_number>2)
			this.throwExtraArgumentException(command_code, object_code, 2, argument_number);

		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		this.context.addLocation(template, name);
		this.context.setLocationProperty(template, name, "committed", true);
		this.command_result.addArgument(template);
		this.command_result.addArgument(name);
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
		this.context.addEdge(template, source, target);
		this.command_result.addArgument(template);
		this.command_result.addArgument(source);
		this.command_result.addArgument(target);
		break;

// for a select check that all required information is present

		case SELECT:
		if (argument_number<4)
		this.throwMissingArgumentException(command_code, object_code, 4, argument_number);
		else if (argument_number>4)
		this.throwExtraArgumentException(command_code, object_code, 4, argument_number);

		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		value = command.getArgumentAt(3);
		this.context.setEdgeProperty(template, source, target, "select", value);
		this.command_result.addArgument(template);
		this.command_result.addArgument(source);
		this.command_result.addArgument(target);
		this.command_result.addArgument(value);
		break;
		
// for a guard check that all required information is present

		case GUARD:
		if (argument_number<4)
		this.throwMissingArgumentException(command_code, object_code, 4, argument_number);
		else if (argument_number>4)
		this.throwExtraArgumentException(command_code, object_code, 4, argument_number);

		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		value = command.getArgumentAt(3);
		this.context.setEdgeProperty(template, source, target, "guard", value);
		this.command_result.addArgument(template);
		this.command_result.addArgument(source);
		this.command_result.addArgument(target);
		this.command_result.addArgument(value);
		break;

// for a sync check that all required information is present

		case SYNC:
		if (argument_number<4)
		this.throwMissingArgumentException(command_code, object_code, 4, argument_number);
		else if (argument_number>4)
		this.throwExtraArgumentException(command_code, object_code, 4, argument_number);

		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		value = command.getArgumentAt(3);
		this.context.setEdgeProperty(template, source, target, "sync", value);
		this.command_result.addArgument(template);
		this.command_result.addArgument(source);
		this.command_result.addArgument(target);
		this.command_result.addArgument(value);
		break;

// for an assign check that all required information is present

		case ASSIGN:
		if (argument_number<4)
		this.throwMissingArgumentException(command_code, object_code, 4, argument_number);
		else if (argument_number>4)
		this.throwExtraArgumentException(command_code, object_code, 4, argument_number);

		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		value = command.getArgumentAt(3);
		this.context.setEdgeProperty(template, source, target, "assign", value);
		this.command_result.addArgument(template);
		this.command_result.addArgument(source);
		this.command_result.addArgument(target);
		this.command_result.addArgument(value);
		break;

// for a system simply set the corresponding chain

		case SYSTEM:

		if (argument_number<1) 
		this.throwMissingArgumentException(command_code, object_code, 1, argument_number);

		if (argument_number>1) 
		this.throwExtraArgumentException(command_code, object_code, 1, argument_number);

		system = command.getArgumentAt(0);
		this.context.setSystem(system);
		this.command_result.addArgument(system);
		break;

// for any other object code throw a wrong object exception

		default:
			this.throwWrongObjectException (Command.CommandCode.SET, object_code);
	}

	this.command_result.setResultCode(CommandResult.ResultCode.OK);
	return this.command_result;
}

/**
* handle rename commands
* @param command the command to handle
* @return the command result corresponding to this command
* @exception an exception describing the type of error which was encountered
*/
private CommandResult handleRename(Command command) {

// process the command depending on its object code

	Command.CommandCode command_code = command.getCommandCode();
	Command.ObjectCode object_code = command.getObjectCode();
	int argument_number = command.getArgumentNumber();
	this.command_result.clear();
	this.command_result.setCommandCode(command_code);
	this.command_result.setObjectCode(object_code);
	String name = null;
	String new_name = null;
	String template = null;
	String value=null;

	switch (object_code) {

// for a query check that we have its name, its formula and its comment

		case QUERY:
			if (argument_number<2) 
				this.throwMissingArgumentException(command_code, object_code, 2, argument_number);
			else if (argument_number>2)
				this.throwExtraArgumentException(command_code, object_code, 2, argument_number);

		name = command.getArgumentAt(0);
		new_name = command.getArgumentAt(1);
			this.context.setQueryProperty(name, "name", new_name);
			command_result.addArgument(name);
			command_result.addArgument(new_name);
		break;

// for a template check that we have its name

		case TEMPLATE:
			if (argument_number<2) 
				this.throwMissingArgumentException(command_code, object_code, 2, argument_number);
			else if (argument_number>2)
				this.throwExtraArgumentException(command_code, object_code, 2, argument_number);

		name = command.getArgumentAt(0);
		new_name = command.getArgumentAt(1);
			this.context.setTemplateProperty(name, "name", new_name);
			command_result.addArgument(name);
			command_result.addArgument(new_name);
		break;

// for a location check that we have its name and the name of its template

		case LOCATION:
		if (argument_number<3)
		this.throwMissingArgumentException(command_code, object_code, 2, argument_number);
		else if (argument_number>3)
			this.throwExtraArgumentException(command_code, object_code, 2, argument_number);

		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		new_name = command.getArgumentAt(2);
		this.context.setLocationProperty(template, name, "name", new_name);
		command_result.addArgument(template);
			command_result.addArgument(name);
			command_result.addArgument(new_name);
		break;

// for any other object code throw a wrong object exception

		default:
			this.throwWrongObjectException (Command.CommandCode.SET, object_code);
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
	this.command_result.setCommandCode(command_code);
	this.command_result.setObjectCode(object_code);
	String name = null;
	String template = null;
	String source = null;
	String target = null;
	String formula = null;
	String comment = null;
	String declaration = null;
	String parameter = null;
	String system = null;
	String value=null;

	switch (object_code) {

// for a formula check that we have the name of the corresponding query

		case FORMULA:
		if (argument_number<2)
		this.throwMissingArgumentException(command_code, object_code, 2, argument_number);
		else if (argument_number>2)
		this.throwExtraArgumentException(command_code, object_code, 2, argument_number);

				name = command.getArgumentAt(0);
				formula = command.getArgumentAt(1);
				this.context.setQueryProperty(name, "formula", formula);
				this.command_result.addArgument(name);
				this.command_result.addArgument(formula);
		break;

// for a comment check that we have the name of the corresponding query

		case COMMENT:
		if (argument_number<2)
		this.throwMissingArgumentException(command_code, object_code, 2, argument_number);
		else if (argument_number>2)
		this.throwExtraArgumentException(command_code, object_code, 2, argument_number);

				name = command.getArgumentAt(0);
				comment = command.getArgumentAt(1);
				this.context.setQueryProperty(name, "comment", comment);
				this.command_result.addArgument(name);
				this.command_result.addArgument(comment);
		break;

// for declarations check that we have a name and if not return the global ones

		case DECLARATION:
		if (argument_number<1)
		this.throwMissingArgumentException(command_code, object_code, 1, argument_number);
		else if (argument_number>2)
		this.throwExtraArgumentException(command_code, object_code, 2, argument_number);
			else if (argument_number==2) {
				name = command.getArgumentAt(0);
				declaration = command.getArgumentAt(1);
				this.context.setTemplateProperty(name, "declaration",  declaration);
				this.command_result.addArgument(name);
			} else {
				declaration = command.getArgumentAt(0);
				this.context.setGlobalDeclaration (declaration);
		}
		break;

// for parameter check that we have the name of the corresponding template

		case PARAMETER:
		if (argument_number<2)
		this.throwMissingArgumentException(command_code, object_code, 2, argument_number);
		else if (argument_number>2)
		this.throwExtraArgumentException(command_code, object_code, 2, argument_number);

				name = command.getArgumentAt(0);
				parameter = command.getArgumentAt(1);
				this.context.setTemplateProperty(name, "parameter", parameter);
				this.command_result.addArgument(name);
				this.command_result.addArgument(parameter);
		break;

// for an invariant check that we have the name of its location and of its template

		case INVARIANT:
		if (argument_number<3)
		this.throwMissingArgumentException(command_code, object_code, 3, argument_number);
		else if (argument_number>3)
			this.throwExtraArgumentException(command_code, object_code, 3, argument_number);

		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		value = command.getArgumentAt(2);
		this.context.setLocationProperty(template, name, "invariant", value);
this.command_result.addArgument(template);
		this.command_result.addArgument(name);
this.command_result.addArgument(value);
		break;


// for an init label check that we have the name of its location and of its template

		case INIT:
		if (argument_number<2)
		this.throwMissingArgumentException(command_code, object_code, 2, argument_number);
		else if (argument_number>2)
			this.throwExtraArgumentException(command_code, object_code, 2, argument_number);

		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		this.context.setLocationProperty(template, name, "init", true);
this.command_result.addArgument(template);
		this.command_result.addArgument(name);
		break;
		
// for a committed label check that we have the name of its location and of its template

		case COMMITTED:
		if (argument_number<2)
		this.throwMissingArgumentException(command_code, object_code, 2, argument_number);
		else if (argument_number>2)
			this.throwExtraArgumentException(command_code, object_code, 2, argument_number);

		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		this.context.setLocationProperty(template, name, "committed", true);
this.command_result.addArgument(template);
		this.command_result.addArgument(name);
		break;

// for a select check that all required information is present

		case SELECT:
		if (argument_number<4)
		this.throwMissingArgumentException(command_code, object_code, 4, argument_number);
		else if (argument_number>4)
		this.throwExtraArgumentException(command_code, object_code, 4, argument_number);

		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		value = command.getArgumentAt(3);
		this.context.setEdgeProperty(template, source, target, "select", value);
this.command_result.addArgument(template);
		this.command_result.addArgument(source);
		this.command_result.addArgument(target);
		this.command_result.addArgument(value);
		break;
		
// for a guard check that all required information is present

		case GUARD:
		if (argument_number<4)
		this.throwMissingArgumentException(command_code, object_code, 4, argument_number);
		else if (argument_number>4)
		this.throwExtraArgumentException(command_code, object_code, 4, argument_number);

		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		value = command.getArgumentAt(3);
		this.context.setEdgeProperty(template, source, target, "guard", value);
this.command_result.addArgument(template);
		this.command_result.addArgument(source);
		this.command_result.addArgument(target);
		this.command_result.addArgument(value);
		break;

// for a sync check that all required information is present

		case SYNC:
		if (argument_number<4)
		this.throwMissingArgumentException(command_code, object_code, 4, argument_number);
		else if (argument_number>4)
		this.throwExtraArgumentException(command_code, object_code, 4, argument_number);

		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		value = command.getArgumentAt(3);
		this.context.setEdgeProperty(template, source, target, "sync", value);
this.command_result.addArgument(template);
		this.command_result.addArgument(source);
		this.command_result.addArgument(target);
		this.command_result.addArgument(value);
		break;

// for an assign check that all required information is present

		case ASSIGN:
		if (argument_number<4)
		this.throwMissingArgumentException(command_code, object_code, 4, argument_number);
		else if (argument_number>4)
		this.throwExtraArgumentException(command_code, object_code, 4, argument_number);

		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		value = command.getArgumentAt(3);
		this.context.setEdgeProperty(template, source, target, "assign", value);
this.command_result.addArgument(template);
		this.command_result.addArgument(source);
		this.command_result.addArgument(target);
		this.command_result.addArgument(value);
		break;

// for a system simply set the corresponding chain

		case SYSTEM:

		if (argument_number<1) 
		this.throwMissingArgumentException(command_code, object_code, 1, argument_number);

		if (argument_number>1) 
		this.throwExtraArgumentException(command_code, object_code, 1, argument_number);

		system = command.getArgumentAt(0);
		this.context.setSystem(system);
		this.command_result.addArgument(system);
		break;

// for any other object code throw a wrong object exception

		default:
			this.throwWrongObjectException (Command.CommandCode.SET, object_code);
	}

	this.command_result.setResultCode(CommandResult.ResultCode.OK);
	return this.command_result;
}


/**
* handle unset commands
* @param command the command to handle
* @return the command result corresponding to this command
* @exception an exception describing the type of error which was encountered
*/
private CommandResult handleUnset(Command command) {

// process the command depending on its object code

	Command.CommandCode command_code = command.getCommandCode();
	Command.ObjectCode object_code = command.getObjectCode();
	int argument_number = command.getArgumentNumber();
	this.command_result.clear();
	this.command_result.setCommandCode(command_code);
	this.command_result.setObjectCode(object_code);
	String name = null;
	String template = null;
	String source = null;
	String target = null;

	switch (object_code) {

// for nulls check that we have a name and if not return the global ones

		case DECLARATION: if (argument_number>1)
		this.throwExtraArgumentException(command_code, object_code, 1, argument_number);
			else if (argument_number==1) {
				name = command.getArgumentAt(0);
				this.context.setTemplateProperty(name, "declaration",  null);
				this.command_result.addArgument(name);
			} else {
				this.context.setGlobalDeclaration (null);
		}
		break;

// for parameter check that we have the name of the corresponding template

		case PARAMETER:
		if (argument_number<1)
		this.throwMissingArgumentException(command_code, object_code, 1, argument_number);
		else if (argument_number>1)
		this.throwExtraArgumentException(command_code, object_code, 1, argument_number);

				name = command.getArgumentAt(0);
				this.context.setTemplateProperty(name, "parameter", null);
		this.command_result.addArgument(name);
		break;

// for an invariant check that we have the name of its location and of its template

		case INVARIANT:
		if (argument_number<2)
		this.throwMissingArgumentException(command_code, object_code, 2, argument_number);
		else if (argument_number>2)
			this.throwExtraArgumentException(command_code, object_code, 2, argument_number);

		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		this.context.setLocationProperty(template, name, "invariant", null);
		this.command_result.addArgument(template);
		this.command_result.addArgument(name);
		break;

// for an init label check that we have the name of its location and of its template

		case INIT:
		if (argument_number<2)
		this.throwMissingArgumentException(command_code, object_code, 2, argument_number);
		else if (argument_number>2)
			this.throwExtraArgumentException(command_code, object_code, 2, argument_number);

		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		this.context.setLocationProperty(template, name, "init", null);
		this.command_result.addArgument(template);
		this.command_result.addArgument(name);
		break;
		
// for a committed label check that we have the name of its location and of its template

		case COMMITTED:
		if (argument_number<2)
		this.throwMissingArgumentException(command_code, object_code, 2, argument_number);
		else if (argument_number>2)
			this.throwExtraArgumentException(command_code, object_code, 2, argument_number);

		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		this.context.setLocationProperty(template, name, "committed", null);
		this.command_result.addArgument(template);
		this.command_result.addArgument(name);
		break;

// for a select check that all required information is present

		case SELECT:
		if (argument_number<3)
		this.throwMissingArgumentException(command_code, object_code, 3, argument_number);
		else if (argument_number>3)
		this.throwExtraArgumentException(command_code, object_code, 3, argument_number);

		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		this.context.setEdgeProperty(template, source, target, "select", null);
		this.command_result.addArgument(template);
		this.command_result.addArgument(source);
		this.command_result.addArgument(target);
		break;
		
// for a guard check that all required information is present

		case GUARD:
		if (argument_number<3)
		this.throwMissingArgumentException(command_code, object_code, 3, argument_number);
		else if (argument_number>3)
		this.throwExtraArgumentException(command_code, object_code, 3, argument_number);

		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		this.context.setEdgeProperty(template, source, target, "guard", null);
		this.command_result.addArgument(template);
		this.command_result.addArgument(source);
		this.command_result.addArgument(target);
		break;

// for a sync check that all required information is present

		case SYNC:
		if (argument_number<3)
		this.throwMissingArgumentException(command_code, object_code, 3, argument_number);
		else if (argument_number>3)
		this.throwExtraArgumentException(command_code, object_code, 3, argument_number);

		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		this.context.setEdgeProperty(template, source, target, "sync", null);
		this.command_result.addArgument(template);
		this.command_result.addArgument(source);
		this.command_result.addArgument(target);
		break;

// for an assign check that all required information is present

		case ASSIGN:
		if (argument_number<3)
		this.throwMissingArgumentException(command_code, object_code, 3, argument_number);
		else if (argument_number>3)
		this.throwExtraArgumentException(command_code, object_code, 3, argument_number);

		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		this.context.setEdgeProperty(template, source, target, "assign", null);
		this.command_result.addArgument(template);
		this.command_result.addArgument(source);
		this.command_result.addArgument(target);
		break;

// for a null simply set the corresponding chain

		case SYSTEM:

		if (argument_number>0) 
		this.throwExtraArgumentException(command_code, object_code, 0, argument_number);

		this.context.setSystem(null);
		break;

// for any other object code throw a wrong object exception

		default:
			this.throwWrongObjectException (Command.CommandCode.UNSET, object_code);
	}

	this.command_result.setResultCode(CommandResult.ResultCode.OK);
	return this.command_result;
}

/**
* handle undo commands
* @param command the command to handle
* @return the command result for this command
*/
private CommandResult handleUndo (Command command) {
	this.context.undo();
	this.command_result.clear();
	this.command_result.setResultCode(CommandResult.ResultCode.OK);
	return this.command_result;
}

/**
* handle undo commands
* @param command the command to handle
* @return the command result for this command
*/
private CommandResult handleRedo (Command command) {
	this.context.redo();
	this.command_result.clear();
	this.command_result.setResultCode(CommandResult.ResultCode.OK);
	return this.command_result;
}
}