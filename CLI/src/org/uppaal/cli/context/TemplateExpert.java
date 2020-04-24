package org.uppaal.cli.context;

/**
* template expert, responsible for all operations on templates
*/


import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.Template;
import com.uppaal.model.core2.QueryList;
import com.uppaal.model.core2.Query;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.Node;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.InsertTemplateCommand;
import com.uppaal.model.core2.RemoveTemplateCommand;
import com.uppaal.model.core2.SetPropertyCommand;
import java.util.LinkedList;

public class TemplateExpert extends AbstractExpert {
public TemplateExpert (Context context) {
	super(context);
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
* add a new template to the current document
* @param name the name of the template
* @param parameter the parameter of the template
*/
public void addTemplate(String name, String parameter) {
	Template template = this.context.getDocument().createTemplate();
	template.setProperty("name", name);
	template.setProperty("parameter", parameter);

	InsertTemplateCommand command = new InsertTemplateCommand(this.context.getDocument(), null, template);
	command.execute();
	this.context.addCommand(command);
}

/**
* get a textual description of a template in xta format
* @param name the name of the template
* @return the description of the specified
*/
public String showTemplate (String name) {

// get the template if it exists

	AbstractTemplate template = this.context.getDocument().getTemplate(name);
	Location init = null;
	Location committed = null;

	if (template==null) 
		this.throwMissingElementException("template", name);

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
	AbstractTemplate template = this.context.getDocument().getTemplate(name);
	if (template==null) 
		this.throwMissingElementException("template", name);
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
	this.context.getModelExpert().clearDocument();
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
}
