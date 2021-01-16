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

public class LocationTest extends AbstractTest {
@Test
public void test() {

// ok commands

	this.parser.parseCommand("set Train").handle();
	this.parser.parseCommand("show Train(*)").handle();
	this.parser.parseCommand("set Train(Go)").handle();
	this.parser.parseCommand("set Train(Go).invariant = 'toto'").handle();
	this.parser.parseCommand("unset Train(Go).invariant").handle();
	this.parser.parseCommand("reset Train(Go) as Stop").handle();
	this.parser.parseCommand("unset Train(Go)").handle();
	this.parser.parseCommand("set Train(Start)={invariant:'toto'}").handle();
	this.parser.parseCommand("unset Train(*)").handle();

// trying to show a location in an unknown template throws a missing element exception

	Exception exception = assertThrows(MissingElementException.class, () -> {
	this.parser.parseCommand("show toto(Go)").handle();
	});
	assertEquals("Error: missing template toto", exception.getMessage());

// trying to show a location in an unknown template throws a missing element exception

	 exception = assertThrows(MissingElementException.class, () -> {
	this.parser.parseCommand("show Train(toto)").handle();
	});
	assertEquals("Error: missing location toto", exception.getMessage());

// trying to show the parameter of an unknown template throws a missing element exception

	 exception = assertThrows(MissingElementException.class, () -> {
	this.parser.parseCommand("show Train(toto).invariant").handle();
	});
	assertEquals("Error: missing location toto", exception.getMessage());

// trying to set the parameter of an unknown template throws a missing element exception

	 exception = assertThrows(MissingElementException.class, () -> {
	this.parser.parseCommand("set Train(toto).invariant = 'toto'").handle();
	});
	assertEquals("Error: missing location toto", exception.getMessage());
}
}