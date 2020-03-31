package org.uppaal.cli.commands;

/**
* document expert, responsible for all operations on a document
*/

import com.uppaal.model.core2.QueryList;
import com.uppaal.model.core2.PrototypeDocument;
import org.uppaal.cli.commands.Command.ObjectCode;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.SetPropertyCommand;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class DocumentExpert extends AbstractExpert {
private Document document;

public DocumentExpert (Context context) {
	super(context);
	this.document = this.context.getDocument();
}

/**
* @return the global declaration field
* @exception a missing element exception if the global declaration field is not set
*/
public String getGlobalDeclaration() {
	if (!this.document.isPropertyLocal("declaration"))
		this.throwMissingElementException(ObjectCode.DECLARATION, null);
	return (String)this.document.getPropertyValue("declaration");
}

/**
* set the global declaration of this document
* @param declaration the new declaration for this document
*/
public void setGlobalDeclaration (String declaration) {
	SetPropertyCommand command = new SetPropertyCommand(this.document, "declaration", declaration);
	command.execute();
	this.context.addCommand(command);
}

/**
* @return a string describing the system of the current document
*/
public String getSystem() {
	if (!this.document.isPropertyLocal("system")) 
		this.throwMissingElementException (ObjectCode.SYSTEM, null);
	return (String) this.document.getPropertyValue("system");
}

/**
* set the value of the system property of this document
* @param system the new system value
*/
public void setSystem (String system) {
	SetPropertyCommand command = new SetPropertyCommand(this.document, "system", system);
	command.execute();
	this.context.addCommand(command);
}

/**
* load a document described by its location
* @param the location of the document
* @return the newly loaded document
* @exception an exception if the location of the document is not well formated
*/

public Document loadDocument (String filename) throws IOException, MalformedURLException  {
	PrototypeDocument doc_loader = new PrototypeDocument();
		//URL location = new URL("file", null, args[0]);
	URL location = new URL("file://localhost"+System.getProperty("user.dir")+"/"+filename);
	this.document = doc_loader.load(location);
	QueryList queries = this.document.getQueryList();
	for (int i=0;i<queries.size();i++)
		queries.get(i).setProperty("name", "q"+(i+1));
	return this.document;
}
}