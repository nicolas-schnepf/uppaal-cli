package org.uppaal.cli.handlers;

import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.Template;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.QueryList;
import com.uppaal.model.core2.Query;
import org.uppaal.cli.commands.Command.OperationCode;
import org.uppaal.cli.commands.CommandResult;
import org.uppaal.cli.commands.Command;
import org.uppaal.cli.commands.Context;

import java.net.MalformedURLException;
import java.util.LinkedList;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
* concrete class implementing a editor handler
* supporting all commands for the editor mode
*/

public class EditorHandler extends ModeHandler {
// array of accepted command codes



// array of accepted object codes

/**
* public constructor of a editor handler
* initializing it from a reference to the uppaal context
* @param context the reference to the uppaal context for this handler
*/

public EditorHandler (Context context) {
	super(context);
	this.operation_map.put(OperationCode.IMPORT, new ImportHandler(context));
	this.operation_map.put(OperationCode.EXPORT, new ExportHandler(context));
	this.operation_map.put(OperationCode.CLEAR, new ClearHandler(context));
	this.operation_map.put(OperationCode.SHOW, new ShowHandler(context));
	this.operation_map.put(OperationCode.REMOVE, new RemoveHandler(context));
	this.operation_map.put(OperationCode.ADD, new AddHandler(context));
	this.operation_map.put(OperationCode.RENAME, new RenameHandler(context));
	this.operation_map.put(OperationCode.SET, new SetHandler(context));
	this.operation_map.put(OperationCode.UNSET, new UnsetHandler(context));
}


@Override
public Handler.HandlerCode getMode () {
	return Handler.HandlerCode.EDITOR;
}

/**
* handle undo commands
* @param command the command to handle
* @return the command result for this command

private CommandResult handleUndo (Command command) {
	this.context.undo();
	this.command_result.clear();
	this.command_result.setResultCode(CommandResult.ResultCode.OK);
	return this.command_result;
}

/**
* handle undo commands
* @param command the command to handle
* @return the command result for this command

private CommandResult handleRedo (Command command) {
	this.context.redo();
	this.command_result.clear();
	this.command_result.setResultCode(CommandResult.ResultCode.OK);
	return this.command_result;
}
*/
}
