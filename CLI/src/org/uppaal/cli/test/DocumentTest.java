package org.uppaal.cli.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import org.junit.Test;
import org.uppaal.cli.commands.Handler;
import org.uppaal.cli.commands.CommandResult;

/**
* concrete class implementing all tests for a document
*/

public class DocumentTest extends AbstractTest {
@Test
public void test() {

// an empty document contains no template

	Handler handler = this.parser.parseCommand("show templates");
	CommandResult result = handler.handle();
	assertEquals(result.getArgumentNumber(), 0);
	System.out.println("finished!");
}
}