package org.uppaal.cli.frontend;

import org.jline.reader.LineReaderBuilder;
import org.jline.reader.LineReader;
import org.uppaal.cli.context.Context;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;

/**
* template selector, used to select the templates from a provided document
*/

public class TemplateSelector extends AbstractSelector {

// array list of selected templates
private ArrayList<Boolean> selected_templates;

// hash set of selected templates
private HashSet<String> templates;

// number of lines of each template
private ArrayList<Integer> lines;

public TemplateSelector (LineReader reader, Context context) {
	super(reader, context, 1);
	this.selected_templates = new ArrayList<Boolean>();
	this.templates = new HashSet<String>();
	this.lines =new ArrayList<Integer>();
}

/**
* @return the set of selected templates
*/
public HashSet<String> getSelectedTemplates() {
	return this.templates;
}

/**
* clear the set of selected templates
*/
public void clearSelectedTemplates() {
	this.templates.clear();
}

/**
* count the number of lines of the currently selected template
* @return the number of lines of the current template
*/

public int countLines() {
	String template = this.context.getTemplateExpert().showLoadedTemplate(this.current_element);
	int lines = 0;
	int pos = template.indexOf("\n");;
	while (pos!=-1) {
		pos = template.indexOf("\n", pos+1);
		lines ++;
	} this.lines.set(this.current_element, lines);

	return lines;
	}
@Override
public void setElementNumber() {
	this.element_number = this.context.getTemplateExpert().getTemplateNumber();
	this.current_element = 0;
	this.lines.clear();
	for (int i=0;i<this.selected_templates.size();i++) {
		this.selected_templates.set(i, false);
		this.lines.add(0);
	}
	for (int j=this.selected_templates.size();j<this.element_number;j++)  {
		this.selected_templates.add(j, false);
		this.lines.add(0);
	}

	int n = this.countLines();
	int height = this.reader.getTerminal().getHeight();
	if (n>height-5) this.view_number = n - (height - 5);
	else this.view_number = 1;
	this.current_view = 0;
}

@Override
public void showNextElement() {
	super.showNextElement();
	if (this.lines.get(this.current_element)==0) this.countLines();
	int height = this.reader.getTerminal().getHeight();
	if (this.lines.get(this.current_element)<=height-5)this.view_number = 1;
	else this.view_number = this.lines.get(this.current_element) - (height - 5);
}

@Override
public void showPreviousElement() {
	super.showPreviousElement();
	if (this.lines.get(this.current_element)==0) this.countLines();
	int height = this.reader.getTerminal().getHeight();
	if (this.lines.get(this.current_element)<=height-5)this.view_number = 1;
	else this.view_number = this.lines.get(this.current_element) - (height - 5);
}

@Override
public boolean isSelected() {
	return this.selected_templates.get(this.current_element);
}

@Override
public void selectCurrentElement() {
	this.context.getTemplateExpert().selectTemplate(this.current_element);
	this.selected_templates.set(this.current_element, true);
}

@Override
public void unselectCurrentElement() {
	this.context.getTemplateExpert().unselectTemplate(this.current_element);
	this.selected_templates.set(this.current_element, false);
}

@Override
public void cancelSelection() {
	this.context.getTemplateExpert().clearLoadedTemplates();
	this.templates.clear();
}

@Override
public void validateSelection() {
	this.templates.clear();
	LinkedList<String> templates = this.context.getTemplateExpert().showSelectedTemplates();
	this.templates.addAll(templates);
	
	this.context.getTemplateExpert().addSelectedTemplates();
	this.context.getTemplateExpert().clearLoadedTemplates();
}

@Override
public String showCurrentElement() {

// if there is enough place to display the current template simply return it

	String template = this.context.getTemplateExpert().showLoadedTemplate(this.current_element);
	int height = this.reader.getTerminal().getHeight();
	if (this.lines.get(this.current_element)<=height-5)return template;

// otherwise split the template in order to make it fit in the current window

	int starting_pos = 0;
	int pos = 0;
	int n = 0;

	while (n< this.current_view+height-5) {
		if (n==this.current_view) starting_pos = pos;
		pos = template.indexOf("\n", pos+1);
		n ++;
	}
	if (pos==-1 || pos>=template.length()) return template.substring(starting_pos);
	else return template.substring(starting_pos, pos);
}
}
