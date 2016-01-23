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
package org.siberia.trans.type.plugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.siberia.TransSiberiaPlugin;
import org.siberia.trans.exception.InvalidPluginDeclaration;
import org.siberia.trans.type.repository.SiberiaRepository;
import org.siberia.type.AbstractSibType;
import org.siberia.type.annotation.bean.Bean;
import org.siberia.type.annotation.bean.BeanProperty;
import org.siberia.utilities.security.check.CheckSum;
import org.siberia.xml.schema.pluginarch.ModuleCategory;

/**
 *
 * Declaration of a module
 * 
 * @author alexis
 */
@Bean(  name="plugin",
        internationalizationRef="org.siberia.rc.i18n.type.PluginBuild",
        expert=true,
        hidden=false,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public class PluginBuild extends PluginStructure implements PropertyChangeListener,
                                                            PluginConstants
{
    /** property license code */
    public static final String PROPERTY_LICENSE_NAME      = "license-name";
    
    /** property license agreement */
    public static final String PROPERTY_LICENSE_AGREEMENT = "license-agreement";
    
    /** property version */
    public static final String PROPERTY_VERSION           = "version";
    
    /** property release date */
    public static final String PROPERTY_RELEASE_DATE      = "release-date";
    
    /** property check type */
    public static final String PROPERTY_CHECKTYPE         = "check-type";
    
    /** property repository */
    public static final String PROPERTY_REPOSITORY        = "repository";
    
    /** property need reboot */
    public static final String PROPERTY_NEED_REBOOT       = "needsReboot";
    
    
    /** indicate the code of the license associated with this plugin */
    
    /* String name of the instance */
    @BeanProperty(name=PluginBuild.PROPERTY_LICENSE_NAME,
                  internationalizationRef="org.siberia.rc.i18n.property.PluginBuild_license_name",
                  expert=false,
                  hidden=true,
                  preferred=true,
                  bound=true,
                  constrained=false,
                  writeMethodName="setLicenseName",
                  writeMethodParametersClass={String.class},
                  readMethodName="getLicenseName",
                  readMethodParametersClass={}
                 )
    private String            licenseName      = null;
    
    /** indicates if the license has already been accepted */
    @BeanProperty(name=PluginBuild.PROPERTY_LICENSE_AGREEMENT,
                  internationalizationRef="org.siberia.rc.i18n.property.PluginBuild_license_agreement",
                  expert=false,
                  hidden=true,
                  preferred=true,
                  bound=true,
                  constrained=false,
                  writeMethodName="setLicenseAccepted",
                  writeMethodParametersClass={boolean.class},
                  readMethodName="isLicenseAccepted",
                  readMethodParametersClass={}
                 )
    private boolean           licenseAgreement = false;
    
    /** version of the plugin */
    @BeanProperty(name=PluginBuild.PROPERTY_VERSION,
                  internationalizationRef="org.siberia.rc.i18n.property.PluginBuild_version",
                  expert=false,
                  hidden=true,
                  preferred=true,
                  bound=true,
                  constrained=false,
                  writeMethodName="setVersion",
                  writeMethodParametersClass={Version.class},
                  readMethodName="getVersion",
                  readMethodParametersClass={}
                 )
    private Version           version          = new Version();
    
    /** release date */
    @BeanProperty(name=PluginBuild.PROPERTY_RELEASE_DATE,
                  internationalizationRef="org.siberia.rc.i18n.property.PluginBuild_release_date",
                  expert=false,
                  hidden=true,
                  preferred=true,
                  bound=true,
                  constrained=false,
                  writeMethodName="setReleaseDate",
                  writeMethodParametersClass={Calendar.class},
                  readMethodName="getReleaseDate",
                  readMethodParametersClass={}
                 )
    private Calendar          releaseDate     = new GregorianCalendar(0, 0, 1);
    
    /** release date */
    @BeanProperty(name=PluginBuild.PROPERTY_CHECKTYPE,
                  internationalizationRef="org.siberia.rc.i18n.property.PluginBuild_check_type",
                  expert=false,
                  hidden=true,
                  preferred=true,
                  bound=true,
                  constrained=false,
                  writeMethodName="setCheckType",
                  writeMethodParametersClass={CheckSum.class},
                  readMethodName="getCheckType",
                  readMethodParametersClass={}
                 )
    private CheckSum          checkType        = CheckSum.SHA1_SUM;
    
    /** needs reboot */
    @BeanProperty(name=PluginBuild.PROPERTY_NEED_REBOOT,
                  internationalizationRef="org.siberia.rc.i18n.property.PluginBuild_needs_reboot",
                  expert=false,
                  hidden=true,
                  preferred=true,
                  bound=true,
                  constrained=false,
                  writeMethodName="setNeedReboot",
                  writeMethodParametersClass={boolean.class},
                  readMethodName="isRebootNeeded",
                  readMethodParametersClass={}
                 )
    private boolean           needReboot       = false;
    
    /** Creates a new instance of Plugin */
    public PluginBuild()
    {   
        this.addPropertyChangeListener(PROPERTY_LICENSE_AGREEMENT, this);
        this.getVersion().addPropertyChangeListener(this);
        
        try
        {   
            //this.setNameCouldChange(false);
            this.setMoveable(false);
//            this.setReadOnly(true);
        }
        catch(PropertyVetoException e)
        {   e.printStackTrace(); }
    }

    /** return the name of the license
     *  @return a String
     */
    public String getLicenseName()
    {   return licenseName; }

    /** initialize the license name
     *  @param licenseName the name of the license
     */
    public void setLicenseName(String licenseName)
    {   String oldLicenseName = this.getLicenseName();
        this.licenseName = licenseName;
        
        this.firePropertyChange(PROPERTY_LICENSE_NAME, oldLicenseName, this.getLicenseName());
    }

    /** return true if the license has been accepted
     *  @return a boolean
     */
    public boolean isLicenseAccepted()
    {   return licenseAgreement; }

    /** indicate if the license has been accepted
     *  @param licenseAccepted true to accept the license
     */
    public void setLicenseAccepted(boolean licenseAccepted)
    {
        if ( licenseAccepted != this.isLicenseAccepted() )
        {
            this.licenseAgreement = licenseAccepted;
            
            this.firePropertyChange(PROPERTY_LICENSE_AGREEMENT, ! this.isLicenseAccepted(), this.isLicenseAccepted());
        }
    }

    /** return the version of the plugin
     *  @return a Version object
     */
    public Version getVersion()
    {   return version; }

    /** initialize the version of the software
     *  @param version a Version
     */
    public void setVersion(Version version)
    {   if ( version == null )
            throw new IllegalArgumentException("version must be non null");
        
        if ( version != this.getVersion() )
        {   Version oldVersion = this.getVersion();
            this.version = version;
            
            this.firePropertyChange(PROPERTY_VERSION, oldVersion, this.getVersion());
        }
    }

    /** return the release date
     *  @return a clone of the calendar that represent the release date
     *
     *      this date could be modified but the real release date won't be affected by such changes
     */
    public Calendar getReleaseDate()
    {   Calendar cal = null;
        if ( this.releaseDate != null )
        {   boolean exceptionThrowed = false;
            try
            {   Method m = this.releaseDate.getClass().getMethod("clone", new Class[]{});
                
                Object result = m.invoke(this.releaseDate, new Object[]{});
                
                if ( result instanceof Calendar )
                    cal = (Calendar)result;
            }
            catch(Exception e)
            {   exceptionThrowed = true; }
            
            if ( exceptionThrowed || cal == null )
            {   
                //throw new CloneNotSupportedException();
            }
        }
        return cal;
    }

    /** initialize the release date
     *  @param releaseDate a Calendar that represent the release date
     */
    public void setReleaseDate(Calendar releaseDate)
    {   
        if ( releaseDate == null )
            throw new IllegalArgumentException();
        
        if ( ! this.releaseDate.equals(releaseDate) )
        {   Calendar oldValue = this.releaseDate;
            
            this.releaseDate = releaseDate;
            
            this.firePropertyChange(PROPERTY_RELEASE_DATE, oldValue, this.releaseDate);
        }
    }

    /** return the kind of checksum to apply to check the validity of the downloaded plugin
     *  @return a CheckSum
     */
    public CheckSum getCheckType()
    {   return checkType; }

    /** initialize the kind of checksum to apply to check the validity of the downloaded plugin
     *  @param checkType a CheckSum
     */
    public void setCheckType(CheckSum checkType)
    {   if ( checkType == null )
            throw new IllegalArgumentException("invalid check type");
        
        if ( ! checkType.equals(this.checkType) )
        {   CheckSum oldValue = this.getCheckType();
            this.checkType = checkType;
            
            this.firePropertyChange(PROPERTY_CHECKTYPE, oldValue, this.getCheckType());
        }
    }

    /** initialize the kind of checksum to apply to check the validity of the downloaded plugin
     *  @param checkType a String representing the abbreviation of a checksum algorithme define in plugin siberia-utilities in class CheckSum
     */
    public void setCheckType(String checkType)
    {   CheckSum checkSum = CheckSum.getCheckSumForAbbreviation(checkType);
        this.setCheckType(checkSum);
    }

    /** return true if this build needs reboot after installation
     *  @return a boolean
     */
    public boolean isRebootNeeded()
    {   return this.needReboot; }

    /** initialize the property needs reboot
     *  @param needReboot true if the build needs reboot after installation
     */
    public void setNeedReboot(boolean needReboot)
    {   
	if ( needReboot != this.isRebootNeeded() )
	{
	    this.needReboot = needReboot;

	    this.firePropertyChange(PROPERTY_NEED_REBOOT, ! this.isRebootNeeded(), this.isRebootNeeded());
	}
    }
    
    /* #########################################################################
     * ##### additional methods to provide an url that refers to the files #####
     * ############################# to download ###############################
     * ######################################################################### */
    
    
    /** returns the name of the archive file representing the plugin
     *  @return the name of the archive file representing the plugin
     */
    public StringBuffer getArchiveName() throws InvalidPluginDeclaration
    {   
        String  name    = this.getName();
        Version version = this.getVersion();
        
        if ( name == null )
            throw new InvalidPluginDeclaration("the name of the plugin is not initialized");
        if ( version == null )
            throw new InvalidPluginDeclaration("the version of the plugin is not initialized");
        
        StringBuffer buffer = new StringBuffer();
        buffer.append(name);
        buffer.append('-');
        buffer.append(version.toString());
//        buffer.append(SIBERIA_PLUGIN_EXTENSION);
        
        return buffer;
    }
    
    /** return the local filename to use when downloading a build
     *	@return a String ('siberia-types-0.0.1.zip')
     */
    public String getLocalArchiveSimplename() throws InvalidPluginDeclaration
    {
	StringBuffer buffer = this.getArchiveName();
	buffer.append(".zip");
		
	return buffer.toString();
    }
    
    /** return 
    
    /** return the base location where to find files linked to a siberia plugin
     *  @return the base location where to find files linked to a siberia plugin
     */
    private StringBuffer getBaseRelatedLocation() throws InvalidPluginDeclaration
    {   
        String  dirPath = this.getDirectoryRelativePath();
        
        Version version = this.getVersion();
        
        if ( dirPath == null )
	{
            throw new InvalidPluginDeclaration("the related path of the directory of the plugin is not initialized");
	}
        if ( version == null )
	{
            throw new InvalidPluginDeclaration("the version of the plugin is not initialized");
	}
        
        StringBuffer buffer = new StringBuffer();
        buffer.append(dirPath);
        
        buffer.append('/');
        buffer.append(version.toString());
        buffer.append('/');
        
        buffer.append(this.getArchiveName());
        
        return buffer;
    }
    
    /** return the location where to find the plugin in siberia plugin repositories
     *  @return the location where to find the plugin in siberia plugin repositories
     */
    public String getRelatedArchiveLocation() throws InvalidPluginDeclaration
    {   return this.getBaseRelatedLocation() + PluginConstants.SIBERIA_PLUGIN_EXTENSION; }

    @Override
    public boolean equals(Object t)
    {
	boolean equals = super.equals(t);
	
	if ( equals && t != null && this.getClass().equals(t.getClass()) )
	{
	    /** must be the same structure and have the same version */
	    PluginBuild other = (PluginBuild)t;
	    
	    if ( this.getVersion() == null )
	    {
		if ( other.getVersion() != null )
		{
		    equals = false;
		}
	    }
	    else
	    {
		equals = this.getVersion().equals(other.getVersion());
	    }
	}
	
	return equals;
    }
    
    /* #########################################################################
     * ################ PropertyChangeListener implementation ##################
     * ######################################################################### */
    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source 
     *   	and the property that has changed.
     */

    public void propertyChange(PropertyChangeEvent evt)
    {   
        if ( evt.getSource() == this )
        {   if ( PROPERTY_LICENSE_NAME.equals(evt.getPropertyName()) )
            {   /** set license agreement to false */
                this.setLicenseAccepted(false);
            }
        }
        else if ( evt.getSource() == this.getVersion() )
        {
            /** create an old version :-( */
            // PENDING : a revoir
            Version old = new Version();
            
            if ( Version.PROPERTY_MAJOR.equals(evt.getPropertyName()) )
            {   old.setMajor( (Integer)evt.getOldValue() ); }
            else
            {   old.setMajor(this.getVersion().getMajor()); }
            
            if ( Version.PROPERTY_MINOR.equals(evt.getPropertyName()) )
            {   old.setMinor( (Integer)evt.getOldValue() ); }
            else
            {   old.setMinor(this.getVersion().getMinor()); }
            
            if ( Version.PROPERTY_REVISION.equals(evt.getPropertyName()) )
            {   old.setRevision( (Integer)evt.getOldValue() ); }
            else
            {   old.setRevision(this.getVersion().getRevision()); }
            
            this.firePropertyChange(PROPERTY_VERSION, old, this.getVersion());
        }
    }
    
}
