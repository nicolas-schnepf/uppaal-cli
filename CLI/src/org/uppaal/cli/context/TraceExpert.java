package org.uppaal.cli.context;

import com.uppaal.engine.Engine;
import com.uppaal.engine.EngineException;
import com.uppaal.engine.CannotEvaluateException;
import com.uppaal.model.system.UppaalSystem;
import com.uppaal.model.system.SystemState;
import com.uppaal.model.system.AbstractTransition;
import com.uppaal.model.system.symbolic.SymbolicState;
import com.uppaal.model.system.symbolic.SymbolicTransition;
import com.uppaal.model.system.concrete.ConcreteState;
import com.uppaal.model.system.concrete.ConcreteTransitionRecord;

import org.uppaal.cli.context.ModeCode;
import org.uppaal.cli.exceptions.TraceFormatException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;

/**
* expert for managing a trace
*/

public class TraceExpert extends AbstractExpert {

// simulation trace of this trace expert
private ArrayList<AbstractTransition> trace;

// current system state of this trace expert if any
private SystemState state;

// current state index of this trace
private int state_index;

public TraceExpert (Context context) {
	super(context);
	this.trace = new ArrayList<AbstractTransition>();
	this.state = null;
	this.state_index = -1;
}

/**
* set the trace of this trace expert as a concrete trace
* @param strategy the strategy to apply to the simulation
* @return the newly created trace
*/

public ArrayList<AbstractTransition> setTrace (String strategy) throws EngineException, CannotEvaluateException {

// get the uppaal engine and clear the trace

	Engine engine = this.context.getEngine();
	UppaalSystem system = this.context.getSystem();
	this.trace.clear();

// compute the initial state

	ConcreteState concrete_state = engine.getConcreteInitialState(system, strategy);
	ConcreteTransitionRecord transition = new ConcreteTransitionRecord (null, null, concrete_state);

// update the information of the context

	this.trace.add(transition);
	this.context.getStateExpert().setState(concrete_state);
	this.context.getStateExpert().setIndex(0);
	return this.trace;
}

/**
* set the trace of this trace expert as a symbolic trace
* @return the newly created trace
*/
public ArrayList<AbstractTransition> setTrace () throws EngineException, CannotEvaluateException {

// get the uppaal engine and clear the trace

	Engine engine = this.context.getEngine();
	UppaalSystem system = this.context.getSystem();
	this.trace.clear();

// compute the information about the initial state

	SymbolicState symbolic_state = engine.getInitialState(system);
	SymbolicTransition transition = new SymbolicTransition (null, null, symbolic_state);

// update the information of the uppaal context

	this.trace.add(transition);
	this.context.getStateExpert().setState(symbolic_state);
	this.context.getStateExpert().setIndex(0);
	return this.trace;
}

/**
* add a transition to the current trace
* @param transition the new transition to add to the trace
*/
public void addTransition (AbstractTransition transition) {
	this.trace.add(transition);
}

/**
* export the trace to a provided file
* @param filename the name of the file to export the trace
* @exception an io exception if the trace could not be written to the specified filename
*/
public void saveTrace (String filename) throws IOException {

// check that the trace is well symbolic

	if (!(this.trace.get(0) instanceof SymbolicTransition)) 
		throw new TraceFormatException();

	FileWriter out = new FileWriter(filename);
	Iterator<AbstractTransition> it = this.trace.iterator();
	((SymbolicTransition) it.next()).getTarget().writeXTRFormat(out);
	while (it.hasNext()) {
		((SymbolicTransition)it.next()).writeXTRFormat(out);
	}
	out.write(".\n");
	out.close();
}

/**
* @return the trace of this expert
*/
public ArrayList<AbstractTransition> getTrace() {
	return this.trace;
}

/**
* @return the size of a trace
*/
public int getTraceSize() {
	return this.trace.size();
}

/**
* select the current state of the trace expert
*/
public void selectState() {
	this.context.getStateExpert().setState(this.state);
	this.clearState();
}

/**
* clear the current state of this trace expert
*/
public void clearState() {
	this.state = null;
	this.state_index = -1;
}

/**
* get a state at a particular index
* @param index the index of the state
* @return the state at the specified index
*/
private SystemState getState (int index) {
	if (index != this.state_index) {
		if (this.trace.get(0) instanceof SymbolicTransition)
			this.state =  ((SymbolicTransition)this.trace.get(index)).getTarget();
	}
	return this.state;
}

/**
* show the information about the processes in a particular state
* @param index the index of the state to inspect
* @return a list containing all the intended information
*/
public String showStateProcesses (int index) {
	LinkedList<String> processes=this.context.getStateExpert().showProcesses(this.getState(index));
	StringBuffer output = new StringBuffer();

	for (String process:processes) {
		if (output.length()>0) output.append("\n");
		output.append(process);
	}

	return output.toString();
}

/**
* show the information about the variables in a particular state
* @param index the index of the state to inspect
* @return a list containing all the intended information
*/
public String showStateVariables (int index) {
	LinkedList<String> variables = this.context.getStateExpert().showVariables(this.getState(index));
	StringBuffer output = new StringBuffer();

	for (String variable:variables) {
		if (output.length()>0) output.append("\n");
		output.append(variable);
	}

	return output.toString();
}
}
