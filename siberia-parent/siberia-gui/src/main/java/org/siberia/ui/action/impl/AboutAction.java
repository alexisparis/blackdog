/* 
 * Siberia gui : siberia plugin defining basics of graphical application 
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
package org.siberia.ui.action.impl;

import java.beans.PropertyVetoException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import org.siberia.env.PluginContext;
import org.siberia.kernel.Kernel;
import org.siberia.type.ApplicationAbout;
import org.siberia.utilities.util.Parameter;
import java.util.List;
import org.siberia.ui.action.TypeReferenceAction.NoItemAnymoreException;

/**
 *
 * Action that force the edition of a ApplicationAbout
 *
 * @author alexis
 */
public class AboutAction extends TypeReferenceEditingAction<ApplicationAbout>
{
    /** Creates a new instance of AboutAction */
    public AboutAction()
    {   super(); }
    
    /** method which create the type if the reference has been broken
     *  this method is called by the getType when reference is broken
     *  @param list a list of Parameter
     *  @return an instanceof ColdAbout
     *
     *  @exception NoItemAnymoreException when no item will be created for greater or equal index
     */
    protected ApplicationAbout createType(List<Parameter> list, int index) throws NoItemAnymoreException
    {   
        if ( index > 0 )
        {   throw new NoItemAnymoreException(); }
        
        PluginContext context = Kernel.getInstance().getPluginContext();
	
	ApplicationAbout about = new ApplicationAbout();
	
	try
	{
	    ResourceBundle rb = ResourceBundle.getBundle(AboutAction.class.getName());
	    about.setName(rb.getString("name"));
	}
	catch(PropertyVetoException e)
	{
	    e.printStackTrace();
	}
	
	about.setAuthors(context.getAuthors());
	about.setVersion(context.getVersion());
	about.setSoftwareName(context.getName());
	
	try
	{
	    about.setWebLink(new URL(context.getURL()));
	}
	catch(MalformedURLException e)
	{
	    e.printStackTrace();
	}
	
	about.setReleaseDate(context.getDate());
	about.setLicense(context.getLicense());
	
	try
	{
	    about.setImage(org.siberia.ResourceLoader.getInstance().getImageNamed(context.getImagePath()));
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	}
        
        return about;
    }
}
