package org.uppaal.cli.context;

/**
* model expert, responsible for all operations on a document and the corresponding system
*/

import com.uppaal.model.core2.QueryList;
import com.uppaal.model.core2.PrototypeDocument;

import com.uppaal.model.core2.QueryList;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.SetPropertyCommand;
import com.uppaal.engine.Engine;
import com.uppaal.engine.EngineException;
import com.uppaal.model.system.UppaalSystem;
import com.uppaal.engine.Problem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.net.MalformedURLException;
import java.net.URL;

public class ModelExpert extends AbstractExpert {
private Document document;
private UppaalSystem system;

public ModelExpert (Context context) {
	super(context);
	com.uppaal.model.io2.XMLReader.setXMLResolver(new com.uppaal.model.io2.UXMLResolver());
	this.document = new Document(new PrototypeDocument());
	this.system = null;
}

/**
* @return the document of this expert
*/
public Document getDocument () {
	return this.document;
}


/**
* return the value of a certain property of this document
* @param property the name of the property to return
* @return the value of the property if it is set
* @exception a missing element exception if the provided property field is not set
*/
public String getDocumentProperty (String property) {
	if (!this.document.isPropertyLocal(property)) {
		switch (property) {
			case "declaration":
			this.throwMissingElementException("declaration", null);
			break;

			case "system":
			this.throwMissingElementException ("system", null);
		break;
		}
	}


	return (String)this.document.getPropertyValue(property);
}

/**
* set the value of a property of the current document
* @param value the name of the value to be set
* @param property the new value for the property
*/
public void setDocumentProperty(String property, String value) {
	SetPropertyCommand command = new SetPropertyCommand(this.document, property, value);
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

	this.context.clearCommands();
	return this.document;
}


/**
* save the document attached to this context
* @param filename the path to the file to save the current document
* @exception an IO exception if there is a problem with the provided filename
*/
public void saveDocument (String filename) throws IOException {
	this.document.save(filename);
}

/**
* clear the current document
* @return the newly created document
*/
public Document clearDocument() {
	this.document = new Document(new PrototypeDocument());
	return this.document;
}

/**
* compile the current document
* @return a list of problems encountered during compilation, empty if everything is find
*/
public LinkedList<String> compileDocument () throws EngineException, IOException {

// try to compile the current document

	Engine engine = this.context.getEngine();
	ArrayList<Problem> problems = new ArrayList<Problem>();
	LinkedList<String> result = new LinkedList<String>();
	UppaalSystem system = engine.getSystem(this.document, problems);
	boolean fatal = false;

// browse the list of compilation problem and ignore warnings

	for (Problem problem:problems) {
		result.add(problem.toString());
		if (!"warning".equals(problem.getType())) fatal = true;
	}

// finally if no serious error was encountered set the system in the context, otherwise set it to null

	if (!fatal) {
		this.system = system;
		this.context.getStateExpert().setSystem(this.system);
	}else {
		this.system = null;
		this.context.getStateExpert().setSystem(null);
	}

	return result;
}


/**
* @return the system of this context
*/
public UppaalSystem getSystem () {
	return this.system;
}

/**
* set the system of this context
* @param system the new system of this context
*/
public void setSystem (UppaalSystem system) {
	this.system = system;
}

/**
* @return true if and only if this context has a system different of null
*/
public boolean hasSystem () {
	return this.system!=null;
}
}
