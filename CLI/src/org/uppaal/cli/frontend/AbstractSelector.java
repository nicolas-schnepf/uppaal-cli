package org.uppaal.cli.frontend;

import org.uppaal.cli.context.Context;
import org.uppaal.cli.exceptions.SelectorException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import org.jline.reader.EndOfFileException;
import org.jline.builtins.Widgets;
import org.jline.reader.impl.*;
import org.jline.reader.*;
import org.jline.reader.Binding;
import org.jline.utils.InfoCmp.Capability;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;

/**
* abstract selector, implements all methods common to all selectors
*/

public abstract class AbstractSelector implements Selector {

// the reader of this selector
protected LineReader reader;

// the number of elements handled by this selector
protected int element_number;

// the current element of this selector
protected int current_element;

// number of views
protected int view_number;

// current view index
protected int current_view;

// the print writer used to display the current selected element
private PrintWriter writer;

// the context of this selector
protected Context context;

// a selector exception thrown on escape
private SelectorException selector_exception;

// boolean stating that the selector is running
private boolean running;

// key map of this selector
private KeyMap<Binding> key_map;

/**
* public constructor of an abstract selector
* @param reader the reader used to initialize the selector
* @param context the context on which invoque the provided action
*/
        public AbstractSelector(LineReader reader, Context context, int view_number) {
	super();
	this.reader = reader;
	this.writer = reader.getTerminal().writer();
	this.context = context;
	this.selector_exception = new SelectorException();
	this.running = false;
	this.view_number = view_number;
	this.key_map = this.reader.getKeyMaps().get(LineReader.MAIN);
        }

/**
* throw the selector exception of this selector
* @exception a selector exception
*/
private void throwSelectorException() {
throw this.selector_exception;
}


@Override
public void refresh () {
	((LineReaderImpl)this.reader).clearScreen();
	this.writer.println (this.showCurrentElement());
	this.writer.print((this.current_element + 1) + "/" + this.element_number);
	if (this.isSelected()) this.writer.print("\t selected");
	this.writer.println("");
	this.writer.flush();
}

@Override
public void run () {

//	KeyMap<Binding> main_key_map = this.reader.getKeyMaps().get(LineReader.MAIN);
//	this.reader.getKeyMaps().put(LineReader.MAIN, this.key_map);
//	this.reader.setKeyMap(LineReader.MAIN);

	try {
		this.setElementNumber();
		this.refresh();
		this.running = true;
				this.reader.readLine("uppaal$");
		this.validateSelection();
	} catch (EndOfFileException e) {
	this.cancelSelection();
	}

	this.running = false;
//	this.reader.getKeyMaps().put(LineReader.MAIN, main_key_map);
//	this.reader.setKeyMap(LineReader.MAIN);
}

@Override
public void showPreviousElement() {
	if (this.current_element>0) this.current_element --;
	else this.current_element = this.element_number - 1;
	this.refresh();
}

@Override
public void showNextElement() {
	this.current_element = (this.current_element + 1) % this.element_number;
	this.refresh();
}

@Override
public void showPreviousView() {
	if (this.current_view>0) this.current_view--;
	else this.current_view = this.view_number - 1;
	this.refresh();
}

@Override
public void showNextView() {
	this.current_view = (this.current_view + 1) % this.view_number;
	this.refresh();
}
}