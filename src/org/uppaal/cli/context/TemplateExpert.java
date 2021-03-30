package org.uppaal.cli.context;

/**
* template expert, responsible for all operations on templates
*/


import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.Template;
import com.uppaal.model.core2.QueryList;
import com.uppaal.model.core2.Query;
import com.uppaal.model.core2.PrototypeDocument;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.Node;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.InsertTemplateCommand;
import com.uppaal.model.core2.RemoveTemplateCommand;
import com.uppaal.model.core2.SetPropertyCommand;

import java.util.LinkedList;
import java.util.List;
import java.util.HashSet;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class TemplateExpert extends AbstractExpert {

// list of loaded templates
private LinkedList<AbstractTemplate> templates;

// set of selected templates
private HashSet<AbstractTemplate> selected_templates;

// private list of locations
private LinkedList<String> locations;

// private linked list of edges
private LinkedList<String> edges;

public TemplateExpert (Context context) {
	super(context);
	this.templates = new LinkedList<AbstractTemplate>();
	this.selected_templates = new HashSet<AbstractTemplate>();
	this.locations = new LinkedList<String>();
	this.edges = new LinkedList<String>();
}

/**
* @return a list containing all template names in this document
*/
public LinkedList<String> showTemplates () {
	LinkedList<String> names = new LinkedList<String>();
	AbstractTemplate template = (AbstractTemplate) this.context.getDocument().getFirst();

// fetch the name and optionally the parameters of each template in the current document

	while (template!=null) {
		names.addLast((String)template.getPropertyValue("name"));
		template = (AbstractTemplate) template.getNext();
	}

	return names;
}

/**
* load the templates from a document described by its location
* @param filename the location of the document
* @return the number of loaded templates
* @throws IOException an io exception if there was some error with the provided file
* @throws MalformedURLException an exception if the provided filename is a malformed url
*/

public int loadTemplates (String filename) throws IOException, MalformedURLException  {
	PrototypeDocument doc_loader = new PrototypeDocument();
	URL location = new URL("file://localhost"+System.getProperty("user.dir")+"/"+filename);
	Document document = doc_loader.load(location);
	AbstractTemplate template = (AbstractTemplate) document.getFirst();

// fetch all templates from the loaded document

	while (template!=null) {
		this.templates.addLast(template);
		template = (AbstractTemplate) template.getNext();
	}

	return this.templates.size();
}

/**
* get the number of loaded templates contained in this template expert
* @return the number of templates contained inside of this template expert
*/
public int getTemplateNumber() {
	return this.templates.size();
}


/**
* select an element from the list of loaded templates
* @param index the index of the template to select
*/
public void selectTemplate (int index) {
	AbstractTemplate template = this.templates.get(index);
	this.selected_templates.add(template);
}

/**
* unselect an element from the list of loaded templates
* @param index the index of the template to unselect
*/
public void unselectTemplate (int index) {
	AbstractTemplate template = this.templates.get(index);
	this.selected_templates.remove(template);
}

/**
* add a new template to the current document
* @param name the name of the template
* @param parameter the parameter of the template
* @param declaration the declaration of this template
*/
public void addTemplate(String name, String parameter, String declaration) {

// check that the template does not already exist

	if (this.context.getDocument().getTemplate(name)!=null)
		this.throwExistingElementException ("template", name);

// create the template and set its attributes

	Template template = this.context.getDocument().createTemplate();
	template.setProperty("name", name);
	template.setProperty("parameter", parameter);
	template.setProperty("declaration", declaration);

// insert the template into the document

	InsertTemplateCommand command = new InsertTemplateCommand(this.context.getDocument(), null, template);
	command.execute();
	this.context.addCommand(command);
}

/**
* copy a template in the document
* @param name1 the name of the first template
* @param name2 the name of the second template
*/
public void copyTemplate(String name1, String name2) {

// check that the first provided name does not match any template

	AbstractTemplate template = this.context.getDocument().getTemplate(name1);
	if (template!=null) this.throwExistingElementException("template", name1);

// check that the second name well refers to an existing template

	template = this.context.getDocument().getTemplate(name2);
	if (template==null) this.throwMissingElementException("template", name2);

// copy the fetched template

	try {
	template = (AbstractTemplate)template.clone();
	template.setProperty("name", name1);

// insert the template into the document

	InsertTemplateCommand command = new InsertTemplateCommand(this.context.getDocument(), null, template);
	command.execute();
	this.context.addCommand(command);
	} catch (Exception e) {
		System.out.println(e.getMessage());
		e.printStackTrace();
		System.exit(1);
	}
}

/**
* add the selected templates to the current document
*/
public void addSelectedTemplates() {
	for (AbstractTemplate template: this.selected_templates) {
		InsertTemplateCommand command = new InsertTemplateCommand(this.context.getDocument(), null, template);
		command.execute();
		this.context.addCommand(command);
	}
}

/**
* show the selected templates of this template expert
* @return a linked list containing the name of all the selected templates
*/
public LinkedList<String> showSelectedTemplates() {
	this.result.clear();
	for (AbstractTemplate template:this.selected_templates)
		this.result.addLast((String)template.getPropertyValue("name"));
	return this.result;
}

/**
* describe a template
* @param template the template to describe
* @return the textual description of the template
*/
private List<String> describeTemplate (AbstractTemplate template) {
// loop over the children of the template

	Node node = template.getFirst();
	Location init = null;
	Location committed = null;

	this.result.clear();
	this.locations.clear();
	this.edges.clear();

	while (node!=null) {

// concatenate edge description to the corresponding buffer

		if (node instanceof Location) {
			Location location = (Location) node;
			locations.addLast("\t"+this.describeLocation(location));
			if (location.isPropertyLocal("init")) init = location;
			if (location.isPropertyLocal("committed")) committed = location;
		}

// concatenate edge description to the corresponding buffer

		else if (node instanceof Edge) {
			Edge edge = (Edge) node;
			edges.addLast("\t"+this.describeEdge(edge));
		}

		node = node.getNext();
	}

// finally build the description of the template and return it

	String header = "process "+template.getPropertyValue("name");
	header += "("+template.getPropertyValue("parameter")+"){";
	this.result.addLast(header);

// add the declaration of the template if it is possible

	for (String line: ((String)template.getPropertyValue("declaration")).split("\n"))
		this.result.addLast(line);

// add the locations of the template to the result

	this.result.addLast("state");
	for (String location: this.locations) this.result.addLast(location);

// if the init state of the template is set add it to the result

	if (init!=null) {
		this.result.addLast("init");
		this.result.addLast("\t"+init.getPropertyValue("name")+";");
	}

// if the committed state of the template is set add it to the template

	if (committed!=null)  {
		this.result.addLast("committed");
		this.result.addLast("\t"+committed.getPropertyValue("name")+";");
	}

// finally add the edges, close the template and return the result

		this.result.addLast("trans");
	for (String edge: this.edges) this.result.addLast(edge);
	this.result.addLast("}");
	return this.result;
}

/**
* get a textual description of a template in xta format
* @param name the name of the template
* @return the description of the specified
*/
public List<String> showTemplate (String name) {

// get the template if it exists

	AbstractTemplate template = this.context.getDocument().getTemplate(name);

	if (template==null) 
		this.throwMissingElementException("template", name);

	return this.describeTemplate(template);
}

/**
* show a loaded template
* @param index the index of the template to show
* @return the textual description of the loaded template
*/
public List<String> showLoadedTemplate (int index) {
	AbstractTemplate template = this.templates.get(index);
	return this.describeTemplate(template);
}

/**
* return a certain property of a template
* @param name the name of the template to return
* @param property the name of the property to return
* @return the corresponding property of the template
*/
public String getTemplateProperty (String name, String property) {
	AbstractTemplate template = this.context.getDocument().getTemplate(name);
	if (template==null) 
		this.throwMissingElementException("template", name);
	return (String)template.getPropertyValue(property);
}

/**
* set a property of an existing template
* @param name the name of the template to update
* @param property the name of the property to update
* @param value the new value for the property of the template
*/
public void setTemplateProperty (String name, String property, String value) {
	AbstractTemplate template = this.context.getDocument().getTemplate(name);
	if (template==null) 
		this.throwMissingElementException("template", name);
	else {
		SetPropertyCommand command = new SetPropertyCommand(template, property, value);
		command.execute();
		this.context.addCommand(command);
	}
}

/**
* clear the templates of this context
*/
public void clearTemplates() {
	for (String template: this.showTemplates()) this.removeTemplate(template);
}

/**
* clear the loaded templates from this expert
*/
public void clearLoadedTemplates() {
	this.templates.clear();
	this.selected_templates.clear();
}


/**
* remove a template described by its name
* @param name the name of the template to remove
*/
public void removeTemplate (String name) {
	AbstractTemplate template = this.context.getDocument().getTemplate(name);
	if (template==null) return;
	RemoveTemplateCommand command = new RemoveTemplateCommand(template);
	command.execute();
	this.context.addCommand(command);
}

/**
* get the value associated with a template property
* @param name the name of the template to inspect
* @param property the name of the property to retrieve
* @return the value associated with the property for the given template
*/
public String getPropertyValue (String name, String property) {
	AbstractTemplate template = this.context.getDocument().getTemplate(name);

	if (template==null) 
		this.throwMissingElementException("template", name);

	return (String) template.getPropertyValue(property);
}

/**
* copy a property from a template to another one
* @param template1 the name of the source template
* @param template2 the name of the target template
* @param property the name of the property to copy
*/
public void copyTemplateProperty (String template1, String template2, String property) {
	String value = this.getPropertyValue(template1, property);
	value = new String(value);
	this.setTemplateProperty(template2, property, value);
}
}
