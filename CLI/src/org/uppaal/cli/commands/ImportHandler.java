package org.uppaal.cli.commands;

import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.Template;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.QueryList;

import org.uppaal.cli.commands.AbstractHandler;

import org.uppaal.cli.context.ModeCode;
import org.uppaal.cli.commands.Handler;


import org.uppaal.cli.enumerations.ResultCode;
import com.uppaal.model.core2.Query;
import org.uppaal.cli.enumerations.ResultCode;

import org.uppaal.cli.context.Context;

import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.HashSet;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
* concrete class implementing an import handler
* supporting all possible import commands under mode control
*/

public class ImportHandler extends AbstractHandler {
public ImportHandler (Context context) {
	super(context, "import");
	try {
	this.operation_map.put("document", this.getClass().getMethod("importDocument"));
	this.operation_map.put("templates", this.getClass().getMethod("importTemplates"));
	this.operation_map.put("queries", this.getClass().getMethod("importQueries"));
	this.operation_map.put("data", this.getClass().getMethod("importData"));
	} catch (Exception e) {
		System.out.println(e.getMessage());
	e.printStackTrace();
	System.exit(1);
	}
}



public void importDocument() {
	String filename = this.getArgumentAt(0);
	int index = filename.length()-1;
	while (filename.charAt(index)!='.' && index>0) index --;
	String extension = filename.substring(index+1);

	try {
		this.checkMode("import", "document", ModeCode.EDITOR);
		if (!extension.equals("xta") && !extension.equals("xml")) 
			this.throwWrongExtensionException ("import", "document", extension);
		this.context.getModelExpert().loadDocument(filename);
		LinkedList<String> templates = this.context.getTemplateExpert().showTemplates();
		for (String template:templates) this.command_result.addArgument(template);
		this.command_result.setResultCode(ResultCode.ADD_TEMPLATES);
	} catch (IOException e) {
		this.command_result.setResultCode(ResultCode.IO_ERROR);
		this.command_result.addArgument(filename);
	}
}

public void importTemplates() {
	String filename = this.getArgumentAt(0);
	int index = filename.length()-1;
	while (filename.charAt(index)!='.' && index>0) index --;
	String extension = filename.substring(index+1);

	try {
		this.checkMode("import", "templates", ModeCode.EDITOR);
		if (!extension.equals("xta") && !extension.equals("xml")) 
			this.throwWrongExtensionException ("import", "templates", extension);
		this.context.getTemplateExpert().clearTemplates();
		int templates = this.context.getTemplateExpert().loadTemplates(filename);
		this.command_result.setResultCode(ResultCode.SELECT_TEMPLATES);
	} catch (IOException e) {
		this.command_result.setResultCode(ResultCode.IO_ERROR);
		this.command_result.addArgument(filename);
	}
}

public void importQueries() {
	String filename = this.getArgumentAt(0);
	int index = filename.length()-1;
	while (filename.charAt(index)!='.' && index>0) index --;
	String extension = filename.substring(index+1);

	try {

		this.checkMode("import", "queries", ModeCode.EDITOR, ModeCode.VERIFIER);
		if (!extension.equals("q")) 
			this.throwWrongExtensionException ("import", this.getObjectType(), extension);
		this.context.getQueryExpert().loadQueries(filename);
	} catch (IOException e) {
		this.command_result.setResultCode(ResultCode.IO_ERROR);
		this.command_result.addArgument(filename);
	}
}

public void importData () {
	String filename = this.getArgumentAt(0);
	int index = filename.length()-1;
	while (filename.charAt(index)!='.' && index>0) index --;
	String extension = filename.substring(index+1);

	try {

		if (!extension.equals("xml")) 
			this.throwWrongExtensionException ("import", this.getObjectType(), extension);
		this.context.getDataExpert().importData(filename);
	} catch (Exception e) {
		this.command_result.setResultCode(ResultCode.IO_ERROR);
		this.command_result.addArgument(filename);
	}
}

public boolean acceptMode (ModeCode mode) {
	switch(mode) {
		case EDITOR:
		case SIMULATOR:
		return true;

		default:
		return false;
	}
}
}
