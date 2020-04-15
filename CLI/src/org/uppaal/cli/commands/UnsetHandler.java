package org.uppaal.cli.commands;

import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.Template;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.QueryList;
import com.uppaal.model.core2.Query;

import org.uppaal.cli.commands.AbstractHandler;
import org.uppaal.cli.commands.ModeHandler;
import org.uppaal.cli.enumerations.ModeCode;
import org.uppaal.cli.commands.Handler;
import org.uppaal.cli.enumerations.OperationCode;
import org.uppaal.cli.enumerations.ObjectCode;
import org.uppaal.cli.enumerations.ResultCode;
import org.uppaal.cli.commands.CommandResult;
import org.uppaal.cli.commands.Command;
import org.uppaal.cli.context.Context;

import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.HashSet;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
* concrete class implementing an unset handler
* supporting all possible unset commands under mode control
*/

public class UnsetHandler extends AbstractHandler {
public UnsetHandler (Context context) {
	super(context, OperationCode.UNSET);
}

/**
* handle unset commands
* @param command the command to handle
* @return the command result corresponding to this command
* @exception an exception describing the type of error which was encountered
*/
public CommandResult handle(Command command) {

// process the command depending on its object code

	OperationCode operation_code = command.getOperationCode();
	ObjectCode object_code = command.getObjectCode();
	int argument_number = command.getArgumentNumber();
	this.command_result.clear();
	this.command_result.setOperationCode(operation_code);
	this.command_result.setObjectCode(object_code);
	String name = null;
	String template = null;
	String source = null;
	String target = null;

	switch (object_code) {

		case DOCUMENT:
		this.context.getModelExpert().clearDocument();
		break;

		case QUERIES:
		this.checkMode(command, ModeCode.EDITOR);
			this.context.getQueryExpert().clearQueries();
		break;

// for templates simply return the list of all templates names and parameters

		case TEMPLATES:
		this.checkMode(command, ModeCode.EDITOR);
		this.checkArgumentNumber(command, 0);
			this.context.getTemplateExpert().clearTemplates();
		break;

// for declarations check that we have a name and if not return the global ones

		case DECLARATION:
			if (argument_number>1)
		this.checkMode(command, ModeCode.EDITOR);
		this.checkArgumentNumber(command, 1);
			if (argument_number==1) {
				name = command.getArgumentAt(0);
				this.context.getTemplateExpert().setTemplateProperty(name, "declaration",  null);
				this.command_result.addArgument(name);
			} else
				this.context.getModelExpert().setDocumentProperty("declaration", null);
		break;

// for a query check that we have its name

		case QUERY:
		this.checkMode(command, ModeCode.EDITOR);
		this.checkArgumentNumber(command, 1, 1);
		name = command.getArgumentAt(0);
			this.context.getQueryExpert().removeQuery(name);
			this.command_result.addArgument(name);
		break;

// for a template check that we have its name

		case TEMPLATE:
		this.checkMode(command, ModeCode.EDITOR);
		this.checkArgumentNumber(command, 1, 1);
		name = command.getArgumentAt(0);
			this.context.getTemplateExpert().removeTemplate(name);
			this.command_result.addArgument(name);
		break;

// for a parameter check that we have the name of its template

		case PARAMETER:
		this.checkMode(command, ModeCode.EDITOR);
		this.checkArgumentNumber(command, 1, 1);
		name = command.getArgumentAt(0);
			this.context.getTemplateExpert().setTemplateProperty(name, "parameter", null);
			this.command_result.addArgument(name);
		break;

// for a location check that we have its name and the name of its template

		case LOCATION:
		this.checkMode(command, ModeCode.EDITOR);
		this.checkArgumentNumber(command, 2, 2);
		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		this.context.getLocationExpert().removeLocation(template, name);
		this.command_result.addArgument(template);
			this.command_result.addArgument(name);
		break;

// for an invariant check that we have the name of its location and of its template

		case INVARIANT:
		this.checkMode(command, ModeCode.EDITOR);
		this.checkArgumentNumber(command, 2, 2);
		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		this.context.getLocationExpert().setLocationProperty(template, name, "invariant", null);
		this.command_result.addArgument(template);
			this.command_result.addArgument(name);
		break;


// for an init label check that we have the name of its location and of its template

		case INIT:
		this.checkMode(command, ModeCode.EDITOR);
		this.checkArgumentNumber(command, 2, 2);
		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		this.context.getLocationExpert().setLocationProperty(template, name, "init", null);
		this.command_result.addArgument(template);
		this.command_result.addArgument(name);
		break;
		
// for a committed label check that we have the name of its location and of its template

		case COMMITTED:
		this.checkMode(command, ModeCode.EDITOR);
		this.checkArgumentNumber(command, 2, 2);
		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		this.context.getLocationExpert().setLocationProperty(template, name, "committed", null);
		this.command_result.addArgument(template);
		this.command_result.addArgument(name);
		break;

// for an edge check that we have the name of its source, its target and its template

		case EDGE:
		this.checkMode(command, ModeCode.EDITOR);
		this.checkArgumentNumber(command, 3, 3);
		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		this.context.getEdgeExpert().removeEdge(template, source, target);
		this.command_result.addArgument(template);
			this.command_result.addArgument(source);
			this.command_result.addArgument(target);
		break;

// for a select check that all required information is present

		case SELECT:
		this.checkMode(command, ModeCode.EDITOR);
		this.checkArgumentNumber(command, 3, 3);
		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		this.context.getEdgeExpert().setEdgeProperty(template, source, target, "select", null);
		this.command_result.addArgument(template);
			this.command_result.addArgument(source);
			this.command_result.addArgument(target);
		break;
		
// for a guard check that all required information is present

		case GUARD:
		this.checkMode(command, ModeCode.EDITOR);
		this.checkArgumentNumber(command, 3, 3);
		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		this.context.getEdgeExpert().setEdgeProperty(template, source, target, "guard", null);
		this.command_result.addArgument(template);
			this.command_result.addArgument(source);
			this.command_result.addArgument(target);
		break;

// for a sync check that all required information is present

		case SYNC:
		this.checkMode(command, ModeCode.EDITOR);
		this.checkArgumentNumber(command, 3, 3);
		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		this.context.getEdgeExpert().setEdgeProperty(template, source, target, "sync", null);
		this.command_result.addArgument(template);
			this.command_result.addArgument(source);
			this.command_result.addArgument(target);
		break;

// for an assign check that all required information is present

		case ASSIGN:
		this.checkMode(command, ModeCode.EDITOR);
		this.checkArgumentNumber(command, 3, 3);
		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		this.context.getEdgeExpert().setEdgeProperty(template, source, target, "assign", null);
		this.command_result.addArgument(template);
			this.command_result.addArgument(source);
			this.command_result.addArgument(target);
		break;

// for a system simply get the corresponding chain and return it

		case SYSTEM:
		this.checkMode(command, ModeCode.EDITOR);
		this.checkArgumentNumber(command, 0);
		this.context.getModelExpert().setDocumentProperty("system", null);
		break;

// for any other object code throw a wrong object exception

		default:
			this.throwWrongObjectException (OperationCode.UNSET, object_code);
	}

	this.command_result.setResultCode(ResultCode.OK);
	return this.command_result;
}

@Override
public boolean acceptMode (ModeCode mode) {
	switch(mode) {
		case EDITOR:
		return true;

		default:
		return false;
	}
}
}
