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
package org.siberia.ui.swing.menu;

import java.awt.Font;
import javax.swing.JMenuBar;
import org.siberia.env.PluginResourcesEvent;
import org.siberia.env.PluginResourcesListener;
import org.siberia.bar.customizer.DefaultMenuBarCustomizer;
import org.siberia.bar.customizer.MenuBarCustomizer;
import org.siberia.ui.bar.PluginBarFactory;
import org.siberia.ui.bar.PluginMenuBarProvider;
import org.siberia.ui.bar.customizer.PluginMenuBarCustomizer;


/**
 *
 * class which represent a menuBar which sub items are configured in xml resource files<br>
 * It uses BarFactory to provide merge habilities when several xml resource files have been found.
 *
 * @author alexis
 */
public class SiberiaMenuBar extends JMenuBar implements PluginResourcesListener
{
    /** font to use for this menubar */
    private Font              font       = null;
    
    /** code */
    private String            code       = null;
    
    /** bar customizer */
    private MenuBarCustomizer customizer = null;
    
    /** Creates a new instance of MainMenuBar */
    public SiberiaMenuBar()
    {   this(null); }
    
    /** Creates a new instance of MainMenuBar
     *  @param code the code
     */
    public SiberiaMenuBar(String code)
    {   super();
        
        this.setCode(code);
    }

    /** return the font to use
     *  @return a Font
     */
    public Font getFont()
    {   return font; }

    /** initialize the font to use
     *  @param font a Font
     */
    public void setFont(Font font)
    {   this.font = font; }

    /** initialize the context of the menubar
     *  @param code the code
     *	@param customizer a ToolBarCustomizer
     */
    public void setContext(String code, MenuBarCustomizer customizer)
    {   
	this.code = code;
	this.customizer = customizer;
	
	if ( this.customizer == null )
	{
	    this.customizer = new PluginMenuBarCustomizer();
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
    private void forceConfiguration(String code, MenuBarCustomizer customizer)
    {   
	/* create sub items */
	try
	{
	    if ( code != null )
	    {   PluginBarFactory factory = PluginBarFactory.getInstance();

		factory.configure(this, new PluginMenuBarProvider(code), customizer, true);
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