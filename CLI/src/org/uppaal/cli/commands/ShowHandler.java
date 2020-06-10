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
	this.operation_map.put("queries", this.getClass().getMethod("showQueries"));
	this.operation_map.put("parameter", this.getClass().getMethod("showParameter"));
	this.operation_map.put("declaration", this.getClass().getMethod("showDeclaration"));
	this.operation_map.put("query", this.getClass().getMethod("showQuery"));
	this.operation_map.put("template", this.getClass().getMethod("showTemplate"));
	this.operation_map.put("location", this.getClass().getMethod("showLocation"));
	this.operation_map.put("edge", this.getClass().getMethod("showEdge"));
	this.operation_map.put("guard", this.getClass().getMethod("showEdgeProperty"));
		this.operation_map.put("select", this.getClass().getMethod("showEdgeProperty"));
		this.operation_map.put("assign", this.getClass().getMethod("showEdgeProperty"));
		this.operation_map.put("sync", this.getClass().getMethod("showEdgeProperty"));
	this.operation_map.put("system", this.getClass().getMethod("showSystem"));
	} catch (Exception e) {
	System.out.println(e.getMessage());
	e.printStackTrace();
	System.exit(1);
	}
}

public void showQueries () {
		LinkedList<String> names = this.context.getQueryExpert().showQueries();
		for (String arg: names) this.command_result.addArgument(arg);
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
			this.command_result.addArgument(this.context.getQueryExpert().showQuery(name));
}

public void showTemplate() {
	String name = this.getArgumentAt(0);
	if (name.equals("*")) {
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
		String value = this.context.getEdgeExpert().getEdgeProperty(template, source, target, type);
		this.command_result.addArgument(value);
}

public void showSystem() {
		this.command_result.addArgument(this.context.getModelExpert().getDocumentProperty("system"));
}


@Override
public boolean acceptMode (ModeCode mode) {
	return true;
}
}
