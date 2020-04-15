package org.uppaal.cli.context;

import com.uppaal.engine.Engine;
import com.uppaal.engine.EngineException;
import com.uppaal.engine.CannotEvaluateException;
import com.uppaal.model.system.UppaalSystem;
import com.uppaal.model.system.SystemState;
import com.uppaal.model.system.SystemLocation;
import com.uppaal.model.system.symbolic.SymbolicState;
import com.uppaal.model.system.symbolic.SymbolicTransition;
import com.uppaal.model.system.concrete
.ConcreteState;
import com.uppaal.model.system.concrete.ConcreteTransition;

import org.uppaal.cli.enumerations.OperationCode;
import org.uppaal.cli.enumerations.ObjectCode;
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
private ArrayList<SymbolicTransition> transitions;

// index of the currently considered transition
private int transition_index;

// system of this state expert
private UppaalSystem system;

public StateExpert (Context context) {
	super(context);
	this.state = null;
	this.index = -1;
	this.transitions = null;
	this.transition_index = -1;
	this.system = null;
}

/**
* @return the current state of this state expert
*/
public SystemState getState () {
	return this.state;
}

/**
* @return the current index of this state handler
*/
public int getIndex () {
	return this.index;
}

/**
* set the current state of this state expert
* @param state the new state for this state expert
* @param index the new index for this state expert
*/
public void setState (SystemState state) {
	this.state = state;
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
* @return the list of locations of this state
*/
public LinkedList<String> showLocations () {
	this.result.clear();
	SystemState watched_state = this.state;
	if (this.hasTransitions()) watched_state = this.transitions.get(this.transition_index).getTarget();

	for (SystemLocation location: watched_state.getLocations()) {
		this.result.addLast(location.getName());
	}

	return this.result;
}

/**
* @return a list containing the name and the value of each variable for the current state
*/
public LinkedList<String> showVariables () {
	if (this.state instanceof ConcreteState) 
		this.throwTraceFormatException(OperationCode.SHOW, ObjectCode.VARIABLES);

	SystemState watched_state = this.state;
	if (this.hasTransitions()) watched_state = this.transitions.get(this.transition_index).getTarget();
	int values[] = ((SymbolicState)watched_state).getVariableValues();
	this.result.clear();

	for (int i=0; i<this.system.getNoOfVariables(); i++) {
		this.result.addLast(this.system.getVariableName(i)+"="+values[i]);
	}

	return this.result;
}

/**
* @return the list of constraints of the current state
* @exception a trace exception if the format of the trace does not support this operation
*/

public LinkedList<String> showConstraints () {
	if (this.state instanceof ConcreteState) 
		this.throwTraceFormatException(OperationCode.SHOW, ObjectCode.CONSTRAINTS);

	this.result.clear();
	SystemState watched_state = this.state;
	if (this.hasTransitions()) watched_state = this.transitions.get(this.transition_index).getTarget();
	((SymbolicState)watched_state).getPolyhedron().getAllConstraints(this.result);
	return this.result;
}

/**
* compute the available transitions for the current state
* @exception an exception related to the engine if a problem was encountered
*/
public void computeTransitions () throws EngineException, CannotEvaluateException {
	Engine engine = this.context.getEngine();
	this.transitions = engine.getTransitions(this.system, (SymbolicState)this.state);
	this.transition_index = 0;
}

/**
* @return true if and only if this state expert currently supports some transitions or next states
*/
public boolean hasTransitions() {
	return this.transitions !=null;
}

/**
* @return a string describing the currently indexed transition
*/
public String showTransition () {
	return this.transitions.get(this.transition_index).traceFormat();
}

/**
* clear the transitions of this state expert
*/
public void clearTransitions () {
	this.transitions = null;
	this.transition_index = -1;
}

/**
* select the current transition and set the current state accordingly
* @return the newly selected transition
*/
public SymbolicTransition selectTransition () {
	SymbolicTransition transition = this.transitions.get(this.transition_index);
	this.state = transition.getTarget();
	this.clearTransitions();
	return transition;
}

/**
* set the transition index to the next available value, 0 if max is reached
*/
public void nextTransition () {
	if (this.hasTransitions())
		this.transition_index = (this.transition_index+1) % this.transitions.size();
}

/**
* set the transition index to the previous available value, max-1 if -1 is reached
*/
public void previousTransition () {
	if (this.hasTransitions()) {
		if (this.transition_index == 0) this.transition_index = this.transitions.size()-1;
		else this.transition_index = this.transition_index - 1;
	}
}
}
