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
package org.siberia.ui.swing.table.conf;

import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.siberia.ui.swing.table.*;

/**
 *
 * Popup menu that allow to configure table
 *
 * @author alexis
 */
public class TableConfigurationPopupMenu extends JPopupMenu implements PopupMenuListener
{
    /* Table configuration panel */
    private TableConfigurationPanel configPanel = null;
    
    /** Creates a new instance of TableConfigurationPopupMenu
     *	@param tablePanel a TablePanel
     */
    public TableConfigurationPopupMenu(TablePanel tablePanel)
    {
	this.configPanel = new TableConfigurationPanel(tablePanel);
	this.add(this.configPanel);
	
	this.addPopupMenuListener(this);
    }
    
    /** method called to free panel
     *	it will not be used anymore
     */
    public void release()
    {
	this.removePopupMenuListener(this);
	if ( this.configPanel != null )
	{
	    this.configPanel.release();
	    this.configPanel = null;
	}
    }
    
    /** return true if the popup menu feel modified
     *	@return true if the popup menu feel modified
     */
    public boolean isModified()
    {
	return this.configPanel.isModified();
    }
    
    /** indicate if the popup menu feel modified
     *	@param modified true if the popup menu has to feel modified
     */
    public void setModified(boolean modified)
    {
	this.configPanel.setModified(modified);
    }

    @Override
    public void setVisible(boolean b)
    {
	if ( this.configPanel != null )
	{
	    if ( this.isVisible() != b )
	    {
		if ( b )
		{
		    this.configPanel.update();
//		    this.invalidate();
//		    this.pack();
		}
		else
		{
		    this.configPanel.prepareConfigurationSave();
		}
	    }
	}
	
	super.setVisible(b);
    }
    
    /* #########################################################################
     * ################### PopupMenuListener implementation ####################
     * ######################################################################### */
    
    public void popupMenuWillBecomeVisible(PopupMenuEvent e)
    {	
	if ( this.configPanel != null )
	{   this.configPanel.setModified(false); }
    }

    public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
    {	}

    public void popupMenuCanceled(PopupMenuEvent e)
    {	}
}
