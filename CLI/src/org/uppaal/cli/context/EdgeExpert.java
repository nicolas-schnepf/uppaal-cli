package org.uppaal.cli.context;

/**
* edge expert, responsible for all edge operations
*/


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
import java.util.LinkedList;

public class EdgeExpert extends AbstractExpert {
public EdgeExpert (Context context) {
	super(context);
}

/**
* add a new edge described by its template, its source and its destination
* @param template_name the name of the template to inspect
* @param source_name the source of the new edge
* @param target_name the target of the new edge
* @param select the select of the new edge
* @param guard the guard of the new edge
* @param sync the sync of the new edge
* @param assignment the assignment of the new edge
*/
public void addEdge(String template_name, String source_name, String target_name, 
String select, String guard, String sync, String assignment) {
	Template template = (Template)this.context.getDocument().getTemplate(template_name);
	if (template==null) 
		this.throwMissingElementException("template", template_name);

	Location source = this.getLocation(template_name, "name", source_name);
	Location target = this.getLocation(template_name, "name", target_name);

        Edge edge = template.createEdge();
	edge.setSource(source);
	edge.setTarget(target);
	edge.setProperty("select", select);
	edge.setProperty("guard", guard);
	edge.setProperty("synchronisation", sync);
	edge.setProperty("assignment", assignment);

	InsertElementCommand command = new InsertElementCommand(edge.getCommandManager(), template, null, edge);
	command.execute();
	this.context.addCommand(command);
}

/**
* copy an edge from a template to another one
* @param name1 the name of the source template
* @param src1 the name of the source of the source edge
* @param tgt1 the name of the target of the source edge
* @param name2 the name of the target template
* @param src2 the name of the source of the target edge
* @param tgt2 the name of the target of the target edge
*/
public void copyEdge (String name1, String src1, String tgt1, String name2, String src2, String tgt2) {

// check that the two templates well exist

	Template template1 = (Template)this.context.getDocument().getTemplate(name1);
	if (template1==null) this.throwMissingElementException("template", name1);
	Template template2 = (Template)this.context.getDocument().getTemplate(name2);
	if (template2==null) this.throwMissingElementException("template", name2);

// check that the source edge well exists but not the target one

	try {
		this.getEdge(name2, src2, tgt2);
		this.throwExistingElementException("edge", src2+"->"+tgt2);
	} catch (MissingElementException e) {}

	Edge edge = this.getEdge(name1, src1, tgt1);
	try {edge = (Edge)edge.clone();}
	catch (Exception e) {}

// get the source and target of the target edge and add them to the created copy

	Location source = this.getLocation(name2, "name", src2);
	if (source==null) this.throwMissingElementException("location", src2);
	edge.setSource(source);

	Location target = this.getLocation(name2, "name", tgt2);
	if (target==null) this.throwMissingElementException("location", tgt2);
	edge.setTarget(target);

// finally insert the edge into the target template

	InsertElementCommand command = new InsertElementCommand(edge.getCommandManager(), template2, null, edge);
	command.execute();
	this.context.addCommand(command);
}

/**
* return an edge based on the name of its source, of its target and of its template
* @param template the name of the template to inspect
* @param source the name of the source of the edge to return
* @param target the name of the target of the edge to return
* @return the corresponding edge if found
*/
private Edge getEdge (String template_name, String source, String target) {

// get the given template if it exists

	AbstractTemplate template = this.context.getDocument().getTemplate(template_name);
	if (template==null) 
		this.throwMissingElementException("template", template_name);

// get the given edge and return it if it exists

	Node node = template.getFirst();
	Edge res = null;

	while(node!=null && res==null) {
		if ((node instanceof Edge)) {
		Edge edge = (Edge) node;
		Location src = (Location)edge.getSource();
		Location tar = (Location) edge.getTarget();
		if (src.getPropertyValue("name").equals(source) && tar.getPropertyValue("name").equals(target)) 
			res = edge;
		}

		node = node.getNext();
	}

	if (res==null) 
		this.throwMissingElementException("edge", source+" -> "+target);
	return res;
}

/**
* return all edges based on the name of their sources, of their targets and of their template
* @param template the name of the template to inspect
* @param source the name of the source of the edges to return
* @param target the name of the target of the edges to return
* @return the corresponding edge if found
*/
private LinkedList<Edge> getEdges (String template_name, String source, String target) {

// get the given template if it exists

	AbstractTemplate template = this.context.getDocument().getTemplate(template_name);
	if (template==null) 
		this.throwMissingElementException("template", template_name);

// get the given edge and return it if it exists

	Node node = template.getFirst();
	LinkedList<Edge> edges = new LinkedList<Edge>();

	while(node!=null) {
		if ((node instanceof Edge)) {
		Edge edge = (Edge) node;
		Location src = (Location)edge.getSource();
		Location tar = (Location) edge.getTarget();
		if ((source.equals("*") ||
		src.getPropertyValue("name").equals(source)) &&
		(target.equals("*") ||
		tar.getPropertyValue("name").equals(target))) 
			edges.addLast(edge);
		}

		node = node.getNext();
	}

	return edges;
}

/**
* remove all edges from a template
* @param name the name of the template to clean
*/
public void removeEdges (String name) {

// get the given template if it exists

	AbstractTemplate template = this.context.getDocument().getTemplate(name);
	if (template==null) 
		this.throwMissingElementException("template", name);

// get the given edge and return it if it exists

	Node node = template.getFirst();
	LinkedList<Edge> edges = new LinkedList<Edge>();

	while(node!=null) {
		if ((node instanceof Edge)) {
		Edge edge = (Edge) node;
			edges.add(edge);
		} 

		node = node.getNext();
	}

// remove all edges from the list

	for (Edge edge:edges) {
		RemoveElementCommand command = new RemoveElementCommand(edge);
		command.execute();
		this.context.addCommand(command);
	}
}

/**
* remove edges based on the name of their source or of their target
* @param name the name of the template to clean
* @param source the name of the source to remove
* @param target the name of the target to remove
*/
public void removeEdges (String name, String source, String target) {

// get the given template if it exists

	AbstractTemplate template = this.context.getDocument().getTemplate(name);
	if (template==null) 
		this.throwMissingElementException("template", name);

// get the given edge and return it if it exists

	Node node = template.getFirst();
	LinkedList<Edge> edges = new LinkedList<Edge>();

	while(node!=null) {
		if ((node instanceof Edge)) {
		Edge edge = (Edge) node;
		Location src = (Location)edge.getSource();
		Location tar = (Location) edge.getTarget();
		if (src.getPropertyValue("name").equals(source) || tar.getPropertyValue("name").equals(target)) 
			edges.add(edge);
		} 

		node = node.getNext();
	}

// remove all edges from the list

	for (Edge edge:edges) {
		RemoveElementCommand command = new RemoveElementCommand(edge);
		command.execute();
		this.context.addCommand(command);
	}
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
*/
public String showEdge (String template, String source, String target) {
	Edge edge = this.getEdge(template, source, target);
	return this.describeEdge(edge);
}

/**
* return the description of all edges given by the name of their sources, of their targets and of their template
* @param template the name of the template to inspect
* @param source the source of the edges to describe
* @param target the target of the edges to describe
* @return the list of all edge description
*/
public LinkedList<String> showEdges (String template, String source, String target) {
	this.result.clear();
	for (Edge edge:this.getEdges(template, source, target))
		this.result.addLast(this.describeEdge(edge));
	return this.result;
}

/**
* get the value associated with an edge property
* @param template the name of the template to inspect
* @param source the source of the edge
* @param target the target of the edge
* @param property the property of the edge
* @return the value associated with the given property name for the given edge
*/
public String getPropertyValue (String template, String source, String target, String property) {
	Edge edge = this.getEdge(template, source, target);
	if (edge==null) this.throwMissingElementException("edge", source + "->" + target);
	if (edge.getPropertyValue(property)==null) return "";
	else return  edge.getPropertyValue(property).toString();
}

/**
* set a property of an edge described by the name of its template, of its source and its target
* @param template the template to update
* @param source the name of the source of the edge
* @param target the name of the target of the edge
* @param name the name of the property to update
* @param value the new value for the property
*/
public void setEdgeProperty (String template, String source, String target, String name, Object value) {
	Edge edge = this.getEdge(template, source, target);
	SetPropertyCommand command = new SetPropertyCommand(edge, name, value);
	command.execute();
	this.context.addCommand(command);
}

/**
* copy a property from an edge to another one
* @param name1 the name of the source template
* @param source1 the name of the source of the source edge
* @param target1 the name of the target of the source edge
* @param name2 the name of the target template
* @param source2 the name of the source of the target edge
* @param target2 the name of the target of the target edge
* @param property the name of the property to copy
*/
public void copyEdgeProperty (String name1, String source1, String target1, String name2, String source2, String target2, String property) {
	String value = this.getPropertyValue(name1, source1, target1, property);
	value = new String (value);
	this.setEdgeProperty(name2, source2, target2, property, value);
}
}
