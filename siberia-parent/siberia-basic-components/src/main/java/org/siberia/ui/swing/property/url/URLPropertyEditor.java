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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.siberia.ui.swing.TextFieldVerifier;
import org.siberia.ui.swing.property.DefaultLinkedPropertyRenderer;
import org.siberia.ui.swing.property.LinkedPropertyEditor;

/**
 *
 * Editor for url
 *
 * @author alexis
 */
public class URLPropertyEditor extends LinkedPropertyEditor<DefaultLinkedPropertyRenderer>
{
    /** url properties dialog */
    private URLPropertiesDialog dialog   = null;
    
    /** old value */
    private URL                 oldValue = null;
    
    /** Creates a new instance of URLPropertyEditor */
    public URLPropertyEditor()
    {
	super(new DefaultLinkedPropertyRenderer());
	
	this.getRenderer().getTextField().requestFocusInWindow();
    }
    
    public URL getValue()
    {
	URL result = null;
	
	URL newUrl = null;
	
	boolean equals = false;
	
	String text = this.getRenderer().getTextField().getText();
	
	/** if the text in the component is the same as this.oldValue.toString
	 *  then no need to create a new URL --> the value did not change
	 */
	if ( text != null && text.trim().length() > 0 )
	{
	    // optimization
	    if ( this.oldValue != null )
	    {
		if ( text.trim().equals(this.oldValue.toString()) )
		{
		    equals = true;
		}
	    }
	    
	    /* if equals, no need to create the same url */
	    if ( ! equals )
	    {
		try
		{
		    newUrl = new URL(text);
		}
		catch(MalformedURLException e)
		{
		    e.printStackTrace();
		}
	    }
	}
	else if ( this.oldValue == null )
	{
	    equals = true;
	}
	
	if ( ! equals )
	{
	    if ( this.oldValue == null )
	    {
		if ( newUrl == null )
		{
		    equals = true;
		}
	    }
	    else if ( newUrl != null )
	    {
		equals = this.oldValue.equals(newUrl);
		
		if ( equals )
		{
		    equals = this.oldValue.getHost().equals(newUrl.getHost());
		}
	    }
	    else
	    {
		equals = false;
	    }
	}
	
	if ( equals )
	{
	    result = this.oldValue;
	}
	else
	{
	    result = newUrl;
	}
	
	return result;
    }
    
    public void setValue(Object value)
    {
	if ( value instanceof URL )
	{
	    this.oldValue = (URL)value;
	}
	else
	{
	    this.oldValue = null;
	}

	this.getRenderer().getTextField().setText( value == null ? "" : value.toString() );
    }

    /**
     * method called when the button is pressed
     * 
     * @param event an ActionEvent representing the event that occured on the link button
     */
    protected void linkActionPerformed(ActionEvent event)
    {
	if ( this.dialog == null )
	{
	    this.dialog = new URLPropertiesDialog(SwingUtilities.getWindowAncestor((Component)event.getSource()));
	}
	
	URL url = this.getValue();
	
	this.dialog.setURL(url);
	this.dialog.display();
	
	if ( this.dialog.getAnswer() == JOptionPane.OK_OPTION )
	{
	    url = this.dialog.getURL();
	    this.getRenderer().getTextField().setText( url == null ? "" : url.toString() );
	}
	
    }
    
}
