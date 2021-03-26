package org.uppaal.cli.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import org.junit.Test;
import org.uppaal.cli.commands.Handler;
import org.uppaal.cli.commands.CommandResult;
import org.uppaal.cli.exceptions.UnknownCommandException;
import org.uppaal.cli.exceptions.ParserException;

/**
* concrete class implementing all tests for the parser
*/

public class ParserTest extends AbstractTest {
@Test
public void test() {

// an unknown command throws an unknown command exception

	Exception exception = assertThrows(UnknownCommandException.class, () -> {
	this.parser.parseCommand("hello");
	});
	assertEquals("Unknown command: hello", exception.getMessage());

// a command requiring an argument without any reference throws a parser exception

	exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("show");
	});

	exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("load");
	});

	exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("save");
	});

	exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("set");
	});

	exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("unset");
	});

	exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("reset");
	});

	exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("reset toto");
	});

	exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("reset toto as");
	});

	exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("start");
	});

	exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("check");
	});

// a command without any argument throws an exception if any is provided

	exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("exit toto");
	});

	exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("compile toto");
	});

	exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("disconnect toto");
	});

	exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("connect toto");
	});

// A start command with an unexpected mode throws an exception

	exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("start toto");
	});

// a load command with an incomplete list of arguments throws an exception

	exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("load document");
	});

	exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("load document from");
	});

	exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("load document to toto.txt");
	});

// a load command with an incomplete list of arguments throws an exception

	exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("save document");
	});

	exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("save document to");
	});

	exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("save document from toto.txt");
	});

// a command with an incomplete reference throws an exception

exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("show Train(");
	});

exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("show Train(Appr");
	});

exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("show Train(Appr->");
	});

exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("show Train(Appr->Stop");
	});

exception = assertThrows(ParserException.class, () -> {
	this.parser.parseCommand("show Train(Appr->Stop).");
	});
}
}