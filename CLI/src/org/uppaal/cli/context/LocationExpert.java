package org.uppaal.cli.context;

/**
* location expert, responsible for all operations on locations
*/


import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.Template;
import com.uppaal.model.core2.QueryList;
import com.uppaal.model.core2.Query;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.Node;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.InsertElementCommand;
import com.uppaal.model.core2.RemoveElementCommand;
import com.uppaal.model.core2.SetPropertyCommand;
import java.util.LinkedList;

public class LocationExpert extends AbstractExpert {
public LocationExpert (Context context) {
	super(context);
}

/**
* add a location described by its name and the name of its template
* @param template_name the name of the template to update
* @param location_name the name of the new location
* @param invariant the invariant of this location
*/
public void addLocation (String template_name, String location_name, String invariant) {
	Template template = (Template) this.context.getDocument().getTemplate(template_name);
	if (template==null) this.throwMissingElementException("template", template_name);
	if (this.getLocation(template_name, "name", location_name)!=null)
		this.throwExistingElementException("location", location_name);

	Location location = template.createLocation();
	location.setProperty("name", location_name);
	location.setProperty("invariant", invariant);

	InsertElementCommand command = new InsertElementCommand(location.getCommandManager(), template, null, location);
	command.execute();
	this.context.addCommand(command);
}

/**
* copy a location from a template to another one
* @param name1 the name of the source template
* @param location1 the name of the source location
* @param name2 the name of the target template
* @param location2 the name of the target location
*/
public void copyLocation (String name1, String location1, String name2, String location2) {

// check that the source and target template well exist

	Template template1 = (Template) this.context.getDocument().getTemplate(name1);
	if (template1==null) this.throwMissingElementException("template", name1);
	Template template2 = (Template) this.context.getDocument().getTemplate(name2);
	if (template2==null) this.throwMissingElementException("template", name2);

// check that the source location well exists

	Location location = this.getLocation(name1, "name", location1);
	if (location==null)this.throwMissingElementException("location", location1);
	if (this.getLocation(name2, "name", location2)!=null) 
		this.throwExistingElementException("location", name2);

// finally insert a copy of the source location in the target template

	try {
	location = (Location)location.clone();
	location.setProperty("name", location2);
	InsertElementCommand command = new InsertElementCommand(location.getCommandManager(), template2, null, location);
	command.execute();
	this.context.addCommand(command);
	} catch (Exception e) {}
}

/**
* copy a property from a location to another one
* @param name1 the name of the source template
* @param location1 the name of the source location
* @param name2 the name of the target template
* @param location2 the name of the target location
* @param property the name of the property to copy
*/
public void copyLocationProperty (String name1, String location1, String name2, String location2, String property) {
	String value = this.getPropertyValue(name1, location1, property);
	value = new String (value);
	this.setLocationProperty(name2, location2, property, value);
}

/**
* remove all locations from a template
* @param name the name of the template to clean
*/
public void removeLocations (String name) {


// get the given template if it exists

	AbstractTemplate template = this.context.getDocument().getTemplate(name);
	if (template==null) 
		this.throwMissingElementException("template", name);

// get the given location and return it if it exists

	LinkedList<Node> nodes = new LinkedList<Node>();
	Node node = template.getFirst();

	while(node!=null) {
		nodes.add(node);

		node = node.getNext();
	}

// remove all locations from the list

	for (Node n:nodes) {
			RemoveElementCommand command = new RemoveElementCommand(n);
			command.execute();
			this.context.addCommand(command);
	}
}

/**
* remove a location based on its name and on the name of its template
* @param template the name of the template to inspect
* @param name the name of the location to remove
*/
public void removeLocation (String template, String name) {
	Location location = this.getLocation(template, "name", name);
	if (location==null) return;
		RemoveElementCommand command = new RemoveElementCommand(location);
		command.execute();
		this.context.addCommand(command);
}

/**
* get the name of all locations of a template
* @param name the name of the template to describe
* @return a linked list containing all locations to show
*/
public LinkedList<String> getLocations (String name) {


// get the given template if it exists

	AbstractTemplate template = this.context.getDocument().getTemplate(name);
	if (template==null) 
		this.throwMissingElementException("template", name);

// get the given location and return it if it exists

	LinkedList<String> locations = new LinkedList<String>();
	Node node = template.getFirst();

	while(node!=null) {
		if (node instanceof Location) 
			locations.add((String)node.getPropertyValue("name"));

		node = node.getNext();
	}

	return locations;
}

/**
* show all locations of a template
* @param name the name of the template to describe
* @return a linked list containing all locations to show
*/
public LinkedList<String> showLocations (String name) {


// get the given template if it exists

	AbstractTemplate template = this.context.getDocument().getTemplate(name);
	if (template==null) 
		this.throwMissingElementException("template", name);

// get the given location and return it if it exists

	LinkedList<String> locations = new LinkedList<String>();
	Node node = template.getFirst();

	while(node!=null) {
		if (node instanceof Location) 
			locations.add(this.describeLocation((Location)node));

		node = node.getNext();
	}

	return locations;
}

/**
* return the description of a location given by its name and the name of its template
* @param template the name of the template to inspect
* @param name the name of the location to describe
* @return a string containing the description of the location
*/
public String showLocation (String template, String name) {
	Location location = this.getLocation (template, "name", name);
	if (location==null) this.throwMissingElementException("location", name);
	return this.describeLocation(location);
}

/**
* set a property of a location described by the name of its template, of its source and its target
* @param template the template to update
* @param name the name of the location to update
* @param property the name of the property to update
* @param value the new value for the property
*/
public void setLocationProperty (String template, String name, String property, Object value) {

// first of all check that such a location does not already exist in the template

	Location location = this.getLocation(template, property, value);
	if (location!=null) {
		switch (property) {
			case "name":
			this.throwExistingElementException("location", template);
			break;
			case "init":
			this.throwExistingElementException("init", template);
			break;
			case "committed":
			this.throwExistingElementException("committed", template);
			break;
		}
	}

// get the given location and update it

	location = this.getLocation(template, "name", name);
	if (location==null) this.throwMissingElementException("location", name);
	SetPropertyCommand command = new SetPropertyCommand(location, property, value);
	command.execute();
	this.context.addCommand(command);
}

/**
* get the value associated with a location property
* @param template the name of the template to inspect
* @param name the name of the edge
* @param property the property of the location
* @return the value associated with the given property name for the given location
*/
public String getPropertyValue (String template, String name, String property) {
	Location location = this.getLocation(template, "name", name);
	if (location==null) this.throwMissingElementException("location", name);
	return (String) location.getPropertyValue(property);
}
}
