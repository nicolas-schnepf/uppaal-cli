package org.uppaal.cli.context;

/**
* class handling an option expert, providing all methods to show, set and update verification options
*/

import com.uppaal.engine.EngineException;
import com.uppaal.engine.Engine;
import org.uppaal.cli.exceptions.TypeException;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import java.io.StringReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Collection;

public class OptionExpert extends AbstractExpert {

// hash map of options of this option expert
private HashMap<String, String> options;

// hash map of default option values of this option expert
private HashMap<String, String> default_values;

// hash map of option types of this option expert
private HashMap<String, String> types;

// option string of this option expert
private String option_string;

// hash map of options choices
private HashMap<String, HashMap<String, String>> choices;

// hash map of option parameters
private HashMap<String, HashMap<String, String>> parameters;

// type exception of this option expert
private TypeException type_exception;

/**
* public constructor of an option expert
* @param context the context of this option expert
*/
public OptionExpert (Context context) {
	super(context);
	this.options = new HashMap<String, String>();
	this.default_values = new HashMap<String, String>();
	this.types = new HashMap<String, String>();
	this.choices = new HashMap<String, HashMap<String, String>>();
	this.parameters = new HashMap<String, HashMap<String, String>>();
	this.type_exception = new TypeException();
	this.type_exception.setType("boolean");
}

/**
* return a list of all available options
* @return a list containing all available options
*/
public Collection<String> getOptions() {
	this.result.clear();
	this.result.addAll(this.options.keySet());
	return this.result;
}

/**
* throw a boolean type exception
*/
private void throwTypeException(String type) {
	this.type_exception.setType(type);
	throw this.type_exception;
}

/**
* load the options from the uppaal engine
* @throws EngineException an exception if a problem was encountered with the engine
* @throws  IOException an exception if there was some error while loading the options
*/
public void loadOptions() throws EngineException, IOException {

// load the option string

Engine engine = this.context.getEngineExpert().getEngine();
String option_info = engine.getOptionsInfo();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	Document document = null;

	try {
		InputSource is = new InputSource(new StringReader(option_info));
			DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.parse(is);
	} catch (Exception e) {
		System.err.println(e.getMessage());
		System.exit(1);
	}

	Element root = document.getDocumentElement();
	NodeList options = root.getChildNodes();

	for (int i = 0;i<options.getLength();i++) {
		Element option = (Element)options.item(i);
		String name = option.getAttribute("name");
	this.types.put(name, option.getAttribute("type"));
		this.options.put(name, option.getAttribute("default"));
		this.default_values.put(name, option.getAttribute("default"));

	switch (option.getAttribute("type")) {
		case "choice":
		NodeList choices = option.getChildNodes();
		HashMap <String, String> possible_choices = new HashMap<String, String>();
		this.choices.put(name, possible_choices);

			for (int j = 0;j<choices.getLength();j++) {
				Element choice = (Element) choices.item(j);
				possible_choices.put(choice.getAttribute("name"), choice.getAttribute("display"));
		}
		break;

			case "parameterset":
			NodeList parameters = option.getChildNodes();
			for (int j = 0;j<parameters.getLength();j++) {
				Element parameter = (Element) parameters.item(j);
				HashMap<String, String> parameter_values = new HashMap<String, String>();
				this.parameters.put(parameter.getAttribute("name"), parameter_values);

				parameter_values.put("display", parameter.getAttribute("display"));
				parameter_values.put("type", parameter.getAttribute("type"));
				parameter_values.put("rangemin", parameter.getAttribute("rangemin"));
				parameter_values.put("rangemax", parameter.getAttribute("rangemax"));
				parameter_values.put("default:", parameter.getAttribute("default"));
				parameter_values.put("fracmin", parameter.getAttribute("fracmin"));
				parameter_values.put("fracmax", parameter.getAttribute("fracmax"));
		}
		break;
		}
	}
}

/**
* get the option string from this option expert and build it if it isn't done
* @return the option string of this option expert
*/

public String getOptionString() {
	if (this.option_string!=null) return this.option_string;
	StringBuffer buffer = new StringBuffer();
	for (String option:this.options.keySet()) {
		if (!this.options.get(option).equals(""))
			buffer.append(option+" "+this.options.get(option)+"\n");
	}
	this.option_string = buffer.toString();
	return this.option_string;
}

/**
* show the list of all options with their current value
* @return the option string of this option expert
*/
public String showOptions() {
	return this.getOptionString();
}

/**
* get the information about a specific option, its current value and possible choices
* @param option the name of the option to show
* @return a string containing all intended information
*/
public String showOption (String option) {

// check that the option well exists

	if (!this.options.keySet().contains(option))
		this.throwMissingElementException("option", option);

// build the output string to return

	StringBuffer output = new StringBuffer();
	output.append(option);
	output.append (" ");

	output.append(this.types.get(option));
	output.append(" ");

	output.append(this.options.get(option));
	output.append("\n");

// add the information about the possible values if necessary

	switch (this.types.get(option)) {
		case "choice":
		output.append("\tPossible choices:\n");
		HashMap<String, String> possible_choices = this.choices.get(option);

		for (String choice:possible_choices.keySet()) {
			String value = possible_choices.get(choice);
			output.append("\t"+choice+" "+value+"\n");
		}
		break;

		case "boolean":
		break;

		case "parameterset":
		output.append("\tPossible parameters:\n");
		for (String parameter:this.parameters.keySet()) {
			output.append("\t"+this.parameters.get(parameter).get("display")+"\n");
		}
		break;

		default:
		break;
	}

	return output.toString();
}

/**
* show the information about a specific parameter for the statistical option
* @param index the index of the parameter to show
* @return a list of strings containing all the intended information about the specified parameter
*/
public LinkedList<String> showParameter(String index) {

// first of all check that the provided index well belongs to the available indices

	if (!this.parameters.keySet().contains(index))
		this.throwMissingElementException("parameter", index);

// add the value of all parameter fields to the result list

	HashMap<String, String> parameter = this.parameters.get(index);
	this.result.clear();
	for (String field:parameter.keySet()) this.result.add(field+" "+parameter.get(field));
	return this.result;
}

/**
* set the value of an option
* @param option the name of the option to update
* @param value the new value of the option
*/
public void setOption (String option, String value) {

// check that the option well exists

	if (!this.options.keySet().contains(option))
		this.throwMissingElementException("option", option);

// perform different checks on the value depending on the type of the option

	switch (this.types.get(option)) {
		case "choice":
		if (this.choices.get(option).keySet().contains(value))
			this.options.put(option, value);
		else
			this.throwMissingElementException("choice", value);
		break;

		case "boolean":
		if (value.equals("0") || value.equals("1")) this.options.put(option, value);
		else this.throwTypeException("boolean");
		break;

		case "parameterset":
		String[] parameters = value.split(" ");
		for (String parameter:parameters) {
			try {
				Double.parseDouble(parameter);
			} catch (NumberFormatException e) {
			System.err.println(parameter);
			System.exit(0);
				this.throwTypeException("double");
			}
		}

		this.options.put(option, value);
		break;

		default:
		break;
	}
}

/**
* reset the value of a particular option
* @param option the name of the option to reset
*/
public void resetOption (String option) {

// check that the option well exists

	if (this.options.keySet().contains(option))
		this.options.put(option, this.default_values.get(option));
	else
		this.throwMissingElementException("option", option);
}

/**
* reset the value of all options
*/
public void resetOptions () {
	for (String option:this.options.keySet()) this.resetOption(option);
}
}
