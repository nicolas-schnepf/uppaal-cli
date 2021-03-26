package org.uppaal.cli.test;

import static org.junit.Assert.assertEquals;
import org.uppaal.cli.context.Context;
import org.uppaal.cli.frontend.CommandParser;
import org.uppaal.cli.frontend.CommandCompleter;

/**
* abstract class implementing a test handler
* providing a generic constructor to init the fields of all test handlers
*/

public abstract class AbstractTest {

// context of this test handler
protected Context context;

// command parser of this test handler
protected CommandParser parser;

public AbstractTest() {
	this.context = new Context();
	this.parser = new CommandParser(this.context);
	CommandCompleter completer = new CommandCompleter(context);
	this.parser.setCommandCompleter(completer);
}
}