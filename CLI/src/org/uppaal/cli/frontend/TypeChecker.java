package org.uppaal.cli.frontend;

/**
* type checker of the uppaal command line interface
* providing methods and data structures to check the type attached to a name while parsing
*/

import org.uppaal.cli.exceptions.TypeException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class TypeChecker {
// hash map of type properties
private HashMap<String, HashSet<String>> type_properties;

// type exception of this type checker
private TypeException type_exception;

public TypeChecker () {
// setup the attributes of this type checker

	this.type_properties = new HashMap<String, HashSet<String>>();
	this.type_exception = new TypeException();

// setup the set of document properties

	HashSet<String> properties = new HashSet<String>();
	Collections.addAll(properties, "system", "declaration");
	this.type_properties.put("document", properties);

// setup the set of template properties

	properties = new HashSet<String>();
	Collections.addAll(properties, "parameter", "declaration", "init", "committed", "name");
	this.type_properties.put("template", properties);

// setup the set of location properties

	properties = new HashSet<String>();
	Collections.addAll(properties, "name", "invariant");
	this.type_properties.put("location", properties);

// setup the set of transition properties

	properties = new HashSet<String>();
	Collections.addAll(properties, "select", "guard", "sync", "assign");
	this.type_properties.put("edge", properties);

// finally setup the set of query properties

	properties = new HashSet<String>();
	Collections.addAll(properties, "name", "formula", "comment");
	this.type_properties.put("query", properties);
}

/**
* check that a given string is property of the given type
* @param type the type to check
* @param property the property to check
* @return true if and only if the provided property is a property of the given type
*/
public boolean isTypeProperty (String type, String property) {
	if (this.type_properties.keySet().contains(type)) 
		return this.type_properties.get(type).contains(property);
	else
		return false;
}

/**
* check that a certain property belongs to a certain type
* @param type the type to check
* @param property the property to check
* @exception a type exception is the given property does not belong to the given type
*/
public void checkTypeProperty (String type, String property) {
	if (this.isTypeProperty(type, property)) return;
	this.type_exception.setType(type);
	this.type_exception.setProperty(property);
	throw this.type_exception;
}

/**
* test if a type is well an element type
* @param type the type to test
* @return true if and only if the type is well an element type
*/
public boolean isElementType(String type) {
	return this.type_properties.keySet().contains(type);
}

/**
* check that a reference is well an element
* @param type the type of the reference to check
* @exception a type exception if the provided type is not an element type
*/
public void checkElement (String type) {
	if (this.type_properties.keySet().contains(type)) return;
	this.type_exception.setType(type);
	this.type_exception.setProperty(null);
	throw this.type_exception;
}

/**
* check that a reference is well an property
* @param type the type of the reference to check
* @exception a type exception if the provided type is not a property type
*/
public void checkProperty (String type) {
	if (!this.type_properties.keySet().contains(type)) return;
	this.type_exception.setType(type);
	this.type_exception.setProperty(null);
	throw this.type_exception;
}

/**
* check that a reference is well an element
* @param type the type of the reference to check
* @exception a type exception if the provided type is not an element type
*/
public void checkType (String type, String ... types) {
	for (String reftype: types) {
		if (type.equals(reftype)) return;
	}

	this.type_exception.setType(type);
	this.type_exception.setProperty(null);
	throw this.type_exception;
}
}