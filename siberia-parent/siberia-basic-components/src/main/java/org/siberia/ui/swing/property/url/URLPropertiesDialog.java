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
package org.siberia.ui.swing.property.url;

import java.awt.Frame;
import java.awt.Window;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import org.siberia.ui.swing.dialog.ValidationDialog;
import com.jgoodies.forms.layout.CellConstraints;

/**
 *
 * Dialog box that display the properties of an URL
 *
 * @author alexis
 */
public class URLPropertiesDialog extends ValidationDialog
{   
    /** url properties panel */
    private URLPropertiesPanel urlPanel = null;
    
    /** url */
    private URL                url      = null;
    
    /** Creates a new instance of URLPropertiesDialog
     *	@param frame
     */
    public URLPropertiesDialog(Window frame)
    {
	super(frame, "");
	
	ResourceBundle rb = ResourceBundle.getBundle(URLPropertiesDialog.class.getName());
	this.setTitle(rb.getString("title"));
	
	this.urlPanel = new URLPropertiesPanel();
	
        CellConstraints cc = new CellConstraints();
        this.getContentPanel().add(this.urlPanel, cc.xy(1, 1));
	
    }
    
    /** initialize the content of the the components according to the given url
     *	@param url an URL
     */
    public void setURL(URL url)
    {
	this.url = url;
	this.urlPanel.setURL(url);
    }
    
    /** return the content of the the components according to the given url
     *	@return an URL
     */
    public URL getURL()
    {
	return this.url;
    }
    
    /* method that tell if valid can be performed
     *	@return true if valid can be performed without error
     */
    public boolean couldValide()
    {   
	boolean result = true;
	try
	{
	    this.url = this.urlPanel.createURL();
	}
	catch (MalformedURLException ex)
	{
	    result = false;
	    
	    ResourceBundle rb = ResourceBundle.getBundle(URLPropertiesDialog.class.getName());
	    JOptionPane.showMessageDialog(this, 
					  "<html>" + rb.getString("urlCreationErrorMessage") + "<br><br>" +
					  "<code>" + ex.getMessage() + "<code/></html>",
					  rb.getString("urlCreationErrorTitle"),
					  JOptionPane.ERROR_MESSAGE);
	}
	return result;
    }
    
}
