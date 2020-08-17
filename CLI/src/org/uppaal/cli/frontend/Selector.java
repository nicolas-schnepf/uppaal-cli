package org.uppaal.cli.frontend;

/**
* selector interface, providing the declaration of all methods common to all selectors
*/

public interface Selector {

/**
* show the current element
* @return a string describing the current element
*/
public String showCurrentElement();

/**
* validate the current selection
*/
public void validateSelection();

/**
* cancel the selection
*/
public void cancelSelection();

/**
* select the current element
*/
public void selectCurrentElement();

/**
* unselect the current element
*/
public void unselectCurrentElement();

/**
* check if the current element is selected
* @return true if and only if the current element is selected
*/
public boolean isSelected();


/**
* refresh the screen with the description of the current element
*/
public void refresh ();


/**
* set the number of elements of this selector
* @param element_number the new number of elements of this selector
*/
public void setElementNumber();

/**
* run the selector
*/
public void run ();

/**
* show the previous element of the selector
*/
public void showPreviousElement();

/**
* show the next element of the selector
*/
public void showNextElement();

/**
* show the previous view of this selector
*/
public void showPreviousView();

/**
* show the next view of this selector
*/
public void showNextView();
}