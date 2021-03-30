package org.uppaal.cli.frontend;

import org.jline.reader.LineReaderBuilder;
import org.jline.reader.LineReader;
import org.uppaal.cli.context.Context;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.HashSet;

/**
* result viewer, used to display a too long result
*/

public class ResultViewer extends TemplateSelector {

public ResultViewer (LineReader reader, Context context) {
	super(reader, context);
	this.element_number = 1;
	this.template_descriptions.addLast(new LinkedList<String>());
}

@Override
public void setElementNumber() {
}

@Override
public void showNextElement() {
}

@Override
public void showPreviousElement() {
}

/**
* set the result to display
* @param result the result to display
*/
public void setResult (Iterable<String> result) {
	LinkedList<String> description = this.template_descriptions.get(0);
	description.clear();
	for (String line: result) description.addLast(line);
}

/**
* set the number of views of this result viewer
* @param view_number the new number of views of this result viewer
*/
public void setViewNumber (int view_number) {
	this.view_number = view_number;
}

@Override
public void showPreviousView() {
	if (this.current_view<this.view_number) this.current_view++;
	this.refresh();
}

@Override
public void showNextView() {
	if (this.current_view>0) this.current_view--;
	this.refresh();
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
}

@Override
public void validateSelection() {
}

@Override
public String showCurrentElement() {

// compute the index of the first and last lines to display

	LinkedList<String> description = this.template_descriptions.get(this.current_element);
	int height = this.reader.getTerminal().getHeight();
	int inf = description.size()<=height-5 ? 1 : this.current_view;
	int sup = description.size()<=height-5 ? description.size() : this.current_view + height - 5;

// compute the text to display for the template

	StringBuffer buffer = new StringBuffer();
	int index = 0;
	for (String line: description) {
		if (index>=inf && index<sup) {
			buffer.append(line);
			buffer.append("\n");
		}
		index++;
	}

	return buffer.toString();
}
}
