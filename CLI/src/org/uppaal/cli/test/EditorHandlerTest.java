package org.uppaal.cli.test;

import com.uppaal.model.core2.Document;
import org.uppaal.cli.enumerations.OperationCode;
import org.uppaal.cli.enumerations.ModeCode;
import org.uppaal.cli.enumerations.ObjectCode;
import org.uppaal.cli.commands.Handler;
import org.uppaal.cli.commands.CommandHandler;
import org.uppaal.cli.commands.CommandResult;
import org.uppaal.cli.context.Context;
import org.uppaal.cli.commands.Command;
import org.uppaal.cli.exceptions.*;

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

	this.context.getEngineExpert().connectEngine();
	this.command.setOperationCode(OperationCode.START);
	this.command.setMode(ModeCode.EDITOR);
	this.command_handler.handle(command);
	assertEquals(this.command_handler.getMode(), ModeCode.EDITOR);
}

@After
public void teardown () {
	this.context.getEngineExpert().disconnectEngine();
}

// first test, the document of the context should not be null after a correct import command

@Test
public void testImportValidDocument () throws Exception {
	this.command.setOperationCode(OperationCode.IMPORT);
	this.command.setObjectCode(ObjectCode.DOCUMENT);
	this.command.addArgument("train-gate.xta");

	Document document = this.context.getDocument();
	this.command_handler.handle(this.command);
	assertNotEquals(this.context.getDocument(), document);
}

// second test, check that importing a file with a wrong extension raises an exception

@Test (expected = WrongExtensionException.class)
public void testImportWrongExtension () throws Exception {
	this.command.setOperationCode(OperationCode.IMPORT);
	this.command.setObjectCode(ObjectCode.DOCUMENT);
	this.command.addArgument("train-gate.doc");

	Document document = this.context.getDocument();
	this.command_handler.handle(this.command);
	assertNotEquals(this.context.getDocument(), document);
}

// third test, check that importing something without argument raises an exception

@Test (expected = MissingArgumentException.class)
public void testImportWithoutFilename () throws Exception {
	this.command.setOperationCode(OperationCode.IMPORT);
	this.command.setObjectCode(ObjectCode.DOCUMENT);
//	this.command.addArgument("train-gate.doc");

	Document document = this.context.getDocument();
	this.command_handler.handle(this.command);
	assertNotEquals(this.context.getDocument(), document);
}

// Fourth test, check that importing something with two arguments raises an exception

@Test (expected = ExtraArgumentException.class)
public void testImportWithTwoFilenames () throws Exception {
	this.command.setOperationCode(OperationCode.IMPORT);
	this.command.setObjectCode(ObjectCode.DOCUMENT);
	this.command.addArgument("train-gate.doc");
	this.command.addArgument("train-gate.doc");

	Document document = this.context.getDocument();
	this.command_handler.handle(this.command);
	assertNotEquals(this.context.getDocument(), document);
}

// fivth test, check that importing a valid list of queries add some queries in the context

@Test
public void testImportValidQueryList () throws Exception {
	this.command.setOperationCode(OperationCode.IMPORT);
	this.command.setObjectCode(ObjectCode.QUERIES);
	this.command.addArgument("train-gate.q");

	this.command_handler.handle(this.command);
	assertNotEquals(this.context.getQueryExpert().getQueryNumber(), 0);
}

// sixth test, check that loading a query file with a wrong exception raise an exception

@Test (expected = WrongExtensionException.class)
public void testImportWrongQueryExtension () throws Exception {
	this.command.setOperationCode(OperationCode.IMPORT);
	this.command.setObjectCode(ObjectCode.QUERIES);
	this.command.addArgument("train-gate.xta");

	this.command_handler.handle(this.command);
	assertNotEquals(this.context.getQueryExpert().getQueryNumber(), 0);
}

// seventh test, check that exporting a valid document well create a file

@Test
public void testExportValidDocument () throws Exception {
	this.command.setOperationCode(OperationCode.IMPORT);
	this.command.setObjectCode(ObjectCode.DOCUMENT);
	this.command.addArgument("train-gate.xta");

	Document document = this.context.getDocument();
	this.command_handler.handle(this.command);
	assertNotEquals(this.context.getDocument(), document);

	this.command.clear();
	this.command.setOperationCode(OperationCode.EXPORT);
	this.command.setObjectCode(ObjectCode.DOCUMENT);
	this.command.addArgument("output.xta");
	this.command_handler.handle(this.command);

	this.command.clear();
	this.command.setOperationCode(OperationCode.IMPORT);
	this.command.setObjectCode(ObjectCode.DOCUMENT);
	this.command.addArgument("output.xta");

	document = this.context.getDocument();
	this.command_handler.handle(this.command);
	assertNotEquals(this.context.getDocument(), document);
}

// eighth test, check that exporting a document with a wrong extension throws an exception

