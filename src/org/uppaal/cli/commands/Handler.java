package org.uppaal.cli.commands;



import org.uppaal.cli.context.ModeCode;
import org.uppaal.cli.commands.CommandResult;

import java.util.List;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;

/**
* interface providing the declaration of all public methods for a handler
*/

public interface Handler {

/**
* @return the current object type of this handler
*/
public String getObjectType() ;

/**
* set the object type of this handler
* @param object_type the new object_type for this handler
*/
public void setObjectType(String object_type);

/**
* handle a command and return its result
* @return the result of the command
*/

public CommandResult handle () ;

/**
* @return current mode attached to this handler
*/

public ModeCode getMode();

/**
* check that a handler accept a certain mode
* @param mode the mode to test
* @return true if and only if the attached command supports this mode for at least one object
*/
public boolean acceptMode (ModeCode mode);

/**
* add an argument to this command
* @param argument the argument to add
*/
public void addArgument(String argument);

/**
* set the argument at the specified index
* @param index the index of the element to set
* @param argument the new argument to set in the handler
*/
public void setArgument (int index, String argument);

/**
* @return the number of arguments of this command
*/
public int getArgumentNumber();

/**
* clear the list of arguments of this command
*/
public void clear() ;

/**
* return an argument at a specified position
* @param index the position of the argument
* @return the intended argument
*/
public String getArgumentAt (int index) ;

/**
* get the help message attached to this handler
* @return the help message of this handler
*/
public String getHelpMessage();

/**
* get the syntax of the command for this handler
* @return a string containing a regex encoding the syntax for this command
*/
public String getSyntax();
}
