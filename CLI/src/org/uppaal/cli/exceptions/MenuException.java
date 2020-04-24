package org.uppaal.cli.exceptions;




import org.uppaal.cli.enumerations.ModeCode;
import java.util.LinkedList;
import java.util.Iterator;

/**
* wrong mode exception, raised when the current mode does not support a certain command
*/

public class MenuException extends ConsoleException {

// code of the wrong command
private LinkedList<String> menu_items;

/**
* public constructor of a wrong mode exception
*/
public MenuException () {
	super();
	this.menu_items = new LinkedList<String>();
}

/**
* @return the current mode of the uppaal command line interface
*/
public Iterator<String> getMenuItems() {
	return this.menu_items.iterator();
}

/**
* add a handler code to this exception
* @param menu_item the new handler code for this exception
*/
public void addMenuItem (String menu_item) {
	this.menu_items.add(menu_item);
}

/**
* clear the list of handler codes
*/
public void clearMenuItems () {
	this.menu_items.clear();
}
}
