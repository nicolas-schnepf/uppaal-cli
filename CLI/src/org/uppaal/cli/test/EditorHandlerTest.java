package org.uppaal.cli.test;

import com.uppaal.model.core2.Document;
import org.uppaal.cli.handlers.Handler;
import org.uppaal.cli.handlers.CommandHandler;
import org.uppaal.cli.Context;
import org.uppaal.cli.Command;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

public class EditorHandlerTest {
private CommandHandler command_handler;
private Context context;
private Command command;

@Before
public void setup () throws Exception {

// initialize the different fields of the test handler

	this.context = new Context();
	this.command_handler = new CommandHandler(context);
	this.command = new Command();

// connect the engine and setup the command handler

	this.context.connectEngine();
	this.command.setCommandCode(Command.CommandCode.START);
	this.command.setMode(Handler.HandlerCode.EDITOR);
	this.command_handler.handle(command);
	assertEquals(this.command_handler.getMode(), Handler.HandlerCode.EDITOR);
}

@After
public void teardown () {
	this.context.disconnectEngine();
}

// first test, the document of the context should not be null after a correct import command

@Test
public void testImportValidDocument () {
	this.command.setCommandCode(Command.CommandCode.IMPORT);
	this.command.setObjectCode(Command.ObjectCode.DOCUMENT);
	this.command.addArgument("train-gate.xta");

	Document document = this.context.getDocument();
	this.command_handler.handle(this.command);
	assertNotEquals(this.context.getDocument(), document);
}
}