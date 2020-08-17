package org.uppaal.cli.frontend;

import java.lang.reflect.Method;
import org.jline.keymap.KeyMap;
import org.jline.builtins.Widgets;
import org.jline.reader.*;
import java.io.PrintWriter;
import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;

/**
* command widget, used to implement actions on key shortcuts
*/

public class CommandWidgets extends Widgets {


// the action to invoque when calling the widget
private Method action;

// the receiver on which call the registered method
private Object receiver;

// the print writer used to display messages when the widget is called
private PrintWriter writer;

// key map of this command widget
private KeyMap<Binding> key_map;

// the message to display when the widget is called
        private String message;

/**
* public constructor of a command widget
* @param reader the reader used to initialize the widget
* @param receiver the receiver on which calls the registered method
* @param action the action to invoque
* @param message the message to display when the action is called
*/
        public CommandWidgets(LineReader reader, Object receiver, Method action, String message) {
	super(reader);
	this.writer = reader.getTerminal().writer();
	this.key_map = reader.getKeyMaps().get(reader.getKeyMap());
	this.receiver = receiver;
	this.message = message;
	this.action = action;
        }


/**
* this method is called when pressing the key shortcut
* @return true
*/
public boolean onKey() {
	try {
		this.action.invoke(this.receiver);
		if (this.message!=null) this.writer.println(this.message);
		this.writer.flush();
	} catch (IllegalAccessException e) {
		System.err.println(e);
		e.printStackTrace();
	} catch (InvocationTargetException e) {
		System.err.println(e);
		e.printStackTrace();
	}
	return true;
}

@Override
public KeyMap<Binding> getKeyMap() {
//	return this.reader.getKeyMaps().get("selector");
	return this.key_map;
}
}