package org.uppaal.cli.frontend;

import org.jline.reader.LineReaderBuilder;
import org.jline.reader.LineReader;
import org.uppaal.cli.context.QueryExpert;
import org.uppaal.cli.context.Context;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;

/**
* query selector, used to select the queries from the current query list
*/

public class QuerySelector extends AbstractSelector {

// hash set of selected templates
private HashSet<Integer> queries;


public QuerySelector (LineReader reader, Context context) {
	super(reader, context, 1);
	this.queries = new HashSet<Integer>();
}

/**
* @return the set of selected queries
*/
public HashSet<Integer> getSelectedQueries() {
	return this.queries;
}

/**
* clear the set of selected queries
*/
public void clearSelectedQueries() {
	this.queries.clear();
}

@Override
public void setElementNumber() {
	this.element_number = this.context.getQueryExpert().getQueryNumber();
	this.current_element = 0;
}

@Override
public boolean isSelected() {
	return this.queries.contains(this.current_element);
}

@Override
public void selectCurrentElement() {
	this.queries.add(this.current_element);
}

@Override
public void unselectCurrentElement() {
	this.queries.remove(this.current_element);
}

@Override
public void cancelSelection() {
	this.queries.clear();
}

@Override
public void validateSelection() {
	QueryExpert query_expert = this.context.getQueryExpert();
	for (int index:this.queries) query_expert.selectQuery(index);
	this.queries.clear();
}

@Override
public String showCurrentElement() {
	return this.context.getQueryExpert().getQueryProperty(this.current_element, "formula") +
	this.context.getQueryExpert().getQueryProperty(this.current_element, "comment");
}
}