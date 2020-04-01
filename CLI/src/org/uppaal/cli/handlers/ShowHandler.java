package org.uppaal.cli.handlers;

import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.Template;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.QueryList;
import com.uppaal.model.core2.Query;
import org.uppaal.cli.commands.Command.OperationCode;
import org.uppaal.cli.commands.Command.ObjectCode;
import org.uppaal.cli.commands.CommandResult;
import org.uppaal.cli.commands.Command;
import org.uppaal.cli.commands.Context;

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

public class ShowHandler extends OperationHandler {

public ShowHandler (Context context) {
	super(context, OperationCode.SHOW);
	this.accepted_objects = new HashSet<ObjectCode>();
	this.accepted_objects.add(ObjectCode.QUERIES);
	this.accepted_objects.add(ObjectCode.TEMPLATES);
	this.accepted_objects.add(ObjectCode.DECLARATION);
	this.accepted_objects.add(ObjectCode.QUERY);
	this.accepted_objects.add(ObjectCode.TEMPLATE);
	this.accepted_objects.add(ObjectCode.LOCATION);
	this.accepted_objects.add(ObjectCode.EDGE);
	this.accepted_objects.add(ObjectCode.SYSTEM);
}

/**
* handle show commands
* @param command the command to handle
* @return the command result corresponding to this command
* @exception an exception describing the type of error which was encountered
*/
public CommandResult handle(Command command) {

// process the command depending on its object code

	Command.OperationCode operation_code = command.getOperationCode();
	Command.ObjectCode object_code = command.getObjectCode();
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
		if (argument_number>0)
		this.throwExtraArgumentException(operation_code, object_code, 0, argument_number);
		LinkedList<String> names = this.context.showQueries();
		for (String arg: names) this.command_result.addArgument(arg);
		break;

		

// for templates simply return the list of all templates names and parameters

		case TEMPLATES:
			if (argument_number>0)
				this.throwExtraArgumentException(operation_code, object_code, 0, argument_number);
			LinkedList<String> headers= this.context.getTemplateHeaders();
			for (String header: headers) this.command_result.addArgument(header);
		break;

// for declarations check that we have a name and if not return the global ones

		case DECLARATION:
			if (argument_number>1)
				this.throwExtraArgumentException(operation_code, object_code, 1, argument_number);
			else if (argument_number==1) {
				name = command.getArgumentAt(0);
				this.command_result.addArgument(this.context.getTemplateProperty(name, "declaration"));
			} else
				this.command_result.addArgument(this.context.getGlobalDeclaration());
		break;

// for a query check that we have its name

		case QUERY:
			if (argument_number==0) 
				this.throwMissingArgumentException(operation_code, object_code, 1, 0);
			else if (argument_number>1)
				this.throwExtraArgumentException(operation_code, object_code, 1, argument_number);

		name = command.getArgumentAt(0);
			this.command_result.addArgument(this.context.showQuery(name));
		break;

// for a template check that we have its name

		case TEMPLATE:
			if (argument_number==0) 
				this.throwMissingArgumentException(operation_code, object_code, 1, 0);
			else if (argument_number>1)
				this.throwExtraArgumentException(operation_code, object_code, 1, argument_number);

		name = command.getArgumentAt(0);
			this.command_result.addArgument(this.context.showTemplate(name));
		break;

// for a location check that we have its name and the name of its template

		case LOCATION:
		if (argument_number<2)
		this.throwMissingArgumentException(operation_code, object_code, 2, argument_number);
		else if (argument_number>2)
			this.throwExtraArgumentException(operation_code, object_code, 2, argument_number);

		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		this.command_result.addArgument(this.context.showLocation(template, name));
		break;

// for an edge check that we have the name of its source, its target and its template

		case EDGE:
		if (argument_number<3)
		this.throwMissingArgumentException(operation_code, object_code, 3, argument_number);
		else if (argument_number>3)
		this.throwExtraArgumentException(operation_code, object_code, 3, argument_number);

		template = command.getArgumentAt(0);
		source = command.getArgumentAt(1);
		target = command.getArgumentAt(2);
		String description = this.context.showEdge(template, source, target);
		this.command_result.addArgument(description);
		break;

// for a system simply get the corresponding chain and return it

		case SYSTEM:
		if (argument_number>0) 
		this.throwExtraArgumentException(operation_code, object_code, 0, argument_number);
		this.command_result.addArgument(this.context.getSystem());
		break;

// for any other object code throw a wrong object exception

		default:
			this.throwWrongObjectException (Command.OperationCode.SHOW, object_code);
	}

	this.command_result.setResultCode(CommandResult.ResultCode.OK);
	return this.command_result;
}
}
