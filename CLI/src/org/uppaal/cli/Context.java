package org.uppaal.cli;

/***
* container class for the uppaal context
* namely an uppaal document, a list of queries and an uppaal engine
*/

import com.uppaal.model.core2.Document;
import com.uppaal.engine.Engine;
import com.uppaal.engine.EngineException;
import com.uppaal.engine.EngineStub;
import com.uppaal.model.core2.Query;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Context {
private LinkedList<Query> queries;
private Document document;
private Engine engine;

/**
* create an empty console without any argument
*/

public Context () {
	this.queries = new LinkedList<Query>();
	this.document = new Document();
	this.engine = null;
}

/***
* @ create a new empty document
*/

public void newDocument () {
	this.document = new Document();
}

/***
* @ return the uppaal document contained inside of this context
*/

public Document getDocument () {
	return this.document;
}

/***
* set a new document to this context
* @param document the new document for this context
*/
public void setDocument (Document document) {
	this.document = document;
}

/***
* @return an iterator on the list of queries contained in this context
*/

public Iterator<Query> getQueryIterator () {
	return this.queries.iterator();
}

/***
* drop all queries of this context
*/

public void dropQueries () {
	this.queries.clear();
}

/***
* add a new query to this context
* @param query the query to add
*/

public void addQuery (Query query) {
	this.queries.add(query);
}

/***
* @return the engine attached to this context
*/

public Engine getEngine () {
	return this.engine;
}

/***
* connect to an uppaal engine based on the path declared in the UPPAALPATH environment variable
*/

public void connectEngine() throws EngineException, IOException {

// compute the path to the engine binary based on the UPPAALPATH environment variable

	String os = System.getProperty("os.name");
		String here = System.getenv("UPPAALPATH");
	String path = null;

// compute the end of the path based on the running operating system

	if ("Linux".equals(os)) {
		path = here+"/bin-Linux/server";
	} else if ("Mac OS X".equals(os)) {
		path = here + "bin-Darwin/server";
	} else {
		path = here+"\\bin-Windows\\server.exe";
	}

// create and configure the new engine

	this.engine = new Engine();
	this.engine.setServerPath(path);
	this.engine.setServerHost("localhost");
	this.engine.setConnectionMode(EngineStub.BOTH);
	this.engine.connect();
}

/***
* disconnect the engine of the uppaal console
*/

public void disconnectEngine () {
	if (this.engine == null) return;
	this.engine.disconnect();
	this.engine = null;
}
}