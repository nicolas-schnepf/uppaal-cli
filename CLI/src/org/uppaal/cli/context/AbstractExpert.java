package org.uppaal.cli.context;

/**
* abstract expert class, provide the common fields shared by all experts
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
import org.uppaal.cli.enumerations.ObjectCode;
import org.uppaal.cli.exceptions.MissingElementException;
import org.uppaal.cli.exceptions.ExistingElementException;
import java.util.LinkedList;

public abstract class AbstractExpert implements Expert {

// protected context handled by this expert

protected Context context;
protected LinkedList<String> result;

// missing element exception, thrown when an element is missing to a document

private MissingElementException missing_element_exception;

// existing element exception, thrown when an element already exists in a document

private ExistingElementException existing_element_exception;

/**
* constructor of an abstract expert, receiving a context as argument
* @param context the context of this expert
*/

public AbstractExpert (Context context) {
	this.context = context;
	this.result = new LinkedList<String>();
	this.missing_element_exception = new MissingElementException();
	this.existing_element_exception = new ExistingElementException();
}

/**
* @exception a missing element exception containing the object code and the name of the missing element
*/
public void throwMissingElementException (ObjectCode object_code, String name) {
	this.missing_element_exception.setObjectCode(object_code);
	this.missing_element_exception.setName(name);
	throw this.missing_element_exception;
}

/**
* @exception a existing element exception containing the object code and the name of the existing element
*/
public void throwExistingElementException (ObjectCode object_code, String name) {
	this.existing_element_exception.setObjectCode(object_code);
	this.existing_element_exception.setName(name);
	throw this.existing_element_exception;
}


/**
* return the description of a location given as parameter
* @param location the location to describe
* @return the string containing the description of the location
*/
protected String describeLocation (Location location) {
	StringBuffer description = new StringBuffer();
	description.append(location.getPropertyValue("name"));

	if (location.isPropertyLocal("invariant"))
		description.append(" { "+location.getPropertyValue("invariant")+" } ");
	return description.toString();
}

/**
* return the description of an edge given as parameter
* @param edge the edge to describe
* @return the string containing the description of the edge
*/
protected String describeEdge (Edge edge) {
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
* return a location based on the name of its template, of one of its properties and its value
* @param template the name of the template to inspect
* @param property the name of the property to inspect
* @param value the value of the property field of the location to return
* @return the corresponding location, if found
* @exception a missing element exception if either the template or the location was not found
*/
protected Location getLocation (String template_name, String property, Object value) {

// get the given template if it exists

	AbstractTemplate template = this.context.getDocument().getTemplate(template_name);
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
}
