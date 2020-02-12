package org.uppaal.cli.handlers;

import org.uppaal.cli.CommandResult;
import org.uppaal.cli.Command;
import java.util.List;

/**
* interface providing the declaration of all public methods for a handler
*/

public interface Handler {
public static enum HandlerCode {
EDITOR,
SIMULATOR,
VERIFIER,
UNKNOWN
}

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
public Command.CommandCode[] getAcceptedCommands();

/**
* check if a command is accepted by a handler
* @param command the command to test
* @return true if and only if the provided command code is accepted by the handler
*/
public boolean acceptCommand(Command command);

/**
* @return the mode or current mode attached to this handler
*/

public HandlerCode getMode();
}