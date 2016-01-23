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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        internationalizationRef="org.siberia.rc.i18n.type.Plugin",
        expert=true,
        hidden=false,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public class Plugin extends PluginStructure implements PluginConstants, PropertyChangeListener
{    
    /** property preferred version */
    public static final String PROPERTY_VERSION_CHOICE = "version-choice";
    
    /** choice version */
    @BeanProperty(name=Plugin.PROPERTY_VERSION_CHOICE,
                  internationalizationRef="org.siberia.rc.i18n.property.Plugin_version_choice",
                  expert=false,
                  hidden=false,
                  preferred=true,
                  bound=true,
                  constrained=true,
                  writeMethodName="setVersionChoice",
                  writeMethodParametersClass={VersionChoice.class},
                  readMethodName="getVersionChoice",
                  readMethodParametersClass={}
                 )
    private VersionChoice          versionChoice        = new VersionChoice();
    
    /** map linking the version of build and the CheckSum method to use */
    private Map<Version, CheckSum> versionCheckMethods  = new HashMap<Version, CheckSum>();
    
    /** map linking the version of build and the calendar representing the date of release */
    private Map<Version, Calendar> versionReleaseDates  = new HashMap<Version, Calendar>();
    
    /** map linking the version of build and the associated license */
    private Map<Version, String>   versionLicenses      = new HashMap<Version, String>();
    
    /** map linking the version of build and the boolean indicating if the build need reboot */
    private Map<Version, Boolean>  versionReboot        = new HashMap<Version, Boolean>();
    
    /** Creates a new instance of Plugin */
    public Plugin()
    {   
        try
        {   
            //this.setNameCouldChange(false);
            this.setMoveable(false);
//            this.setReadOnly(true);
	    
	    VersionChoice choice = new VersionChoice();
	    
	    // TODO : a supprimer
	    choice.addAvailableVersion(Version.parse("0.0.1"));
	    choice.addAvailableVersion(Version.parse("0.0.2"));
	    this.setVersionChoice(choice);
        }
        catch(PropertyVetoException e)
        {   e.printStackTrace(); }
    }
    
    /** return true if this plugin contains available version
     *	@return true if the plugin contains available version
     */
    public boolean containsAvailableVersions()
    {
	return this.getVersionChoice().containsAvailableVersions();
    }
    
    /** set the checksum method for the given version of plugin
     *	@param version a version of the plugin
     *	@param checksum a CheckSum
     */
    public void setCheckSumForVersion(Version version, CheckSum checksum)
    {
	this.versionCheckMethods.put(version, checksum);
    }
    
    /** set the release date for the given version of plugin
     *	@param version a version of the plugin
     *	@param releaseDate a Calendar
     */
    public void setReleaseDateForVersion(Version version, Calendar releaseDate)
    {
	this.versionReleaseDates.put(version, releaseDate);
    }
    
    /** set the license code for the given version of plugin
     *	@param version a version of the plugin
     *	@param license the code of a license
     */
    public void setLicenseVersion(Version version, String license)
    {
	this.versionLicenses.put(version, license);
    }
    
    /** set if this build version needs reboot
     *	@param version a version of the plugin
     *	@param reboot true if he build need reboot after installation
     */
    public void setRebootableVersion(Version version, Boolean reboot)
    {
	this.versionReboot.put(version, reboot);
    }
    
    /** get the checksum method for the given version of plugin
     *	@param version a version of the plugin
     *	@return a CheckSum
     */
    public CheckSum getCheckSumForVersion(Version version)
    {
	return this.versionCheckMethods.get(version);
    }
    
    /** get the release date for the given version of plugin
     *	@param version a version of the plugin
     *	@return a Calendar
     */
    public Calendar getReleaseDateForVersion(Version version)
    {
	return this.versionReleaseDates.get(version);
    }
    
    /** get the license code for the given version of plugin
     *	@param version a version of the plugin
     *	@return the code of a license
     */
    public String getLicenseVersion(Version version)
    {
	return this.versionLicenses.get(version);
    }
    
    /** return true if this build version needs reboot
     *	@param version a version of the plugin
     *	@return true if he build need reboot after installation
     */
    public boolean getRebootableVersion(Version version)
    {
	return this.versionReboot.get(version);
    }
    
    /** set the preferred VersionChoice for this plugin
     *	@param versionChoice a VersionChoice
     *	
     *	@exception PropertyVetoException if a PropertyVetoListener disagree to the modification
     */
    public void setVersionChoice(VersionChoice versionChoice) throws PropertyVetoException
    {
	if ( versionChoice != this.getVersionChoice() )
	{
	    PropertyChangeEvent event = new PropertyChangeEvent(this, PROPERTY_VERSION_CHOICE, this.getVersionChoice(), versionChoice);
	    
	    this.fireVetoableChange(event);
	    
	    if ( this.getVersionChoice() != null )
	    {
		this.getVersionChoice().removePropertyChangeListener(this);
	    }
	    
	    this.versionChoice = versionChoice;
	    
	    if ( this.getVersionChoice() != null )
	    {
		this.getVersionChoice().addPropertyChangeListener(this);
	    }
	    
	    this.firePropertyChange(event);
	}
    }
    
    /** return the VersionChoice linked to this plugin
     *	@return a VersionChoice
     */
    public VersionChoice getVersionChoice()
    {	return this.versionChoice; }
    
    /** create a PluginBuild according to the information of the given Plugin
     *	@return a PluginBuild
     */
    public PluginBuild createBuild()
    {
	PluginBuild build = new PluginBuild();
	
	try
	{
	    build.setName(this.getName());
	    build.setShortDescription(this.getShortDescription());
	    build.setRepository(this.getRepository());
	    build.setPluginId(this.getPluginId());
	    build.setPluginDeclarationFilename(this.getPluginDeclarationFilename());
	    build.setDirectoryRelativePath(this.getDirectoryRelativePath());
	    build.setDescription(this.getDescription());
	    build.setCategory(this.getCategory());
	    
	    build.setVersion(this.getVersionChoice().getSelectedVersion());
	    
	    CheckSum sum = this.versionCheckMethods.get(build.getVersion());
	    build.setCheckType(sum);
	    
	    Calendar release = this.versionReleaseDates.get(build.getVersion());
	    build.setReleaseDate(release);
	    
	    String license = this.versionLicenses.get(build.getVersion());
	    build.setLicenseName(license);
	    
	    Boolean needReboot = this.versionReboot.get(build.getVersion());
	    build.setNeedReboot( (needReboot == null ? true : needReboot.booleanValue()) );
	}
	catch(PropertyVetoException e)
	{
	    e.printStackTrace();
	}
	
	return build;
    }
    
    /* #########################################################################
     * ################# PropertyChangeListener implementation #################
     * ######################################################################### */
    
    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source 
     *   	and the property that has changed.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
	if ( evt.getSource() == this.getVersionChoice() )
	{
	    /** create a new VersionChoice and set it as current VersionChoice
	     *	force to see the change on a versionChange by creating a new reference that has the same caracteristics
	     *	as the current VersionChoice
	     */
	    // TODO : do better
	    try
	    {
		this.setVersionChoice(this.getVersionChoice().copy());
	    }
	    catch (PropertyVetoException ex)
	    {	ex.printStackTrace(); }
	}
    }
    
}
