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
* concrete class implementing a reset handler
* supporting all possible reset commands under mode control
*/

public class ResetHandler extends AbstractHandler {
public ResetHandler (Context context) {
	super(context, OperationCode.RESET);
}
/**
* handle rename commands
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
	String new_name = null;
	String template = null;
	String value=null;

	switch (object_code) {

// for a query check that we have its name, its formula and its comment

		case QUERY:
			this.checkMode(command, ModeCode.EDITOR);
		this.checkArgumentNumber(command, 2, 2);
		name = command.getArgumentAt(0);
		new_name = command.getArgumentAt(1);
			this.context.getQueryExpert().setQueryProperty(name, "name", new_name);
			command_result.addArgument(name);
			command_result.addArgument(new_name);
		break;

// for a template check that we have its name

		case TEMPLATE:
			this.checkMode(command, ModeCode.EDITOR);
		this.checkArgumentNumber(command, 2, 2);
		name = command.getArgumentAt(0);
		new_name = command.getArgumentAt(1);
			this.context.getTemplateExpert().setTemplateProperty(name, "name", new_name);
			command_result.addArgument(name);
			command_result.addArgument(new_name);
		break;

// for a location check that we have its name and the name of its template

		case LOCATION:
		this.checkMode(command, ModeCode.EDITOR);
		this.checkArgumentNumber(command, 3, 3);
		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		new_name = command.getArgumentAt(2);
		this.context.getLocationExpert().setLocationProperty(template, name, "name", new_name);
		command_result.addArgument(template);
			command_result.addArgument(name);
			command_result.addArgument(new_name);
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
		return true;

		default:
		return false;
	}
}
}
