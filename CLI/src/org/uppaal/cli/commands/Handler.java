package org.uppaal.cli.commands;

import org.uppaal.cli.enumerations.OperationCode;
import org.uppaal.cli.enumerations.ObjectCode;
import org.uppaal.cli.enumerations.ModeCode;
import org.uppaal.cli.commands.CommandResult;
import org.uppaal.cli.commands.Command;
import java.util.List;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;

/**
* interface providing the declaration of all public methods for a handler
*/

public interface Handler {

/**
* handle a command and return its result
* @param command the command to execute
* @return the result of the command
*/

public CommandResult handle (Command command) ;

/**
* return the list of commands handled by this handler
* @return the list of commands used by this handler
*/
public HashSet<OperationCode> getAcceptedOperations();

/**
* @return current mode attached to this handler
*/

public ModeCode getMode();

/**
* check that a handler accept a certain mode
* @param mode the mode to test
* @param true if and only if the attached command supports this mode for at least one object
*/
public boolean acceptMode (ModeCode mode);
}
