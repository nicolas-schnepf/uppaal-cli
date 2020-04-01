package org.uppaal.cli.handlers;

/**
* base abstract class for all handlers
* providing access to a protected context and to the current mode
*/

import org.uppaal.cli.commands.Context;

public abstract class AbstractHandler implements Handler {

// protected context of this handler

protected Context context;

/**
* protected constructor of an abstract handler
* @param context the context of this handler
*/
protected AbstractHandler (Context context) {
	this.context = context;
}


/***
* set the uppaal context of this command handler
* @param context the context to set
*/

public void setContext (Context context) {
	this.context = context;
}

@Override
public HandlerCode getMode () {
	return this.context.getMode();
}
}
