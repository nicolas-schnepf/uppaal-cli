package org.uppaal.cli.context;

import com.uppaal.engine.EngineException;
import com.uppaal.model.core2.Query;

/**
* Class implementing a strategy expert
* provide all methods to save and load a strategy
*/

public class StrategyExpert extends AbstractExpert {

// static query for saving a strategy
private static final Query save_query = new Query("saveStrategy(\"%s\", %s)", null);

// static query for loading a strategy
private static final Query load_query = new Query("strategy %s = loadStrategy(\"%s\")", null);

/**
* public constructor of a strategy expert
* @param context context of this strategy expert
*/
public StrategyExpert(Context context) {
	super(context);
}

/**
* save a strategy to a provided file name
* @param strategy the strategy to save
* @param filename the name of the file to save the strategy
* @return a string describing the status of the executed query
* @throws EngineException an exception if a problem was encountered with the engine
*/
public String saveStrategy(String strategy, String filename) throws EngineException {
	String formula = save_query.getFormula();
	save_query.setFormula(String.format(formula, filename, strategy));
	String result = this.context.getEngineExpert().checkQuery(save_query);
	save_query.setFormula(formula);
	return result;
}

/**
* load a strategy from a provided file
* @param strategy the name of the strategy to save
* @param filename the name of the file to load the strategy
* @return a string describing the status of the executed query
* @throws EngineException an exception if a problem was encountered with the engine
*/
public String loadStrategy(String strategy, String filename) throws EngineException {
	String formula = load_query.getFormula();
	load_query.setFormula(String.format(formula, strategy, filename));
	String result = this.context.getEngineExpert().checkQuery(load_query);
	load_query.setFormula(formula);
	return result;
}
}
