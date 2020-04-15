package org.uppaal.cli.context;

/**
* edge expert, responsible for all edge operations
*/

import org.uppaal.cli.enumerations.ObjectCode;
import org.uppaal.cli.exceptions.MissingElementException;
import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.Template;
import com.uppaal.model.core2.QueryList;
import com.uppaal.model.core2.Query;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.Node;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.Edge;
import com.uppaal.model.core2.InsertElementCommand;
import com.uppaal.model.core2.RemoveElementCommand;
import com.uppaal.model.core2.SetPropertyCommand;

public class EdgeExpert extends AbstractExpert {
public EdgeExpert (Context context) {
	super(context);
}

/**
* add a new edge described by its template, its source and its destination
* @param template_name the name of the template to inspect
* @param source_name the source of the new edge
* @param target_name the target of the new edge
* @exception an exception is thrown if either the template, the source or the think does not exist
*/
public void addEdge(String template_name, String source_name, String target_name) {
	Template template = (Template)this.context.getDocument().getTemplate(template_name);
	if (template==null) 
		this.throwMissingElementException(ObjectCode.TEMPLATE, template_name);

	Location source = this.getLocation(template_name, "name", source_name);
	Location target = this.getLocation(template_name, "name", target_name);

        Edge edge = template.createEdge();
	edge.setSource(source);
	edge.setTarget(target);

	InsertElementCommand command = new InsertElementCommand(edge.getCommandManager(), template, null, edge);
	command.execute();
	this.context.addCommand(command);
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

	AbstractTemplate template = this.context.getDocument().getTemplate(template_name);
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
		this.context.addCommand(command);
	} catch (MissingElementException e) {
	}
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
	this.context.addCommand(command);
}
}
