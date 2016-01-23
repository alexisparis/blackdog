/*
 * Siberia docking window : define an editor support based on Infonode docking window framework
 *
 * Copyright (C) 2008 Alexis PARIS
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.siberia.ui.component.docking.layout;

import java.util.HashMap;
import java.util.Map;
import net.infonode.docking.RootWindow;
import net.infonode.docking.View;
import org.apache.log4j.Logger;

/**
 *
 * abstract implementation of a ViewLayout
 *
 * @author alexis
 */
public abstract class AbstractViewLayout implements ViewLayout
{
    /** logger */
    private transient Logger logger = Logger.getLogger(AbstractViewLayout.class);
    
    /** map linking Object and ViewPlacement */
    protected Map<Object, ViewConstraints> defaultPlacements = null;
    
    /** Creates a new instance of AbstractViewLayout */
    public AbstractViewLayout()
    {   }
    
    /** indicates if the layout accept those kind of ViewPlacement
     *  @param placement a ViewPlacement
     *  @return true if the layout support this kind of ViewPlacement
     */
    public abstract boolean accept(ViewConstraints placement);
    
    /** create a default ViewPlacement
     *  @return a ViewPlacement
     */
    public abstract ViewConstraints createDefaultPlacement();
    
    /** lay a view out into a RootWindow
     *  @param panel a RootWindow
     *  @param view the view
     *  @param object an Object related to the view
     *  @param placement a ViewPlacement
     */
    public abstract void doLayoutImpl(RootWindow panel, View view, Object object, ViewConstraints placement);
    
    /** return a placement for a given Object
     *  @param object an Object related to a view
     *  @return a ViewPlacement
     */
    public ViewConstraints getPosition(Object object)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getPosition(" + object + ")");
	}
	ViewConstraints placement = null;
        if ( this.defaultPlacements != null )
        {   placement = this.defaultPlacements.get(object); }
        
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of getPosition(" + object + ") returns " + placement);
	}
        return placement;
    }
    
    /** set the default placement for a given Object
     *  @param object an Object related to a view
     *  @param placement a ViewPlacement
     */
    public void setPosition(Object object, ViewConstraints placement)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling setPosition(" + object + ", " + placement + ")");
	}
	if ( object != null )
        {   if ( this.accept(placement) )
            {   if ( this.defaultPlacements == null )
                    this.defaultPlacements = new HashMap<Object, ViewConstraints>();
                this.defaultPlacements.put(object, placement);
            }
	    else
	    {
		logger.debug("placement " + placement + " not accepted by this layout");
	    }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of setPosition(" + object + ", " + placement + ")");
	}
    }
    
    /** return a preferred constraints for a given object
     *  @param object an Object that is contained in defaultPlacements
     *  @return a ViewConstraints
     */
    protected ViewConstraints getDefaultConstraintsFor(Object object)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getDefaultConstraintsFor(" + object + ")");
	}
	ViewConstraints placement = null;
        if ( this.defaultPlacements != null )
            placement = this.defaultPlacements.get(object);
        
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of getDefaultConstraintsFor(" + object + ") returns " + placement);
	}
        return placement;
    }
    
    /** lay a view out into a RootWindow
     *  @param panel a RootWindow
     *  @param view the view
     *  @param object an Object related to the view
     */
    public void doLayout(RootWindow panel, View view, Object object)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling doLayout(panel" + panel + ", view=" + view + ", object=" + object + ")");
	}
	ViewConstraints placement = this.getDefaultConstraintsFor(object);
        if ( placement == null )
        {   placement = this.createDefaultPlacement(); }
        this.doLayout(panel, view, object, placement);
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of doLayout(panel" + panel + ", view=" + view + ", object=" + object + ")");
	}
    }
    
    /** lay a view out into a RootWindow
     *  @param panel a RootWindow
     *  @param view the view
     *  @param object an Object related to the view
     *  @param placement a ViewPlacement
     */
    public void doLayout(RootWindow panel, View view, Object object, ViewConstraints placement)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling doLayout(panel" + panel + ", view=" + view + ", object=" + object + ", placement=" + placement + ")");
	}
	ViewConstraints place = placement;
        if ( place == null )
        {   place = this.createDefaultPlacement();
            
	    // warning : avant if ( place != null && this.accept(place) )
	    if ( place != null && ! this.accept(place) )
	    {
                place = null;
	    }
        }
        
        this.doLayoutImpl(panel, view, object, placement);
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of doLayout(panel" + panel + ", view=" + view + ", object=" + object + ", placement=" + placement + ")");
	}
    }
    
}
