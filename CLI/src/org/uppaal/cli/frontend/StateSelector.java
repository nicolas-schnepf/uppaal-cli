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
* state selector, used to select a state from the trace
*/

public class StateSelector extends AbstractSelector {

// edge constant for the show element method
private static final int PROCESSES = 0;

// assignmentment constant for the show element method
private static final int VARIABLES = 1;

// end of file exception of this selector
private EndOfFileException eof_exception;


public StateSelector (LineReader reader, Context context) {
	super(reader, context, 2);
	this.eof_exception = new EndOfFileException();
}

@Override
public void setElementNumber() {
	this.element_number = this.context.getTraceExpert().getTraceSize();
	this.current_element = 0;
}

@Override
public boolean isSelected() {
	return false;
}

@Override
public void selectCurrentElement() {
}

@Override
public void unselectCurrentElement() {
}

@Override
public void cancelSelection() {
	this.context.getTraceExpert().clearState();
}

@Override
public void validateSelection() {
	this.context.getTraceExpert().selectState();
}

@Override
public String showCurrentElement() {
	switch (this.current_view) {
		case PROCESSES:
		return this.context.getTraceExpert().showStateProcesses(this.current_element);

		case VARIABLES:
		return this.context.getTraceExpert().showStateVariables(this.current_element);
	}

	return null;
}
}
