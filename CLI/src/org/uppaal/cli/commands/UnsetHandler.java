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

import org.uppaal.cli.context.Context;

import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.HashSet;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
* concrete class implementing an unset handler
* supporting all possible unset commands under mode control
*/

public class UnsetHandler extends AbstractHandler {
public UnsetHandler (Context context) {
	super(context, "unset");
	try {
	this.operation_map.put("document", this.getClass().getMethod("unsetDocument"));
	this.operation_map.put("queries", this.getClass().getMethod("unsetQueries"));
	this.operation_map.put("templates", this.getClass().getMethod("unsetTemplates"));
	this.operation_map.put("declaration", this.getClass().getMethod("unsetDeclaration"));
	this.operation_map.put("query", this.getClass().getMethod("unsetQuery"));
	this.operation_map.put("template", this.getClass().getMethod("unsetTemplate"));
	this.operation_map.put("parameter", this.getClass().getMethod("unsetParameter"));
	this.operation_map.put("location", this.getClass().getMethod("unsetLocation"));
	this.operation_map.put("invariant", this.getClass().getMethod("unsetInvariant"));
	this.operation_map.put("init", this.getClass().getMethod("unsetInit"));
	this.operation_map.put("committed", this.getClass().getMethod("unsetCommitted"));
	this.operation_map.put("edge", this.getClass().getMethod("unsetEdge"));
	this.operation_map.put("select", this.getClass().getMethod("unsetSelect"));
	this.operation_map.put("guard", this.getClass().getMethod("unsetGuard"));
	this.operation_map.put("sync", this.getClass().getMethod("unsetSync"));
	this.operation_map.put("assign", this.getClass().getMethod("unsetAssign"));
	this.operation_map.put("system", this.getClass().getMethod("unsetSystem"));
	} catch (Exception e) {
	System.out.println(e.getMessage());
	e.printStackTrace();
	System.exit(1);
	}
}

public void unsetDocument () {
		this.context.getModelExpert().clearDocument();
}

public void unsetQueries () {
		this.checkMode("unset", "queries", ModeCode.EDITOR);
			this.context.getQueryExpert().clearQueries();
}

public void unsetTemplates () {
		this.checkMode("unset", "templates", ModeCode.EDITOR);
			this.context.getTemplateExpert().clearTemplates();
}

public void unsetDeclaration() {
		this.checkMode("unset", "declaration", ModeCode.EDITOR);
			if (this.getArgumentNumber()==1) {
				String name =  name = this.getArgumentAt(0);
				this.context.getTemplateExpert().setTemplateProperty(name, "declaration",  null);
				this.command_result.addArgument(name);
			} else
				this.context.getModelExpert().setDocumentProperty("declaration", null);
}

public void unsetQuery () {
		this.checkMode("unset", "query", ModeCode.EDITOR);
		String name =  name = this.getArgumentAt(0);
			this.context.getQueryExpert().removeQuery(name);
			this.command_result.addArgument(name);
}

public void unsetTemplate() {
		this.checkMode("unset", "template", ModeCode.EDITOR);
		String name =  name = this.getArgumentAt(0);
			this.context.getTemplateExpert().removeTemplate(name);
			this.command_result.addArgument(name);
}

public void unsetParameter () {
		this.checkMode("unset", "parameter", ModeCode.EDITOR);
		String name =  name = this.getArgumentAt(0);
			this.context.getTemplateExpert().setTemplateProperty(name, "parameter", null);
			this.command_result.addArgument(name);
}

public void unsetLocation () {
	this.checkMode("unset", "location", ModeCode.EDITOR);
	String template = this.getArgumentAt(0);
	String name =  name = this.getArgumentAt(1);
	if (name.equals("*")) 
		this.context.getLocationExpert().removeLocations(template);
	else {
		this.context.getEdgeExpert().removeEdges(template, name, name);
		this.context.getLocationExpert().removeLocation(template, name);
	}
}

public void unsetInvariant() {
		this.checkMode("unset", "invariant", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String name = this.getArgumentAt(1);
		this.context.getLocationExpert().setLocationProperty(template, name, "invariant", null);
		this.command_result.addArgument(template);
			this.command_result.addArgument(name);

}


public void unsetInit() {
		this.checkMode("unset", "init", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String name = this.getArgumentAt(1);
		this.context.getLocationExpert().setLocationProperty(template, name, "init", null);
		this.command_result.addArgument(template);
		this.command_result.addArgument(name);

}

public void unsetCommitted () {
		this.checkMode("unset", "committed", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String name = this.getArgumentAt(1);
		this.context.getLocationExpert().setLocationProperty(template, name, "committed", null);
		this.command_result.addArgument(template);
		this.command_result.addArgument(name);

}

public void unsetEdge() {
		this.checkMode("unset", "edge", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String source = this.getArgumentAt(1);
		String target = this.getArgumentAt(2);

	if (source.equals("*") && target.equals("*"))
		this.context.getEdgeExpert().removeEdges(template);
	else if (source.equals("*") || target.equals("*"))
		this.context.getEdgeExpert().removeEdges(template, source, target);
	else
		this.context.getEdgeExpert().removeEdge(template, source, target);
}

public void unsetSelect() {
		this.checkMode("unset", "select", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String source = this.getArgumentAt(1);
		String target = this.getArgumentAt(2);
		this.context.getEdgeExpert().setEdgeProperty(template, source, target, "select", null);
		this.command_result.addArgument(template);
			this.command_result.addArgument(source);
			this.command_result.addArgument(target);

}
		
public void unsetGuard () {
		this.checkMode("unset", "guard", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String source = this.getArgumentAt(1);
		String target = this.getArgumentAt(2);
		this.context.getEdgeExpert().setEdgeProperty(template, source, target, "guard", null);
		this.command_result.addArgument(template);
			this.command_result.addArgument(source);
			this.command_result.addArgument(target);

}

public void unsetSync () {
		this.checkMode("unset", "sync", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String source = this.getArgumentAt(1);
		String target = this.getArgumentAt(2);
		this.context.getEdgeExpert().setEdgeProperty(template, source, target, "sync", null);
		this.command_result.addArgument(template);
			this.command_result.addArgument(source);
			this.command_result.addArgument(target);

}

public void unsetAssign () {
		this.checkMode("unset", "assign", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String source = this.getArgumentAt(1);
		String target = this.getArgumentAt(2);
		this.context.getEdgeExpert().setEdgeProperty(template, source, target, "assign", null);
		this.command_result.addArgument(template);
			this.command_result.addArgument(source);
			this.command_result.addArgument(target);

}

public void unsetSystem () {
		this.checkMode("unset", "system",  ModeCode.EDITOR);
		this.context.getModelExpert().setDocumentProperty("system", null);

}


@Override
public boolean acceptMode (ModeCode mode) {
	switch(mode) {
		case EDITOR:
		return true;

		default:
		return false;
	}
}
}
