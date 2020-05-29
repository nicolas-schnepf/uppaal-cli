package org.uppaal.cli.context;

import com.uppaal.engine.Engine;
import com.uppaal.engine.EngineException;
import com.uppaal.engine.CannotEvaluateException;
import com.uppaal.model.system.UppaalSystem;
import com.uppaal.model.system.SystemState;
import com.uppaal.model.system.AbstractTrace;
import com.uppaal.model.system.symbolic.SymbolicTrace;
import com.uppaal.model.system.symbolic.SymbolicState;
import com.uppaal.model.system.symbolic.SymbolicTransition;
import com.uppaal.model.system.concrete.ConcreteTrace;
import com.uppaal.model.system.concrete.ConcreteState;
import com.uppaal.model.system.concrete.ConcreteTransitionRecord;

import org.uppaal.cli.context.ModeCode;
import org.uppaal.cli.exceptions.TraceFormatException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

/**
* expert for managing a trace
*/

public class TraceExpert extends AbstractExpert {
private AbstractTrace trace;

public TraceExpert (Context context) {
	super(context);
	this.trace = null;
}

/**
* set the trace of this trace expert
* @param type the type of the trace to be set to
* @return the newly created trace
*/

public AbstractTrace setTrace (ModeCode mode) throws EngineException, CannotEvaluateException {

	Engine engine = this.context.getEngine();
	UppaalSystem system = this.context.getSystem();

	switch (mode) {
// if the requested type is concrete set the trace accordingly

		case CONCRETE_SIMULATOR:
		ConcreteState concrete_state = engine.getConcreteInitialState(system);
		ConcreteTrace concrete_trace = new ConcreteTrace();
		this.trace = concrete_trace;
		ConcreteTransitionRecord concrete_transition = new ConcreteTransitionRecord (null, null, concrete_state);
		concrete_trace.add(concrete_transition);
		break;

// otherwise if the provided type is symbolic set it accordingly

		case SYMBOLIC_SIMULATOR:
		SymbolicState symbolic_state = engine.getInitialState(system);
		SymbolicTrace symbolic_trace = new SymbolicTrace();
		this.trace = symbolic_trace;
		SymbolicTransition transition = new SymbolicTransition (null, null, symbolic_state);
		symbolic_trace.add(transition);
		break;
	}

	return this.trace;
}

/**
* export the trace to a provided file
* @param filename the name of the file to export the trace
* @exception an io exception if the trace could not be written to the specified filename
*/
public void saveTrace (String filename) throws IOException {

// check that the trace is well symbolic

	if (!(this.trace instanceof SymbolicTrace)) 
		throw new TraceFormatException();

	FileWriter out = new FileWriter(filename);
	Iterator<SymbolicTransition> it = ((SymbolicTrace)this.trace).iterator();
	it.next().getTarget().writeXTRFormat(out);
	while (it.hasNext()) {
		it.next().writeXTRFormat(out);
	}
	out.write(".\n");
	out.close();
}

/**
* @return the trace of this expert
*/
public AbstractTrace getTrace() {
	return this.trace;
}

/**
* @return the size of a trace
*/
public int getTraceSize() {
	return this.trace.size();
}

/**
* get a state at a particular index
* @param index the index of the state
* @return the state at the specified index
*/
public SystemState getState (int index) {
	return this.trace.get(index).getTarget();
}
}
