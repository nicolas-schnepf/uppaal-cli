package org.uppaal.cli.context;

/***
* container class for the uppaal context
* supports different experts to handle the different entities of an uppaal model
*/

import com.uppaal.model.core2.AbstractCommand;
import com.uppaal.model.core2.Document;
import com.uppaal.model.system.UppaalSystem;
import com.uppaal.engine.Engine;
import com.uppaal.model.system.AbstractTransition;
import org.uppaal.cli.exceptions.UnknownModeException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;

public class Context {

// active mode of this context
private ModeCode mode;

// unknown mode exception of this context
private UnknownModeException unknown_mode_exception;

// hash map of accepted modes for this command manager
private HashMap <String, ModeCode> accepted_modes;

// hashmap of mode names for this console manager
private HashMap<ModeCode, String> mode_names;

// list of executed commands
private LinkedList<AbstractCommand> commands;


// list of undone commands
private LinkedList<AbstractCommand> undone_commands;

private ModelExpert model_expert;
private EngineExpert engine_expert;
private QueryExpert query_expert;
private TemplateExpert template_expert;
private LocationExpert location_expert;
private EdgeExpert edge_expert;
private TraceExpert trace_expert;
private StateExpert state_expert;
private OptionExpert option_expert;
private DataExpert data_expert;
private StrategyExpert strategy_expert;

/**
* create an empty console without any argument
*/

public Context () {
	this.commands = new LinkedList<AbstractCommand>();
	this.undone_commands = new LinkedList<AbstractCommand>();

	this.accepted_modes = new HashMap<String, ModeCode>();
	this.mode_names = new HashMap<ModeCode, String>();
	this.unknown_mode_exception = new UnknownModeException();

// initialize the map of accepted modes

	this.mode = ModeCode.EDITOR;
	this.accepted_modes.put("editor", ModeCode.EDITOR);
	this.accepted_modes.put("simulator", ModeCode.SIMULATOR);
	this.accepted_modes.put("verifier", ModeCode.VERIFIER);

// initialize the map of mode names from the previous one

	for (String mode_name: this.accepted_modes.keySet()) {
		ModeCode mode_code = this.accepted_modes.get(mode_name);
		this.mode_names.put(mode_code, mode_name);
	}

	this.model_expert = new ModelExpert(this);
	this.engine_expert = new EngineExpert(this);
	this.query_expert = new QueryExpert(this);
	this.template_expert = new TemplateExpert(this);
	this.location_expert = new LocationExpert(this);
	this.edge_expert = new EdgeExpert(this);
	this.trace_expert = new TraceExpert(this);
	this.state_expert = new StateExpert(this);
	this.option_expert = new OptionExpert(this);
	this.data_expert = new DataExpert(this);
	this.strategy_expert = new StrategyExpert(this);
}

/**
* throw an unknown mode exception
* @param mode the mode for the unknown mode exception
* @exception an unknown mode exception with all the required information
*/
private void throwUnknownModeException (String mode) {
	this.unknown_mode_exception.setMode(mode);
	throw this.unknown_mode_exception;
}

/**
* get the document attached to this context
* @return the document of this context
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
public ArrayList<AbstractTransition> getTrace () {
	return this.trace_expert.getTrace();
}

/**
* @return the current mode of this context
*/
public ModeCode getMode() {
	return this.mode;
}

/**
* convert the string encoding the name of a mode into its enumeration code
* @param mode the name of the mode to convert
* @return the corresponding mode code
*/
public ModeCode getMode(String mode) {
	if (!this.accepted_modes.keySet().contains(mode)) this.throwUnknownModeException(mode);
	return this.accepted_modes.get(mode);
}

/**
* set the current mode of this context
* @param mode the new mode for this context
*/
public void setMode(String mode) {
	if (this.accepted_modes.keySet().contains(mode))
		this.mode = this.accepted_modes.get(mode);
	else
		this.throwUnknownModeException(mode);
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
* @return the location expert of this context
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
* @return the state expert of this context
*/
public  StateExpert getStateExpert () {
	return this.state_expert;
}

/**
* @return the option expert of this context
*/
public OptionExpert getOptionExpert() {
	return this.option_expert;
}

/**
* @return the data expert of this context
*/
public DataExpert getDataExpert() {
	return this.data_expert;
}

/**
* get the strategy expert of this context
* @return the strategy expert of this controller
*/
public StrategyExpert getStrategyExpert() {
	return this.strategy_expert;
}

/**
* add a command to the list of commands of this context
* @param command the command to add
*/
public void addCommand (AbstractCommand command) {
	this.undone_commands.clear();
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

/**
* clear the lists of commands of this context
*/
public void clearCommands() {
	this.commands.clear();
	this.undone_commands.clear();
}
}
