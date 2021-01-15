package org.uppaal.cli.frontend;

import org.uppaal.cli.context.Context;
import org.jline.reader.CompletingParsedLine;
import org.jline.reader.impl.completer.FileNameCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;
import org.jline.reader.Completer;
import org.jline.reader.Candidate;

import java.util.Collections;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
* class implementing a jline style command completer
* completing the tokens of the current command line depending on the current context
*/

public class CommandCompleter implements Completer {

// context of this command completer
private Context context;

// current command of this command completer
private String command;

// current type of this command completer
private String type;

// set of candidate objects
private HashSet<Candidate> objects;

// set of candidate templates of this completer
private HashSet<Candidate> templates;

// hash set of candidate options
private HashSet<Candidate> options;

// hash map of type properties
private HashMap <String, HashSet<Candidate>> type_properties;

// completer for the command names
private HashSet<Candidate> commands;

// completer for importable objects
private StringsCompleter importable_completer;

// completer for exportable objects
private StringsCompleter exportable_completer;

// completer for checkable objects
private StringsCompleter checkable_completer;

// mode completer of this command completer
private StringsCompleter mode_completer;

// file name completer of this command completer
private FileNameCompleter file_name_completer;

public CommandCompleter(Context context) {

// init the command, property and file name completers of this command completer

	this.context = context;
	this.command = null;
	this.commands = new HashSet<Candidate>();
	this.file_name_completer = new FileNameCompleter();
	this.templates = new HashSet<Candidate>();
	this.options = new HashSet<Candidate>();
	this.type_properties = new HashMap<String, HashSet<Candidate>>();

// create the importable completer of this command completer

	HashSet<String> importable = new HashSet<String>();
		Collections.addAll(importable, "document", "queries", "templates", "strategy", "data");
	this.importable_completer = new StringsCompleter(importable);

// create the exportable completer of this command completer

	HashSet<String> exportable = new HashSet<String>();
		Collections.addAll(exportable, "document", "queries", "trace", "strategy", "data");
	this.exportable_completer = new StringsCompleter(exportable);

// create the checkable completer of this command completer

	HashSet<String> checkable = new HashSet<String>();
		Collections.addAll(checkable, "queries", "selection");
	this.checkable_completer = new StringsCompleter(checkable);

// create the mode completer of this command completer

	HashSet<String> mode = new HashSet<String>();
		Collections.addAll(mode, "editor", "simulator", "verifier");
	this.mode_completer = new StringsCompleter(mode);

// init the set of candidate objects

	this.objects = new HashSet<Candidate>();
	this.objects.add(new Candidate("templates"));
	this.objects.add(new Candidate("queries"));
	this.objects.add(new Candidate("selection"));
	this.objects.add(new Candidate("options"));
	this.objects.add(new Candidate("parameters"));
	this.objects.add(new Candidate("variables"));
	this.objects.add(new Candidate("processes"));
	this.objects.add(new Candidate("trace"));
	this.objects.add(new Candidate("data"));
	this.objects.add(new Candidate("state"));
	this.objects.add(new Candidate("constraints"));
}

/**
* clear the information contained by this command completer
*/
public void clear () {
	this.command = null;
	this.type = null;
}

/**
* set the options of this command completer
* @param options the new collection of options for this command completer
*/
public void setOptions(Collection<String> options) {
	this.options.clear();
	for (String option:options)this.options.add(new Candidate(option));
}

/**
* add a new template name to this completer
* @param template the template name to add
*/
public void addTemplate(String template) {
	this.templates.add(new Candidate(template));
}

/**
* add all templates from a provided collection
* @param templates the collection of templates to add
*/
public void addTemplates(Collection<String> templates) {
	for (String template:templates) this.templates.add(new Candidate(template));
}

/**
* remove a template name from this completer
* @param template the template to remove
*/
public void removeTemplate(String template) {
	Candidate item = null;
	boolean found= false;

	for (Candidate candidate:this.templates) {
		if (candidate.value().equals(template)) {
			item = candidate;
			found = true;
			break;
		}
	}

	if (found)
	this.templates.remove(item);
}

/**
* clear the set of template names
*/
public void clearTemplates() {
	this.templates.clear();
}

/**
* set the current type of this command completer
* @param type the new type of this command completer
*/
public void setType (String type) {
	this.type = type;
}

/**
* set the properties for a given type
* @param type the type for which set the properties
* @param properties the properties to set
*/
public void setProperties(String type, Collection<String> properties) {
	this.type_properties.put(type, new HashSet<Candidate>());
	for (String property : properties) this.type_properties.get(type).add(new Candidate(property));
}

/**
* set the current command of this command completer
* @param command the new command of this command completer
*/
public void setCommand(String command) {
	this.command = command;
}

/**
* set the list of command keywords of this command completer
* @param commands the new collection of commands of this command completer
*/
public void setCommands (Collection<String> commands) {
	for (String command: commands) this.commands.add(new Candidate(command));
}

@Override
public void complete(LineReader reader,ParsedLine command_line, List<Candidate> candidates) {

//	System.out.println(command_line.wordIndex());
//	System.out.println(command_line.cursor());
//	System.out.println(command_line.wordCursor());
//	System.out.println(((CompletingParsedLine)command_line).rawWordCursor());
//	System.out.println(((CompletingParsedLine)command_line).rawWordLength());

// if no command is currently set into this command completer try to complete it

	if (this.command == null) {
		candidates.addAll(this.commands);
		this.completeRef(reader, command_line, candidates);
		return;
	}

// otherwise try to complete based on the name of the current command

	switch (this.command) {

// for a start command return the list of available modes

		case "start":
		if (command_line.words().size()==2)
		this.mode_completer.complete(reader, command_line, candidates);
		break;

// for a check command return the list of checkable objects

		case "check":
		if (command_line.words().size()==2) 
		this.checkable_completer.complete(reader, command_line, candidates);
		break;
// for an import or an export simply complete the type of object, the syntactic sugar and the file name

		case "load":
		switch (command_line.words().size()) {
			case 2:
			this.importable_completer.complete(reader, command_line, candidates);
			break;

			case 3:
			candidates.add(new Candidate("from"));
			break;

			case 4:
			this.file_name_completer.complete(reader, command_line, candidates);
			break;
		}
		break;

		case "save":
		switch (command_line.words().size()) {
			case 2:
			this.exportable_completer.complete(reader, command_line, candidates);
			break;

			case 3:
			candidates.add(new Candidate("to"));
			break;

			case 4:
			this.file_name_completer.complete(reader, command_line, candidates);
			break;
		}
		break;

// for a command requiring a reference to an uppaal object complete the reference

		case "set":
		case "reset":
		case "unset":
		case "show":
		this.completeRef(reader, command_line, candidates);
		break;

// otherwise do nothing

		default:
		break;
	}
}

/**
// complete a reference to an uppaal object
* @param reader the line reader
* @param command_line the parsed command line to analyze
* @param candidates the list of candidates to fill in
*/
public void completeRef(LineReader reader, ParsedLine command_line, List<Candidate> candidates) {

// complete the command based on the type of the current element

	switch(this.type) {

// if the current element is a template complete the command line based on the current number of words

		case "document":
		case "template":
		if (command_line.words().size()<=2) {
			candidates.addAll(this.type_properties.get("document"));
			candidates.addAll(this.objects);
			candidates.addAll(this.templates);
		} else candidates.addAll(this.type_properties.get("template"));
		break;

// if the current type is location fetch their list from the context and add them as candidates

		case "location":
		if (command_line.words().size()>3) 
			candidates.addAll(this.type_properties.get("location"));
		else {
			List<String> locations = 
			this.context.getLocationExpert().getLocations(command_line.words().get(1));
			for (String location:locations) {
				candidates.add(new Candidate(location));
				if (location.equals(command_line.words().get(command_line.words().size()-1))) {
					candidates.clear();
					return;
				}
			}
	}
		break;

// if the current type is edge do the same thing as for a location

		case "edge":
		if (command_line.words().size()>4) 
			candidates.addAll(this.type_properties.get("edge"));
		else {
			List<String> locations = 
			this.context.getLocationExpert().getLocations(command_line.words().get(1));
			for (String location:locations) {
				candidates.add(new Candidate(location));
				if (location.equals(command_line.words().get(command_line.words().size()-1))) {
					candidates.clear();
					return;
				}
			}
	}
		break;

// if the current type is process do the same work as for templates, except the document properties

		case "process":
		if (command_line.words().size()==2) {
			candidates.addAll(this.objects);
			candidates.addAll(this.templates);
		}
		break;

// if the current type is option try to complete the name of the parsed option

		case "option":
		if (command_line.words().size()==2) candidates.addAll(this.options);
		break;

// otherwise try to complete the property of the current type if sufficiently many words were parsed

		default:
		if (command_line.words().size()==3 && this.type_properties.keySet().contains(this.type))
			candidates.addAll(this.type_properties.get(this.type));
		break;
	}
}
}
