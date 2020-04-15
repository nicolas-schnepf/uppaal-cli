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
* concrete class implementing a set handler
* supporting all possible set commands under mode control
*/

public class SetHandler extends AbstractHandler {
public SetHandler (Context context) {
	super(context, OperationCode.SET);
}

/**
* handle set commands
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
	String formula = null;
	String comment = null;
	String declaration = null;
	String parameter = null;
	String system = null;
	String value=null;

	switch (object_code) {

// for a query check that we have its name, its formula and its comment

		case QUERY:
		this.checkArgumentNumber(command, 3, 3);
		this.checkMode(command, ModeCode.EDITOR);
		name = command.getArgumentAt(0);
		formula = command.getArgumentAt(1);
		comment = command.getArgumentAt(2);
			this.context.getQueryExpert().addQuery(name, formula, comment);
this.command_result.addArgument(name);
		break;

// for a formula check that we have the name of the corresponding query

		case FORMULA:
		this.checkArgumentNumber(command, 2, 2);
		this.checkMode(command, ModeCode.EDITOR);
				name = command.getArgumentAt(0);
				formula = command.getArgumentAt(1);
				this.context.getQueryExpert().setQueryProperty(name, "formula", formula);
				this.command_result.addArgument(name);
				this.command_result.addArgument(formula);
		break;

// for a comment check that we have the name of the corresponding query

		case COMMENT:
		this.checkArgumentNumber(command, 2, 2);
		this.checkMode(command, ModeCode.EDITOR);
				name = command.getArgumentAt(0);
				comment = command.getArgumentAt(1);
				this.context.getQueryExpert().setQueryProperty(name, "comment", comment);
				this.command_result.addArgument(name);
				this.command_result.addArgument(comment);
		break;

// for a template check that we have its name

		case TEMPLATE:
		this.checkArgumentNumber(command, 1, 2);
		this.checkMode(command, ModeCode.EDITOR);
		name = command.getArgumentAt(0);
		if (argument_number==2) parameter = command.getArgumentAt(1);
			this.context.getTemplateExpert().addTemplate(name, parameter);
			this.command_result.addArgument(name);
		break;

// for declarations check that we have a name and if not return the global ones

		case DECLARATION:
		this.checkArgumentNumber(command, 1, 2);
		this.checkMode(command, ModeCode.EDITOR);
		if (argument_number==2) {
				name = command.getArgumentAt(0);
				declaration = command.getArgumentAt(1);
				this.context.getTemplateExpert().setTemplateProperty(name, "declaration",  declaration);
				this.command_result.addArgument(name);
			} else {
				declaration = command.getArgumentAt(0);
				this.context.getModelExpert().setDocumentProperty("declaration", declaration);
		}
		break;

// for parameter check that we have the name of the corresponding template

		case PARAMETER:
		this.checkArgumentNumber(command, 2, 2);
		this.checkMode(command, ModeCode.EDITOR);
				name = command.getArgumentAt(0);
				parameter = command.getArgumentAt(1);
				this.context.getTemplateExpert().setTemplateProperty(name, "parameter", parameter);
				this.command_result.addArgument(name);
				this.command_result.addArgument(parameter);
		break;


// for a location check that we have its name and the name of its template

		case LOCATION:
	this.checkArgumentNumber(command, 2, 2);
		this.checkMode(command, ModeCode.EDITOR);
		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		this.context.getLocationExpert().addLocation(template, name);
		this.command_result.addArgument(template);
		this.command_result.addArgument(name);
		break;

// for an invariant check that we have the name of its location and of its template

		case INVARIANT:
		this.checkArgumentNumber(command, 3, 3);
		this.checkMode(command, ModeCode.EDITOR);
		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		value = command.getArgumentAt(2);
		this.context.getLocationExpert().setLocationProperty(template, name, "invariant", value);
this.command_result.addArgument(template);
		this.command_result.addArgument(name);
this.command_result.addArgument(value);
		break;

// for an init label check that we have the name of its location and of its template

		case INIT:
		this.checkArgumentNumber(command, 2, 2);
		this.checkMode(command, ModeCode.EDITOR);
		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		this.context.getLocationExpert().setLocationProperty(template, name, "init", true);
this.command_result.addArgument(template);
		this.command_result.addArgument(name);
		break;
		
// for a committed label check that we have the name of its location and of its template

		case COMMITTED:
		this.checkArgumentNumber(command, 2, 2);
		this.checkMode(command, ModeCode.EDITOR);
		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		this.context.getLocationExpert().setLocationProperty(template, name, "committed", true);
this.command_result.addArgument(template);
		this.command_result.addArgument(name);
		break;

// for an edge check that we have the name of its source, its target and its template

		case EDGE:
		this.checkArgumentNumber(command, 3, 3);
		this.checkMode(command, ModeCode.EDITOR);
		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		this.context.getEdgeExpert().addEdge(template, source, target);
		this.command_result.addArgument(template);
		this.command_result.addArgument(source);
		this.command_result.addArgument(target);
		break;

// for a select check that all required information is present

		case SELECT:
		this.checkArgumentNumber(command, 4, 4);
		this.checkMode(command, ModeCode.EDITOR);
		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		value = command.getArgumentAt(3);
		this.context.getEdgeExpert().setEdgeProperty(template, source, target, "select", value);
this.command_result.addArgument(template);
		this.command_result.addArgument(source);
		this.command_result.addArgument(target);
		this.command_result.addArgument(value);
		break;
		
// for a guard check that all required information is present

		case GUARD:
		this.checkArgumentNumber(command, 4, 4);
		this.checkMode(command, ModeCode.EDITOR);
		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		value = command.getArgumentAt(3);
		this.context.getEdgeExpert().setEdgeProperty(template, source, target, "guard", value);
this.command_result.addArgument(template);
		this.command_result.addArgument(source);
		this.command_result.addArgument(target);
		this.command_result.addArgument(value);
		break;

// for a sync check that all required information is present

		case SYNC:
		this.checkArgumentNumber(command, 4, 4);
		this.checkMode(command, ModeCode.EDITOR);
		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		value = command.getArgumentAt(3);
		this.context.getEdgeExpert().setEdgeProperty(template, source, target, "sync", value);
this.command_result.addArgument(template);
		this.command_result.addArgument(source);
		this.command_result.addArgument(target);
		this.command_result.addArgument(value);
		break;

// for an assign check that all required information is present

		case ASSIGN:
		this.checkArgumentNumber(command, 4, 4);
		this.checkMode(command, ModeCode.EDITOR);
		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		value = command.getArgumentAt(3);
		this.context.getEdgeExpert().setEdgeProperty(template, source, target, "assign", value);
this.command_result.addArgument(template);
		this.command_result.addArgument(source);
		this.command_result.addArgument(target);
		this.command_result.addArgument(value);
		break;

// for a system simply set the corresponding chain

		case SYSTEM:
		this.checkArgumentNumber(command, 1, 1);
		this.checkMode(command, ModeCode.EDITOR);
		system = command.getArgumentAt(0);
		this.context.getModelExpert().setDocumentProperty("system", system);
		this.command_result.addArgument(system);
		break;

// for any other object code throw a wrong object exception

		default:
			this.throwWrongObjectException (OperationCode.SET, object_code);
	}

	this.command_result.setResultCode(ResultCode.OK);
	return this.command_result;
}

@Override
public boolean acceptMode (ModeCode mode) {
	switch(mode) {
		case EDITOR:
		case SYMBOLIC_SIMULATOR:
		case CONCRETE_SIMULATOR:
		return true;

		default:
		return false;
	}
}
}
