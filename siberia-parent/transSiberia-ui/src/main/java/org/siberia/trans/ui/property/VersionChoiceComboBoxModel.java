/* 
 * TransSiberia-ui : siberia plugin frontend for TransSiberia
 *
 * Copyright (C) 2008 Alexis PARIS
 * Project Lead:  Alexis Paris
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
package org.siberia.trans.ui.property;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import org.siberia.trans.type.plugin.Version;
import org.siberia.trans.type.plugin.VersionChoice;

/**
 *
 * Combo box model for VersionChoice
 *
 * @author alexis
 */
public class VersionChoiceComboBoxModel extends DefaultComboBoxModel implements PropertyChangeListener
{
    /** version choice */
    private VersionChoice choice = null;
    
    /**
     * Creates a new instance of VersionChoiceComboBoxModel
     */
    public VersionChoiceComboBoxModel()
    {
	super();
    }
    
    /** initialize the VersionChoice
     *	@param choice a VersionChoice
     */
    public void setVersionChoice(VersionChoice choice)
    {
	if ( this.choice != choice )
	{
	    if ( this.choice != null )
	    {	this.choice.removePropertyChangeListener(this); }
	    
	    this.choice = choice;
	    
	    if ( this.choice != null )
	    {	this.choice.addPropertyChangeListener(this); }
	    
	    this.updateModel();
	}
    }
    
    /** allow to update the model according to the current */
    private void updateModel()
    {
	this.removeAllElements();

	if ( this.choice != null )
	{
	    List<Version> versions = this.choice.getAvailableVersions();
	    for(int i = 0; i < versions.size(); i++)
	    {
		this.insertElementAt(versions.get(i), 0);
	    }

	    this.setSelectedItem(this.choice.getSelectedVersion());
	}
	else
	{   this.setSelectedItem(null); }
    }
    
    /** ########################################################################
     *  ################ PropertyChangeListener implementation #################
     *  ######################################################################## */
    
    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source 
     *   	and the property that has changed.
     */

    public void propertyChange(PropertyChangeEvent evt)
    {
	if ( evt.getSource() == this.choice )
	{
	    if ( evt.getPropertyName().equals(VersionChoice.PROPERTY_PREFERRED_VERSION) )
	    {
		/** update selection */
		this.setSelectedItem(evt.getNewValue());
	    }
	    else if ( evt.getPropertyName().equals(VersionChoice.PROPERTY_AVAILABLE_VERSIONS) )
	    {
		this.updateModel();
	    }
	}
    }
    
}
