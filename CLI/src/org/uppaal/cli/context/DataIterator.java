package org.uppaal.cli.context;

/**
* data iterator, provides all methods to iterate over a query data
* and to compute its variations and convexity.
*/

import com.uppaal.model.core2.Data2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.Iterator;

public class DataIterator implements Iterator<double[]> {

// linked list of rows of this data iterator
private LinkedList<double[]> rows;

// row iterator of this data iterator
private Iterator<double[]> row_iterator;

/**
* public constructor of a data iterator
*/
public DataIterator () {
	this.rows = new LinkedList<double[]>();
}

/**
* reset the row iterator of this data iterator
*/
public void reset() {
	this.row_iterator = this.rows.iterator();
}

/**
* add a new row to the list of rows
* @param x the x value of the new row
* @param y the y value of the new row
* @param variation the variation of the new row
* @param curvature the curvature of the new row
*/
public void addNewRow(double x, double y, double variation, double curvature) {
	double[] row = {x, y, variation, curvature};
	this.rows.addLast(row);
}

/**
* set the data of this data iterator
* @param trajectory the data to be set
*/
public void setData (Data2D trajectory) {

// declare the variables for the algorithm

	Iterator<Point2D.Double> iterator = trajectory.iterator();
	this.rows.clear();
	Point2D.Double p1, p2, extremum;
	double var1_x, var1_y, var2_x, var2_y, variation, curvature;

// get the two first points of the list

	extremum = p1 = iterator.next();
	p2 = iterator.next();

	var1_x = var1_y = curvature = Double.NaN;
	var2_y = (p2.getY() - p1.getY());
	var2_x = p2.getX()-p1.getX();
	variation = var2_y;

// loop over all remaining points and compute the tables of the monotony table

	while (iterator.hasNext()) {
		p1 = p2;
		p2 = iterator.next();

		var1_y = var2_y;
		var2_y = p2.getY() - p1.getY();

		var1_x = var2_x;
		var2_x = p2.getX() - p1.getX();

// if the variation does not change of direction check the curvature

		if (Math.signum(var2_y)==Math.signum(variation)) {

// if the curvature does not change add the variation to the accumulator

			if (Double.isNaN(curvature) || Math.signum(var2_y/var2_x-var1_y/var1_x)==curvature) {
				curvature = Math.signum(var2_y/var2_x-var1_y/var1_x);
				variation += var2_y;
			} 

// otherwise if the curvature changes create a new row in the table

			else {
				this.addNewRow(extremum.getX(), extremum.getY(), variation, curvature);
			extremum = p1;
			variation = var2_y;
			curvature = Double.NaN;
			}
		} 

// if the direction of the variation changes create a new row in the table

		else {
			this.addNewRow(extremum.getX(), extremum.getY(), variation, curvature);
			extremum = p1;
			variation = var2_y;
			curvature = Double.NaN;
		}
	}

// finally add the last row(s) of the table

	this.addNewRow(extremum.getX(), extremum.getY(), variation, curvature);
	this.addNewRow(p2.getX(), p2.getY(), Double.NaN, Double.NaN);
}

@Override
public double[] next() {
	return this.row_iterator.next();
}

@Override
public boolean hasNext() {
	return this.row_iterator.hasNext();
}
}
