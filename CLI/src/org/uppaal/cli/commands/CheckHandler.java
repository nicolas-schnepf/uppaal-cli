package org.uppaal.cli.commands;

import org.uppaal.cli.context.ModeCode;
import org.uppaal.cli.enumerations.ResultCode;
import org.uppaal.cli.context.Context;
import com.uppaal.engine.EngineException;
import java.util.LinkedList;

/**
* concrete class implementing a check handler
* supporting all possible check commands under mode control
*/

public class CheckHandler extends AbstractHandler {
public CheckHandler (Context context) {
	super(context, "check");
	try {
	this.operation_map.put("query", this.getClass().getMethod("checkQuery"));
	this.operation_map.put("selection", this.getClass().getMethod("checkSelection"));
	} catch (Exception e) {
	System.out.println(e.getMessage());
	e.printStackTrace();
	System.exit(1);
	}
}

public void checkQuery() {
			this.checkMode("check", "query", ModeCode.VERIFIER);
		String name = this.getArgumentAt(0);

	try {
		if (name.equals("queries")) {
			LinkedList<String> results = this.context.getEngineExpert().checkQueries();
			for (String result: results) this.command_result.addArgument(result);
		} else {
			int index = Integer.parseInt(name);
			String result = this.context.getEngineExpert().checkQuery(index);
			this.command_result.addArgument(result);
		}
	} catch (EngineException e) {
		this.command_result.setResultCode(ResultCode.ENGINE_ERROR);
	}
}

public void checkSelection () {
	this.checkMode("check", "selection", ModeCode.VERIFIER);

	try {
	LinkedList<String> results = this.context.getEngineExpert().checkSelectedQueries();
	for (String result: results) this.command_result.addArgument(result);
	} catch (EngineException e) {
		this.command_result.setResultCode(ResultCode.ENGINE_ERROR);
	}
}

@Override
public boolean acceptMode (ModeCode mode) {
	switch(mode) {
		case VERIFIER:
		return true;

		default:
		return false;
	}
}
}
