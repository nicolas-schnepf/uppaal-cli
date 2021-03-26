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
import javax.xml.transform.TransformerException;
import javax.xml.parsers.ParserConfigurationException;

/**
* concrete class implementing an save handler
* supporting all possible save commands under mode control
*/

public class SaveHandler extends AbstractHandler {
public SaveHandler (Context context) {
	super(context, "save");
	try {
	this.operation_map.put("document", this.getClass().getMethod("saveDocument"));
	this.operation_map.put("queries", this.getClass().getMethod("saveQueries"));
	this.operation_map.put("trace", this.getClass().getMethod("saveTrace"));
	this.operation_map.put("xmla", this.getClass().getMethod("saveData"));
	this.operation_map.put("strategy", this.getClass().getMethod("saveStrategy"));
	} catch (Exception e) {
	System.out.println(e.getMessage());
	e.printStackTrace();
	System.exit(1);
	}
}

public void saveDocument () {
	String filename = this.getArgumentAt(0);
	int index = filename.length()-1;
	while (filename.charAt(index)!='.' && index>0) index --;
	String extension = filename.substring(index+1);

	try {
		this.checkMode("save", "document", ModeCode.EDITOR);
		if (!extension.equals("xta") && !extension.equals("xml")) 
			this.throwWrongExtensionException ("save", "document", extension);
		this.context.getModelExpert().saveDocument(filename);
	} catch (IOException e) {
		this.command_result.setResultCode(ResultCode.IO_ERROR);
		this.command_result.addArgument(filename);

	} 
}

public void saveQueries () {
	String filename = this.getArgumentAt(0);
	int index = filename.length()-1;
	while (filename.charAt(index)!='.' && index>0) index --;
	String extension = filename.substring(index+1);

	try {
		this.checkMode("save", "queries", ModeCode.EDITOR, ModeCode.VERIFIER);
		if (!extension.equals("q")) 
			this.throwWrongExtensionException ("save", "queries", extension);
		this.context.getQueryExpert().saveQueries(filename);
	} catch (IOException e) {
		this.command_result.setResultCode(ResultCode.IO_ERROR);
		this.command_result.addArgument(filename);

	} 
}

public void saveTrace () {
	String filename = this.getArgumentAt(0);
	int index = filename.length()-1;
	while (filename.charAt(index)!='.' && index>0) index --;
	String extension = filename.substring(index+1);

	try {
this.checkMode("save", "trace", ModeCode.SIMULATOR);
		if (!extension.equals("xtr")) 
			this.throwWrongExtensionException ("save", "trace", extension);
		this.context.getTraceExpert().saveTrace(filename);
	} catch (IOException e) {
		this.command_result.setResultCode(ResultCode.IO_ERROR);
		this.command_result.addArgument(filename);

	} 
}

public void saveData () {
	int index = this.arguments.size()>1? Integer.parseInt(this.getArgumentAt(0)): -1;
	String filename = this.arguments.getLast();
	int idx = filename.length()-1;
	while (filename.charAt(idx)!='.' && idx>0) idx --;
	String extension = filename.substring(idx+1);

	try {
		if (!extension.equals("dat")) 
			this.throwWrongExtensionException ("save", "data", extension);

		if (index==-1 || this.context.getDataExpert().importData(index))
			this.context.getDataExpert().saveData(filename);
		else
			this.command_result.addArgument("No data to save.");
	}  catch (TransformerException e) {
		this.command_result.setResultCode(ResultCode.IO_ERROR);
		this.command_result.addArgument(filename);
	} catch (ParserConfigurationException e) {
		this.command_result.setResultCode(ResultCode.IO_ERROR);
		this.command_result.addArgument(filename);
	}
}

public void saveStrategy() {
	String strategy = this.getArgumentAt(0);
	String filename = this.getArgumentAt(1);
	int index = filename.length()-1;
	while (filename.charAt(index)!='.' && index>0) index --;
	String extension = filename.substring(index+1);

	try {
		if (!extension.equals("json"))
			this.throwWrongExtensionException ("save", "strategy", extension);
		String result = this.context.getStrategyExpert().saveStrategy(strategy, filename);
		this.command_result.addArgument(result);
	} catch (EngineException e) {
		this.command_result.setResultCode(ResultCode.ENGINE_ERROR);
		this.command_result.addArgument(filename);
	}
}

@Override
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
	return "Save some information into a provided file.";
}

@Override
public String getSyntax() {
	return "\"save\" SAVEABLE \"to\" FILENAME";
}
}
