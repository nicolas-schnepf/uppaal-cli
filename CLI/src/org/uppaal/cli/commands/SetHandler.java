package org.uppaal.cli.commands;

import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.Template;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.QueryList;
import com.uppaal.model.core2.Query;

import org.uppaal.cli.commands.AbstractHandler;

import org.uppaal.cli.enumerations.ModeCode;
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
* concrete class implementing a set handler
* supporting all possible set commands under mode control
*/

public class SetHandler extends AbstractHandler {
public SetHandler (Context context) {
	super(context, "set");
	try {
	this.operation_map.put("query", this.getClass().getMethod("setQuery"));
	this.operation_map.put("formula", this.getClass().getMethod("setFormula"));
	this.operation_map.put("comment",this.getClass().getMethod("setComment "));
	this.operation_map.put("template",this.getClass().getMethod("setTemplate "));
	this.operation_map.put("declaration",this.getClass().getMethod("setDeclaration"));
	this.operation_map.put("parameter", this.getClass().getMethod("setParameter"));
	this.operation_map.put("location", this.getClass().getMethod("setLocation"));
	this.operation_map.put("invariant", this.getClass().getMethod("setInvariant "));
	this.operation_map.put("init", this.getClass().getMethod("setInit "));
	this.operation_map.put("committed", this.getClass().getMethod("setCommitted "));
	this.operation_map.put("edge", this.getClass().getMethod("setEdge "));
	this.operation_map.put("select", this.getClass().getMethod("setSelect"));
	this.operation_map.put("guard", this.getClass().getMethod("setGuard"));
	this.operation_map.put("sync", this.getClass().getMethod("setSync "));
	this.operation_map.put("assign", this.getClass().getMethod("setAssign "));
	this.operation_map.put("system", this.getClass().getMethod("setSystem"));
	} catch (Exception e) {
	System.out.println(e.getMessage());
	e.printStackTrace();
	System.exit(1);
	}
}

public void setQuery () {QUERY:
		this.checkMode("set", "query", ModeCode.EDITOR);
		String name = this.getArgumentAt(0);
		String formula = this.getArgumentAt(1);
		String comment = this.getArgumentAt(2);
			this.context.getQueryExpert().addQuery(name, formula, comment);
this.command_result.addArgument(name);
}

public void setFormula() {
		this.checkMode("set", "formula", ModeCode.EDITOR);
				String name = this.getArgumentAt(0);
				String formula = this.getArgumentAt(1);
				this.context.getQueryExpert().setQueryProperty(name, "formula", formula);
				this.command_result.addArgument(name);
				this.command_result.addArgument(formula);
}

public void setComment () {
		this.checkMode("set", "comment", ModeCode.EDITOR);
				String name = this.getArgumentAt(0);
				String comment = this.getArgumentAt(1);
				this.context.getQueryExpert().setQueryProperty(name, "comment", comment);
				this.command_result.addArgument(name);
				this.command_result.addArgument(comment);
}

public void setTemplate () {
		this.checkMode("set", "template", ModeCode.EDITOR);
		String name = this.getArgumentAt(0);
		String parameter = this.getArgumentAt(1);
			this.context.getTemplateExpert().addTemplate(name, parameter);
			this.command_result.addArgument(name);
}

public void setDeclaration() {
		this.checkMode("set", "declaration", ModeCode.EDITOR);
		if (this.getArgumentNumber()==2) {
				String name = this.getArgumentAt(0);
				String declaration = this.getArgumentAt(1);
				this.context.getTemplateExpert().setTemplateProperty(name, "declaration",  declaration);
				this.command_result.addArgument(name);
			} else {
				String declaration = this.getArgumentAt(0);
				this.context.getModelExpert().setDocumentProperty("declaration", declaration);
		}
}

public void setParameter() {
		this.checkMode("set", "parameter", ModeCode.EDITOR);
				String name = this.getArgumentAt(0);
				String parameter = this.getArgumentAt(1);
				this.context.getTemplateExpert().setTemplateProperty(name, "parameter", parameter);
				this.command_result.addArgument(name);
				this.command_result.addArgument(parameter);
}


public void setLocation() {
		this.checkMode("set", "location", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String name = this.getArgumentAt(1);
		this.context.getLocationExpert().addLocation(template, name);
		this.command_result.addArgument(template);
		this.command_result.addArgument(name);
}

public void setInvariant () {
		this.checkMode("set", "invariant", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String name = this.getArgumentAt(1);
		String value = this.getArgumentAt(2);
		this.context.getLocationExpert().setLocationProperty(template, name, "invariant", value);
this.command_result.addArgument(template);
		this.command_result.addArgument(name);
this.command_result.addArgument(value);
}

public void setInit () {
		this.checkMode("set", "init", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String name = this.getArgumentAt(1);
		this.context.getLocationExpert().setLocationProperty(template, name, "init", true);
this.command_result.addArgument(template);
		this.command_result.addArgument(name);
}
		
public void setCommitted () {
		this.checkMode("set", "committed", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String name = this.getArgumentAt(1);
		this.context.getLocationExpert().setLocationProperty(template, name, "committed", true);
this.command_result.addArgument(template);
		this.command_result.addArgument(name);
}

public void setEdge () {
		this.checkMode("set", "edge", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String source = this.getArgumentAt(1);
		String target = this.getArgumentAt(2);
		this.context.getEdgeExpert().addEdge(template, source, target);
		this.command_result.addArgument(template);
		this.command_result.addArgument(source);
		this.command_result.addArgument(target);
}

public void setSelect() {
		this.checkMode("set", "select", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String source = this.getArgumentAt(1);
		String target = this.getArgumentAt(2);
		String value = this.getArgumentAt(3);
		this.context.getEdgeExpert().setEdgeProperty(template, source, target, "select", value);
this.command_result.addArgument(template);
		this.command_result.addArgument(source);
		this.command_result.addArgument(target);
		this.command_result.addArgument(value);
}
		
public void setGuard() {
		this.checkMode("set", "guard", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String source = this.getArgumentAt(1);
		String target = this.getArgumentAt(2);
		String value = this.getArgumentAt(3);
		this.context.getEdgeExpert().setEdgeProperty(template, source, target, "guard", value);
this.command_result.addArgument(template);
		this.command_result.addArgument(source);
		this.command_result.addArgument(target);
		this.command_result.addArgument(value);
}

public void setSync () {
		this.checkMode("set", "sync", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String source = this.getArgumentAt(1);
		String target = this.getArgumentAt(2);
		String value = this.getArgumentAt(3);
		this.context.getEdgeExpert().setEdgeProperty(template, source, target, "sync", value);
this.command_result.addArgument(template);
		this.command_result.addArgument(source);
		this.command_result.addArgument(target);
		this.command_result.addArgument(value);
}

public void setAssign () {
		this.checkMode("set", "assign", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String source = this.getArgumentAt(1);
		String target = this.getArgumentAt(2);
		String value = this.getArgumentAt(3);
		this.context.getEdgeExpert().setEdgeProperty(template, source, target, "assign", value);
this.command_result.addArgument(template);
		this.command_result.addArgument(source);
		this.command_result.addArgument(target);
		this.command_result.addArgument(value);
}
public void setSystem() {
		this.checkMode("set", "system", ModeCode.EDITOR);
		String system = this.getArgumentAt(0);
		this.context.getModelExpert().setDocumentProperty("system", system);
		this.command_result.addArgument(system);
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
