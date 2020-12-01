package org.uppaal.cli.frontend;

import org.jline.reader.LineReaderBuilder;
import org.jline.reader.LineReader;
import org.uppaal.cli.context.DataExpert;
import org.uppaal.cli.context.Context;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;

/**
* data selector, used to select the data to plot from the data expert
*/

public class DataSelector extends AbstractSelector {

// precision of this data selector
private double precision;

// current width of the terminal in which this data selector will print its output
private int width;

// string format of this data selector
private String string_format;

public DataSelector (LineReader reader, Context context) {
	super(reader, context, 1);
	this.precision = 0.0;
	this.width = this.reader.getTerminal().getWidth();
	this.string_format = "%1$-"+(this.width/4)+"s";
}

/**
* set the precision of this data selector
* @param precision the new precision for this data selector
*/
public void setPrecision (double precision) {
	this.precision = precision;
}

@Override
public void setElementNumber() {
	this.element_number = this.context.getDataExpert().getPlotNumber();
	this.current_element = 0;
	this.current_view = 0;
}

@Override
public void showPreviousElement() {
	super.showPreviousElement();
	this.view_number = this.context.getDataExpert().getTrajectoryNumber(this.current_element);
	if (this.current_view>=this.view_number) this.current_view = 0;
}

@Override
public void showNextElement() {
	super.showNextElement();
	this.view_number = this.context.getDataExpert().getTrajectoryNumber(this.current_element);
	if (this.current_view>=this.view_number) this.current_view = 0;
}

@Override
public boolean isSelected() {
	return false;
}

@Override
public void selectCurrentElement() {
}

@Override
public void unselectCurrentElement() {
}

@Override
public void cancelSelection() {
}

@Override
public void validateSelection() {
}

@Override
public String showCurrentElement() {

// first of all compute the width of the terminal and the corresponding formats

	if (this.width!=this.reader.getTerminal().getWidth()) {
		this.width = this.reader.getTerminal().getWidth();
		this.string_format = "%1$-"+(this.width/4)+"s";
	}

// select the current plot in the data expert

	DataExpert data_expert = this.context.getDataExpert();
	LinkedList<String> headers = data_expert.selectData (this.current_element, this.current_view);
	StringBuffer output = new StringBuffer();
	for (String header: headers) output.append(header);
	output.append("\n");

// compute the header of the monotony table

	output.append(String.format(this.string_format, "ABSCISSA"));
	output.append(String.format(this.string_format, "ORDINATE"));
	output.append(String.format(this.string_format, "VARIATION"));
	output.append(String.format(this.string_format, "CURVATURE"));
	output.append("\n");

// generate the text for each line of the monotony table

	double[] accumulator = new double[4];
	accumulator[2] = 0.0;
	boolean accumulate = false;

	for (double[] row: data_expert) {
// if the variation in the current row is sufficient directly plot it
	System.out.println(""+row[0]+" "+row[1]+" "+row[2]+" "+row[3]);
		if (Double.isNaN(row[2]) || Math.abs(row[2])>this.precision) {

// first plot the accumulated rows if there are some

		if (accumulate) {
			this.buildRow(accumulator, output);
			accumulator[2] = 0.0;
			accumulate = false;
		}

		this.buildRow(row, output);
		}

// otherwise add the current row to the accumulator

		else {
			if (accumulate) {
				accumulator[2] += row[2];
				if (Math.abs(accumulator[2])>this.precision) {
					this.buildRow(accumulator, output);
					accumulator[2] = 0.0;
					accumulate = false;
				}
			}
			else {
				accumulator[0] = row[0];
				accumulator[1] = row[1];
				accumulator[2] = row[2];
				accumulate = true;
			}
		}
	}
	return output.toString();
}

/**
*build the next row of the output
* @param row the row to build
* @param output the string buffer in which append the new row
*/
private void buildRow(double[] row, StringBuffer output) {

// first of all add the coordinates of the first point of the interval

	output.append(String.format(this.string_format, this.formatNumber(row[0])));
	output.append(String.format(this.string_format, this.formatNumber(row[1])));

// then append the information on the variation of the function

	if (Double.isNaN(row[2])) output.append(String.format(this.string_format, "-"));
	else {
		switch ((int)Math.signum(row[2])) {

// if the function is increasing add the corresponding label depending on the available size

			case 1:
				if (this.width/4>10) output.append(String.format(this.string_format, "Increasing"));
				else output.append(String.format(this.string_format, "Inc."));
			break;

// same thing if it is stationary

			case 0:
				if (this.width/4>10) output.append(String.format(this.string_format, "Stationary"));
				else output.append(String.format(this.string_format, "Stat."));
			break;

// same thing if it is decreasing

			case -1:
				if (this.width/4>10) output.append(String.format(this.string_format, "Decreasing"));
				else output.append(String.format(this.string_format, "Dec."));
			break;
		}
	}

// finally append the information about the curvature of the function

	if (Double.isNaN(row[3])) output.append(String.format(this.string_format, "-"));
	else {
		switch ((int)Math.signum(row[3])) {

// if the function is convexe add the corresponding label depending on the available size

			case 1:
				if (this.width/4>10) output.append(String.format(this.string_format, "Convexe"));
				else output.append(String.format(this.string_format, "Conv."));
			break;

// same thing if it is straight

			case 0:
				if (this.width/4>8) output.append(String.format(this.string_format, "Straight"));
				else output.append(String.format(this.string_format, "Str."));
			break;

// same thing if it is concave

			case -1:
				if (this.width/4>7) output.append(String.format(this.string_format, "Concave"));
				else output.append(String.format(this.string_format, "Conc."));
			break;
		}
	}

	output.append("\n");
}

/**
* format a floating point number with double precision
* @param x the number to format
* @return a string representing the number and optimizing the use of the available space
*/
private String formatNumber(double x) {

// compute the length of the integer part of x

	int int_length;
	if (Math.floor(x)==0.0) int_length = 1;
	else int_length = 2+(int)Math.log10(x);

// if the integer part of the number is larger than a column return its scientific notation

	try {
	if (this.width/4 <= int_length)
		return String.format("%3."+(this.width/4-7)+"e", x);

// otherwise return the number in standard notation

	else {
		String output = String.format("%."+(this.width/4-(2+int_length))+"f", x);
		output = output.replaceAll("0*$", "");
		if (output.charAt(output.length()-1)=='.') output = output.substring(0, output.length()-1);
		return output;
	}
	} catch (Exception e) {
	System.out.println("%."+(this.width/4-(2+(int)Math.log10(x)))+"f");
	System.exit(1);
	}
	return null;
}
}