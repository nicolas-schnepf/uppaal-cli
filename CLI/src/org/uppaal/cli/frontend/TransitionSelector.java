package org.uppaal.cli.frontend;

import com.uppaal.engine.EngineException;
import org.jline.reader.EndOfFileException;
import com.uppaal.engine.CannotEvaluateException;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.LineReader;
import org.uppaal.cli.context.Context;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;

/**
* transition selector, used to select the next transition to execute in a simulation
*/

public class TransitionSelector extends AbstractSelector {

// edge constant for the show element method
private static final int EDGES = 0;

// assignmentment constant for the show element method
private static final int ASSIGNMENT = 1;

// end of file exception of this selector
private EndOfFileException eof_exception;


public TransitionSelector (LineReader reader, Context context) {
	super(reader, context, 1);
	this.eof_exception = new EndOfFileException();
}

@Override
public void setElementNumber() {
	this.element_number = this.context.getStateExpert().getTransitionNumber();
	this.current_element = 0;
}

@Override
public boolean isSelected() {
	return false;
}

@Override
public void selectCurrentElement() {
	try {
		this.context.getStateExpert().selectTransition(this.current_element);
		this.context.getStateExpert().computeTransitions();
		this.setElementNumber();
	} catch (EngineException e) {
		System.err.println("Engine error: try to disconnect and connect and retry the simulation");
		this.eof_exception.setStackTrace(Thread.currentThread().getStackTrace());
		throw this.eof_exception;
	} catch (CannotEvaluateException e) {
		System.err.println("Evaluation error: select another transition and retry");
		this.eof_exception.setStackTrace(Thread.currentThread().getStackTrace());
		throw this.eof_exception;
	}
}

@Override
public void unselectCurrentElement() {
}

@Override
public void cancelSelection() {
	this.context.getStateExpert().clearTransitions();
}

@Override
public void validateSelection() {
}

@Override
public String showCurrentElement() {
	StringBuffer output = new StringBuffer();
	output.append("Processes:\n");
	output.append(this.context.getStateExpert().showTransition(this.current_element)+"\n\n");

	output.append("Variables:\n");
	output.append(this.context.getStateExpert().showVariableAssignment(this.current_element));
	return output.toString();
}
}
