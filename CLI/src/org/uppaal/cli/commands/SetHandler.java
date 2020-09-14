package org.uppaal.cli.commands;

import com.uppaal.engine.EngineException;
import com.uppaal.engine.CannotEvaluateException;
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
import java.util.HashMap;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
* concrete class implementing a set handler
* supporting all possible set commands under mode control
*/

public class SetHandler extends AbstractHandler {
	private HashMap <String, String> properties = new HashMap<String, String>();

public SetHandler (Context context) {
	super(context, "set");
	this.properties = new HashMap<String, String>();

	try {
	this.operation_map.put("query", this.getClass().getMethod("setQuery"));
	this.operation_map.put("formula", this.getClass().getMethod("setFormula"));
	this.operation_map.put("comment",this.getClass().getMethod("setComment"));
	this.operation_map.put("template",this.getClass().getMethod("setTemplate"));
	this.operation_map.put("declaration",this.getClass().getMethod("setDeclaration"));
	this.operation_map.put("parameter", this.getClass().getMethod("setParameter"));
	this.operation_map.put("location", this.getClass().getMethod("setLocation"));
	this.operation_map.put("invariant", this.getClass().getMethod("setInvariant"));
	this.operation_map.put("init", this.getClass().getMethod("setInit"));
	this.operation_map.put("committed", this.getClass().getMethod("setCommitted"));
	this.operation_map.put("edge", this.getClass().getMethod("setEdge"));
	this.operation_map.put("select", this.getClass().getMethod("setSelect"));
	this.operation_map.put("guard", this.getClass().getMethod("setGuard"));
	this.operation_map.put("sync", this.getClass().getMethod("setSync"));
	this.operation_map.put("assign", this.getClass().getMethod("setAssign"));
	this.operation_map.put("system", this.getClass().getMethod("setSystem"));
	this.operation_map.put("state", this.getClass().getMethod("setState"));
	this.operation_map.put("option", this.getClass().getMethod("setOption"));
	this.operation_map.put("selection", this.getClass().getMethod("setSelection"));
	} catch (Exception e) {
	System.out.println(e.getMessage());
	e.printStackTrace();
	System.exit(1);
	}
}

/**
* add a new property to this set handler
* @param property the name of the property to handle
* @param value the value assigned to the property
*/
public void addProperty (String property, String value) {
	this.properties.put(property, value);
}

/**
* check if a property already exists in the map of this handler
* @param property the name of the property to check
* @return true if and only if the property already exists in the map of this handler
*/
public boolean containsProperty (String property) {
	return this.properties.keySet().contains(property);
}

@Override
public void clear () {
	super.clear();
	this.properties.clear();
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
				int index = Integer.parseInt(this.getArgumentAt(0));
				String formula = this.getArgumentAt(1);
				this.context.getQueryExpert().setQueryProperty(index, "formula", formula);
}

public void setComment () {
		this.checkMode("set", "comment", ModeCode.EDITOR);
				int index = Integer.parseInt(this.getArgumentAt(0));
				String comment = this.getArgumentAt(1);
				this.context.getQueryExpert().setQueryProperty(index, "comment", comment);
}

public void setTemplate () {
		this.checkMode("set", "template", ModeCode.EDITOR);
		String name = this.getArgumentAt(0);
		String parameter = this.properties.get("parameter");
		String declaration = this.properties.get("declaration");
			this.context.getTemplateExpert().addTemplate(name, parameter, declaration);
}

public void setDeclaration() {
		this.checkMode("set", "declaration", ModeCode.EDITOR);
		if (this.getArgumentNumber()==2) {
				String name = this.getArgumentAt(0);
				String declaration = this.getArgumentAt(1);
				this.context.getTemplateExpert().setTemplateProperty(name, "declaration",  declaration);
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
}


public void setLocation() {
		this.checkMode("set", "location", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String name = this.getArgumentAt(1);
		String invariant = this.properties.get("invariant");
		this.context.getLocationExpert().addLocation(template, name, invariant);
}

public void setInvariant () {
		this.checkMode("set", "invariant", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String name = this.getArgumentAt(1);
		String value = this.getArgumentAt(2);
		this.context.getLocationExpert().setLocationProperty(template, name, "invariant", value);
}

public void setInit () {
		this.checkMode("set", "init", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String name = this.getArgumentAt(1);
		this.context.getLocationExpert().setLocationProperty(template, name, "init", true);
}
		
public void setCommitted () {
		this.checkMode("set", "committed", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String name = this.getArgumentAt(1);
		this.context.getLocationExpert().setLocationProperty(template, name, "committed", true);
}

public void setEdge () {
		this.checkMode("set", "edge", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String source = this.getArgumentAt(1);
		String target = this.getArgumentAt(2);
		String select = this.properties.get("select");
		String guard = this.properties.get("guard");
		String sync = this.properties.get("sync");
		String assign = this.properties.get("assign");
		this.context.getEdgeExpert().addEdge(template, source, target, select, guard, sync, assign);
}

public void setSelect() {
		this.checkMode("set", "select", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String source = this.getArgumentAt(1);
		String target = this.getArgumentAt(2);
		String value = this.getArgumentAt(3);
		this.context.getEdgeExpert().setEdgeProperty(template, source, target, "select", value);
}
		
public void setGuard() {
		this.checkMode("set", "guard", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String source = this.getArgumentAt(1);
		String target = this.getArgumentAt(2);
		String value = this.getArgumentAt(3);
		this.context.getEdgeExpert().setEdgeProperty(template, source, target, "guard", value);
}

public void setSync () {
		this.checkMode("set", "sync", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String source = this.getArgumentAt(1);
		String target = this.getArgumentAt(2);
		String value = this.getArgumentAt(3);
		this.context.getEdgeExpert().setEdgeProperty(template, source, target, "sync", value);
}

public void setAssign () {
		this.checkMode("set", "assign", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String source = this.getArgumentAt(1);
		String target = this.getArgumentAt(2);
		String value = this.getArgumentAt(3);
		this.context.getEdgeExpert().setEdgeProperty(template, source, target, "assign", value);
}

public void setSystem() {
		this.checkMode("set", "system", ModeCode.EDITOR);
		String system = this.getArgumentAt(0);
		this.context.getModelExpert().setDocumentProperty("system", system);
}

public void setState () {
	try {
		this.context.getStateExpert().computeTransitions();
	this.command_result.setResultCode(ResultCode.SELECT_TRANSITION);
	} catch (EngineException e) {
		this.command_result.setResultCode(ResultCode.ENGINE_ERROR);
	} catch (CannotEvaluateException e) {
		this.command_result.setResultCode(ResultCode.EVALUATION_ERROR);
	}
}

public void setOption () {
	String option = this.arguments.get(0);
	String value = this.arguments.get(1);
	this.context.getOptionExpert().setOption(option, value);
}

public void setSelection () {
	this.command_result.setResultCode(ResultCode.SELECT_QUERIES);
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

/**
* @return the value associated with a property
*/
public String getPropertyValue() {
	this.checkMode("set", this.object_type, ModeCode.EDITOR);
	int arguments = this.arguments.size();
	String template, location, source, target;
	String result = null;
	String type = this.object_type;

	switch (arguments) {
		case 0:
		result = (String) this.context.getDocument().getPropertyValue(this.object_type);
		break;

		case 1:
		template = this.arguments.get(0);
		result = this.context.getTemplateExpert().getPropertyValue(template, this.object_type);
		break;

		case 2:
		template = this.arguments.get(0);
		location = this.arguments.get(1);
		result = this.context.getLocationExpert().getPropertyValue(template, location, type);
		break;

		case 3:
		template = this.arguments.get(0);
		source = this.arguments.get(1);
		target = this.arguments.get(2);
		result = this.context.getEdgeExpert().getPropertyValue(template, source, target, type);
		break;
	}

	if (result==null) result = "";
	return result;
	}
}
