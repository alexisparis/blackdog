/*
 * blackdog : audio player / manager
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
package org.blackdog.action.impl.cover;

import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;
import org.awl.WizardConstants;
import org.blackdog.ui.cover.CoverResearchEditor;

/**
 *
 * Open a wizard that allow to choose and download desired plugins
 *
 * @author alexis
 */
public class DisplayPreviousCoverAction extends AbstractCoverAction
{   
    /** logger */
    private Logger logger = Logger.getLogger(DisplayPreviousCoverAction.class);
    
    /**
     * Creates a new instance of DisplayPreviousCoverAction
     */
    public DisplayPreviousCoverAction()
    {    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
	CoverResearchEditor editor = this.getCoverSearchEditor();
	
	if ( editor != null )
	{
	    editor.showPreviousImage();
	}
    }
    
}
