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
* concrete class implementing a show handler
* supporting all possible show commands under mode control
*/

public class ShowHandler extends AbstractHandler {

public ShowHandler (Context context) {
	super(context, OperationCode.SHOW);
}

/**
* handle show commands
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
		case QUERIES:
		this.checkArgumentNumber(command, 0);
		LinkedList<String> names = this.context.getQueryExpert().showQueries();
		for (String arg: names) this.command_result.addArgument(arg);
		break;

		

// for templates simply return the list of all templates names and parameters

		case TEMPLATES:
		this.checkArgumentNumber(command, 0);
			LinkedList<String> templates = this.context.getTemplateExpert().showTemplates();
			for (String temp: templates) this.command_result.addArgument(temp);
		break;

// for declarations check that we have a name and if not return the global ones

		case DECLARATION:
			this.checkArgumentNumber(command, 1);
			if (argument_number==1) {
				name = command.getArgumentAt(0);
				String declaration = this.context.getTemplateExpert().getTemplateProperty(name, "declaration");
				this.command_result.addArgument(declaration);
			} else
				this.command_result.addArgument(this.context.getModelExpert().getDocumentProperty("declaration"));
		break;

// for a query check that we have its name

		case QUERY:
		this.checkArgumentNumber(command, 1, 1);
		name = command.getArgumentAt(0);
			this.command_result.addArgument(this.context.getQueryExpert().showQuery(name));
		break;

// for a template check that we have its name

		case TEMPLATE:
		this.checkArgumentNumber(command, 1, 1);
		name = command.getArgumentAt(0);
			this.command_result.addArgument(this.context.getTemplateExpert().showTemplate(name));
		break;

// for a location check that we have its name and the name of its template

		case LOCATION:
		this.checkArgumentNumber(command, 2, 2);

		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		this.command_result.addArgument(this.context.getLocationExpert().showLocation(template, name));
		break;

// for an edge check that we have the name of its source, its target and its template

		case EDGE:
		this.checkArgumentNumber(command, 3, 3);
		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		String description = this.context.getEdgeExpert().showEdge(template, source, target);
		this.command_result.addArgument(description);
		break;

// for a system simply get the corresponding chain and return it

		case SYSTEM:
		this.checkArgumentNumber(command, 0);
		this.command_result.addArgument(this.context.getModelExpert().getDocumentProperty("system"));
		break;

// for any other object code throw a wrong object exception

		default:
			this.throwWrongObjectException (OperationCode.SHOW, object_code);
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
