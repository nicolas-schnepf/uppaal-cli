package org.uppaal.cli.commands;

import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.Template;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.QueryList;

import org.uppaal.cli.commands.AbstractHandler;
import org.uppaal.cli.commands.ModeHandler;
import org.uppaal.cli.enumerations.ModeCode;
import org.uppaal.cli.commands.Handler;
import org.uppaal.cli.enumerations.OperationCode;
import com.uppaal.model.core2.Query;
import org.uppaal.cli.enumerations.ResultCode;
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
* concrete class implementing an export handler
* supporting all possible export commands under mode control
*/

public class ExportHandler extends AbstractHandler {
public ExportHandler (Context context) {
	super(context, OperationCode.EXPORT);
}

/**
* handle export commands
* @param command the command to handle
* @return the command result corresponding to this command
* @exception an exception describing the type of error which was encountered
*/
public CommandResult handle(Command command)  {

// check that the command contains exactly one argument

	OperationCode operation_code= command.getOperationCode();
	ObjectCode object_code = command.getObjectCode();
	int argument_number = command.getArgumentNumber();
	this.checkArgumentNumber(command, 1, 1);

// process the command depending on its object code

	String filename = command.getArgumentAt(0);
	int index = filename.length()-1;
	while (filename.charAt(index)!='.' && index>0) index --;
	String extension = filename.substring(index+1);

	try {
	switch (command.getObjectCode()) {
		case DOCUMENT:
		this.checkMode(command, ModeCode.EDITOR);
		if (!extension.equals("xta") && !extension.equals("xml")) 
			this.throwWrongExtensionException (operation_code, object_code, extension);
		this.context.getModelExpert().saveDocument(filename);
		break;

		case QUERIES:
		this.checkMode(command, ModeCode.EDITOR, ModeCode.VERIFIER);
		if (!extension.equals("q")) 
			this.throwWrongExtensionException (operation_code, object_code, extension);
		this.context.getQueryExpert().saveQueries(filename);
		break;

		case TRACE:
		this.checkMode(command, ModeCode.SYMBOLIC_SIMULATOR, ModeCode.CONCRETE_SIMULATOR);
		if (!extension.equals("xtr")) 
			this.throwWrongExtensionException (operation_code, object_code, extension);
		this.context.getTraceExpert().saveTrace(filename);
		break;
	}

	this.command_result.setResultCode(ResultCode.OK);
	return this.command_result;
	} catch (IOException e) {
		this.command_result.setResultCode(ResultCode.IO_ERROR);
		this.command_result.addArgument(filename);
		return this.command_result;
	} 
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
