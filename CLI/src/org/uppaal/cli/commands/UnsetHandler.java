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
	this.operation_map.put("formula", this.getClass().getMethod("unsetFormula"));
	this.operation_map.put("comment", this.getClass().getMethod("unsetComment"));
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
	this.operation_map.put("option", this.getClass().getMethod("unsetOption"));
	this.operation_map.put("options", this.getClass().getMethod("unsetOptions"));
	this.operation_map.put("selection", this.getClass().getMethod("unsetSelection"));
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
			} else
				this.context.getModelExpert().setDocumentProperty("declaration", null);
}

public void unsetQuery () {
		this.checkMode("unset", "query", ModeCode.EDITOR);
		String name =  name = this.getArgumentAt(0);
			this.context.getQueryExpert().removeQuery(name);
}

public void unsetFormula () {
		this.checkMode("unset", "query", ModeCode.EDITOR);
		String index = this.getArgumentAt(0);
			this.context.getQueryExpert().setQueryProperty(index, "formula", null);
}

public void unsetComment () {
		this.checkMode("unset", "query", ModeCode.EDITOR);
		String index = this.getArgumentAt(0);
			this.context.getQueryExpert().setQueryProperty(index, "comment", null);
}

public void unsetTemplate() {
	this.checkMode("unset", "template", ModeCode.EDITOR);
	String name =  this.getArgumentAt(0);
	this.context.getTemplateExpert().removeTemplate(name);
	if (name.equals("*")) this.command_result.setResultCode(ResultCode.CLEAR_TEMPLATES);
	else {
		this.command_result.setResultCode(ResultCode.REMOVE_TEMPLATE);
		this.command_result.addArgument(name);
	}
}

public void unsetParameter () {
		this.checkMode("unset", "parameter", ModeCode.EDITOR);
		String name =  name = this.getArgumentAt(0);
			this.context.getTemplateExpert().setTemplateProperty(name, "parameter", null);
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

}


public void unsetInit() {
		this.checkMode("unset", "init", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String name = this.getArgumentAt(1);
		this.context.getLocationExpert().setLocationProperty(template, name, "init", null);

}

public void unsetCommitted () {
		this.checkMode("unset", "committed", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String name = this.getArgumentAt(1);
		this.context.getLocationExpert().setLocationProperty(template, name, "committed", null);

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

}
		
public void unsetGuard () {
		this.checkMode("unset", "guard", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String source = this.getArgumentAt(1);
		String target = this.getArgumentAt(2);
		this.context.getEdgeExpert().setEdgeProperty(template, source, target, "guard", null);

}

public void unsetSync () {
		this.checkMode("unset", "sync", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String source = this.getArgumentAt(1);
		String target = this.getArgumentAt(2);
		this.context.getEdgeExpert().setEdgeProperty(template, source, target, "sync", null);

}

public void unsetAssign () {
		this.checkMode("unset", "assign", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String source = this.getArgumentAt(1);
		String target = this.getArgumentAt(2);
		this.context.getEdgeExpert().setEdgeProperty(template, source, target, "assign", null);

}

public void unsetSystem () {
		this.checkMode("unset", "system",  ModeCode.EDITOR);
		this.context.getModelExpert().setDocumentProperty("system", null);
}

public void unsetOption () {
	String option = this.arguments.get(0);
	this.context.getOptionExpert().resetOption(option);
}

public void unsetOptions () {
	this.context.getOptionExpert().resetOptions();
}

public void unsetSelection () {
	String name = this.arguments.get(0);
	if (name.equals("selection"))
		this.context.getQueryExpert().clearSelectedQueries();
	else {
		int index = Integer.parseInt(name);
		this.context.getQueryExpert().unselectQuery(index);
	}
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
