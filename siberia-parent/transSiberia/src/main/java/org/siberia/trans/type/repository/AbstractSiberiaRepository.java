/* 
 * TransSiberia : siberia plugin allowing to update siberia pplications and download new plugins
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
package org.siberia.trans.type.repository;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.siberia.trans.exception.InvalidPluginDeclaration;
import org.siberia.trans.exception.InvalidRepositoryException;
import org.siberia.trans.type.RepositoryUtilities;
import org.siberia.trans.type.plugin.PluginBuild;
import org.siberia.type.AbstractSibType;
import org.siberia.type.annotation.bean.Bean;
import org.siberia.type.annotation.bean.BeanProperty;
import org.siberia.utilities.io.IOUtilities;

/**
 *
 * Abstract SiberiaRepository based on an URL
 *
 * @author alexis
 */
@Bean(  name="repository",
        internationalizationRef="org.siberia.rc.i18n.type.AbstractSiberiaRepository",
        expert=true,
        hidden=false,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public abstract class AbstractSiberiaRepository extends AbstractSibType implements SiberiaRepository
{
    
    /** URL of the repository */
    @BeanProperty(name=PROPERTY_URL,
                  internationalizationRef="org.siberia.rc.i18n.property.AbstractSiberiaRepository_url",
                  expert=false,
                  hidden=true,
                  preferred=true,
                  bound=true,
                  constrained=true,
                  writeMethodName="setURL",
                  writeMethodParametersClass={URL.class},
                  readMethodName="getURL",
                  readMethodParametersClass={}
                 )
    private URL url = null;
    
    /** URL of the repository */
    @BeanProperty(name=PROPERTY_ADMIN_MAIL,
                  internationalizationRef="org.siberia.rc.i18n.property.AbstractSiberiaRepository_adminMail",
                  expert=false,
                  hidden=true,
                  preferred=true,
                  bound=true,
                  constrained=true,
                  writeMethodName="setAdministratorMail",
                  writeMethodParametersClass={String.class},
                  readMethodName="getAdministratorMail",
                  readMethodParametersClass={}
                 )
    private String adminMail = null;
    
    /** indicate if we already try to get the administrator mail of this repository */
    private boolean alreadyTryToGetAdminMail = false;
    
    /** Creates a new instance of AbstractURLSiberiaRepository */
    public AbstractSiberiaRepository()
    {   super();
        
//        try
//        {   this.setNameCouldChange(false);
//            this.setMoveable(false);
//            this.setReadOnly(true);
//        }
//        catch(PropertyVetoException e)
//        {   e.printStackTrace(); }
    }
    
    /** initialize the URL of the repository
     *  @param url the URL of the repository
     */
    public void setURL(URL url) throws PropertyVetoException
    {   if ( url == null )
        {
            throw new IllegalArgumentException("url must be non null");
        }
        
        if ( this.getURL() != url )
        {
            URL oldValue = this.getURL();
            
            this.fireVetoableChange(PROPERTY_URL, oldValue, url);
            
            this.checkReadOnlyProperty(PROPERTY_URL, oldValue, url);
            
            this.url = url;
            
            this.firePropertyChange(PROPERTY_URL, oldValue, url);
        }
    }
    
    /** return the URL of the repository
     *  @return the URL of the repository
     */
    public URL getURL()
    {   return this.url; }
    
    /** initialize the mail of the repository administrator
     *  @param mail the mail of the repository administrator
     */
    public void setAdministratorMail(String mail) throws PropertyVetoException
    {
        boolean changed = false;
        
        String oldValue = this.adminMail;//this.getAdministratorMail();
        
        if ( oldValue == null )
        {
            if ( mail != null )
            {
                changed = true;
            }
        }
        else
        {
            changed = ! oldValue.equals(mail);
        }
        
        if ( changed )
        {
            this.fireVetoableChange(PROPERTY_ADMIN_MAIL, oldValue, mail);
            
            this.checkReadOnlyProperty(PROPERTY_ADMIN_MAIL, oldValue, mail);
            
            this.adminMail = mail;
            
            this.firePropertyChange(PROPERTY_ADMIN_MAIL, oldValue, mail);
        }
    }
    
    /** return the mail of the repository administrator
     *  @return the mail of the repository administrator
     */
    public String getAdministratorMail()
    {
        /**
         * Lazy loading of the administrator mail since its use is reduced
         * 
         * if the admin mail is not initialized and we never try to set it,
         *  then, ask RepositoryUtilities to return it
         */
        if ( this.adminMail == null && ! this.alreadyTryToGetAdminMail )
        {
            String mail = null;
	    
	    try
	    {
		mail = RepositoryUtilities.getAdministratorMail(this);
	    }
	    catch (InvalidRepositoryException ex)
	    {
		ex.printStackTrace();
	    }
            
            if ( mail != null )
            {
                try
                {
                    this.setAdministratorMail(mail);
                }
                catch(PropertyVetoException e)
                {
                    e.printStackTrace();
                }
            }
            
            this.alreadyTryToGetAdminMail=  true;
        }
        
        return this.adminMail;
    }
    
    /** return the URL representing the given Build
     *	@param build a PluginBuild
     *	@return an URL of null if error
     */
    protected URL getBuildURL(PluginBuild build) throws InvalidPluginDeclaration,
							MalformedURLException
    {
	URL result = null;
	
	if ( build != null )
	{
	    String path = this.getURL().getPath();
	
	    result = new URL(this.getURL().getProtocol(),
			     this.getURL().getHost(),
			     this.getURL().getPort(),
			     path + File.separator + build.getRelatedArchiveLocation());
	}
	
	return result;
    }
    
    /** return the number of bytes of the given PluginBuild
     *	@param build a PluginBuild
     *	@return the number of bytes of the given PluginBuild
     */
    public int getBuildSize(PluginBuild build) throws InvalidPluginDeclaration,
						      IOException,
						      MalformedURLException
    {
	int size = 0;
	
	if ( build != null )
	{
	    size = IOUtilities.bytesAvailable(this.getBuildURL(build));
	}
	
	return size;
    }

    @Override
    public boolean equals(Object t)
    {
	boolean equals = false;
	
	if ( t instanceof SiberiaRepository )
	{ 
	    SiberiaRepository rep = (SiberiaRepository)t;
	    
	    if ( rep.getURL() == null )
	    {
		if ( this.getURL() == null )
		{
		    equals = true;
		}
	    }
	    else
	    {
		equals = rep.getURL().equals(this.getURL());
	    }
	}
	
	return equals;
    }

    @Override
    public int hashCode()
    {
	URL url = this.getURL();
	
	if ( url == null )
	{
	    return 0;
	}
	else
	{
	    return url.hashCode();
	}
    }
    
}
