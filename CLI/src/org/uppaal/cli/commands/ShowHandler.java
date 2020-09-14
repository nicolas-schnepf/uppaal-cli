package org.uppaal.cli.commands;

import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.Template;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.QueryList;
import com.uppaal.model.core2.Query;

import org.uppaal.cli.commands.AbstractHandler;

import org.uppaal.cli.context.ModeCode;
import org.uppaal.cli.commands.Handler;


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
* concrete class implementing a show handler
* supporting all possible show commands under mode control
*/

public class ShowHandler extends AbstractHandler {

public ShowHandler (Context context) {
	super(context, "show");
	try {
	this.operation_map.put("parameter", this.getClass().getMethod("showParameter"));
	this.operation_map.put("declaration", this.getClass().getMethod("showDeclaration"));
	this.operation_map.put("query", this.getClass().getMethod("showQuery"));
	this.operation_map.put("formula", this.getClass().getMethod("showFormula"));
	this.operation_map.put("comment", this.getClass().getMethod("showComment"));
	this.operation_map.put("template", this.getClass().getMethod("showTemplate"));
	this.operation_map.put("location", this.getClass().getMethod("showLocation"));
	this.operation_map.put("edge", this.getClass().getMethod("showEdge"));
	this.operation_map.put("guard", this.getClass().getMethod("showEdgeProperty"));
		this.operation_map.put("select", this.getClass().getMethod("showEdgeProperty"));
		this.operation_map.put("assign", this.getClass().getMethod("showEdgeProperty"));
		this.operation_map.put("sync", this.getClass().getMethod("showEdgeProperty"));
	this.operation_map.put("system", this.getClass().getMethod("showSystem"));
	this.operation_map.put("process", this.getClass().getMethod("showProcess"));
	this.operation_map.put("variable", this.getClass().getMethod("showVariable"));
	this.operation_map.put("trace", this.getClass().getMethod("showTrace"));
	this.operation_map.put("option", this.getClass().getMethod("showOption"));
	this.operation_map.put("setting", this.getClass().getMethod("showSetting"));
	this.operation_map.put("selection", this.getClass().getMethod("showSelection"));
	this.operation_map.put("result", this.getClass().getMethod("showResult"));
	this.operation_map.put("data", this.getClass().getMethod("showData"));
	} catch (Exception e) {
	System.out.println(e.getMessage());
	e.printStackTrace();
	System.exit(1);
	}
}

public void showParameter () {
	String name = this.getArgumentAt(0);
	String parameter = this.context.getTemplateExpert().getTemplateProperty(name, "parameter");
				if (parameter!=null) this.command_result.addArgument(parameter);
}

public void showDeclaration() {
			if (this.getArgumentNumber()==1) {
				String name = this.getArgumentAt(0);
				String declaration = this.context.getTemplateExpert().getTemplateProperty(name, "declaration");
				this.command_result.addArgument(declaration);
			} else
				this.command_result.addArgument(this.context.getModelExpert().getDocumentProperty("declaration"));
}

public void showQuery() {
	String name = this.getArgumentAt(0);
	if (name.equals("queries")) {
		LinkedList<String> names = this.context.getQueryExpert().showQueries();
		for (String arg: names) this.command_result.addArgument(arg);
	} else
			this.command_result.addArgument(this.context.getQueryExpert().showQuery(Integer.parseInt(name)));
}

public void showFormula () {
	int index = Integer.parseInt(this.arguments.get(0));
	String formula = this.context.getQueryExpert().getQueryProperty(index, "formula");
	this.command_result.addArgument(formula);
}

public void showComment () {
	int index = Integer.parseInt(this.arguments.get(0));
	String comment = this.context.getQueryExpert().getQueryProperty(index, "comment");
	this.command_result.addArgument(comment);
}

public void showResult () {
	int index = Integer.parseInt(this.arguments.get(0));
	String result = this.context.getQueryExpert().showQueryResult(index);
	this.command_result.addArgument(result);
}

public void showData () {
	int index = Integer.parseInt(this.arguments.get(0));
	boolean result = this.context.getDataExpert().importData(index);
	if (result) this.command_result.setResultCode(ResultCode.SELECT_DATA);
	else this.command_result.addArgument("No data to plot.");
}

public void showTemplate() {
	String name = this.getArgumentAt(0);
	if (name.equals("templates")) {
		LinkedList<String> templates = this.context.getTemplateExpert().showTemplates();
		for (String temp: templates) this.command_result.addArgument(temp);
	} else
		this.command_result.addArgument(this.context.getTemplateExpert().showTemplate(name));
}

public void showLocation () {
	String template = this.getArgumentAt(0);
	String name = this.getArgumentAt(1);

	if (name.equals("*")) {
		for (String location: this.context.getLocationExpert().showLocations(template))
			this.command_result.addArgument(location);
	} else
		this.command_result.addArgument(this.context.getLocationExpert().showLocation(template, name));
}

public void showEdge () {
		String template = this.getArgumentAt(0);
		String source = this.getArgumentAt(1);
		String target = this.getArgumentAt(2);
		String description = this.context.getEdgeExpert().showEdge(template, source, target);
		this.command_result.addArgument(description);
}

public void showEdgeProperty () {
		String template = this.getArgumentAt(0);
		String source = this.getArgumentAt(1);
		String target = this.getArgumentAt(2);
		String type = this.object_type;
		String value = this.context.getEdgeExpert().getPropertyValue(template, source, target, type);
		this.command_result.addArgument(value);
}

public void showSystem() {
		this.command_result.addArgument(this.context.getModelExpert().getDocumentProperty("system"));
}

public void showProcess() {
	this.checkMode("show", "trace", ModeCode.SIMULATOR);
	String name = this.getArgumentAt(0);

	if (name.equals("processes")) {
		LinkedList<String> processes = this.context.getStateExpert().showProcesses();
		for (String process: processes) this.command_result.addArgument(process);
	} else {
		if (this.arguments.size()>1)
			name = name + "(" + this.getArgumentAt(1) + ")";
		this.command_result.addArgument(this.context.getStateExpert().showProcess(name));
	}
}

public void showVariable() {
	this.checkMode("show", "trace", ModeCode.SIMULATOR);
	String name = this.getArgumentAt(0);
	if (name.equals("variables")) {
		LinkedList<String> variables = this.context.getStateExpert().showVariables();
		for (String var: variables) this.command_result.addArgument(var);
	} else
		this.command_result.addArgument(this.context.getStateExpert().showVariable(name));
}

public void showTrace() {
	this.checkMode("show", "trace", ModeCode.SIMULATOR);
	this.command_result.setResultCode(ResultCode.SELECT_STATE);
}

public void showOption () {
	String option = this.arguments.get(0);
	if (option.equals("options"))
		this.command_result.addArgument(this.context.getOptionExpert().showOptions());
	else
		this.command_result.addArgument(this.context.getOptionExpert().showOption(option));
}

public void showSetting() {
	String index = this.arguments.get(0);
	LinkedList<String> output = this.context.getOptionExpert().showParameter(index);
	for (String line:output) this.command_result.addArgument(line);
}

public void showSelection () {
	String name = this.arguments.get(0);
	if (name.equals("selection")) {
		LinkedList<String> selection = this.context.getQueryExpert().showSelectedQueries();
		for (String query: selection) this.command_result.addArgument(query);
	} else {
		int index = Integer.parseInt(name);
		String query = this.context.getQueryExpert().showSelectedQuery(index);
		this.command_result.addArgument(query);
	}
}

@Override
public boolean acceptMode (ModeCode mode) {
	return true;
}
}