@Test (expected = WrongExtensionException.class)
public void testExportWrongExtension () throws Exception {
	this.command.setOperationCode(OperationCode.EXPORT);
	this.command.setObjectCode(ObjectCode.DOCUMENT);
	this.command.addArgument("train-gate.doc");

	Document document = this.context.getDocument();
	this.command_handler.handle(this.command);
	assertNotEquals(this.context.getDocument(), document);
}

// ninth  test, check that exporting something without filename raise an exception

@Test (expected = MissingArgumentException.class)
public void testExportWithoutFilename () throws Exception {
	this.command.setOperationCode(OperationCode.EXPORT);
	this.command.setObjectCode(ObjectCode.DOCUMENT);
//	this.command.addArgument("train-gate.doc");

	Document document = this.context.getDocument();
	this.command_handler.handle(this.command);
	assertNotEquals(this.context.getDocument(), document);
}

// tenth test, check that exporting something with two arguments throws an exception

@Test (expected = ExtraArgumentException.class)
public void testExportWithTwoFilenames () throws Exception {
	this.command.setOperationCode(OperationCode.EXPORT);
	this.command.setObjectCode(ObjectCode.DOCUMENT);
	this.command.addArgument("train-gate.doc");
	this.command.addArgument("train-gate.doc");

	Document document = this.context.getDocument();
	this.command_handler.handle(this.command);
	assertNotEquals(this.context.getDocument(), document);
}


// eleventh test, check that exporting a valid list of queries create an importable file

@Test
public void testExportValidQueryList () throws Exception {
	this.command.setOperationCode(OperationCode.IMPORT);
	this.command.setObjectCode(ObjectCode.QUERIES);
	this.command.addArgument("train-gate.q");

	this.command_handler.handle(this.command);
	assertNotEquals(this.context.getQueryExpert().getQueryNumber(), 0);

	this.command.clear();
	this.command.setOperationCode(OperationCode.EXPORT);
	this.command.setObjectCode(ObjectCode.QUERIES);
	this.command.addArgument("output.q");
	this.command_handler.handle(this.command);

	this.command.clear();
	this.command.setOperationCode(OperationCode.IMPORT);
	this.command.setObjectCode(ObjectCode.QUERIES);
	this.command.addArgument("output.q");

	this.command_handler.handle(this.command);
	assertNotEquals(this.context.getQueryExpert().getQueryNumber(), 0);
}

// twelvth test, check that exporting a list of queries with a wrong extension throws an exception

@Test (expected = WrongExtensionException.class)
public void testExportWrongQueryExtension () throws Exception {
	this.command.setOperationCode(OperationCode.EXPORT);
	this.command.setObjectCode(ObjectCode.QUERIES);
	this.command.addArgument("output.xta");

	this.command_handler.handle(this.command);
	assertNotEquals(this.context.getQueryExpert().getQueryNumber(), 0);
}

// Thirteenth test, check that clearing a valid document remove the current one

@Test 
public void testClearValidDocument () throws Exception {
	this.command.setOperationCode(OperationCode.IMPORT);
	this.command.setObjectCode(ObjectCode.DOCUMENT);
	this.command.addArgument("train-gate.xta");

	Document document = this.context.getDocument();
	this.command_handler.handle(this.command);
	assertNotEquals(this.context.getDocument(), document);

	this.command.clear();
	this.command.setOperationCode(OperationCode.UNSET);
	this.command.setObjectCode(ObjectCode.DOCUMENT);

	document = this.context.getDocument();
	this.command_handler.handle(this.command);
	assertNotEquals(this.context.getDocument(), document);
}

// fourteenth test, check that clearing the document with an argument throws an exception

@Test (expected = ExtraArgumentException.class)
public void testClearExtraArgument () throws Exception {
	this.command.setOperationCode(OperationCode.UNSET);
	this.command.setObjectCode(ObjectCode.DOCUMENT);
	this.command.addArgument("train-gate.doc");

	Document document = this.context.getDocument();
	this.command_handler.handle(this.command);
	assertNotEquals(this.context.getDocument(), document);
}

// fiveteenth test, check that clearing a valid list of queries removes the queries of the current document

@Test 
public void testClearValidQueries () throws Exception {
	this.command.setOperationCode(OperationCode.IMPORT);
	this.command.setObjectCode(ObjectCode.QUERIES);
	this.command.addArgument("train-gate.q");
	this.command_handler.handle(this.command);
	assertNotEquals(this.context.getQueryExpert().getQueryNumber(), 0);

	this.command.clear();
	this.command.setOperationCode(OperationCode.UNSET);
	this.command.setObjectCode(ObjectCode.QUERIES);
	this.command_handler.handle(this.command);
	assertEquals(this.context.getQueryExpert().getQueryNumber(), 0);
}

// sixteenth test, check that showing templates after a correct import returns two headers

