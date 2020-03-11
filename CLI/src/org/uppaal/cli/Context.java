package org.uppaal.cli;

/***
* container class for the uppaal context
* namely an uppaal document, a list of queries and an uppaal engine
*/

import com.uppaal.model.core2.PrototypeDocument;
import com.uppaal.model.core2.QueryList;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Element;
import com.uppaal.engine.Engine;
import com.uppaal.engine.EngineException;
import com.uppaal.engine.EngineStub;
import com.uppaal.model.core2.Query;

import org.uppaal.cli.exceptions.WrongFormatException;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Context {
private Document document;
private Engine engine;

/**
* create an empty console without any argument
*/

public Context () {
	com.uppaal.model.io2.XMLReader.setXMLResolver(new com.uppaal.model.io2.UXMLResolver());
	this.document = new Document(new PrototypeDocument());
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

public QueryList getQueryList () {
	return this.document.getQueryList();
}

/**
* @return the number of queries of this context
*/
public int getQueryNumber () {
	return this.document.getQueryList().size();
}

/**
* drop all queries of this context
*/

public void clearQueries () {
	this.document.getQueryList().removeAll();
}

/***
* add a new query to this context
* @param query the query to add
*/

public void addQuery (Query query) {
	this.document.getQueryList().addLast(query);
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

/**
* load a document provided by its filename
* @param filename the path to the document to load
*/
public void loadDocument (String filename) throws IOException, MalformedURLException  {
		PrototypeDocument doc_loader = new PrototypeDocument();
		//URL location = new URL("file", null, args[0]);
		URL location = new URL("file://localhost"+System.getProperty("user.dir")+"/"+filename);
		this.document = doc_loader.load(location);
}

/**
* save the document attached to this context
* @param filename the path to the file to save the current document
* @exception an IO exception if there is a problem with the provided filename
*/
public void saveDocument (String filename) throws IOException {
	this.document.save(filename);
}

/**
* clear the current document
*/
public void clearDocument() {
	this.document = new Document(new PrototypeDocument());
}

/**
* load a list of queries from the provided filename
* @param filename the path to the file from which queries should be loaded
* @exception an io exception if there is a problem with the provided filename
*/
public void loadQueries (String filename) throws IOException {

// first open the provided file

	BufferedReader reader = new BufferedReader(new FileReader(filename));
	QueryList queries = this.document.getQueryList();
	StringBuffer query = new StringBuffer();
	StringBuffer comment = new StringBuffer();

	String line = reader.readLine();
	int line_number = 1;
	boolean parsing = true;
	boolean multiline_comment = false;

	int pos = 0;
	int current_index = 0;
	int opening_index = 0;
	int closing_index = 0;

	while (parsing) {

// if the end of the file is reached finish parsing

		if (line==null) {
// if we are in a multiline comment throw an exception

			if (multiline_comment) {
				reader.close();
				WrongFormatException exception = new WrongFormatException();
				exception.setFilename(filename);
				exception.setLineNumber(line_number);
				exception.setMessage("End of file reached while parsing comment.");
				throw exception;
			}

// otherwise simply finishes the parsing

			else {
				parsing = false;
				reader.close();
			}
		}

// in a multiline comment check that we only add the part which is before the closing sequence

		else if (multiline_comment) {

// check that no new comment is open in the line

			opening_index = line.indexOf("/*", current_index);
			closing_index = line.indexOf("*/", current_index);
			if (opening_index != -1) {
				reader.close();
				WrongFormatException exception = new WrongFormatException();
				exception.setFilename(filename);
				exception.setLineNumber(line_number);
				exception.setMessage("New comment open while parsing comment.");
				throw exception;
			}

// otherwise only add the text which is before the closing sequence

			else if (closing_index==-1) {
				if (current_index==0) comment.append(line+"\n");
				else comment.append(line.substring(current_index)+"\n");

				line = reader.readLine();
				current_index = 0;
				pos = 0;
				if (line!=null) line_number++;
			}
			else {
				comment.append(line.substring(current_index, closing_index)+"\n");
				current_index = closing_index + 2;
				pos = current_index;
				multiline_comment = false;
			}
		} 

// otherwise treat the line as regular text

		else {
// parse the white spaces at the beginning of the current block

			while (pos<line.length() && line.charAt(pos)==' ') pos++;
			if (pos>=line.length()) {
				line = reader.readLine();
				current_index = 0;
				pos = 0;
			}

// otherwise if we open a multiline comment pass in the corresponding mode

			else if (line.indexOf("/*", pos)==pos) {
				pos +=2;
				current_index = pos;
				multiline_comment = true;
			}

// if we find an inline comment treat it as a multiline one

			else if (line.indexOf("//", pos)==pos) {
				pos +=2;
				current_index = pos;
				comment.append(line.substring(pos)+"\n");
				line = reader.readLine();
				pos = 0;
				current_index = 0;
				if (line!=null) line_number ++;
			}

// otherwise treat the text as a query

			else {
				if (line.indexOf("/*", pos)!=-1) {
					opening_index = line.indexOf("/*", pos);
					query.append(line.substring(current_index, opening_index));
					current_index = opening_index;
					pos = current_index;
				} else if (line.indexOf("//", pos)!=-1) {
System.out.println(query.length());
					query.append(line.substring(pos, line.indexOf("//", pos)));
					line = reader.readLine();
					if (line != null) line_number ++;
					current_index = 0;
					pos = 0;
				} else {
					if (pos<line.length()) query.append(line.substring(pos));
					line = reader.readLine();
					current_index = 0;
					pos = 0;
				}

				if (query.length()>0) 
					queries . addLast(new Query(query.toString(), comment.toString()));
					query.delete(0, query.length());
					comment.delete(0, comment.length());
			}

		}
	}
}

/**
* save the queries attached to this context
* @param filename the path to the file to save the current query list
* @exception an IO exception if there is a problem with the provided filename
*/
public void saveQueries (String filename) throws IOException {
	BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
	QueryList queries = this.document.getQueryList();

// write each query in the provided file

	for (Query query: queries) {
		writer.write("/*\n"+query.getComment()+"\n*/\n");
		writer.write(query.getFormula()+"\n\n");
	}

writer.close();
}
}