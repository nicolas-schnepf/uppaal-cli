package org.uppaal.cli.context;

/**
* query handler, responsible for all operations on queries
*/


import org.uppaal.cli.exceptions.WrongFormatException;
import com.uppaal.model.core2.QueryList;
import com.uppaal.model.core2.Query;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.Node;
import com.uppaal.model.core2.InsertQueryCommand;
import com.uppaal.model.core2.RemoveQueryCommand;
import com.uppaal.model.core2.SetPropertyCommand;
import com.uppaal.engine.QueryResult;

import java.util.HashSet;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class QueryExpert extends AbstractExpert {

// hash set of selected queries of this expert
private QueryList selected_queries;

public QueryExpert (Context context) {
	super(context);
	this.selected_queries = new QueryList();
}

/**
* load a list of queries from the provided filename
* @param filename the path to the file from which queries should be loaded
* @throws IOException an io exception if there is a problem with the provided filename
*/
public void loadQueries (String filename) throws IOException {

// first open the provided file

	BufferedReader reader = new BufferedReader(new FileReader(filename));
	QueryList queries = this.context.getDocument().getQueryList();
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
					queries . addLast(new Query(query.toString()+"\n", comment.toString()));
					queries.get(queries.size()-1).setProperty("name", "q"+queries.size());
					query.delete(0, query.length());
					comment.delete(0, comment.length());
			}

		}
	}
}

/**
* save the queries attached to this context
* @param filename the path to the file to save the current query list
* @throws IOException an IO exception if there is a problem with the provided filename
*/
public void saveQueries (String filename) throws IOException {
	BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
	QueryList queries = this.context.getDocument().getQueryList();

// write each query in the provided file

	for (Query query: queries) {
		writer.write("/*\n"+query.getComment()+"\n*/\n");
		writer.write(query.getFormula()+"\n\n");
	}

writer.close();
}

/**
* drop all queries of this context
*/

public void clearQueries () {
	this.context.getDocument().getQueryList().removeAll();
}

/**
* get the value of a specific query property
* @param index the index of the query to inspect
* @param property the name of the property to inspect
* @return the value of the specific property for the query at the specified index
*/
public String getQueryProperty (int index, String property) {

// return the value of the provided property

	Query query = this.getQuery(index);
	return (String) query.getPropertyValue(property);
}

/**
* set the value of a specific query property
* @param index the index of the query to update
* @param property the name of the property to update
* @param value the new value for the query property
*/
public void setQueryProperty (int index, String property, String value) {

// set the value of the specified property for the query at the specified index

	Query query = this.getQuery(index);
	SetPropertyCommand command = new SetPropertyCommand(query, property, value);
	command.execute();
	this.context.addCommand(command);
}

/**
* get a query specified by its index
* @param index the index of the query to return
* @return the intended query if it exists
*/
public Query getQuery(int index) {

// get the query at the specified index

	if (index>=this.getQueryNumber())
		this.throwMissingElementException("query", ""+index);

	Query query = this.context.getDocument().getQueryList().get(index);
		if (query==null)
		this.throwMissingElementException("query", ""+index);
	return query;
}
/**
* get a query described by one of its properties and a corresponding value
* @param property the name of the property to search for
* @param the value of the property to search for
* @return the corresponding query if found, null otherwise
*/
private Query getQuery (String property, String value) {
	for (Query query:this.context.getDocument().getQueryList()) {
		if (query.getPropertyValue(property)==value) return query;
	}
	return null;
}

/**
* show the name of all queries of this document
* @return the list of names of all queries in this document
*/

public LinkedList<String> showQueries() {
	QueryList queries = this.context.getDocument().getQueryList();
	LinkedList<String> result = this.result;
	result.clear();
	int index = 0;

	for (Query query : queries) {
		result.addLast(""+index+": "+(String)query.getPropertyValue("formula"));
		index++;
	}

	return result;
}

/**
* show the information about a specific query described by its name
* @param index the index of the query to show
* @return the description of the corresponding query
*/
public String showQuery (int index) {

	Query query = this.getQuery(index);
	StringBuffer description = new StringBuffer();
	description.append(query.getFormula()+"\n");
	description.append(query.getComment());
	return description.toString();
}

/**
* show the result of a query specified by its index
* @param index the index whose we need to know the result
* @return a string describing the result of the query, possibly null
*/
public String showQueryResult (int index) {

// returns the result of the query

	Query query = this.getQuery(index);
	QueryResult result = query.getResult();
	return result.toString();
}

/**
* add a new query to this context
* @param name the name of the query to add
* @param formula the formula of the query to add
* @param comment the comment of the query to add
*/

public void addQuery (String name, String formula, String comment) {

// check that the query does not already exists

	if (name!=null) {
		if (this.getQuery("name", name)!=null)
			this.throwExistingElementException("query", name);
	}

// otherwise insert the query at the end of the list

	QueryList list = this.context.getDocument().getQueryList();
	InsertQueryCommand command = new InsertQueryCommand(list, list.size());
	command.execute();
	this.context.addCommand(command);

// finally set the properties of the query

	Query query = list.get(list.size()-1);
	if (name!=null) query.setProperty("name", name);
	else query.setProperty("name", "q"+(list.size()+1));
	query.setFormula(formula);
	query.setComment(comment);
}

/**
* remove a query from this context
* @param index the index of the query to remove
*/

public void removeQuery (int index) {

// otherwise insert the query at the end of the list

	Query query = this.getQuery(index);
	QueryList list = this.context.getDocument().getQueryList();
	RemoveQueryCommand command = new RemoveQueryCommand(list, index);
	command.execute();
	this.context.addCommand(command);
}

/**
* @return the number of queries of this context
*/
public int getQueryNumber () {
	return this.context.getDocument().getQueryList().size();
}

/**
* show the name of all queries of this document
* @return the list of names of all queries in this document
*/

public LinkedList<String> showSelectedQueries() {
	LinkedList<String> result = this.result;
	result.clear();
	int index = 0;

	for (Query query : this.selected_queries) {
		result.addLast(""+index+": "+(String)query.getPropertyValue("formula"));
		index++;
	}

	return result;
}

/**
* show the information about a selected query
* @param index the index of the selected query to show
* @return the description of the corresponding selected query
*/
public String showSelectedQuery (int index) {

// check that the query already exists

	Query query = this.selected_queries.get(index);
		if (query==null)
		this.throwMissingElementException("query", ""+index);

// otherwise return the name, the formula and the comment of the query

	StringBuffer description = new StringBuffer();
	description.append(query.getFormula()+"\n");
	description.append(query.getComment());
	return description.toString();
}

/**
* clear the set of selected queries
*/
public void clearSelectedQueries() {
	this.selected_queries.removeAll();
}

/**
* select a query based on its index
* @param index the index of the query to select
*/
public void selectQuery(int index) {
	Query query = this.context.getDocument().getQueryList().get(index);
	if (query!=null)
		this.selected_queries.addLast(new Query(query.getFormula(), query.getComment()));
	else 
			this.throwMissingElementException("query", ""+index);
}

/**
* unselect a query based on its index
* @param index the index of the query to unselect
*/
public void unselectQuery(int index) {
	if (index<0 || index>=this.selected_queries.size()) this.selected_queries.remove(index);
}

/**
* @return the list of selected queries
*/
public QueryList getSelectedQueries() {
	return this.selected_queries;
}
}
