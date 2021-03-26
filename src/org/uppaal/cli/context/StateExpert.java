package org.uppaal.cli.context;

import com.uppaal.engine.Engine;
import com.uppaal.engine.EngineException;
import com.uppaal.engine.CannotEvaluateException;
import com.uppaal.model.system.UppaalSystem;
import com.uppaal.model.system.SystemState;
import com.uppaal.model.system.SystemLocation;
import com.uppaal.model.system.SystemEdge;
import com.uppaal.model.system.AbstractTransition;
import com.uppaal.model.system.symbolic.SymbolicState;
import com.uppaal.model.system.symbolic.SymbolicTransition;
import com.uppaal.model.system.concrete
.ConcreteState;
import com.uppaal.model.system.concrete.ConcreteTransition;



import org.uppaal.cli.exceptions.TraceFormatException;
import java.util.LinkedList;
import java.util.ArrayList;

/**
* state expert providing all methods to manipulate uppaal states
*/

public class StateExpert extends SimulatorExpert {

// the state of this expert
private SystemState state;

// the index of the current state
private int index;

// list of transitions for the current state
private ArrayList<AbstractTransition> transitions;

// system of this state expert
private UppaalSystem system;

public StateExpert (Context context) {
	super(context);
	this.state = null;
	this.index = -1;
	this.transitions = new ArrayList<AbstractTransition>();
	this.system = null;
}

/**
* @return the current state of this state expert
*/
public SystemState getState () {
	return this.state;
}

/**
* set the current state of this state expert
* @param state the new state for this state expert
*/
public void setState (SystemState state) {
	this.state = state;
}


/**
* @return the current index of this state handler
*/
public int getIndex () {
	return this.index;
}

/**
* set the index of this state expert
* @param index the new index of this state expert
*/
public void setIndex (int index) {
	this.index = index;
}


/**
* @return the system of this state expert
*/
public UppaalSystem getSystem () {
	return this.system;
}

/**
* set the system of this state expert
* @param system the new system for this state expert
*/
public void setSystem (UppaalSystem system) {
	this.system = system;
}

/**
* @return a string containing the textual description of the current state
*/
public String showState () {
	return this.state.traceFormat();
}

/**
* show the information about the processes in the current state
* @return a string with all the intended information
*/
public LinkedList<String> showProcesses () {
	return this.showProcesses(this.state);
}

/**
* show the information about the processes in a particular state
* @param watched_state the state to watch
* @return the list of processes with their current locations in this state
*/
public LinkedList<String> showProcesses (SystemState watched_state) {
	this.result.clear();
	SystemLocation[] locations = watched_state.getLocations();

	for (int i=0;i<this.system.getNoOfProcesses();i++) {
		String res = this.system.getProcess(i).getName()+": "+locations[i].getName();
		this.result.addLast(res);
	}

	return this.result;
}

/**
* show the current state of a specific process
* @param id the id of the desired process
* @return a string describing the required process and its current state
*/
public String showProcess (String id) {
	this.result.clear();
	SystemState watched_state = this.state;
	int index = -1;

	try {
		index = Integer.parseInt(id);
	} catch (NumberFormatException e) {
		index = this.system.getProcessIndex(id);
	}

	if (index==-1)
		this.throwMissingElementException("process", id);

	SystemLocation[] locations = watched_state.getLocations();
	String res = this.system.getProcess(index).getName()+": "+locations[index].getName();
	return res;
}

/**
* show the information about the variables in the current state
* @return a list containing the intended information
*/
public LinkedList<String> showVariables () {
	return this.showVariables(this.state);
}

/**
* show the information about the variables in a particular state
* @param watched_state the state to watch
* @return a list containing the name and the value of each variable for the current state
*/
public LinkedList<String> showVariables (SystemState watched_state) {
	if (this.state instanceof ConcreteState) 
		this.throwTraceFormatException("show", "variables");

	int values[] = ((SymbolicState)watched_state).getVariableValues();
	this.result.clear();

	for (int i=0; i<this.system.getNoOfVariables(); i++) {
		this.result.addLast(this.system.getVariableName(i)+"="+values[i]);
	}

	return this.result;
}

/**
* show the current value of a specific variable
* @param name the name of the intended variable
* @return a string describing the required variable and its current value
*/
public String showVariable (String name) {
	this.result.clear();
	SystemState watched_state = this.state;

	int index = this.system.getVariables().indexOf(name);
	if (index==-1) return this.showConstraints(name);
		int values[] = ((SymbolicState)watched_state).getVariableValues();
	return name+" = "+values[index];
}

/**
* return all clock constraints of the current state
* @return the list of constraints of the current state
*/

public LinkedList<String> showConstraints () {
	if (this.state instanceof ConcreteState) 
		this.throwTraceFormatException("show", "constraints");

	this.result.clear();
	SystemState watched_state = this.state;
	((SymbolicState)watched_state).getPolyhedron().getAllConstraints(this.result);
	return this.result;
}

/**
* return all constraints for a given clock in the current state
* @param name the name of the clock to show
* @return the list of constraints for the given clock in the current state
*/

public String showConstraints (String name) {
	if (this.state instanceof ConcreteState) 
		this.throwTraceFormatException("show", "constraints");

	this.result.clear();
	SystemState watched_state = this.state;
	((SymbolicState)watched_state).getPolyhedron().getAllConstraints(this.result);
	StringBuffer output = new StringBuffer();

	for (String constraint: this.result) {
		if (constraint.contains(name)) {
			if (output.length()>0) output.append("\n");
			output.append(constraint);
		}
	}

	if (output.length()==0) this.throwMissingElementException("variable", name);
	return output.toString();
}

/**
* compute the available transitions for the current state
* @throws EngineException an exception if some problem was encountered with the engine
* @throws  CannotEvaluateException an exception if it is not possible to evaluate the current state
*/
public void computeTransitions () throws EngineException, CannotEvaluateException {
	Engine engine = this.context.getEngine();
	if (this.state instanceof SymbolicState) 
		this.transitions.addAll(engine.getTransitions(this.system, (SymbolicState)this.state));
	else if (this.state instanceof ConcreteState) {
	}
}

/**
* @return the number of transitions currently supported by this state expert
*/
public int getTransitionNumber() {
	return this.transitions.size();
}

/**
* @return true if and only if this state expert currently supports some transitions or next states
*/
public boolean hasTransitions() {
	return this.transitions !=null;
}

/**
* show a text description of a transition from its index
* @param index the index of the transition to show
* @return a string describing a transition described by its index
*/
public String showTransition (int index) {

	AbstractTransition transition = this.transitions.get(index);
	if (transition instanceof SymbolicTransition) return this.showSymbolicTransition((SymbolicTransition)transition);
	else return this.showConcreteTransition((ConcreteTransition)transition);
}

/**
* show the description of a symbolic transition
* @param transition the transition to describe
* @return the textual description of the input transition
*/
private String showSymbolicTransition(SymbolicTransition transition) {
			// check the number of edges involved:
	if (transition.getSize()==0) {
// no edges, something special (like "deadlock"):
			return transition.getEdgeDescription();
	} else {
// one or more edges involved, print them:
		StringBuffer buffer = new StringBuffer();
		for (SystemEdge e: transition.getEdges()) {
			if (buffer.length()>0) buffer.append("\n");
			buffer.append(e.getProcessName()+": "
			+ e.getEdge().getSource().getPropertyValue("name")
									 + " -> " // " \u2192 "
			+ e.getEdge().getTarget().getPropertyValue("name")+";");
		}

		return buffer.toString();
	}
}

/**
* return the textual description of a concrete transition
* @param transition the transition to describe
* @return a textual description of the input transition
*/
private String showConcreteTransition (ConcreteTransition transition) {
	return "";
}

/**
* show how variables are affected by a certain transition
* @param index the index of the transition to detail
* @return a string detailing the assignment of variables of a transition
*/
public String showVariableAssignment (int index) {

// check that the trace is well symbolic

	if (this.state instanceof ConcreteState) 
		this.throwTraceFormatException("show", "variables");

// get the source and the destination of the transition and prepare the output

	AbstractTransition transition = this.transitions.get(index);
	int[] source_values = ((SymbolicState) this.state).getVariableValues();
	int[] target_values = null;
	if (transition instanceof SymbolicTransition) 
		target_values = ((SymbolicTransition)transition).getTarget().getVariableValues();
	StringBuffer output = new StringBuffer();

// iterate over all variables to find those that are affected by the transition

	for (int i=0;i<this.system.getNoOfVariables();i++) {
		int source = source_values[i];
		int target = target_values[i];
		if (source!=target)
			output.append(this.system.getVariableName(i) + ": " + source + " -> " + target + ";\n");
	}

	return output.toString();
}

/**
* clear the transitions of this state expert
*/
public void clearTransitions () {
	this.transitions.clear();
}

/**
* select the current transition and set the current state accordingly
* @param index the index of the transition to select
* @return the newly selected transition
*/
public AbstractTransition selectTransition (int index) {
	AbstractTransition transition = this.transitions.get(index);
	if (transition instanceof SymbolicTransition)
		this.state = ((SymbolicTransition)transition).getTarget();
	this.context.getTraceExpert().addTransition(transition);
	this.clearTransitions();
	return transition;
}
}