@Test
public void testshowTemplates () throws Exception {
	this.command.setOperationCode(OperationCode.IMPORT);
	this.command.setObjectCode(ObjectCode.DOCUMENT);
	this.command.addArgument("train-gate.xta");
	Document document = this.context.getDocument();
	this.command_handler.handle(this.command);

	this.command.clear();
	this.command.setOperationCode(OperationCode.SHOW);
	this.command.setObjectCode(ObjectCode.TEMPLATES);
	CommandResult result = this.command_handler.handle(command);
	assertEquals(result.getArgumentNumber(), 2);
}

// seventeenth test, check that showing templates with an extra argument throws an exception

@Test (expected = ExtraArgumentException.class)
public void testshowTemplatesWithExtraArgument () throws Exception {
	this.command.setOperationCode(OperationCode.IMPORT);
	this.command.setObjectCode(ObjectCode.DOCUMENT);
	this.command.addArgument("train-gate.xta");
	Document document = this.context.getDocument();
	this.command_handler.handle(this.command);

	this.command.clear();
	this.command.setOperationCode(OperationCode.SHOW);
	this.command.setObjectCode(ObjectCode.TEMPLATES);
	this.command.addArgument("document");
	CommandResult result = this.command_handler.handle(command);
	assertEquals(result.getArgumentNumber(), 2);
}

// eighteenth test, test that showing declarations well returns the global declaration of the document

@Test
public void testshowDeclaration () throws Exception {
	this.command.setOperationCode(OperationCode.IMPORT);
	this.command.setObjectCode(ObjectCode.DOCUMENT);
	this.command.addArgument("train-gate.xta");
	Document document = this.context.getDocument();
	this.command_handler.handle(this.command);

	this.command.clear();
	this.command.setOperationCode(OperationCode.SHOW);
	this.command.setObjectCode(ObjectCode.DECLARATION);
	CommandResult result = this.command_handler.handle(command);
	assertEquals(result.getArgumentNumber(), 1);
	assertNotNull(result.getArgumentAt(0));
}

// nineteenth test, check that showing the declaration of an existing template well returns it

@Test
public void testshowTemplateDeclaration () throws Exception {
	this.command.setOperationCode(OperationCode.IMPORT);
	this.command.setObjectCode(ObjectCode.DOCUMENT);
	this.command.addArgument("train-gate.xta");
	Document document = this.context.getDocument();
	this.command_handler.handle(this.command);

	this.command.clear();
	this.command.setOperationCode(OperationCode.SHOW);
	this.command.setObjectCode(ObjectCode.DECLARATION);
	this.command.addArgument("Train");
	CommandResult result = this.command_handler.handle(command);
	assertEquals(result.getArgumentNumber(), 1);
	assertNotNull(result.getArgumentAt(0));
}

// twentyth test, check that showing an unexisting template throws an exception

@Test (expected = MissingElementException.class)
public void testshowUnexistingTemplateDeclaration () throws Exception {
	this.command.setOperationCode(OperationCode.IMPORT);
	this.command.setObjectCode(ObjectCode.DOCUMENT);
	this.command.addArgument("train-gate.xta");
	Document document = this.context.getDocument();
	this.command_handler.handle(this.command);

	this.command.clear();
	this.command.setOperationCode(OperationCode.SHOW);
	this.command.setObjectCode(ObjectCode.DECLARATION);
	this.command.addArgument("Unexisting");
	CommandResult result = this.command_handler.handle(command);
	assertEquals(result.getArgumentNumber(), 1);
	assertNotNull(result.getArgumentAt(0));
}

// twentyfirst test, check that showing declaration with an extra argument throws an exception

@Test (expected = ExtraArgumentException.class)
public void testShowDeclarationWithExtraArgument () throws Exception {
	this.command.setOperationCode(OperationCode.IMPORT);
	this.command.setObjectCode(ObjectCode.DOCUMENT);
	this.command.addArgument("train-gate.xta");
	Document document = this.context.getDocument();
	this.command_handler.handle(this.command);

	this.command.clear();
	this.command.setOperationCode(OperationCode.SHOW);
	this.command.setObjectCode(ObjectCode.DECLARATION);
	this.command.addArgument("Unexisting");
	this.command.addArgument("Unexisting");
	CommandResult result = this.command_handler.handle(command);
	assertEquals(result.getArgumentNumber(), 1);
	assertNotNull(result.getArgumentAt(0));
}

// twenty second test, check that showing a template well returns it

@Test
public void testshowTemplate () throws Exception {
	this.command.setOperationCode(OperationCode.IMPORT);
	this.command.setObjectCode(ObjectCode.DOCUMENT);
	this.command.addArgument("train-gate.xta");
	Document document = this.context.getDocument();
	this.command_handler.handle(this.command);

	this.command.clear();
	this.command.setOperationCode(OperationCode.SHOW);
	this.command.setObjectCode(ObjectCode.TEMPLATE);
	this.command.addArgument("Train");
	CommandResult result = this.command_handler.handle(command);
	assertEquals(result.getArgumentNumber(), 1);
	assertNotNull(result.getArgumentAt(0));
	System.out.println(result.getArgumentAt(0));
}
}
