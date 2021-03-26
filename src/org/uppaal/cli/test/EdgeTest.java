package org.uppaal.cli.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import org.junit.Test;
import org.uppaal.cli.commands.Handler;
import org.uppaal.cli.commands.CommandResult;
import org.uppaal.cli.exceptions.UnknownCommandException;
import org.uppaal.cli.exceptions.ParserException;
import org.uppaal.cli.exceptions.MissingElementException;

/**
* concrete class implementing all tests for the parser
*/

public class EdgeTest extends AbstractTest {
@Test
public void test() {

// ok commands

	this.parser.parseCommand("set Train").handle();
	this.parser.parseCommand("set Train(Go)").handle();
	this.parser.parseCommand("set Train(Stop)").handle();
	this.parser.parseCommand("set Train(Go -> Stop)").handle();

	this.parser.parseCommand("show Train(*->*)").handle();
	this.parser.parseCommand("show Train(Go -> Stop).guard").handle();
	this.parser.parseCommand("show Train(Go -> Stop).assign").handle();
	this.parser.parseCommand("show Train(Go -> Stop).sync").handle();
	this.parser.parseCommand("show Train(Go -> Stop).select").handle();
	this.parser.parseCommand("show Train(Go -> Stop).controllable").handle();

	this.parser.parseCommand("set Train(Go -> Stop).guard = 'toto'").handle();
	this.parser.parseCommand("set Train(Go -> Stop).assign = 'toto'").handle();
	this.parser.parseCommand("set Train(Go -> Stop).sync = 'toto'").handle();
	this.parser.parseCommand("set Train(Go -> Stop).select = 'toto'").handle();
	this.parser.parseCommand("set Train(Go -> Stop).controllable").handle();

	this.parser.parseCommand("unset Train(Go -> Stop).guard").handle();
	this.parser.parseCommand("unset Train(Go -> Stop).assign").handle();
	this.parser.parseCommand("unset Train(Go -> Stop).sync").handle();
	this.parser.parseCommand("unset Train(Go -> Stop).select").handle();
	this.parser.parseCommand("unset Train(Go -> Stop).controllable").handle();

	this.parser.parseCommand("unset Train(Go -> Stop)").handle();
	this.parser.parseCommand("set Train(Go -> Stop) = {guard:'toto';assign:'toto';select:'toto';sync:'toto'}").handle();
		this.parser.parseCommand("unset Train(*->*)").handle();

// trying to show an edge from an unknown template throws a missing element exception

	Exception exception = assertThrows(MissingElementException.class, () -> {
	this.parser.parseCommand("show toto(Go -> Stop)").handle();
	});
	assertEquals("Error: missing template toto", exception.getMessage());

// trying to show the guard of an unknown edge throws a missing element exception

	 exception = assertThrows(MissingElementException.class, () -> {
	this.parser.parseCommand("show Train(Go -> Stop).guard").handle();
	});
	assertEquals("Error: missing edge Go -> Stop", exception.getMessage());

// trying to set the parameter of an unknown template throws a missing element exception

	 exception = assertThrows(MissingElementException.class, () -> {
	this.parser.parseCommand("set Train(Go -> Stop).guard = 'toto'").handle();
	});
	assertEquals("Error: missing edge Go -> Stop", exception.getMessage());
	
	// trying to show the declaration of an unknown template throws a missing element exception

	 exception = assertThrows(MissingElementException.class, () -> {
	this.parser.parseCommand("show Train(Go -> Stop).assign").handle();
	});
	assertEquals("Error: missing edge Go -> Stop", exception.getMessage());

// trying to set the declaration of an unknown template throws a missing element exception

	 exception = assertThrows(MissingElementException.class, () -> {
	this.parser.parseCommand("set Train(Go -> Stop).assign = 'toto'").handle();
	});
	assertEquals("Error: missing edge Go -> Stop", exception.getMessage());


	// trying to show the declaration of an unknown template throws a missing element exception

	 exception = assertThrows(MissingElementException.class, () -> {
	this.parser.parseCommand("show Train(Go -> Stop).sync").handle();
	});
	assertEquals("Error: missing edge Go -> Stop", exception.getMessage());

// trying to set the declaration of an unknown template throws a missing element exception

	 exception = assertThrows(MissingElementException.class, () -> {
	this.parser.parseCommand("set Train(Go -> Stop).sync = 'toto'").handle();
	});
	assertEquals("Error: missing edge Go -> Stop", exception.getMessage());
	
	
	// trying to show the declaration of an unknown template throws a missing element exception

	 exception = assertThrows(MissingElementException.class, () -> {
	this.parser.parseCommand("show Train(Go -> Stop).select").handle();
	});
	assertEquals("Error: missing edge Go -> Stop", exception.getMessage());

// trying to set the declaration of an unknown template throws a missing element exception

	 exception = assertThrows(MissingElementException.class, () -> {
	this.parser.parseCommand("set Train(Go -> Stop).select = 'toto'").handle();
	});
	assertEquals("Error: missing edge Go -> Stop", exception.getMessage());
}
}