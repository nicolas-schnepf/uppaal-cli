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
* concrete class implementing a rename handler
* supporting all possible rename commands under mode control
*/

public class RenameHandler extends OperationHandler {
public RenameHandler (Context context) {
	super(context, OperationCode.RENAME);
	this.accepted_objects = new HashSet<ObjectCode>();
	this.accepted_objects.add(ObjectCode.QUERY);
	this.accepted_objects.add(ObjectCode.TEMPLATE);
	this.accepted_objects.add(ObjectCode.LOCATION);
}
/**
* handle rename commands
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
	String new_name = null;
	String template = null;
	String value=null;

	switch (object_code) {

// for a query check that we have its name, its formula and its comment

		case QUERY:
			if (argument_number<2) 
				this.throwMissingArgumentException(operation_code, object_code, 2, argument_number);
			else if (argument_number>2)
				this.throwExtraArgumentException(operation_code, object_code, 2, argument_number);

		name = command.getArgumentAt(0);
		new_name = command.getArgumentAt(1);
			this.context.setQueryProperty(name, "name", new_name);
			command_result.addArgument(name);
			command_result.addArgument(new_name);
		break;

// for a template check that we have its name

		case TEMPLATE:
			if (argument_number<2) 
				this.throwMissingArgumentException(operation_code, object_code, 2, argument_number);
			else if (argument_number>2)
				this.throwExtraArgumentException(operation_code, object_code, 2, argument_number);

		name = command.getArgumentAt(0);
		new_name = command.getArgumentAt(1);
			this.context.setTemplateProperty(name, "name", new_name);
			command_result.addArgument(name);
			command_result.addArgument(new_name);
		break;

// for a location check that we have its name and the name of its template

		case LOCATION:
		if (argument_number<3)
		this.throwMissingArgumentException(operation_code, object_code, 2, argument_number);
		else if (argument_number>3)
			this.throwExtraArgumentException(operation_code, object_code, 2, argument_number);

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
			this.throwWrongObjectException (Command.OperationCode.SET, object_code);
	}

	this.command_result.setResultCode(CommandResult.ResultCode.OK);
	return this.command_result;
}
}