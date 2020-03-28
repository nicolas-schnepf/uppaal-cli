package org.uppaal.cli;

/***
* container class for the uppaal context
* namely an uppaal document, a list of queries and an uppaal engine
*/


import org.uppaal.cli.Command.ObjectCode;

import com.uppaal.model.core2.PrototypeDocument;
import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.Template;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.QueryList;
import com.uppaal.model.core2.Query;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.Node;
import com.uppaal.engine.Engine;
import com.uppaal.engine.EngineException;
import com.uppaal.engine.EngineStub;

import com.uppaal.model.core2.AbstractCommand;
import com.uppaal.model.core2.InsertTemplateCommand;
import com.uppaal.model.core2.InsertElementCommand;
import com.uppaal.model.core2.InsertQueryCommand;
import com.uppaal.model.core2.RemoveTemplateCommand;
import com.uppaal.model.core2.RemoveElementCommand;
import com.uppaal.model.core2.RemoveQueryCommand;
import com.uppaal.model.core2.SetPropertyCommand;
import com.uppaal.model.core2.SetSourceCommand;
import com.uppaal.model.core2.SetTargetCommand;

import org.uppaal.cli.exceptions.MissingElementException;
import org.uppaal.cli.exceptions.ExistingElementException;
import org.uppaal.cli.exceptions.WrongFormatException;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Context {
private Document document;
private Engine engine;
private MissingElementException missing_element_exception;
private ExistingElementException existing_element_exception;
private LinkedList<AbstractCommand> commands;
private LinkedList<AbstractCommand> undone_commands;

/**
* create an empty console without any argument
*/

public Context () {
	com.uppaal.model.io2.XMLReader.setXMLResolver(new com.uppaal.model.io2.UXMLResolver());
	this.document = new Document(new PrototypeDocument());
	this.engine = null;
	this.missing_element_exception = new MissingElementException();
	this.existing_element_exception = new ExistingElementException();
	this.commands = new LinkedList<AbstractCommand>();
	this.undone_commands = new LinkedList<AbstractCommand>();
}

/**
* @exception a missing element exception containing the object code and the name of the missing element
*/
public void throwMissingElementException (Command.ObjectCode object_code, String name) {
	this.missing_element_exception.setObjectCode(object_code);
	this.missing_element_exception.setName(name);
	throw this.missing_element_exception;
}

/**
* @exception a existing element exception containing the object code and the name of the existing element
*/
public void throwExistingElementException (Command.ObjectCode object_code, String name) {
	this.existing_element_exception.setObjectCode(object_code);
	this.existing_element_exception.setName(name);
	throw this.existing_element_exception;
}

/**
* @ create a new empty document
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

/**
* drop all queries of this context
*/

public void clearQueries () {
	this.document.getQueryList().removeAll();
}

/**
* get a query described by one of its properties and a corresponding value
* @param property the name of the property to search for
* @param the value of the property to search for
* @return the corresponding query if found, null otherwise
*/
private Query getQuery (String property, String value) {
	for (Query query:this.document.getQueryList()) {
		if (query.getPropertyValue(property)==value) return query;
	}
	return null;
}

/**
* show the name of all queries of this document
* @return the list of names of all queries in this document
*/

public LinkedList<String> showQueries() {
	QueryList queries = this.document.getQueryList();
	LinkedList<String> names = new LinkedList<String>();
	for (Query query : queries) names.addLast((String)query.getPropertyValue("name"));
	return names;
}

/**
* show the information about a specific query described by its name
* @param name the name of the query to show
* @return the description of the corresponding query
* @exception a missing element exception if the query does not exist
*/
public String showQuery (String name) {

// check that the query already exists

	Query query = this.getQuery("name", name);
		if (query==null)
		this.throwMissingElementException(ObjectCode.QUERY, name);

// otherwise return the name, the formula and the comment of the query

	StringBuffer description = new StringBuffer();
	description.append("query "+name+" = \n");
	description.append(query.getFormula()+"\n");
	description.append(query.getComment());
	return description.toString();
}
/**
* add a new query to this context
* @param name the name of the query to add
* @param formula the formula of the query to add
* @param comment the comment of the query to add
*/

public void addQuery (String name, String formula, String comment) {

// check that the query does not already exists

	if (name!=null) {
		if (this.getQuery("name", name)!=null)
			this.throwExistingElementException(ObjectCode.QUERY, name);
	}

// otherwise insert the query at the end of the list

	QueryList list = this.document.getQueryList();
	InsertQueryCommand command = new InsertQueryCommand(list, list.size());
	command.execute();
	this.commands.addFirst(command);

// finally set the properties of the query

	Query query = list.get(list.size()-1);
	if (name!=null) query.setProperty("name", name);
	else query.setProperty("name", "q"+(list.size()+1));
	query.setFormula(formula);
	query.setComment(comment);
}

/**
* remove a query from this context
* @param name the name of the query to remove
*/

public void removeQuery (String name) {

// check that the query does not already exists

	Query query = this.getQuery("name", name);
	if (query==null)
		return;

// otherwise insert the query at the end of the list

	QueryList list = this.document.getQueryList();
	int index = list.indexOf(query);
	RemoveQueryCommand command = new RemoveQueryCommand(list, index);
	command.execute();
	this.commands.addFirst(command);
}

/**
* update a query in this context
* @param name the name of the query to update
* @param property the name of the property to update
* @param value the value of the property to update
*/

public void setQueryProperty(String name, String property, String value) {

// check that the query already exists

	Query query = this.getQuery("name", name);
		if (query==null)
		this.throwMissingElementException(ObjectCode.QUERY, name);

// otherwise update the provided query

	SetPropertyCommand command = new SetPropertyCommand(query, property, value);
	command.execute();
	this.commands.addFirst(command);
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
	PrototypeDocument doc_loader = new PrototypeDocument();
		//URL location = new URL("file", null, args[0]);
	URL location = new URL("file://localhost"+System.getProperty("user.dir")+"/"+filename);
	this.document = doc_loader.load(location);
	QueryList queries = this.document.getQueryList();
	for (int i=0;i<queries.size();i++)
		queries.get(i).setProperty("name", "q"+(i+1));
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

// first open the provided file

	BufferedReader reader = new BufferedReader(new FileReader(filename));
	QueryList queries = this.document.getQueryList();
	StringBuffer query = new StringBuffer();
	StringBuffer comment = new StringBuffer();

	String line = reader.readLine();
	int line_number = 1;
	boolean parsing = true;
	boolean multiline_comment = false;

	int pos = 0;
	int current_index = 0;
	int opening_index = 0;
	int closing_index = 0;

	while (parsing) {

// if the end of the file is reached finish parsing

		if (line==null) {
// if we are in a multiline comment throw an exception

			if (multiline_comment) {
				reader.close();
				WrongFormatException exception = new WrongFormatException();
				exception.setFilename(filename);
				exception.setLineNumber(line_number);
				exception.setMessage("End of file reached while parsing comment.");
				throw exception;
			}

// otherwise simply finishes the parsing

			else {
				parsing = false;
				reader.close();
			}
		}

// in a multiline comment check that we only add the part which is before the closing sequence

		else if (multiline_comment) {

// check that no new comment is open in the line

			opening_index = line.indexOf("/*", current_index);
			closing_index = line.indexOf("*/", current_index);
			if (opening_index != -1) {
				reader.close();
				WrongFormatException exception = new WrongFormatException();
				exception.setFilename(filename);
				exception.setLineNumber(line_number);
				exception.setMessage("New comment open while parsing comment.");
				throw exception;
			}

// otherwise only add the text which is before the closing sequence

			else if (closing_index==-1) {
				if (current_index==0) comment.append(line+"\n");
				else comment.append(line.substring(current_index)+"\n");

				line = reader.readLine();
				current_index = 0;
				pos = 0;
				if (line!=null) line_number++;
			}
			else {
				comment.append(line.substring(current_index, closing_index)+"\n");
				current_index = closing_index + 2;
				pos = current_index;
				multiline_comment = false;
			}
		} 

// otherwise treat the line as regular text

		else {
// parse the white spaces at the beginning of the current block

			while (pos<line.length() && line.charAt(pos)==' ') pos++;
			if (pos>=line.length()) {
				line = reader.readLine();
				current_index = 0;
				pos = 0;
			}

// otherwise if we open a multiline comment pass in the corresponding mode

			else if (line.indexOf("/*", pos)==pos) {
				pos +=2;
				current_index = pos;
				multiline_comment = true;
			}

// if we find an inline comment treat it as a multiline one

			else if (line.indexOf("//", pos)==pos) {
				pos +=2;
				current_index = pos;
				comment.append(line.substring(pos)+"\n");
				line = reader.readLine();
				pos = 0;
				current_index = 0;
				if (line!=null) line_number ++;
			}

// otherwise treat the text as a query

			else {
				if (line.indexOf("/*", pos)!=-1) {
					opening_index = line.indexOf("/*", pos);
					query.append(line.substring(current_index, opening_index));
					current_index = opening_index;
					pos = current_index;
				} else if (line.indexOf("//", pos)!=-1) {
System.out.println(query.length());
					query.append(line.substring(pos, line.indexOf("//", pos)));
					line = reader.readLine();
					if (line != null) line_number ++;
					current_index = 0;
					pos = 0;
				} else {
					if (pos<line.length()) query.append(line.substring(pos));
					line = reader.readLine();
					current_index = 0;
					pos = 0;
				}

				if (query.length()>0) 
					queries . addLast(new Query(query.toString(), comment.toString()));
					queries.get(queries.size()-1).setProperty("name", "q"+queries.size());
					query.delete(0, query.length());
					comment.delete(0, comment.length());
			}

		}
	}
}

/**
* save the queries attached to this context
* @param filename the path to the file to save the current query list
* @exception an IO exception if there is a problem with the provided filename
*/
public void saveQueries (String filename) throws IOException {
	BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
	QueryList queries = this.document.getQueryList();

// write each query in the provided file

	for (Query query: queries) {
		writer.write("/*\n"+query.getComment()+"\n*/\n");
		writer.write(query.getFormula()+"\n\n");
	}

writer.close();
}

/**
* @return a list containing all template headers in this document
*/
public LinkedList<String> getTemplateHeaders () {
	LinkedList<String> headers = new LinkedList<String>();
	AbstractTemplate template = (AbstractTemplate) this.document.getFirst();
	StringBuffer header = new StringBuffer();

// fetch the name and optionally the parameters of each template in the current document

	while (template!=null) {
		header.append((String)template.getPropertyValue("name"));
		header.append(" ");

		if (template.isPropertyLocal("parameter")) 
			header.append ("("+template.getPropertyValue("parameter")+")");
		else
			header.append("()");

		headers.add(header.toString());
		header.delete(0, header.length());
		template = (AbstractTemplate) template.getNext();
	}

	return headers;
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
	Template template = this.document.createTemplate();
	template.setProperty("name", name);
	template.setProperty("parameter", parameter);

	InsertTemplateCommand command = new InsertTemplateCommand(this.document, null, template);
	command.execute();
	this.commands.addFirst(command);
}

/**
* get a textual description of a template in xta format
* @param name the name of the template
* @return the description of the specified
*/
public String showTemplate (String name) {

// get the template if it exists

	AbstractTemplate template = this.document.getTemplate(name);
	Location init = null;
	Location committed = null;

	if (template==null) 
		this.throwMissingElementException(ObjectCode.TEMPLATE, name);

// loop over the children of the template

	Node node = template.getFirst();
	StringBuffer locations = new StringBuffer();
	StringBuffer edges = new StringBuffer();

	while (node!=null) {

// concatenate edge description to the corresponding buffer

		if (node instanceof Location) {
			Location location = (Location) node;
			if (locations.length()>0) locations.append(",\n");
			locations.append("\t");
			locations.append(this.describeLocation(location));

			if (location.isPropertyLocal("init"))
				init = location;

			if (location.isPropertyLocal("committed"))
				committed = location;
		}

// concatenate edge description to the corresponding buffer

		else if (node instanceof Edge) {
			Edge edge = (Edge) node;
			if (edges.length()!=0) edges.append(",\n");
			edges.append("\t"+this.describeEdge(edge));
		}

		node = node.getNext();
	}

// finally build the description of the template and return it

	StringBuffer description = new StringBuffer();
	description.append("process "+template.getPropertyValue("name"));
	description.append("("+template.getPropertyValue("parameter")+"){\n");
	description.append(template.getPropertyValue("declaration")+"\n");;

	if (locations.length()>0) 
		description.append("states\n"+locations.toString()+";\n");

	if (init!=null) 
		description.append("init\n\t"+init.getPropertyValue("name")+";\n");

	if (committed!=null) 
		description.append("committed\n\t"+committed.getPropertyValue("name")+";\n");

	if (edges.length()!=0)
		description.append("trans\n"+edges.toString()+";\n");

	description.append("}");
	return description.toString();
}

/**
* return a certain property of a template
* @param name the name of the template to return
* @return the corresponding property of the template
* @exception a missing element exception if the template is missing
*/
public String getTemplateProperty (String name, String property) {
	AbstractTemplate template = this.document.getTemplate(name);
	if (template==null) 
		this.throwMissingElementException(ObjectCode.TEMPLATE, name);
	return (String)template.getPropertyValue(property);
}

/**
* set a property of an existing template
* @param name the name of the template to update
* @param value the value to update
* @param value the new value for the property of the template
* @exception a missing element exception if the template was not found
*/
public void setTemplateProperty (String name, String property, String value) {
	AbstractTemplate template = this.document.getTemplate(name);
	if (template==null) 
		this.throwMissingElementException(ObjectCode.TEMPLATE, name);
	else {
		SetPropertyCommand command = new SetPropertyCommand(template, property, value);
		command.execute();
		this.commands.addFirst(command);
	}
}

/**
* remove a template described by its name
* @param name the name of the template to remove
*/
public void removeTemplate (String name) {
	AbstractTemplate template = this.document.getTemplate(name);
	if (template==null) return;
	RemoveTemplateCommand command = new RemoveTemplateCommand(template);
	command.execute();
	this.commands.addFirst(command);
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
	this.commands.addFirst(command);
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
	this.commands.addFirst(command);
}

/**
* add a location described by its name and the name of its template
* @param template_name the name of the template to update
* @param location_name the name of the new location
* @exception an exception will be thrown if the template does not exist
*/
public void addLocation (String template_name, String location_name) {
	Template template = (Template) this.document.getTemplate(template_name);
	if (template==null) this.throwMissingElementException(ObjectCode.TEMPLATE, template_name);
	if (this.getLocation(template_name, "name", location_name)!=null)
		this.throwExistingElementException(ObjectCode.LOCATION, location_name);

	Location location = template.createLocation();
	location.setProperty("name", location_name);
	InsertElementCommand command = new InsertElementCommand(location.getCommandManager(), template, null, location);
	command.execute();
	this.commands.addFirst(command);
}

/**
* return a location based on the name of its template, of one of its properties and its value
* @param template the name of the template to inspect
* @param property the name of the property to inspect
* @param value the value of the property field of the location to return
* @return the corresponding location, if found
* @exception a missing element exception if either the template or the location was not found
*/
private Location getLocation (String template_name, String property, Object value) {

// get the given template if it exists

	AbstractTemplate template = this.document.getTemplate(template_name);
	if (template==null) 
		this.throwMissingElementException(ObjectCode.TEMPLATE, template_name);

// get the given location and return it if it exists

	Node node = template.getFirst();
	Location location = null;

	while(node!=null && location==null) {
		if (!(node instanceof Location)) continue;
		else if (node.getPropertyValue(property)==value) location = (Location)node;
		node = node.getNext();
	}

	return location;
}


/**
* remove a location based on its name and on the name of its template
* @param template the name of the template to inspect
* @param location the name of the location to remove
*/
public void removeLocation (String template, String name) {
	Location location = this.getLocation(template, "name", name);
	if (location==null) this.throwMissingElementException(ObjectCode.LOCATION, name);
		RemoveElementCommand command = new RemoveElementCommand(location);
		command.execute();
		this.commands.addFirst(command);
}

/**
* return the description of a location given as parameter
* @param location the location to describe
* @return the string containing the description of the location
*/
private String describeLocation (Location location) {
	StringBuffer description = new StringBuffer();
	description.append(location.getPropertyValue("name"));

	if (location.isPropertyLocal("invariant"))
		description.append(" { "+location.getPropertyValue("invariant")+" } ");
	return description.toString();
}

/**
* return the description of a location given by its name and the name of its template
* @param template the name of the template to inspect
* @param name the name of the location to describe
* @return a string containing the description of the location
* @exception a missing element exception if either the template or the location does not exist
*/
public String showLocation (String template, String name) {
	Location location = this.getLocation (template, "name", name);
	if (location==null) this.throwMissingElementException(ObjectCode.LOCATION, name);
	return this.describeLocation(location);
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

// first of all check that such a location does not already exist in the template

	Location location = this.getLocation(template, property, value);

	if (location!=null) {
		switch (property) {
			case "name":
			this.throwExistingElementException(ObjectCode.LOCATION, template);
			break;
			case "init":
			this.throwExistingElementException(ObjectCode.INIT, template);
			break;
			case "committed":
			this.throwExistingElementException(ObjectCode.COMMITTED, template);
			break;
		}
	}

// get the given location and update it

	location = this.getLocation(template, "name", name);
	if (location==null) this.throwMissingElementException(ObjectCode.LOCATION, name);
	SetPropertyCommand command = new SetPropertyCommand(location, property, value);
	command.execute();
	this.commands.addFirst(command);
}

/**
* add a new edge described by its template, its source and its destination
* @param template_name the name of the template to inspect
* @param source_name the source of the new edge
* @param target_name the target of the new edge
* @exception an exception is thrown if either the template, the source or the think does not exist
*/
public void addEdge(String template_name, String source_name, String target_name) {
	Template template = (Template)this.document.getTemplate(template_name);
	if (template==null) 
		this.throwMissingElementException(ObjectCode.TEMPLATE, template_name);

	Location source = this.getLocation(template_name, "name", source_name);
	Location target = this.getLocation(template_name, "name", target_name);

        Edge edge = template.createEdge();
	edge.setSource(source);
	edge.setTarget(target);

	InsertElementCommand command = new InsertElementCommand(edge.getCommandManager(), template, null, edge);
	command.execute();
	this.commands.addFirst(command);
}

/**
* return an edge based on the name of its source, of its target and of its template
* @param template the name of the template to inspect
* @param source the name of the source of the edge to return
* @param target the name of the target of the edge to return
* @return the corresponding edge if found
* @exception a missing element exception if either the template or the edge was not found
*/
private Edge getEdge (String template_name, String source, String target) {

// get the given template if it exists

	AbstractTemplate template = this.document.getTemplate(template_name);
	if (template==null) 
		this.throwMissingElementException(ObjectCode.TEMPLATE, template_name);

// get the given edge and return it if it exists

	Node node = template.getFirst();
	Edge res = null;

	while(node!=null && res==null) {
		if (!(node instanceof Edge)) continue;
		Edge edge = (Edge) node;
		Location src = (Location)edge.getSource();
		Location tar = (Location) edge.getTarget();
		if (src.getPropertyValue("name")==source && tar.getPropertyValue("name")==target) 
			res = edge;
		node = node.getNext();
	}

	if (res==null) 
		this.throwMissingElementException(ObjectCode.EDGE, source+" -> "+target);
	return res;
}

/**
* remove an edge based on the name of its source, of its target and of its template
* @param template the name of the template to inspect
* @param source the name of the source of the edge to remove
* @param target the name of the target of the edge to remove
*/
public void removeEdge (String template, String source, String target) {
	try {
		Edge edge = this.getEdge(template, source, target);
		RemoveElementCommand command = new RemoveElementCommand(edge);
		command.execute();
		this.commands.addFirst(command);
	} catch (MissingElementException e) {
	}
}

/**
* return the description of an edge given as parameter
* @param edge the edge to describe
* @return the string containing the description of the edge
*/
private String describeEdge (Edge edge) {
	StringBuffer description = new StringBuffer();
	StringBuffer properties = new StringBuffer();
	Location source = (Location)edge.getSource();
	Location target = (Location)edge.getTarget();
	description.append(source.getPropertyValue("name")+" -> "+target.getPropertyValue("name"));

// append the select of the edge if any

	if (edge.isPropertyLocal("select"))
		properties.append("select "+edge.getPropertyValue("select"));

// append the guard of the edge if any

	if (edge.isPropertyLocal("guard")) {
		if (properties.length()!=0) properties.append("; ");
		properties.append("guard "+edge.getPropertyValue("guard"));
	}

// append the synchronization of the edge if any

	if (edge.isPropertyLocal("synchronisation")) {
		if (properties.length()!=0) properties.append("; ");
		properties.append("sync "+edge.getPropertyValue("synchronisation"));
	}

// append the assignment of the edge if any

	if (edge.isPropertyLocal("assignment")) {
		if (properties.length()!=0) properties.append("; ");
		properties.append("assign "+edge.getPropertyValue("assignment"));
	}

			description.append(" { "+properties.toString()+" } ");
	return description.toString();
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
	Edge edge = this.getEdge(template, source, target);
	return this.describeEdge(edge);
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
	Edge edge = this.getEdge(template, source, target);
	SetPropertyCommand command = new SetPropertyCommand(edge, name, value);
	command.execute();
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