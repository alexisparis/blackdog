/* 
 * Siberia resources : siberia plugin to facilitate resource loading
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
package org.siberia.env;

import java.util.EventObject;

/**
 *
 * Describe severall modifications in the plugin context
 *
 * @author alexis
 */
public class PluginResourcesEvent extends EventObject
{
    /** phase of event */
    public static enum Phase
    {
	_1,
	_2,
	_3,
	_4,
	_5,
	_6,
	_7,
	_8,
	_9,
	_10;
    }
    
    /** list of modifications */
    private PluginResourceModification[] modifications = null;
    
    /** phase of the the event
     *	this allow to PluginResourcesListener to act on a specific phase
     */
    private Phase                        phase         = null;
    
    /** Creates a new instance of PluginResourcesEvent
     *	@param source the source of the event
     *	@param phase the Phase of the event
     *	@param modifications the list of modification
     */
    public PluginResourcesEvent(Object source, Phase phase, PluginResourceModification[] modifications)
    {
	super(source);
	
	this.phase         = phase;
	this.modifications = modifications;
    }
    
    /** return the phase of the event
     *	@return a Phase
     */
    public Phase getPhase()
    {
	return this.phase;
    }
    
    /** return the number of modifications registered
     *	@return the number of modifications registered
     */
    public int getModificationCount()
    {
	return (this.modifications == null ? 0 : this.modifications.length);
    }
    
    /** return the modification at the given index
     *	@param index an index in the list of declared modifications
     *	@return a PluginResourceModification
     */
    public PluginResourceModification getModification(int index)
    {
	return this.modifications[index];
    }
    
}
