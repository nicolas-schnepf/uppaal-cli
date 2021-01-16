package org.uppaal.cli.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import org.junit.Test;
import org.uppaal.cli.commands.Handler;
import org.uppaal.cli.commands.CommandResult;
import org.uppaal.cli.exceptions.UnknownCommandException;
import org.uppaal.cli.exceptions.ParserException;
import org.uppaal.cli.exceptions.MissingElementException;
import org.uppaal.cli.exceptions.ExistingElementException;
import org.uppaal.cli.commands.CommandResult;
import org.uppaal.cli.enumerations.ResultCode;

/**
* concrete class implementing all tests for the parser
*/

public class TemplateTest extends AbstractTest {
@Test
public void test() {

// ok commands

	this.parser.parseCommand("show templates").handle();
	this.parser.parseCommand("unset Train").handle();
	this.parser.parseCommand("set Train").handle();
	this.parser.parseCommand("show Train").handle();
	this.parser.parseCommand("show Train.parameter").handle();
	this.parser.parseCommand("show Train.declaration").handle();
	this.parser.parseCommand("unset Train").handle();
	this.parser.parseCommand("set Train = {parameter:'toto'; declaration:'toto'}").handle();
	this.parser.parseCommand("reset Train as Toto").handle();
	this.parser.parseCommand("unset templates").handle();

// trying to import templates from a non existing file returns an io error

	CommandResult result = this.parser.parseCommand("load templates from toto.xml").handle();
 assertEquals(ResultCode.IO_ERROR, result.getResultCode());

// trying to show an unknown template throws a missing element exception

	Exception exception = assertThrows(MissingElementException.class, () -> {
	this.parser.parseCommand("show toto").handle();
	});
	assertEquals("Error: missing template toto", exception.getMessage());

// trying to show the parameter of an unknown template throws a missing element exception

	 exception = assertThrows(MissingElementException.class, () -> {
	this.parser.parseCommand("show toto.parameter").handle();
	});
	assertEquals("Error: missing template toto", exception.getMessage());

// trying to set the parameter of an unknown template throws a missing element exception

	 exception = assertThrows(MissingElementException.class, () -> {
	this.parser.parseCommand("set toto.parameter = 'toto'").handle();
	});
	assertEquals("Error: missing template toto", exception.getMessage());
	
	// trying to show the declaration of an unknown template throws a missing element exception

	 exception = assertThrows(MissingElementException.class, () -> {
	this.parser.parseCommand("show toto.declaration").handle();
	});
	assertEquals("Error: missing template toto", exception.getMessage());

// trying to set the declaration of an unknown template throws a missing element exception

	 exception = assertThrows(MissingElementException.class, () -> {
	this.parser.parseCommand("set toto.declaration = 'toto'").handle();
	});
	assertEquals("Error: missing template toto", exception.getMessage());

// trying to reset an inexisting template throws a missing element exception

	 exception = assertThrows(MissingElementException.class, () -> {
	this.parser.parseCommand("reset toto as titi").handle();
	});
	assertEquals("Error: missing template toto", exception.getMessage());

// trying to set an existing template throws an existing element exception

	this.parser.parseCommand("set toto").handle();
	 exception = assertThrows(ExistingElementException.class, () -> {
	this.parser.parseCommand("set toto").handle();
	});
	assertEquals("Error: existing template toto", exception.getMessage());
}
}