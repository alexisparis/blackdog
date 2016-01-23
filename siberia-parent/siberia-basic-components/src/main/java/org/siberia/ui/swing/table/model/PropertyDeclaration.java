/* 
 * Siberia basic components : siberia plugin defining components supporting siberia types
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
package org.siberia.ui.swing.table.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * describe a Property that could be managed by an IntrospectionSibTypeListTableModel
 *
 * @author alexis
 */
public class PropertyDeclaration
{
    /** property enabled */
    public static final String PROPERTY_ENABLED        = "enabled";
    
    /** property consider property */
    public static final String PROPERTY_MODIFY_ENABLED = "modify-enabled";
    
    /** property preferred size */
    public static final String PROPERTY_PREFERRED_SIZE = "preferred-size";
    
    /** the name of the property */
    private String                propertyName  = null;
    
    /** enabled */
    private boolean               enabled       = true;
    
    /** property enabled can be changed */
    private boolean               modifyEnabled = true;
    
    /** preferred size of the column */
    private int                   preferredSize = -1;
    
    /** property change support */
    private PropertyChangeSupport support       = new PropertyChangeSupport(this);
    
    /** Creates a new instance of PropertyDeclaration
     *	@param propertyName the name of the property
     */
    public PropertyDeclaration(String propertyName)
    {
	this(propertyName, true);
    }
    
    /** Creates a new instance of PropertyDeclaration
     *	@param propertyName the name of the property
     *	@param enabled true to enable property
     */
    public PropertyDeclaration(String propertyName, boolean enabled)
    {
	this(propertyName, enabled, true);
    }
    
    /** Creates a new instance of PropertyDeclaration
     *	@param propertyName the name of the property
     *	@param enabled true to enable property
     *	@param modifyEnable true to indicate that the property enabled can be changed
     */
    public PropertyDeclaration(String propertyName, boolean enabled, boolean modifyEnable)
    {
	this(propertyName, enabled, modifyEnable, 100);
    }
    
    /** Creates a new instance of PropertyDeclaration
     *	@param propertyName the name of the property
     *	@param enabled true to enable property
     *	@param prefSize the preferred width for the column
     *	@param modifyEnable true to indicate that the property enabled can be changed
     */
    public PropertyDeclaration(String propertyName, boolean enabled, boolean modifyEnable, int prefSize)
    {
	this.propertyName  = propertyName;
	this.setEnabled(enabled);
	this.setEnabledModifiable(modifyEnable);
	this.setPreferredSize(prefSize);
    }

    /** retourne la taille préférentielle de la colonne
     *	@return un entier
     */
    public int getPreferredSize()
    {	return preferredSize; }

    /** initialise la taille préférentielle de la colonne
     *	@param preferredSize un entier
     */
    public void setPreferredSize(int preferredSize)
    {
	if ( preferredSize != this.getPreferredSize() )
	{
	    int old = this.getPreferredSize();
	    
	    this.preferredSize = preferredSize;
	    
	    this.firePropertyChange(PROPERTY_PREFERRED_SIZE, old, this.getPreferredSize());
	}
    }
    
    /** return the name of the property
     *	@return a String
     */
    public String getPropertyName()
    {	return this.propertyName; }
    
    /** return true if this property is enabled
     *	@return a boolean
     */
    public boolean isEnabled()
    {	return this.enabled; }
    
    /** enabled or not the property
     *	@param enabled a boolean
     */
    public void setEnabled(boolean enabled)
    {	if ( enabled != this.isEnabled() && this.isEnabledModifiable() )
	{
	     this.enabled = enabled;
	     
	     this.firePropertyChange(PROPERTY_ENABLED, ! enabled, enabled);
	}
    }
    
    /** return true if this property enabled can be modify
     *	@return a boolean
     */
    public boolean isEnabledModifiable()
    {	return this.modifyEnabled; }
    
    /** indicate if this property enabled can be modify
     *	@param modifiable a boolean
     */
    public void setEnabledModifiable(boolean modifiable)
    {	
	if ( modifiable != this.isEnabledModifiable() )
	{
	    this.modifyEnabled = modifiable;
	    
	    this.firePropertyChange(PROPERTY_MODIFY_ENABLED, ! modifiable, modifiable);
	}
    }

    @Override
    public String toString()
    {
	return "property declaration {name=" + this.getPropertyName() + ", pref width=" + this.getPreferredSize() + ", " +
				      "enabled=" + this.isEnabled() + ", enabled modifiable=" + this.isEnabledModifiable() + "}";
    }
    
    /** ########################################################################
     *  ################### PropertyChangeListeners methods ####################
     *  ######################################################################## */
    
    /** add a listener
     *	@param listener a PropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
	this.support.addPropertyChangeListener(listener);
    }
    
    /** add a listener
     *	@param propertyName the name of a property
     *	@param listener a PropertyChangeListener
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
	this.support.addPropertyChangeListener(propertyName, listener);
    }
    
    /** remove a listener
     *	@param listener a PropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
	this.support.removePropertyChangeListener(listener);
    }
    
    /** remove a listener
     *	@param propertyName the name of a property
     *	@param listener a PropertyChangeListener
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {
	this.support.removePropertyChangeListener(propertyName, listener);
    }
    
    /** fire a PropertyChnageEvent
     *	@param propertyName the name of a property
     *	@param oldValue the old value of the property
     *	@param newValue the new value of the property
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue)
    {
	this.support.firePropertyChange(propertyName, oldValue, newValue);
    }
    
    
}
