/* 
 * Siberia browser : siberia plugin defining a simple web browser
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
package org.siberia.ui.editor.impl.html;

import org.siberia.type.SibType;
import org.siberia.editor.annotation.Editor;
import org.siberia.type.SibHtmlURL;
import org.siberia.editor.AdaptedEditor;
import org.siberia.editor.AdaptedEditor.EditorBuildingFailedException;
import org.siberia.ui.swing.text.HtmlViewer;

/**
 *
 * @author alexis
 */
@Editor(relatedClasses={org.siberia.type.SibHtmlURL.class},
                  description="Editor for instances of SibURL",
                  name="Html browser",
                  launchedInstancesMaximum=-1)
public class ColdHtmlBrowser extends AdaptedEditor
{
    private HtmlViewer viewer = null;
    
    /**
     * Creates a new instance of ColdHtmlBrowser 
     */
    public ColdHtmlBrowser()
    {   this(null); }
    
    /**
     * Creates a new instance of ColdHtmlBrowser 
     */
    public ColdHtmlBrowser(SibHtmlURL instance)
    {   super(null, false, false);
        
        this.setLayoutType(AdaptedEditor.BOX_LAYOUT);
        
        this.setUseValidation(false);
        
        this.setInstance(instance);
	
	this.viewer = new HtmlViewer();
	
	this.configureContent(this.viewer);

	try
	{
	    this.buildingCompleted();
	}
	catch(EditorBuildingFailedException e){ e.printStackTrace(); }
    }

    /**
     * set the SibType instance associated with the editor
     * 
     * @param instance instance of SibType
     */
    @Override
    public void setInstance(SibType instance)
    {
        super.setInstance(instance);
        
        if ( this.instance instanceof SibHtmlURL )
        {
            if ( ((SibHtmlURL)instance).getURL() != null )
	    {
		this.viewer.loadPage(((SibHtmlURL)instance).getURL().toString());
	    }
        }
    }
    
    public void startLoading()
    {   this.viewer.start(); }
    
    @Override
    public void save()
    {   /* do nothing */ }
    
    /** method to update the graphical components in the editor
     *  according to the state of the relative associated entity
     */
    @Override
    public void load()
    {   /* do nothing */ }
}
