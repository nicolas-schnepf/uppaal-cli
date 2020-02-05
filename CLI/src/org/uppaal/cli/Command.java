/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uppaal.cli;

import org.uppaal.cli.handlers.Handler;

/**
 * Command class parses an input line into a command and its arguments separated by white space.
 * @author Marius Mikucionis <marius@cs.aau.dk>
 */
public class Command
{
public static enum CommandCode {
ADD, IMPORT, EXPORT, START, EXIT, CHECK
}

/*** enumeration of object codes */

public static enum ObjectCode {
DOCUMENT, QUERIES, TEMPLATES, QUERY, TEMPLATE;
}

    private String command = null;
    private CommandCode command_code;
    private Handler.HandlerCode mode;
    private String args = null;
    public Command(String line) {
        int pos = 0;
        // skip white space:
        while (pos < line.length() && Character.isWhitespace(line.charAt(pos)))
            ++pos;
        int begin = pos;
        // find white next space:
        while (pos < line.length() && !Character.isWhitespace(line.charAt(pos)))
            ++pos;
        command = line.substring(begin,pos);
        // skip white space:
        while (pos < line.length() && Character.isWhitespace(line.charAt(pos)))
            ++pos;        
        args = line.substring(pos);
    }
    public String getCommand() {
        return command;
    }

/**
* set the command code of this command
* @param command_code the new command code for this command
*/
public void setCommandCode() {
	this.command_code = command_code;
}

/**
* @return the command code of this command
*/
public CommandCode getCommandCode () {
	return this.command_code;
}

/**
* set the mode of this command
* @param mode the new mode for this command
*/
public void setMode (Handler.HandlerCode mode) {
	this.mode = mode;
}

/**
* @return the mode of this command
*/
public Handler.HandlerCode getMode() {
	return this.mode;
}

    public String getArgs(){
        return args;
    }
}
