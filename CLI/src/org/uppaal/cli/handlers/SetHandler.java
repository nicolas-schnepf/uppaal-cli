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
* concrete class implementing a set handler
* supporting all possible set commands under mode control
*/

public class SetHandler extends OperationHandler {
public SetHandler (Context context) {
	super(context, OperationCode.SET);
	this.accepted_objects = new HashSet<ObjectCode>();
	this.accepted_objects.add(ObjectCode.FORMULA);
	this.accepted_objects.add(ObjectCode.COMMENT);
	this.accepted_objects.add(ObjectCode.DECLARATION);
	this.accepted_objects.add(ObjectCode.PARAMETER);
	this.accepted_objects.add(ObjectCode.INVARIANT);
	this.accepted_objects.add(ObjectCode.INIT);
	this.accepted_objects.add(ObjectCode.COMMITTED);
	this.accepted_objects.add(ObjectCode.SELECT);
	this.accepted_objects.add(ObjectCode.GUARD);
	this.accepted_objects.add(ObjectCode.SYNC);
	this.accepted_objects.add(ObjectCode.ASSIGN);
	this.accepted_objects.add(ObjectCode.SYSTEM);
}

/**
* handle set commands
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
		this.throwMissingArgumentException(operation_code, object_code, 2, argument_number);
		else if (argument_number>2)
		this.throwExtraArgumentException(operation_code, object_code, 2, argument_number);

				name = command.getArgumentAt(0);
				formula = command.getArgumentAt(1);
				this.context.setQueryProperty(name, "formula", formula);
				this.command_result.addArgument(name);
				this.command_result.addArgument(formula);
		break;

// for a comment check that we have the name of the corresponding query

		case COMMENT:
		if (argument_number<2)
		this.throwMissingArgumentException(operation_code, object_code, 2, argument_number);
		else if (argument_number>2)
		this.throwExtraArgumentException(operation_code, object_code, 2, argument_number);

				name = command.getArgumentAt(0);
				comment = command.getArgumentAt(1);
				this.context.setQueryProperty(name, "comment", comment);
				this.command_result.addArgument(name);
				this.command_result.addArgument(comment);
		break;

// for declarations check that we have a name and if not return the global ones

		case DECLARATION:
		if (argument_number<1)
		this.throwMissingArgumentException(operation_code, object_code, 1, argument_number);
		else if (argument_number>2)
		this.throwExtraArgumentException(operation_code, object_code, 2, argument_number);
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
		this.throwMissingArgumentException(operation_code, object_code, 2, argument_number);
		else if (argument_number>2)
		this.throwExtraArgumentException(operation_code, object_code, 2, argument_number);

				name = command.getArgumentAt(0);
				parameter = command.getArgumentAt(1);
				this.context.setTemplateProperty(name, "parameter", parameter);
				this.command_result.addArgument(name);
				this.command_result.addArgument(parameter);
		break;

// for an invariant check that we have the name of its location and of its template

		case INVARIANT:
		if (argument_number<3)
		this.throwMissingArgumentException(operation_code, object_code, 3, argument_number);
		else if (argument_number>3)
			this.throwExtraArgumentException(operation_code, object_code, 3, argument_number);

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
		this.throwMissingArgumentException(operation_code, object_code, 2, argument_number);
		else if (argument_number>2)
			this.throwExtraArgumentException(operation_code, object_code, 2, argument_number);

		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		this.context.setLocationProperty(template, name, "init", true);
this.command_result.addArgument(template);
		this.command_result.addArgument(name);
		break;
		
// for a committed label check that we have the name of its location and of its template

		case COMMITTED:
		if (argument_number<2)
		this.throwMissingArgumentException(operation_code, object_code, 2, argument_number);
		else if (argument_number>2)
			this.throwExtraArgumentException(operation_code, object_code, 2, argument_number);

		template = command.getArgumentAt(0);
		name = command.getArgumentAt(1);
		this.context.setLocationProperty(template, name, "committed", true);
this.command_result.addArgument(template);
		this.command_result.addArgument(name);
		break;

// for a select check that all required information is present

		case SELECT:
		if (argument_number<4)
		this.throwMissingArgumentException(operation_code, object_code, 4, argument_number);
		else if (argument_number>4)
		this.throwExtraArgumentException(operation_code, object_code, 4, argument_number);

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
		this.throwMissingArgumentException(operation_code, object_code, 4, argument_number);
		else if (argument_number>4)
		this.throwExtraArgumentException(operation_code, object_code, 4, argument_number);

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
		this.throwMissingArgumentException(operation_code, object_code, 4, argument_number);
		else if (argument_number>4)
		this.throwExtraArgumentException(operation_code, object_code, 4, argument_number);

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
		this.throwMissingArgumentException(operation_code, object_code, 4, argument_number);
		else if (argument_number>4)
		this.throwExtraArgumentException(operation_code, object_code, 4, argument_number);

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
		this.throwMissingArgumentException(operation_code, object_code, 1, argument_number);

		if (argument_number>1) 
		this.throwExtraArgumentException(operation_code, object_code, 1, argument_number);

		system = command.getArgumentAt(0);
		this.context.setSystem(system);
		this.command_result.addArgument(system);
		break;

// for any other object code throw a wrong object exception

		default:
			this.throwWrongObjectException (Command.OperationCode.SET, object_code);
	}

	this.command_result.setResultCode(CommandResult.ResultCode.OK);
	return this.command_result;
}
}
