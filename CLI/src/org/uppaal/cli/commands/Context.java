package org.uppaal.cli.commands;

/***
* container and facade class for the uppaal context
* namely an uppaal document, a list of queries and an uppaal engine
* also support different experts for each type of uppaal objects to manage
*/

import org.uppaal.cli.handlers.Handler.HandlerCode;
import com.uppaal.model.core2.PrototypeDocument;
import com.uppaal.model.core2.QueryList;
import com.uppaal.model.core2.Query;
import com.uppaal.model.core2.Document;
import com.uppaal.engine.Engine;
import com.uppaal.engine.EngineException;
import com.uppaal.engine.EngineStub;
import com.uppaal.model.core2.AbstractCommand;
import com.uppaal.model.core2.SetPropertyCommand;

import java.util.LinkedList;
import java.util.Iterator;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Context {
private HandlerCode mode;
private Document document;
private Engine engine;
private LinkedList<AbstractCommand> commands;
private LinkedList<AbstractCommand> undone_commands;
private DocumentExpert document_expert;
private QueryExpert query_expert;
private TemplateExpert template_expert;
private LocationExpert location_expert;
private EdgeExpert edge_expert;

/**
* create an empty console without any argument
*/

public Context () {
	com.uppaal.model.io2.XMLReader.setXMLResolver(new com.uppaal.model.io2.UXMLResolver());
	this.document = new Document(new PrototypeDocument());
	this.engine = null;
	this.commands = new LinkedList<AbstractCommand>();
	this.undone_commands = new LinkedList<AbstractCommand>();
	this.document_expert = new DocumentExpert(this);
	this.query_expert = new QueryExpert(this);
	this.template_expert = new TemplateExpert(this);
	this.location_expert = new LocationExpert(this);
	this.edge_expert = new EdgeExpert(this);
}

/**
* @return the current mode of this context
*/
public HandlerCode getMode() {
	return this.mode;
}

/**
* set the current mode of this context
* @param mode the new mode for this context
*/
public void setMode(HandlerCode mode) {
	this.mode = mode;
}

/**
* create a new empty document
*/

public void newDocument () {
	this.document = new Document();
}

/***
* @ return the uppaal document contained inside of this context
*/

public Document getDocument () {
	return this.document;
}

/***
* set a new document to this context
* @param document the new document for this context
*/
public void setDocument (Document document) {
	this.document = document;
}

/***
* @return an iterator on the list of queries contained in this context
*/

public QueryList getQueryList () {
	return this.document.getQueryList();
}

/**
* @return the number of queries of this context
*/
public int getQueryNumber () {
	return this.document.getQueryList().size();
}



/***
* @return the engine attached to this context
*/

public Engine getEngine () {
	return this.engine;
}

/***
* connect to an uppaal engine based on the path declared in the UPPAALPATH environment variable
*/

public void connectEngine() throws EngineException, IOException {

// compute the path to the engine binary based on the UPPAALPATH environment variable

	String os = System.getProperty("os.name");
		String here = System.getenv("UPPAALPATH");
	String path = null;

// compute the end of the path based on the running operating system

	if ("Linux".equals(os)) {
		path = here+"/bin-Linux/server";
	} else if ("Mac OS X".equals(os)) {
		path = here + "bin-Darwin/server";
	} else {
		path = here+"\\bin-Windows\\server.exe";
	}

// create and configure the new engine

	this.engine = new Engine();
	this.engine.setServerPath(path);
	this.engine.setServerHost("localhost");
	this.engine.setConnectionMode(EngineStub.BOTH);
	this.engine.connect();
}

/***
* disconnect the engine of the uppaal console
*/

public void disconnectEngine () {
	if (this.engine == null) return;
	this.engine.disconnect();
	this.engine = null;
}

/**
* load a document provided by its filename
* @param filename the path to the document to load
*/
public void loadDocument (String filename) throws IOException, MalformedURLException  {
	this.document = this.document_expert.loadDocument(filename);
}
/**
* @return the global declaration field
* @exception a missing element exception if the global declaration field is not set
*/
public String getGlobalDeclaration() {
	return this.document_expert.getGlobalDeclaration();
}

/**
* set the global declaration of this document
* @param declaration the new declaration for this document
*/
public void setGlobalDeclaration (String declaration) {
	this.document_expert.setGlobalDeclaration(declaration);
}

/**
* @return a string describing the system of the current document
*/
public String getSystem() {
	return this.document_expert.getSystem();
}

/**
* set the value of the system property of this document
* @param system the new system value
*/
public void setSystem (String system) {
	this.document_expert.setSystem(system);
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
*/
public void clearDocument() {
	this.document = new Document(new PrototypeDocument());
}


/**
* load a list of queries from the provided filename
* @param filename the path to the file from which queries should be loaded
* @exception an io exception if there is a problem with the provided filename
*/
public void loadQueries (String filename) throws IOException {
	this.query_expert.loadQueries(filename);
}

/**
* save the queries attached to this context
* @param filename the path to the file to save the current query list
* @exception an IO exception if there is a problem with the provided filename
*/
public void saveQueries (String filename) throws IOException {
	this.query_expert.saveQueries(filename);
}

/**
* drop all queries of this context
*/

public void clearQueries () {
	this.document.getQueryList().removeAll();
}

/**
* show the name of all queries of this document
* @return the list of names of all queries in this document
*/

public LinkedList<String> showQueries() {
	return this.query_expert.showQueries();
}

/**
* show the information about a specific query described by its name
* @param name the name of the query to show
* @return the description of the corresponding query
* @exception a missing element exception if the query does not exist
*/
public String showQuery (String name) {
	return this.query_expert.showQuery(name);
}
/**
* add a new query to this context
* @param name the name of the query to add
* @param formula the formula of the query to add
* @param comment the comment of the query to add
*/

public void addQuery (String name, String formula, String comment) {
	this.query_expert.addQuery(name, formula, comment);
}

/**
* remove a query from this context
* @param name the name of the query to remove
*/

public void removeQuery (String name) {
	this.query_expert.removeQuery(name);
}

/**
* update a query in this context
* @param name the name of the query to update
* @param property the name of the property to update
* @param value the value of the property to update
*/

public void setQueryProperty(String name, String property, String value) {
	this.query_expert.setQueryProperty(name, property, value);
}

/**
* @return a list containing all template headers in this document
*/
public LinkedList<String> getTemplateHeaders () {
	return this.template_expert.getTemplateHeaders();
}

/**
* clear all templates from a document
*/
public void clearTemplates() {
	this.clearDocument();
}


/**
* add a new template to the current document
* @param name the name of the template
* @param parameter the parameter of the template
*/
public void addTemplate(String name, String parameter) {
	this.template_expert.addTemplate(name, parameter);
}

/**
* get a textual description of a template in xta format
* @param name the name of the template
* @return the description of the specified
*/
public String showTemplate (String name) {
	return this.template_expert.showTemplate(name);
}

/**
* return a certain property of a template
* @param name the name of the template to return
* @return the corresponding property of the template
* @exception a missing element exception if the template is missing
*/
public String getTemplateProperty (String name, String property) {
	return this.template_expert.getTemplateProperty(name, property);
}

/**
* set a property of an existing template
* @param name the name of the template to update
* @param value the value to update
* @param value the new value for the property of the template
* @exception a missing element exception if the template was not found
*/
public void setTemplateProperty (String name, String property, String value) {
	this.template_expert.setTemplateProperty(name, property, value);
}

/**
* remove a template described by its name
* @param name the name of the template to remove
*/
public void removeTemplate (String name) {
	this.template_expert.removeTemplate(name);
}

/**
* add a location described by its name and the name of its template
* @param template_name the name of the template to update
* @param location_name the name of the new location
* @exception an exception will be thrown if the template does not exist
*/
public void addLocation (String template_name, String location_name) {
	this.location_expert.addLocation(template_name, location_name);
}

/**
* remove a location based on its name and on the name of its template
* @param template the name of the template to inspect
* @param location the name of the location to remove
*/
public void removeLocation (String template, String name) {
	this.location_expert.removeLocation(template, name);
}

/**
* return the description of a location given by its name and the name of its template
* @param template the name of the template to inspect
* @param name the name of the location to describe
* @return a string containing the description of the location
* @exception a missing element exception if either the template or the location does not exist
*/
public String showLocation (String template, String name) {
		return this.location_expert.showLocation(template, name);
}

/**
* set a property of a location described by the name of its template, of its source and its target
* @param template the template to update
* @param name the name of the location to update
* @param property the name of the property to update
* @param value the new value for the property
* @exception an exception if the template, the source or the target does not exist
*/
public void setLocationProperty (String template, String name, String property, Object value) {
	this.location_expert.setLocationProperty(template, name, property, value);
}

/**
* add a new edge described by its template, its source and its destination
* @param template_name the name of the template to inspect
* @param source_name the source of the new edge
* @param target_name the target of the new edge
* @exception an exception is thrown if either the template, the source or the think does not exist
*/
public void addEdge(String template_name, String source_name, String target_name) {
	this.edge_expert.addEdge(template_name, source_name, target_name);
}

/**
* remove an edge based on the name of its source, of its target and of its template
* @param template the name of the template to inspect
* @param source the name of the source of the edge to remove
* @param target the name of the target of the edge to remove
*/
public void removeEdge (String template, String source, String target) {
	this.edge_expert.removeEdge(template, source, target);
}

/**
* return the description of an edge given by the name of its source, of its target and of its template
* @param template the name of the template to inspect
* @param source the source of the edge to describe
* @param target the target of the edge to describe
* @return a string containing the description of the edge
* @exception a missing element exception if either the template or the edge does not exist
*/
public String showEdge (String template, String source, String target) {
	return this.edge_expert.showEdge(template, source, target);
}

/**
* set a property of an edge described by the name of its template, of its source and its target
* @param template the template to update
* @param source the name of the source of the edge
* @param target the name of the target of the edge
* @param name the name of the property to update
* @param value the new value for the property
* @exception an exception if the template, the source or the target does not exist
*/
public void setEdgeProperty (String template, String source, String target, String name, String value) {
	this.edge_expert.setEdgeProperty(template, source, target, name, value);
}

/**
* add a command to the list of commands of this context
* @param command the command to add
*/
public void addCommand (AbstractCommand command) {
	this.commands.addFirst(command);
}

/**
* undo the first command of the list
*/
public void undo () {
	if (this.commands.size()==0) return;
	AbstractCommand command = this.commands.removeFirst();
	command.undo();
	this.undone_commands.addFirst(command);
}

/**
* redo the first command of the list
*/
public void redo () {
	if (this.undone_commands.size()==0) return;
	AbstractCommand command = this.undone_commands.removeFirst();
	command.execute();
	this.commands.addFirst(command);
}
}
