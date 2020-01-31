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
VERIFIER
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
public Command.CommandCode[] getCommandList();
}