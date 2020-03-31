package org.uppaal.cli.commands;

/**
* location expert, responsible for all operations on locations
*/

import org.uppaal.cli.commands.Command.ObjectCode;
import com.uppaal.model.core2.AbstractTemplate;
import com.uppaal.model.core2.Template;
import com.uppaal.model.core2.QueryList;
import com.uppaal.model.core2.Query;
import com.uppaal.model.core2.Document;
import com.uppaal.model.core2.Element;
import com.uppaal.model.core2.Node;
import com.uppaal.model.core2.Location;
import com.uppaal.model.core2.InsertElementCommand;
import com.uppaal.model.core2.RemoveElementCommand;
import com.uppaal.model.core2.SetPropertyCommand;

public class LocationExpert extends AbstractExpert {
public LocationExpert (Context context) {
	super(context);
}

/**
* add a location described by its name and the name of its template
* @param template_name the name of the template to update
* @param location_name the name of the new location
* @exception an exception will be thrown if the template does not exist
*/
public void addLocation (String template_name, String location_name) {
	Template template = (Template) this.context.getDocument().getTemplate(template_name);
	if (template==null) this.throwMissingElementException(ObjectCode.TEMPLATE, template_name);
	if (this.getLocation(template_name, "name", location_name)!=null)
		this.throwExistingElementException(ObjectCode.LOCATION, location_name);

	Location location = template.createLocation();
	location.setProperty("name", location_name);
	InsertElementCommand command = new InsertElementCommand(location.getCommandManager(), template, null, location);
	command.execute();
	this.context.addCommand(command);
}

/**
* remove a location based on its name and on the name of its template
* @param template the name of the template to inspect
* @param location the name of the location to remove
*/
public void removeLocation (String template, String name) {
	Location location = this.getLocation(template, "name", name);
	if (location==null) this.throwMissingElementException(ObjectCode.LOCATION, name);
		RemoveElementCommand command = new RemoveElementCommand(location);
		command.execute();
		this.context.addCommand(command);
}


/**
* return the description of a location given by its name and the name of its template
* @param template the name of the template to inspect
* @param name the name of the location to describe
* @return a string containing the description of the location
* @exception a missing element exception if either the template or the location does not exist
*/
public String showLocation (String template, String name) {
	Location location = this.getLocation (template, "name", name);
	if (location==null) this.throwMissingElementException(ObjectCode.LOCATION, name);
	return this.describeLocation(location);
}

/**
* set a property of a location described by the name of its template, of its source and its target
* @param template the template to update
* @param name the name of the location to update
* @param property the name of the property to update
* @param value the new value for the property
* @exception an exception if the template, the source or the target does not exist
*/
public void setLocationProperty (String template, String name, String property, Object value) {

// first of all check that such a location does not already exist in the template

	Location location = this.getLocation(template, property, value);

	if (location!=null) {
		switch (property) {
			case "name":
			this.throwExistingElementException(ObjectCode.LOCATION, template);
			break;
			case "init":
			this.throwExistingElementException(ObjectCode.INIT, template);
			break;
			case "committed":
			this.throwExistingElementException(ObjectCode.COMMITTED, template);
			break;
		}
	}

// get the given location and update it

	location = this.getLocation(template, "name", name);
	if (location==null) this.throwMissingElementException(ObjectCode.LOCATION, name);
	SetPropertyCommand command = new SetPropertyCommand(location, property, value);
	command.execute();
	this.context.addCommand(command);
}
}
