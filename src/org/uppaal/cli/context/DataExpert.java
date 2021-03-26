package org.uppaal.cli.context;

/**
* data expert, provide all methods to extract and process the data of a query result
* especially those to compute and plot the associated array of variations
*/

import com.uppaal.model.core2.DataSet2D;
import com.uppaal.model.core2.Data2D;
import com.uppaal.model.core2.QueryData;
import com.uppaal.model.core2.Query;
import java.awt.geom.Point2D;
import java.awt.Color;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;

import java.io.IOException;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DataExpert extends AbstractExpert implements Iterable<double[]> {

// query data of this data expert
private QueryData query_data;

// list of trajectories of this query data
private HashMap<String, ArrayList<Data2D>> trajectories;

// data iterator of this data expert
private DataIterator iterator;

// precision of this data expert
private double precision;

// boolean telling if the precision of this data expert is floating or not
private boolean floating;

// index of the current query data
private int index;

public DataExpert (Context context) {
	super(context);
	this.trajectories = new HashMap<String, ArrayList<Data2D>>();
	this.iterator = new DataIterator();
	this.precision = 0;
	this.floating = false;
	this.index = -1;
}

/**
* get the precision of this data expert
* @return the precision of this data expert
*/
public double getPrecision () {
	return this.precision;
}

/**
* get the value of the boolean telling if the precision is floating or not
* @return true if and only if the precision is floating
*/
public boolean getFloating() {
	return this.floating;
}

/**
* set the value of the boolean telling if the precision is floating
* @param floating the new value for the boolean
*/
public void setFloating (boolean floating) {
	this.floating = floating;
}

/**
* get the index of the current query data
* @return the index of the current query data
*/
public int getIndex() {
	return this.index;
}

/**
* clear this data expert
*/
public void clear() {
	this.query_data = null;
	this.trajectories.clear();
	this.precision = 0.0;
	this.floating = true;
	this.index = -1;
}

/**
* import the data from a query specified by its index
* @param index the index of the query data source
* @return true if and only if some data was imported
*/
public boolean importData(int index) {

// get the query at the specified index if necessary

	if (index==this.index) return true;
	Query query = this.context.getDocument().getQueryList().get(index);
		if (query==null)
		this.throwMissingElementException("query", ""+index);

	QueryData query_data = query.getResult().getData();
	return this.importData(query_data);
}

/**
* import the data from a query
* @param query_data the query data to import
* @return true if and only if some data was imported
*/
public boolean importData(QueryData query_data) {

// import the data from the query

	this.clear();
	this.query_data = query_data;

	for (String title: this.query_data.getDataTitles()) {
		DataSet2D plot = this.query_data.getData(title);
	ArrayList<Data2D> trajectories = new ArrayList<Data2D>();
		this.trajectories.put(title, trajectories);
		for (Data2D trajectory: plot) trajectories.add(trajectory);
	}

	this.index = index;
	return this.trajectories.size()>0;
}

/**
* set the current data of this data expert
* @param plot_index the index of the plot to display
* @param trajectory_index the index of the trajectory to plot
* @return a list of strings containing the title of the graphic to display in the command line
*/
public LinkedList<String> selectData (int plot_index, int trajectory_index) {

// get the trajectory from the list and set the data iterator accordingly

	String title = this.query_data.getDataTitles().get(plot_index);
	DataSet2D plot = this.query_data.getData(title);
	Data2D trajectory = this.trajectories.get(title).get(trajectory_index);

	if (this.floating) {
		double max = trajectory.getMaximum().getY();
		double min = trajectory.getMinimum().getY();
	} this.iterator.setData(trajectory);

// compute the list of strings to return

	this.result.clear();
	this.result.addLast ("Plot \""+plot.getTitle()+"\" showing \"" + plot.getYLabel() + "\"\n");
	this.result.addLast ("over \"" + plot.getXLabel()+"\"\n");
	this.result.addLast ("Trajectory " + trajectory.getTitle()+":\n");
	return this.result;
}

@Override
public Iterator<double[]> iterator () {
	this.iterator.reset();
	return this.iterator;
}

/**
* export the curent query data to a xml file
* @param filename the name of the file to export the data
* @throws TransformerException an exception if there was some problem with the transformer
* @throws ParserConfigurationException an exception if there was some problem with the parser configuration
*/

public void saveData (String filename) throws TransformerException, ParserConfigurationException {

// create the xml document object and its root

	DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
	Document document = documentBuilder.newDocument();
	Element root = document.createElement("querydata");
	document.appendChild(root);

// for each plot of the query data create a dataset node

	for (String title: this.query_data.getDataTitles()) {
		DataSet2D plot = this.query_data.getData(title);
		Element dataset = document.createElement("dataset");
		root.appendChild(dataset);

// add its title to each dataset

		Element element = document.createElement("title");
		element.appendChild(document.createTextNode(title));
		dataset.appendChild(element);

// same thing for the x label

		element = document.createElement("xlabel");
		element.appendChild(document.createTextNode(plot.getXLabel()));
		dataset.appendChild(element);

// same thing for the y label

		element = document.createElement("ylabel");
		element.appendChild(document.createTextNode(plot.getYLabel()));
		dataset.appendChild(element);

// finally add its trajectories to the dataset

		Element datas = document.createElement("datas");
		dataset.appendChild(datas);

		for (Data2D trajectory: plot) {
			Element data = document.createElement("data");
		datas.appendChild(data);

// add its title to the trajectory

			element = document.createElement("title");
			element.appendChild(document.createTextNode(trajectory.getTitle()));
			data.appendChild(element);

// same thing for its type


			element = document.createElement("type");
			element.appendChild(document.createTextNode(trajectory.getType()));
			data.appendChild(element);

// same thing for its color

			element = document.createElement("color");
			data.appendChild(element);


// add its red value to the color

				Attr r = document.createAttribute("r");
				r.setValue(""+trajectory.getColor().getRed());
				element.setAttributeNode(r);

// same thing for the green value

				Attr g = document.createAttribute("g");
				g.setValue(""+trajectory.getColor().getGreen());
				element.setAttributeNode(g);

// same thing for the blue value

				Attr b = document.createAttribute("b");
				b.setValue(""+trajectory.getColor().getBlue());
				element.setAttributeNode(b);

// add its points to the trajectory

			Element points = document.createElement("points");
			data.appendChild(points);

			for (Point2D.Double p: trajectory) {
			Element point = document.createElement("point");
			points.appendChild(point);

// add its x coordinate to each point

				Attr x = document.createAttribute("x");
				x.setValue(""+p.getX());
				point.setAttributeNode(x);

// same thing for the y label

				Attr y = document.createAttribute("y");
				y.setValue(""+p.getY());
				point.setAttributeNode(y);
			}
		}
	}

//transform the DOM Object to an XML File

	TransformerFactory transformerFactory = TransformerFactory.newInstance();
	Transformer transformer = transformerFactory.newTransformer();
	DOMSource domSource = new DOMSource(document);
	StreamResult streamResult = new StreamResult(new File(filename));
	transformer.transform(domSource, streamResult);
}

/**
* load the data from a data file
* @param filename the path to the file where the data is stored
* @throws ParserConfigurationException an exception if there was some error with the parser configuration
* @throws  SAXException an exception if there was some error with the sax library
* @throws IOException an exception if there was some error while reading the input file
*/
public void loadData(String filename) 
throws ParserConfigurationException, SAXException, IOException {

// load the data from the provided file

	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder = factory.newDocumentBuilder();
	Document document = builder.parse(new File(filename));
	Element root = document.getDocumentElement();
	NodeList plots = root.getChildNodes();
	QueryData query_data = new QueryData();

// add each dataset of the loaded file to the newly created query data
	for (int i = 0;i<plots.getLength();i++) {
		Element dataset = (Element)plots.item(i);
		String title = dataset.getChildNodes().item(0).getFirstChild().getTextContent();
		String xlabel = dataset.getChildNodes().item(1).getFirstChild().getTextContent();
		String ylabel = dataset.getChildNodes().item(2).getFirstChild().getTextContent();

		DataSet2D plot = new DataSet2D(title, xlabel, ylabel);
		query_data.addData(plot);
		NodeList datas = dataset.getChildNodes().item(3).getChildNodes();

// add its datas to the plot

		for (int j=0;j<datas.getLength();j++) {
			Element data =(Element) datas.item(j);
			String trajectory_title = data.getChildNodes().item(0).getFirstChild().getTextContent();
			String type = data.getChildNodes().item(1).getFirstChild().getTextContent();

			Element color = (Element) data.getChildNodes().item(2);
			int r = Integer.parseInt(color.getAttribute("r"));
			int g = Integer.parseInt(color.getAttribute("g"));
			int b = Integer.parseInt(color.getAttribute("b"));

			Data2D trajectory = new Data2D(trajectory_title, type, new Color(r, g, b));
			plot.addData2D(trajectory);
			NodeList points = data.getChildNodes().item(3).getChildNodes();

// add all points to the trajectory

			for (int k=0;k<points.getLength();k++) {
				Element point =(Element) points.item(k);
				double x = Double.parseDouble(point.getAttribute("x"));
				double y = Double.parseDouble(point.getAttribute("y"));
				Point2D.Double p = new Point2D.Double(x, y);
				trajectory.addSample(p);
			}
		}
	}

// finally import the content of the newly created query data into the data expert

	this.importData(query_data);
}

/**
* return the number of plots currently handled by this data expert
* @return the number of plots currently handled by this data expert()
*/
public int getPlotNumber() {
	return this.trajectories.size();
}

/**
* get the number of trajectories of a given plot
* @param index the index of the plot whose we would like to know the number of trajectories
* @return the number of trajectories of the requested plot
*/
public int getTrajectoryNumber(int index) {
	if (index<0 || index>=this.trajectories.size())
		return -1;
	else {
		String title = this.query_data.getDataTitles().get(index);
		return this.trajectories.get(title).size();
	}
}

/**
* check that this data expert currently has some data to plot
* @return true if and only if the data expert has some data to plot
*/
public boolean hasData() {
	return this.trajectories.size()>0;
}
}
