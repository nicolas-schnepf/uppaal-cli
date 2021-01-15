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


public TemplateSelector (LineReader reader, Context context) {
	super(reader, context, 1);
	this.selected_templates = new ArrayList<Boolean>();
	this.templates = new HashSet<String>();
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
	for (int i=0;i<this.selected_templates.size();i++) this.selected_templates.set(i, false);
	for (int j=this.selected_templates.size();j<this.element_number;j++) this.selected_templates.add(j, false);
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
	return this.context.getTemplateExpert().showLoadedTemplate(this.current_element);
}
}
