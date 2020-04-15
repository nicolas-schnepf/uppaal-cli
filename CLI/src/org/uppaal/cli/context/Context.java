package org.uppaal.cli.context;

/***
* container class for the uppaal context
* supports different experts to handle the different entities of an uppaal model
*/

import org.uppaal.cli.enumerations.ModeCode;
import com.uppaal.model.core2.AbstractCommand;
import com.uppaal.model.core2.Document;
import com.uppaal.model.system.UppaalSystem;
import com.uppaal.engine.Engine;
import com.uppaal.model.system.AbstractTrace;
import java.util.LinkedList;

public class Context {
private ModeCode mode;
private LinkedList<AbstractCommand> commands;
private LinkedList<AbstractCommand> undone_commands;

private ModelExpert model_expert;
private EngineExpert engine_expert;
private QueryExpert query_expert;
private TemplateExpert template_expert;
private LocationExpert location_expert;
private EdgeExpert edge_expert;
private TraceExpert trace_expert;
private StateExpert state_expert;

/**
* create an empty console without any argument
*/

public Context () {
	this.commands = new LinkedList<AbstractCommand>();
	this.undone_commands = new LinkedList<AbstractCommand>();

	this.model_expert = new ModelExpert(this);
	this.engine_expert = new EngineExpert(this);
	this.query_expert = new QueryExpert(this);
	this.template_expert = new TemplateExpert(this);
	this.location_expert = new LocationExpert(this);
	this.edge_expert = new EdgeExpert(this);
	this.trace_expert = new TraceExpert(this);
	this.state_expert = new StateExpert(this);
}

/**
* get the document attached to this context
*/
public Document getDocument () {
	return this.model_expert.getDocument();
}

/**
* @return the system attached to this context
*/
public UppaalSystem getSystem() {
	return this.model_expert.getSystem();
}

/**
* @return the engine attached to this context
*/
public Engine getEngine () {
	return this.engine_expert.getEngine();
}

/**
* @return the trace attached to this context
*/
public AbstractTrace getTrace () {
	return this.trace_expert.getTrace();
}

/**
* @return the current mode of this context
*/
public ModeCode getMode() {
	return this.mode;
}

/**
* set the current mode of this context
* @param mode the new mode for this context
*/
public void setMode(ModeCode mode) {
	this.mode = mode;
}

/**
* @return the document expert of this context
*/
public ModelExpert getModelExpert () {
	return this.model_expert;
}

/**
* @return the engine expert of this context
*/
public EngineExpert getEngineExpert() {
	return this.engine_expert;
}

/**
* @return the query expert of this context
*/
public QueryExpert getQueryExpert() {
	return this.query_expert;
}

/**
* @return the template expert of this context
*/
public TemplateExpert getTemplateExpert() {
	return this.template_expert;
}

/**
* return the location expert of this context
*/
public LocationExpert getLocationExpert() {
	return this.location_expert;
}

/**
* @return the edge expert of this context
*/
public EdgeExpert getEdgeExpert() {
	return this.edge_expert;
}

/**
* @return the trace expert of this context
*/
public  TraceExpert getTraceExpert () {
	return this.trace_expert;
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
