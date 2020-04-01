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
* concrete class implementing a clear handler
* supporting all possible clear commands under mode control
*/

public class ClearHandler extends OperationHandler {
public ClearHandler (Context context) {
	super(context, OperationCode.CLEAR);
	this.accepted_objects = new HashSet<ObjectCode>();
	this.accepted_objects.add(ObjectCode.DOCUMENT);
	this.accepted_objects.add(ObjectCode.QUERIES);
}

/**
* handle clear commands
* @param command the command to handle
* @return the command result corresponding to this command
*/
public CommandResult handle(Command command) {

// check that the command contains exactly one argument

	Command.OperationCode operation_code= command.getOperationCode();
	Command.ObjectCode object_code = command.getObjectCode();
	int argument_number = command.getArgumentNumber();

 if (argument_number>0)
		this.throwExtraArgumentException (operation_code, object_code, 0, argument_number);

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
}
