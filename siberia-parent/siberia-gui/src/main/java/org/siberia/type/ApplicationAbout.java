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
package org.siberia.type;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.Date;
import java.util.List;
import org.siberia.type.AbstractSibType;
import org.siberia.type.annotation.bean.Bean;
import org.siberia.type.annotation.bean.BeanProperty;
import org.siberia.type.annotation.bean.UnmergeableProperty;

/**
 *
 * @author alexis
 */
@Bean(  name="ApplicationAbout",
        internationalizationRef="org.siberia.rc.i18n.type.ApplicationAbout",
        expert=true,
        hidden=false,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public class ApplicationAbout extends AbstractSibType
{
    /** property release-date */
    public static final String PROPERTY_RELEASE_DATE = "ApplicationAbout.release.date";
    
    /** property version */
    public static final String PROPERTY_VERSION      = "ApplicationAbout.version";
    
    /** property web link */
    public static final String PROPERTY_WEB_LINK     = "ApplicationAbout.webLink";
    
    /** property image */
    public static final String PROPERTY_IMAGE        = "ApplicationAbout.image";
    
    /** property license */
    public static final String PROPERTY_LICENSE      = "ApplicationAbout.license";
    
    /** property authors */
    public static final String PROPERTY_AUTHORS      = "ApplicationAbout.authors";
    
    /** property authors */
    public static final String PROPERTY_SOFT_NAME    = "ApplicationAbout.software.name";
    
    /* release date of the instance */
    @BeanProperty(name=PROPERTY_RELEASE_DATE,
                  internationalizationRef="org.siberia.rc.i18n.property.ApplicationAbout_releaseDate",
                  expert=true,
                  hidden=false,
                  preferred=true,
                  bound=true,
                  constrained=false,
                  writeMethodName="setReleaseDate",
                  writeMethodParametersClass={Date.class},
                  readMethodName="getReleaseDate",
                  readMethodParametersClass={}
                 )
    private   Date                      releaseDate  = null;
    
    /* version of the instance */
    @BeanProperty(name=PROPERTY_VERSION,
                  internationalizationRef="org.siberia.rc.i18n.property.ApplicationAbout_version",
                  expert=true,
                  hidden=false,
                  preferred=true,
                  bound=true,
                  constrained=false,
                  writeMethodName="setVersion",
                  writeMethodParametersClass={String.class},
                  readMethodName="getVersion",
                  readMethodParametersClass={}
                 )
    private   String                    version      = null;
    
    /* web-link of the instance */
    @BeanProperty(name=PROPERTY_WEB_LINK,
                  internationalizationRef="org.siberia.rc.i18n.property.ApplicationAbout_webLink",
                  expert=true,
                  hidden=false,
                  preferred=true,
                  bound=true,
                  constrained=false,
                  writeMethodName="setWebLink",
                  writeMethodParametersClass={URL.class},
                  readMethodName="getWebLink",
                  readMethodParametersClass={}
                 )
    private   URL                       webLink      = null;
    
    /* image of the instance */
    @BeanProperty(name=PROPERTY_IMAGE,
                  internationalizationRef="org.siberia.rc.i18n.property.ApplicationAbout_image",
                  expert=true,
                  hidden=false,
                  preferred=true,
                  bound=true,
                  constrained=false,
                  writeMethodName="setImage",
                  writeMethodParametersClass={Image.class},
                  readMethodName="getImage",
                  readMethodParametersClass={}
                 )
    private   Image                     image        = null;
    
    /* image of the instance */
    @BeanProperty(name=PROPERTY_LICENSE,
                  internationalizationRef="org.siberia.rc.i18n.property.ApplicationAbout_license",
                  expert=true,
                  hidden=false,
                  preferred=true,
                  bound=true,
                  constrained=false,
                  writeMethodName="setLicense",
                  writeMethodParametersClass={String.class},
                  readMethodName="getLicense",
                  readMethodParametersClass={}
                 )
    private   String                    license      = null;
    
    /** author list */
    @BeanProperty(name=PROPERTY_AUTHORS,
                  internationalizationRef="org.siberia.rc.i18n.property.ApplicationAbout_authors",
                  expert=true,
                  hidden=false,
                  preferred=true,
                  bound=true,
                  constrained=false,
                  writeMethodName="setAuthors",
                  writeMethodParametersClass={List.class},
                  readMethodName="getAuthors",
                  readMethodParametersClass={}
                 )
    private   List<String>              authors      = null;
    
    /** software name */
    @BeanProperty(name=PROPERTY_SOFT_NAME,
                  internationalizationRef="org.siberia.rc.i18n.property.ApplicationAbout_softwareName",
                  expert=true,
                  hidden=false,
                  preferred=true,
                  bound=true,
                  constrained=false,
                  writeMethodName="setSoftwareName",
                  writeMethodParametersClass={String.class},
                  readMethodName="getSoftwareName",
                  readMethodParametersClass={}
                 )
    private   String                    softwareName = null;
    
    /** Creates a new instance of ApplicationAbout */
    public ApplicationAbout()
    {	}
    
    public String getSoftwareName()
    {   return this.softwareName; }     
    
    public void setSoftwareName(String value)
    {   
	boolean equals = false;
	
	if ( value == null )
	{
	    if ( this.getSoftwareName() == null )
	    {
		equals = true;
	    }
	}
	else
	{
	    equals = value.equals(this.getSoftwareName());
	}
	
	if ( ! equals )
	{
	    String tmpName = this.getSoftwareName();

	    PropertyChangeEvent changeEvent = new PropertyChangeEvent(this, PROPERTY_SOFT_NAME, tmpName, value);

	    this.softwareName = value;

	    this.firePropertyChange(changeEvent);
	}
    }
    
    public Date getReleaseDate()
    {
	return this.releaseDate;
    }   
    
    public void setReleaseDate(Date value)
    {   
	boolean equals = false;
	
	if ( value == null )
	{
	    if ( this.getReleaseDate() == null )
	    {
		equals = true;
	    }
	}
	else
	{
	    equals = value.equals(this.getReleaseDate());
	}
	
	if ( ! equals )
	{
	    Date tmpDate = this.getReleaseDate();

	    PropertyChangeEvent changeEvent = new PropertyChangeEvent(this, PROPERTY_RELEASE_DATE, tmpDate, value);

	    this.releaseDate = value;

	    this.firePropertyChange(changeEvent);
	}
    }
    
    /** retrn the version of the software
     *	@return a String
     */
    public String getLicense()
    {
	return this.license;
    }
    
    public void setLicense(String value)
    {   
	boolean equals = false;
	
	if ( value == null )
	{
	    if ( this.getLicense() == null )
	    {
		equals = true;
	    }
	}
	else
	{
	    equals = value.equals(this.getLicense());
	}
	
	if ( ! equals )
	{
	    String tmpLicense = this.getLicense();

	    PropertyChangeEvent changeEvent = new PropertyChangeEvent(this, PROPERTY_LICENSE, tmpLicense, value);

	    this.license = value;

	    this.firePropertyChange(changeEvent);
	}
    }
    
    /** retrn the web link of the software
     *	@return an URL
     */
    public URL getWebLink()
    {
	return this.webLink;
    }
    
    public void setWebLink(URL value)
    {   
	boolean equals = false;
	
	if ( value == null )
	{
	    if ( this.getWebLink() == null )
	    {
		equals = true;
	    }
	}
	else
	{
	    equals = value.equals(this.getWebLink());
	}
	
	if ( ! equals )
	{
	    URL tmpWebLink = this.getWebLink();

	    PropertyChangeEvent changeEvent = new PropertyChangeEvent(this, PROPERTY_WEB_LINK, tmpWebLink, value);

	    this.webLink = value;

	    this.firePropertyChange(changeEvent);
	}
    }
    
    /** retrn the image of the software
     *	@return an Image
     */
    public Image getImage()
    {
	return this.image;
    }
    
    public void setImage(Image value)
    {   
	boolean equals = value == this.getImage();
	
	if ( ! equals )
	{
	    Image tmpImage = this.getImage();

	    PropertyChangeEvent changeEvent = new PropertyChangeEvent(this, PROPERTY_IMAGE, tmpImage, value);

	    this.image = value;

	    this.firePropertyChange(changeEvent);
	}
    }
    
    /** retrn the version of the software
     *	@return a String
     */
    public String getVersion()
    {
	return this.version;
    }
    
    public void setVersion(String value)
    {   
	boolean equals = false;
	
	if ( value == null )
	{
	    if ( this.getVersion() == null )
	    {
		equals = true;
	    }
	}
	else
	{
	    equals = value.equals(this.getVersion());
	}
	
	if ( ! equals )
	{
	    String tmpVersion = this.getVersion();

	    PropertyChangeEvent changeEvent = new PropertyChangeEvent(this, PROPERTY_VERSION, tmpVersion, value);

	    this.version = value;

	    this.firePropertyChange(changeEvent);
	}
    }
    
    /** retrn the authors of the software
     *	@return a list of String
     */
    public List<String> getAuthors()
    {
	return this.authors;
    }
    
    public void setAuthors(List<String> value)
    {   
	List<String> tmpAuthors = this.getAuthors();

	PropertyChangeEvent changeEvent = new PropertyChangeEvent(this, PROPERTY_AUTHORS, tmpAuthors, value);

	this.authors = value;

	this.firePropertyChange(changeEvent);
    }
}
