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
package org.siberia.ui.swing.tool;

import org.siberia.env.PluginResourcesEvent;
import org.siberia.env.PluginResourcesListener;
import org.siberia.bar.customizer.DefaultToolBarCustomizer;
import org.siberia.bar.customizer.ToolBarCustomizer;
import org.siberia.ui.bar.PluginBarFactory;
import org.siberia.ui.bar.PluginToolBarProvider;
import org.siberia.ui.bar.customizer.PluginToolBarCustomizer;


/**
 *
 * @author alexis
 */
public class SiberiaToolBar extends AbstractToolBar implements PluginResourcesListener
{   
    /** code */
    private String            code       = null;
    
    /** bar customizer */
    private ToolBarCustomizer customizer = null;
    
    /** Creates a new instance of MainToolBar */
    public SiberiaToolBar()
    {   super(true); }

    /** initialize the context of the menubar
     *  @param code the code
     *	@param customizer a ToolBarCustomizer
     */
    public void setContext(String code, ToolBarCustomizer customizer)
    {   
	this.code = code;
	this.customizer = customizer;
	
	if ( this.customizer == null )
	{
	    this.customizer = new PluginToolBarCustomizer();
	}
	
	this.forceConfiguration(this.code, this.customizer);
    }

    /** initialize the code of the menubar
     *  @param code the code
     */
    public void setCode(String code)
    {	
	this.setContext(code, null);
    }
    
    /** force the reconfiguration of the menu bar */
    private void forceConfiguration(String code, ToolBarCustomizer customizer)
    {   /* clear the current list */
        if ( this.getComponentCount() > 0 )
        {   this.removeAll(); }
        
	try
	{
	    /* create sub items */
	    if ( code != null )
	    {   PluginBarFactory factory = PluginBarFactory.getInstance();

		factory.configure(this, new PluginToolBarProvider(code), customizer, true);
	    }

	    this.revalidate();
	    this.repaint();
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	}
    }
    
    /* #########################################################################
     * ################# PluginResourcesListener implementation ################
     * ######################################################################### */
    
    /** indicate to a listener that a modification has been made in the plugin context
     *	@param evt a PluginResourcesEvent describing the modification
     */
    public void pluginContextChanged(PluginResourcesEvent evt)
    {
	if ( evt.getPhase().equals(PluginResourcesEvent.Phase._7) )
	{
	    this.forceConfiguration(this.code, this.customizer);
	}
    }
}
