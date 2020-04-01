package org.uppaal.cli.handlers;

import org.uppaal.cli.commands.Command.OperationCode;
import org.uppaal.cli.commands.Command.ObjectCode;
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

public CommandResult handle (Command command) throws MalformedURLException, IOException;

/**
* return the list of commands handled by this handler
* @return the list of commands used by this handler
*/
public HashSet<OperationCode> getAcceptedOperations();

/**
* return the list of objects handled by this handler
* @return the list of objects used by this handler
*/
public HashSet<ObjectCode> getAcceptedObjects();

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
