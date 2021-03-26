package org.uppaal.cli.frontend;

import org.uppaal.cli.exceptions.TypeException;
import java.util.Collections;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;

/**
* type checker of the uppaal command line interface
* providing methods and data structures to check the type attached to a name while parsing
*/

public class TypeChecker implements Iterable<String> {
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
	Collections.addAll(properties, "system", "declaration", "document");
	this.type_properties.put("document", properties);

// setup the set of template properties

	properties = new HashSet<String>();
	Collections.addAll(properties, "parameter", "declaration", "init", "committed");
	this.type_properties.put("template", properties);

// setup the set of location properties

	properties = new HashSet<String>();
	Collections.addAll(properties, "invariant");
	this.type_properties.put("location", properties);

// setup the set of transition properties

	properties = new HashSet<String>();
	Collections.addAll(properties, "select", "guard", "sync", "synchronisation", "assign", "assignment", "controllable");
	this.type_properties.put("edge", properties);

// finally setup the set of query properties

	properties = new HashSet<String>();
	Collections.addAll(properties, "formula", "comment", "result", "data");
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
* check that two types are equals
* @param type1 the first type to check
* @param type2 the second type to check
*/
public void checkTypeEquals (String type1, String type2) {
	this.type_exception.clear();
	if (type1.equals(type2)) return;
	this.type_exception.setRefType(type1);
	this.type_exception.setType(type2);
	this.type_exception.setStackTrace(Thread.currentThread().getStackTrace());
	throw this.type_exception;
}

/**
* check that a certain property belongs to a certain type
* @param type the type to check
* @param property the property to check
*/
public void checkTypeProperty (String type, String property) {
	this.type_exception.clear();
	if (this.isTypeProperty(type, property)) return;
	this.type_exception.setType(type);
	this.type_exception.setProperty(property);
	this.type_exception.setStackTrace(Thread.currentThread().getStackTrace());
	throw this.type_exception;
}

/**
* test if a type is well an element type
* @param type the type to test
* @return true if and only if the type is well an element type
*/
public boolean isElementType(String type) {
	this.type_exception.clear();
	return this.type_properties.keySet().contains(type);
}

/**
* check that a reference is well an element
* @param type the type of the reference to check
*/
public void checkElement (String type) {
	this.type_exception.clear();
	if (this.type_properties.keySet().contains(type)) return;
	this.type_exception.setType(type);
	this.type_exception.setProperty(null);
	this.type_exception.setStackTrace(Thread.currentThread().getStackTrace());
	throw this.type_exception;
}

/**
* check that a reference is well an property
* @param type the type of the reference to check
*/
public void checkProperty (String type) {
this.type_exception.clear();
	if (!this.type_properties.keySet().contains(type)) return;
	this.type_exception.setType(type);
	this.type_exception.setProperty(null);
	this.type_exception.setStackTrace(Thread.currentThread().getStackTrace());
	throw this.type_exception;
}

/**
* check that a reference is well an element
* @param type the type of the reference to check
* @param types the accepted types
*/
public void checkType (String type, String ... types) {
	this.type_exception.clear();
	for (String reftype: types) {
		if (type.equals(reftype)) return;
	}

	this.type_exception.setType(type);
	this.type_exception.setProperty(null);
	this.type_exception.setStackTrace(Thread.currentThread().getStackTrace());
	throw this.type_exception;
}

@Override
public Iterator<String> iterator () {
	return this.type_properties.keySet().iterator();
}

/**
* get the properties associated with a type
* @param type the type to lookup
* @return a collection of properties associated with the provided type
*/
public Collection<String> getProperties(String type) {
	return this.type_properties.get(type);
}
}
