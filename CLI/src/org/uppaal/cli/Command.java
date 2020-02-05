/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uppaal.cli;

/**
 * Command class parses an input line into a command and its arguments separated by white space.
 * @author Marius Mikucionis <marius@cs.aau.dk>
 */
public class Command
{
public static enum CommandCode {
ADD, IMPORT, EXPORT
}

/*** enumeration of object codes */

public static enum ObjectCode {
DOCUMENT, QUERIES, TEMPLATES, QUERY, TEMPLATE;
}

    private String command = null;
    private CommandCode command_code;
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
public CommandCode getCommandCode () {
	return this.command_code;
}
    public String getArgs(){
        return args;
    }
}
