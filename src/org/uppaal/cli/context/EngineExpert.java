package org.uppaal.cli.context;

/**
* engine expert handling the connection and the disconnection to the uppaal binary
*/

import com.uppaal.model.core2.QueryList;
import com.uppaal.model.core2.Query;
import com.uppaal.engine.Engine;
import com.uppaal.engine.EngineException;
import com.uppaal.model.system.concrete.ConcreteTransitionRecord;
import com.uppaal.model.system.symbolic.SymbolicTransition;
import com.uppaal.engine.EngineStub;
import com.uppaal.model.system.UppaalSystem;
import com.uppaal.engine.QueryFeedback;
import com.uppaal.engine.QueryResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;

public class EngineExpert extends AbstractExpert {
// the engine of this expert
private Engine engine;

// query feedback of this query expert
private QueryFeedback query_feedback;

// result hash map of this query expert
private HashMap<Query, QueryResult> query_results;

public EngineExpert (Context context) {
	super(context);
	this.engine = null;
	this.query_results = new HashMap<Query, QueryResult>();
	this.query_feedback  = new QueryFeedback() {
		@Override
		public void setProgressAvail(boolean availability)
		{
		}

		@Override
			public void setProgress(int load, long vm, long rss, long cached, long avail, long swap, long swapfree, long user, long sys, long timestamp)
		{
		}

		@Override
		public void setSystemInfo(long vmsize, long physsize, long swapsize)
		{
		}

		@Override
		public void setLength(int length)
		{
		}

		@Override
		public void setCurrent(int pos)
		{
		}


	    @Override
	    public void setTrace(char result, String feedback,
				 ArrayList<SymbolicTransition> trace, int cycle,
				 QueryResult queryVerificationResult)
	    {
	    }

	    public void setTraceSMC(char result, String feedback,
				    ArrayList<ConcreteTransitionRecord> trace, int cycle,
				    QueryResult queryVerificationResult)
	    {
	    }

		@Override
		public void setFeedback(String feedback)
		{
			if (feedback != null && feedback.length() > 0) {
				System.out.println(feedback);
			}
		}

		@Override
		public void appendText(String s)
		{
			if (s != null && s.length() > 0) {
				System.out.println(s);
			}
		}

		@Override
		public void setResultText(String s)
		{
			if (s != null && s.length() > 0) {
				System.out.println(s);
			}
		}
	};
}


/***
* @return the engine attached to this context
*/

public Engine getEngine () {
	return this.engine;
}

/***
* connect to an uppaal engine based on the path declared in the UPPAALPATH environment variable
@throws EngineException an engine exception if there were a problem with the engine
@throws IOException an io exception if some input / output error was encountered while connecting to the engine
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
	this.context.getOptionExpert().loadOptions();
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
* check the syntax of a query
* @param query the query to check
* @return true if and only if the syntax of the query is valid
*/
public boolean checkQuerySyntax(Query query) {
	String formula = query.getFormula();
	int length = formula.length();
	int index = 0;
	boolean ok = false;

	while (index<length && !ok) {
		char c = formula.charAt(index);
		ok = (c!='\n') && (c!=' ') && (c!='\t');
		index++;
	}

	return ok;
}

/**
* check a single query described by its index
* @param index the index of the query to check
* @return a string describing the result of the check
* @throws EngineException an engine exception if there were a problem with the engine
*/
public String checkQuery(int index) throws EngineException {

// first of all check that the query well exists

	QueryList query_list = this.context.getDocument().getQueryList();
	if (index<0 || index>=query_list.size())
		this.throwMissingElementException("query", ""+index);

// get all the intended information to check the query

	Query query = query_list.get(index);
	return this.checkQuery(query);
}

/**
* check a query provided as parameter
* @param query the query to check
* @return a string describing the status of the check
* @throws EngineException an engine exception if some problem was encountered with the engine
*/
public String checkQuery (Query query) throws EngineException {
	if (!this.checkQuerySyntax(query)) return "";
	UppaalSystem system = this.context.getModelExpert().getSystem();
	Engine engine = this.context.getEngineExpert().getEngine();
	String options = this.context.getOptionExpert().getOptionString();

// finally check the query, store and return its result

	QueryResult query_result = engine.query(system, options, query, query_feedback);
	query.setResult(query_result);
	return query_result.toString();
}

/**
* check all queries
* @return a string describing the result of the checks
* @throws EngineException an engine exception if there were a problem with the engine
*/
public LinkedList<String> checkQueries() throws EngineException {

// get all the intended information to check the queries

	QueryList query_list = this.context.getDocument().getQueryList();
	UppaalSystem system = this.context.getModelExpert().getSystem();
	Engine engine = this.context.getEngineExpert().getEngine();
	String options = this.context.getOptionExpert().getOptionString();
	this.result.clear();

// check all queries, store the corresponding results and return their text representation

	for (Query query: query_list) {
		if (!this.checkQuerySyntax(query)) continue;
		QueryResult query_result = engine.query(system, options, query, query_feedback);
		this.query_results.put(query, query_result);
		this.result.addLast(query.getFormula()+": "+query_result.toString());
	}

	return this.result;
}

/**
* check all selected queries
* @return a string describing the result of the checks
* @throws EngineException an engine exception if there were a problem with the engine
*/
public LinkedList<String> checkSelectedQueries() throws EngineException {

// get all the intended information to check the queries

	QueryList selected_queries = this.context.getQueryExpert().getSelectedQueries();
	UppaalSystem system = this.context.getModelExpert().getSystem();
	Engine engine = this.context.getEngineExpert().getEngine();
	String options = this.context.getOptionExpert().getOptionString();
	this.result.clear();

// check all queries, store the corresponding results and return their text representation

	for (Query query: selected_queries) {
		if (!this.checkQuerySyntax(query)) continue;
		QueryResult query_result = engine.query(system, options, query, query_feedback);
		this.query_results.put(query, query_result);
		this.result.addLast(query.getFormula()+": "+query_result.toString());
	}

	return this.result;
}
}
