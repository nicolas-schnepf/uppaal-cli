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
* concrete class implementing a reset handler
* supporting all possible reset commands under mode control
*/

public class ResetHandler extends AbstractHandler {
public ResetHandler (Context context) {
	super(context, "reset");
	try {
	this.operation_map.put("template", this.getClass().getMethod("resetTemplate"));
	this.operation_map.put("location", this.getClass().getMethod("resetLocation"));
	this.operation_map.put("option", this.getClass().getMethod("resetOption"));
	this.operation_map.put("options", this.getClass().getMethod("resetOptions"));
	this.operation_map.put("precision", this.getClass().getMethod("resetPrecision"));
	} catch (Exception e) {
	System.out.println(e.getMessage());
	e.printStackTrace();
	System.exit(1);
	}
}

public void resetTemplate () {
			this.checkMode("reset", "template", ModeCode.EDITOR);
		String name = this.getArgumentAt(0);
		String new_name = this.getArgumentAt(1);
			this.context.getTemplateExpert().setTemplateProperty(name, "name", new_name);
			this.command_result.addArgument(name);
			this.command_result.addArgument(new_name);
			this.command_result.setResultCode(ResultCode.RENAME_TEMPLATE);
}

public void resetLocation () {
		this.checkMode("reset", "location", ModeCode.EDITOR);
		String template = this.getArgumentAt(0);
		String name = this.getArgumentAt(1);
		String new_name = this.getArgumentAt(2);
		this.context.getLocationExpert().setLocationProperty(template, name, "name", new_name);
}


public void resetOption () {
	String option = this.arguments.get(0);
	this.context.getOptionExpert().resetOption(option);
}

public void resetOptions () {
	this.context.getOptionExpert().resetOptions();
}

public void resetPrecision () {
	this.command_result.addArgument("0.0");
	this.command_result.setResultCode(ResultCode.SET_PRECISION);
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
