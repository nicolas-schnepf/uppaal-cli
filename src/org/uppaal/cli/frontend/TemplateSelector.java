package org.uppaal.cli.frontend;

import org.jline.reader.LineReaderBuilder;
import org.jline.reader.LineReader;
import org.uppaal.cli.context.Context;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
protected LinkedList<LinkedList<String>> template_descriptions;

public TemplateSelector (LineReader reader, Context context) {
	super(reader, context, 1);
	this.selected_templates = new ArrayList<Boolean>();
	this.templates = new HashSet<String>();
	this.template_descriptions = new LinkedList<LinkedList<String>>();
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

@Override
public void setElementNumber() {
	this.element_number = this.context.getTemplateExpert().getTemplateNumber();
	this.current_element = 0;

	for (int i=0;i<this.selected_templates.size();i++) {
		this.selected_templates.set(i, false);
		this.template_descriptions.get(i).clear();
	}
	for (int j=this.selected_templates.size();j<this.element_number;j++)  {
		this.selected_templates.add(j, false);
		this.template_descriptions.add(new LinkedList<String>());
	}

	List<String> desc = this.context.getTemplateExpert().showLoadedTemplate(this.current_element);
	for (String line: desc) this.template_descriptions.get(this.current_element).addLast(line);

	int n = this.template_descriptions.get(this.current_element).size();
	int height = this.reader.getTerminal().getHeight();
	if (n>height-5) this.view_number = n - (height - 5);
	else this.view_number = 1;
	this.current_view = 0;
}

@Override
public void showNextElement() {
	this.current_element = (this.current_element + 1) % this.element_number;
	if (this.template_descriptions.get(this.current_element).size()==0) {
	List<String> desc = this.context.getTemplateExpert().showLoadedTemplate(this.current_element);
		for (String line: desc)
			this.template_descriptions.get(this.current_element).addLast(line);
	}

// finally setup the number of views for the current element and refresh the screen

	int height = this.reader.getTerminal().getHeight();
	if (this.template_descriptions.get(this.current_element).size()<=height-5)this.view_number = 1;
	else this.view_number = this.template_descriptions.get(this.current_element).size() - (height - 5);
	this.refresh();
}

@Override
public void showPreviousElement() {

// start by decrementing the index of the current element

	if (this.current_element>0) this.current_element--;
	else this.current_element = this.element_number - 1;

// load the corresponding template description if necessary

	if (this.template_descriptions.get(this.current_element).size()==0) {
	List<String> desc = this.context.getTemplateExpert().showLoadedTemplate(this.current_element);
		for (String line: desc)
			this.template_descriptions.get(this.current_element).addLast(line);
	}

// finally setup the number of views for the current element and refresh the screen

	int height = this.reader.getTerminal().getHeight();
	if (this.template_descriptions.get(this.current_element).size()<=height-5)this.view_number = 1;
	else this.view_number = this.template_descriptions.get(this.current_element).size() - (height - 5);
	this.refresh();
}

@Override
public void showPreviousView() {
	if (this.current_view<this.view_number) this.current_view++;
	this.refresh();
}

@Override
public void showNextView() {
	if (this.current_view>0) this.current_view--;
	this.refresh();
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

// compute the index of the first and last lines to display

	LinkedList<String> description = this.template_descriptions.get(this.current_element);
	int height = this.reader.getTerminal().getHeight();
	int inf = description.size()<=height-5 ? 1 : this.current_view;
	int sup = description.size()<=height-5 ? description.size() : this.current_view + height - 5;

// compute the text to display for the template

	StringBuffer buffer = new StringBuffer();
	int index = 0;
	for (String line: description) {
		if (index>=inf && index<sup) {
			buffer.append(line);
			buffer.append("\n");
		}
		index++;
	}

	return buffer.toString();
}
}
