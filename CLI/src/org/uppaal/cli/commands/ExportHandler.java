package org.uppaal.cli.commands;

import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.Template;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.QueryList;

import org.uppaal.cli.commands.AbstractHandler;

import org.uppaal.cli.context.ModeCode;
import org.uppaal.cli.commands.Handler;

import com.uppaal.model.core2.Query;
import org.uppaal.cli.enumerations.ResultCode;

import org.uppaal.cli.enumerations.ResultCode;
import org.uppaal.cli.commands.CommandResult;

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
	super(context, "export");
	try {
	this.operation_map.put("document", this.getClass().getMethod("exportDocument"));
	this.operation_map.put("queries", this.getClass().getMethod("exportQueries"));
	this.operation_map.put("trace", this.getClass().getMethod("exportTrace"));
	} catch (Exception e) {
	System.out.println(e.getMessage());
	e.printStackTrace();
	System.exit(1);
	}
}

public void exportDocument () {
	String filename = this.getArgumentAt(0);
	int index = filename.length()-1;
	while (filename.charAt(index)!='.' && index>0) index --;
	String extension = filename.substring(index+1);

	try {
		this.checkMode("export", "document", ModeCode.EDITOR);
		if (!extension.equals("xta") && !extension.equals("xml")) 
			this.throwWrongExtensionException ("export", "document", extension);
		this.context.getModelExpert().saveDocument(filename);
	} catch (IOException e) {
		this.command_result.setResultCode(ResultCode.IO_ERROR);
		this.command_result.addArgument(filename);

	} 
}

public void exportQueries () {
	String filename = this.getArgumentAt(0);
	int index = filename.length()-1;
	while (filename.charAt(index)!='.' && index>0) index --;
	String extension = filename.substring(index+1);

	try {
		this.checkMode("export", "queries", ModeCode.EDITOR, ModeCode.VERIFIER);
		if (!extension.equals("q")) 
			this.throwWrongExtensionException ("export", "queries", extension);
		this.context.getQueryExpert().saveQueries(filename);
	} catch (IOException e) {
		this.command_result.setResultCode(ResultCode.IO_ERROR);
		this.command_result.addArgument(filename);

	} 
}

public void exportTrace () {
	String filename = this.getArgumentAt(0);
	int index = filename.length()-1;
	while (filename.charAt(index)!='.' && index>0) index --;
	String extension = filename.substring(index+1);

	try {
this.checkMode("export", "trace", ModeCode.SYMBOLIC_SIMULATOR, ModeCode.CONCRETE_SIMULATOR);
		if (!extension.equals("xtr")) 
			this.throwWrongExtensionException ("export", "trace", extension);
		this.context.getTraceExpert().saveTrace(filename);
	} catch (IOException e) {
		this.command_result.setResultCode(ResultCode.IO_ERROR);
		this.command_result.addArgument(filename);

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
