package org.uppaal.cli.frontend;

/**
* selection manager, handle the different selection screens of the uppaal command line manager
*/

import org.uppaal.cli.context.Context;
import java.lang.reflect.Method;
import java.util.HashSet;

import org.jline.utils.InfoCmp.Capability;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.LineReader;
import org.jline.reader.Reference;
import org.jline.terminal.Terminal;
import org.jline.keymap.KeyMap;

public class SelectionManager {

// selection reader of this selection manager
private LineReader selection_reader;

// current selector of this selection manager
private Selector selector;

// template selector of this selection manager
private TemplateSelector template_selector;

// transition selector of this selection manager
private TransitionSelector transition_selector;

// state selector of this selection manager
private StateSelector state_selector;

// query selector of this selection manager
private QuerySelector query_selector;

// data selector of this selection manager
private DataSelector data_selector;

/**
* public constructor of a selection manager
* @param terminal the terminal for this selection manager
* @param context the context for this selection manager
*/
public SelectionManager (Terminal terminal, Context context) {

	this.selection_reader = LineReaderBuilder.builder().terminal(terminal).build();
	this.selector = null;
	this.template_selector = new TemplateSelector(this.selection_reader, context);
	this.transition_selector = new TransitionSelector(this.selection_reader, context);
	this.state_selector = new StateSelector(this.selection_reader, context);
	this.query_selector = new QuerySelector(this.selection_reader, context);
	this.data_selector = new DataSelector(this.selection_reader, context);
	this.selection_reader.setKeyMap(LineReader.MAIN);

	try {
		Method prev_element = this.getClass().getMethod("showPreviousElement");
	CommandWidgets prev_element_widget = new CommandWidgets(this.selection_reader, this, prev_element, null);
		prev_element_widget.addWidget("prev_element", prev_element_widget::onKey);
		prev_element_widget.getKeyMap().bind(new Reference("prev_element"), KeyMap.key(this.selection_reader.getTerminal(), Capability.key_left));

		Method next_element = this.getClass().getMethod("showNextElement");
	CommandWidgets next_element_widget = new CommandWidgets(this.selection_reader, this, next_element, null);
		next_element_widget.addWidget("next_element", next_element_widget::onKey);
		next_element_widget.getKeyMap().bind(new Reference("next_element"), KeyMap.key(this.selection_reader.getTerminal(), Capability.key_right));

		Method prev_view = this.getClass().getMethod("showPreviousView");
	CommandWidgets prev_view_widget = new CommandWidgets(this.selection_reader, this, prev_view, null);
		prev_view_widget.addWidget("prev_view", prev_view_widget::onKey);
		prev_view_widget.getKeyMap().bind(new Reference("prev_view"), KeyMap.key(this.selection_reader.getTerminal(), Capability.key_down));

		Method next_view = this.getClass().getMethod("showNextView");
	CommandWidgets next_view_widget = new CommandWidgets(this.selection_reader, this, next_view, null);
		next_view_widget.addWidget("next_view", next_view_widget::onKey);
		next_view_widget.getKeyMap().bind(new Reference("next_view"), KeyMap.key(this.selection_reader.getTerminal(), Capability.key_up));

		Method validate = this.getClass().getMethod("validateSelection");
	CommandWidgets selection_widget = new CommandWidgets(this.selection_reader, this, validate, null);
		selection_widget.addWidget("validate", selection_widget::onKey);
		selection_widget.getKeyMap().bind(new Reference("validate"), " ");

//		this.reader.getKeyMaps().put(LineReader.MAIN, key_map);
//		this.reader.setKeyMap(LineReader.MAIN);

//		Method on_escape = this.getClass().getMethod("onEscape");
//	CommandWidgets escaping_widget = new CommandWidgets(this.reader, this, on_escape, null);
//		escaping_widget.addWidget("on_escape", escaping_widget::onKey);
//		escaping_widget.getKeyMap().bind(new Reference("on_escape"), KeyMap.esc());
	} catch (Exception e) {
		System.err.println(e);
		e.printStackTrace();
		System.exit(1);
	}
}

/**
* set the precision of the data selector
* @param precision the new precision for the data selector
*/
public void setPrecision (double precision) {
	this.data_selector.setPrecision(precision);
}
/**
* manage a template selection
* @return the set of selected template names
*/
public HashSet<String> selectTemplates () {
	this.selector = this.template_selector;
	this.selector.run();
	this.selector = null;
	return this.template_selector.getSelectedTemplates();
}

/**
* perform a transition selection
*/
public void selectTransition() {
	this.selector = this.transition_selector;
	this.selector.run();
	this.selector = null;
}

/**
* perform a state selection
*/
public void selectState() {
	this.selector = this.state_selector;
	this.selector.run();
	this.selector = null;
}

/**
* perform a query selection
*/
public void selectQueries() {
	this.selector = this.query_selector;
	this.selector.run();
	this.selector = null;
}

/**
* perform a data selection
*/
public void selectData() {
	this.selector = this.data_selector;
	this.selector.run();
	this.selector = null;
}

/**
* this method is called when pressing the left arrow
* it decrements the index of the current element and it refreshes it
* @return true
*/
public boolean showPreviousElement () {
	if (this.selector==null) return true;
	this.selector.showPreviousElement();
	return true;
}

/**
* this method is called when pressing the right arrow key
* it increments the index of the current element and refreshes the screen
* @return true
*/

public boolean showNextElement () {
	if (this.selector==null) return true;
	this.selector.showNextElement();
	return true;
}

/**
* this method is called when pressing the down arrow
* it decrements the index of the current view and it refreshes it
* @return true
*/
public boolean showPreviousView () {
	if (this.selector==null) return true;
	this.selector.showPreviousView();
	return true;
}

/**
* this method is called when pressing the up arrow key
* it increments the index of the current view and refreshes the screen
* @return true
*/

public boolean showNextView () {
	if (this.selector==null) return true;
	this.selector.showNextView();
	return true;
}
/**
* this method is called when the space key is pressed
* it selects the current element if it is not already so, otherwise it unselects it
* @return true
*/
public boolean validateSelection () {
	if (this.selector==null) return true;
	if (this.selector.isSelected()) this.selector.unselectCurrentElement();
	else this.selector.selectCurrentElement();
	this.selector.refresh();
	return true;
}
}
