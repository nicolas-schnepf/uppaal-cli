package org.uppaal.cli.commands;

import com.uppaal.engine.EngineException;
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
import java.io.File;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
* concrete class implementing a load handler
* supporting all possible load commands under mode control
*/

public class LoadHandler extends AbstractHandler {
public LoadHandler (Context context) {
	super(context, "load");
	try {
	this.operation_map.put("document", this.getClass().getMethod("loadDocument"));
	this.operation_map.put("templates", this.getClass().getMethod("loadTemplates"));
	this.operation_map.put("queries", this.getClass().getMethod("loadQueries"));
	this.operation_map.put("data", this.getClass().getMethod("loadData"));
	this.operation_map.put("strategy", this.getClass().getMethod("loadStrategy"));
	} catch (Exception e) {
		System.out.println(e.getMessage());
	e.printStackTrace();
	System.exit(1);
	}
}



public void loadDocument() {
	String filename = this.getArgumentAt(0);
	int index = filename.length()-1;
	while (filename.charAt(index)!='.' && index>0) index --;
	String extension = filename.substring(index+1);

	try {
		this.checkMode("load", "document", ModeCode.EDITOR);
		if (!extension.equals("xta") && !extension.equals("xml")) 
			this.throwWrongExtensionException ("load", "document", extension);
		this.context.getModelExpert().loadDocument(filename);
		LinkedList<String> templates = this.context.getTemplateExpert().showTemplates();
		for (String template:templates) this.command_result.addArgument(template);
		this.command_result.setResultCode(ResultCode.ADD_TEMPLATES);
	} catch (IOException e) {
		this.command_result.setResultCode(ResultCode.IO_ERROR);
		this.command_result.addArgument(filename);
	}
}

public void loadTemplates() {
	String filename = this.getArgumentAt(0);
	int index = filename.length()-1;
	while (filename.charAt(index)!='.' && index>0) index --;
	String extension = filename.substring(index+1);

	try {
		this.checkMode("load", "templates", ModeCode.EDITOR);
		if (extension.equals("xta") || extension.equals("xml"))
			this.context.getTemplateExpert().loadTemplates(filename);
		else if (extension.equals("upl")) {
			if (new File(filename).exists())
				this.command_result.addArgument(filename);
			else if (new File(System.getenv("UPPAALPATH")+"/"+filename).exists()) {
				StringBuffer buffer = new StringBuffer();
				buffer.append(System.getenv("UPPAALPATH"));
				buffer.append("/");
				buffer.append(filename);
				this.command_result.addArgument(buffer.toString());
			} else {
				this.command_result.addArgument(filename);
				this.command_result.setResultCode(ResultCode.IO_ERROR);
			}
		} else
			this.throwWrongExtensionException ("load", "templates", extension);

		this.command_result.setResultCode(ResultCode.SELECT_TEMPLATES);
	} catch (IOException e) {
		this.command_result.setResultCode(ResultCode.IO_ERROR);
		this.command_result.addArgument(filename);
	}
}

public void loadQueries() {
	String filename = this.getArgumentAt(0);
	int index = filename.length()-1;
	while (filename.charAt(index)!='.' && index>0) index --;
	String extension = filename.substring(index+1);

	try {

		this.checkMode("load", "queries", ModeCode.EDITOR, ModeCode.VERIFIER);
		if (!extension.equals("q")) 
			this.throwWrongExtensionException ("load", this.getObjectType(), extension);
		this.context.getQueryExpert().loadQueries(filename);
	} catch (IOException e) {
		this.command_result.setResultCode(ResultCode.IO_ERROR);
		this.command_result.addArgument(filename);
	}
}

public void loadData () {
	String filename = this.getArgumentAt(0);
	int index = filename.length()-1;
	while (filename.charAt(index)!='.' && index>0) index --;
	String extension = filename.substring(index+1);

	try {

		if (!extension.equals("xml")) 
			this.throwWrongExtensionException ("load", this.getObjectType(), extension);
		this.context.getDataExpert().loadData(filename);
	} catch (Exception e) {
		this.command_result.setResultCode(ResultCode.IO_ERROR);
		this.command_result.addArgument(filename);
	}
}

public void loadStrategy() {
	String strategy = this.getArgumentAt(0);
	String filename = this.getArgumentAt(1);
	int index = filename.length()-1;
	while (filename.charAt(index)!='.' && index>0) index --;
	String extension = filename.substring(index+1);

	try {
		if (!extension.equals("json"))
			this.throwWrongExtensionException ("load", "strategy", extension);
		String result = this.context.getStrategyExpert().loadStrategy(strategy, filename);
		this.command_result.addArgument(result);
	} catch (EngineException e) {
		this.command_result.setResultCode(ResultCode.ENGINE_ERROR);
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

@Override
public String getHelpMessage() {
	return "Load some information from a file.";
}

@Override
public String getSyntax() {
	return "\"load\" LOADABLE \"from\" FILENAME";
}
}
