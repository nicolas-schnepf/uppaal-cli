package org.uppaal.cli.handlers;

import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.Template;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.QueryList;
import org.uppaal.cli.commands.Command.OperationCode;
import com.uppaal.model.core2.Query;
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
* concrete class implementing an export handler
* supporting all possible export commands under mode control
*/

public class ExportHandler extends OperationHandler {
public ExportHandler (Context context) {
	super(context, OperationCode.EXPORT);
	this.accepted_objects = new HashSet<ObjectCode>();
	this.accepted_objects.add(ObjectCode.DOCUMENT);
	this.accepted_objects.add(ObjectCode.QUERIES);
}

/**
* handle export commands
* @param command the command to handle
* @return the command result corresponding to this command
* @exception an exception describing the type of error which was encountered
*/
public CommandResult handle(Command command) throws MalformedURLException, IOException {

// check that the command contains exactly one argument

	Command.OperationCode operation_code= command.getOperationCode();
	Command.ObjectCode object_code = command.getObjectCode();
	int argument_number = command.getArgumentNumber();

	if (argument_number<1)
		this.throwMissingArgumentException(operation_code, object_code, 1, argument_number);
	else if (argument_number>1)
		this.throwExtraArgumentException (operation_code, object_code, 1, argument_number);

// process the command depending on its object code

	String filename = command.getArgumentAt(0);
	int index = filename.length()-1;
	while (filename.charAt(index)!='.' && index>0) index --;
	String extension = filename.substring(index+1);

	switch (command.getObjectCode()) {
		case DOCUMENT:
		if (!extension.equals("xta") && !extension.equals("xml")) 
			this.throwWrongExtensionException (operation_code, object_code, extension);
		this.context.saveDocument(filename);
		break;

		case QUERIES:
		if (!extension.equals("q")) 
			this.throwWrongExtensionException (operation_code, object_code, extension);
		this.context.saveQueries(filename);
		break;
	}

	return this.command_result;
}
}