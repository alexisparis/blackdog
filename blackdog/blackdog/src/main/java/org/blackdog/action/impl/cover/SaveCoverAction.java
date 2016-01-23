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

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import org.awl.WizardConstants;
import org.blackdog.type.cover.CoverSearch;
import org.blackdog.ui.cover.CoverPanel;
import org.blackdog.ui.cover.CoverResearchEditor;

/**
 *
 * Open a wizard that allow to choose and download desired plugins
 *
 * @author alexis
 */
public class SaveCoverAction extends AbstractCoverAction
{   
    /** logger */
    private Logger logger = Logger.getLogger(SaveCoverAction.class);
    
    /**
     * Creates a new instance of DisplayPreviousCoverAction
     */
    public SaveCoverAction()
    {    
	this.setEnabled(false);
    }
    
    /** indicate to the action that a new CoverPanel is being displayed
     *	@param panel a CoverPanel
     */
    @Override
    public void currentCoverChanged(CoverPanel panel)
    {
	super.currentCoverChanged(panel);
	
	boolean enabled = false;
	
	if ( panel != null )
	{
	    URL url = panel.getURL();
	    
	    if ( url != null && url.getProtocol() != null )
	    {
		enabled = ! "file".equals(url.getProtocol());
	    }
	}
	this.setEnabled(enabled);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
	CoverPanel coverPanel = this.getCurrentCoverPanel();
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("logger is : " + logger);
	}
	
	if ( coverPanel != null )
	{
	    URL url = coverPanel.getURL();
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("url : " + url);
	    }
	    
	    if ( url != null )
	    {
		CoverResearchEditor editor = this.getCoverSearchEditor();

		if ( logger.isDebugEnabled() )
		{
		    logger.debug("editor : " + editor);
		}
		
		if ( editor != null )
		{
		    CoverSearch search = editor.getInstance();

		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("search is " + search);
		    }
		    
		    if ( search != null )
		    {
			try
			{
			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("before copying image to local");
			    }
			    
			    URL newUrl = search.copyImage(url);
			    
			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("after copying image to local");
				logger.debug("new url of the local image : " + newUrl);
			    }
			    
			    if ( newUrl != null && ! newUrl.equals(url) )
			    {
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("before reading local image");
				}
				
				Image img = ImageIO.read(newUrl);
				
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("after reading local image --> img = " + img);
				}

				if ( img != null )
				{
				    if ( logger.isDebugEnabled() )
				    {
					logger.debug("applying new image to CoverPanel");
				    }
				    coverPanel.setImage(newUrl, img);
				    
				    setEnabled(false);
				}
			    }
			}
			catch(IOException ex)
			{
			    logger.error("unable to copy imge from " + url, ex);
			}
		    }
		}
	    }
	}
    }
    
}
