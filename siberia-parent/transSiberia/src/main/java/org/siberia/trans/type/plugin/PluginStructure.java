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

import java.beans.PropertyVetoException;
import org.siberia.trans.type.repository.SiberiaRepository;
import org.siberia.type.AbstractSibType;
import org.siberia.type.annotation.bean.Bean;
import org.siberia.type.annotation.bean.BeanProperty;
import org.siberia.xml.schema.pluginarch.ModuleCategory;

/**
 *
 * Declaration of a module structure
 * 
 * @author alexis
 */
@Bean(  name="plugin",
        internationalizationRef="org.siberia.rc.i18n.type.PluginStructure",
        expert=true,
        hidden=false,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public abstract class PluginStructure extends AbstractSibType implements PluginConstants
{   
    /** property repository */
    public static final String PROPERTY_REPOSITORY        = "plugin-repository";
    
    /** property relative directory path */
    public static final String PROPERTY_REL_DIR_PATH      = "plugin-relative-directory-path";
    
    /** property plugin declaration filename */
    public static final String PROPERTY_DECL_FILENAME     = "plugin-declaration-filename";
    
    /** property short description */
    public static final String PROPERTY_SHORT_DESC        = "plugin-short-description";
    
    /** property long description */
    public static final String PROPERTY_LONG_DESC         = "plugin-long-description";
    
    /** property category */
    public static final String PROPERTY_CATEGORY          = "pluginCategory";
    
    /** property plugin id */
    public static final String PROPERTY_PLUGIN_ID         = "plugin-pluginId";
    
    
    /** indicate the code of the license associated with this plugin */
    
    /** repository where the plugin could be downloaded */
    private SiberiaRepository repository       = null;
    
    /** the relative location of the module directory in the repository of the plugin */
    @BeanProperty(name=PluginStructure.PROPERTY_REL_DIR_PATH,
                  internationalizationRef="org.siberia.rc.i18n.property.PluginStructure_rel_dir_path",
                  expert=false,
                  hidden=true,
                  preferred=true,
                  bound=true,
                  constrained=false,
                  writeMethodName="setDirectoryRelativePath",
                  writeMethodParametersClass={String.class},
                  readMethodName="getDirectoryRelativePath",
                  readMethodParametersClass={}
                 )
    private String            pluginDirRelPath = null;
    
    /** the name of the module declaration that can be located with attribute pluginDirRelPath
     *  this module declaration list all available versions of the plugin
     */
    @BeanProperty(name=PluginStructure.PROPERTY_DECL_FILENAME,
                  internationalizationRef="org.siberia.rc.i18n.property.PluginStructure_decl_filename",
                  expert=false,
                  hidden=true,
                  preferred=true,
                  bound=true,
                  constrained=false,
                  writeMethodName="setPluginDeclarationFilename",
                  writeMethodParametersClass={String.class},
                  readMethodName="getPluginDeclarationFilename",
                  readMethodParametersClass={}
                 )
    private String            pluginDeclName   = null;
    
    /** short description of the plugin */
    @BeanProperty(name=PluginStructure.PROPERTY_SHORT_DESC,
                  internationalizationRef="org.siberia.rc.i18n.property.PluginStructure_short_description",
                  expert=false,
                  hidden=true,
                  preferred=true,
                  bound=true,
                  constrained=false,
                  writeMethodName="setShortDescription",
                  writeMethodParametersClass={String.class},
                  readMethodName="getShortDescription",
                  readMethodParametersClass={}
                 )
    private String            shortDescription = "";
    
    /** short description of the plugin */
    @BeanProperty(name=PluginStructure.PROPERTY_LONG_DESC,
                  internationalizationRef="org.siberia.rc.i18n.property.PluginStructure_long_description",
                  expert=false,
                  hidden=true,
                  preferred=true,
                  bound=true,
                  constrained=false,
                  writeMethodName="setDescription",
                  writeMethodParametersClass={String.class},
                  readMethodName="getDescription",
                  readMethodParametersClass={}
                 )
    private String            description      = "";
    
    /** category of the plugin */
    @BeanProperty(name=PluginStructure.PROPERTY_CATEGORY,
                  internationalizationRef="org.siberia.rc.i18n.property.PluginStructure_category",
                  expert=false,
                  hidden=true,
                  preferred=true,
                  bound=true,
                  constrained=false,
                  writeMethodName="setCategory",
                  writeMethodParametersClass={ModuleCategory.class},
                  readMethodName="getCategory",
                  readMethodParametersClass={}
                 )
    private ModuleCategory    category         = null;//ModuleCategory.SYSTEM;
    
    /** id of the plugin */
    @BeanProperty(name=PluginStructure.PROPERTY_PLUGIN_ID,
                  internationalizationRef="org.siberia.rc.i18n.property.PluginStructure_pluginId",
                  expert=false,
                  hidden=true,
                  preferred=true,
                  bound=true,
                  constrained=false,
                  writeMethodName="setPluginId",
                  writeMethodParametersClass={String.class},
                  readMethodName="getPluginId",
                  readMethodParametersClass={}
                 )
    private String            pluginId         = null;
    
    
    /** Creates a new instance of PluginStructure */
    public PluginStructure()
    {   
        try
        {   
            //this.setNameCouldChange(false);
            this.setMoveable(false);
//            this.setReadOnly(true);
        }
        catch(PropertyVetoException e)
        {   e.printStackTrace(); }
    }

    /** return the plugin id
     *  @return a String
     */
    public String getPluginId()
    {   return this.pluginId; }

    /** initialize the plugin id
     *  @param id the id of the plugin
     */
    public void setPluginId(String id)
    {   String oldId = this.getPluginId();
        this.pluginId = id;
        
        this.firePropertyChange(PROPERTY_PLUGIN_ID, oldId, this.getPluginId());
    }

    /** return the SiberiaRepository where to download this plugin
     *  @return a SiberiaRepository
     */
    public SiberiaRepository getRepository()
    {   return repository; }

    /** return the SiberiaRepository where to download this plugin
     *  @return a SiberiaRepository
     */
    public void setRepository(SiberiaRepository repository)
    {   if ( repository == null )
            throw new IllegalArgumentException("repository could not be null");
        SiberiaRepository old = this.getRepository();
        this.repository = repository;
        
        this.firePropertyChange(PROPERTY_REPOSITORY, old, this.getRepository());
    }

    /** return the relative path of the plugin directory
     *  @return a String
     */
    public String getDirectoryRelativePath()
    {   return pluginDirRelPath; }

    /** set the relative path of the plugin directory
     *  @param pluginDirRelPath a String<br>
     *          it is recommended to make pluginDirRelPath start with a '/' or '\'
     */
    public void setDirectoryRelativePath(String pluginDirRelPath)
    {   
        if ( pluginDirRelPath == null )
            throw new IllegalArgumentException("invalid pluginDirRelPath");
        
        String oldValue = this.getDirectoryRelativePath();
        String newValue = pluginDirRelPath;
        
        boolean charFound = false;
        if ( newValue.length() > 0 )
        {   char lastChar = newValue.charAt(newValue.length() - 1);
            charFound = true;
            if ( lastChar != '/' && lastChar != '\\' )
                newValue += "/";
        }
        
        if ( ! charFound )
            newValue = "/";
        
        this.pluginDirRelPath = pluginDirRelPath;
        
        this.firePropertyChange(PROPERTY_REL_DIR_PATH, oldValue, this.getDirectoryRelativePath());
    }

    /** return the filename (not the path!!) of the plugin declaration
     *  @return a String
     */
    public String getPluginDeclarationFilename()
    {   return pluginDeclName; }

    /** set the filename of the plugin declaration
     *  @param pluginDeclName a String
     */
    public void setPluginDeclarationFilename(String pluginDeclName)
    {   
        if ( pluginDeclName == null )
            throw new IllegalArgumentException("invalid pluginDeclName");
        
        String oldValue = this.getPluginDeclarationFilename();
        this.pluginDeclName = pluginDeclName;
        
        this.firePropertyChange(PROPERTY_DECL_FILENAME, oldValue, this.getPluginDeclarationFilename());
    }

    /** return a short description of the plugin
     *  @return a String
     */
    public String getShortDescription()
    {   return shortDescription; }

    /** initialize the short description of the plugin
     *  @param shortDescription a String
     */
    public void setShortDescription(String shortDescription)
    {
        if ( shortDescription == null )
            throw new IllegalArgumentException("shortDescription could not be null");
        
        if ( ! this.getShortDescription().equals(shortDescription) )
        {   
            String oldValue = this.getShortDescription();
            this.shortDescription = shortDescription;
            
            this.firePropertyChange(PROPERTY_SHORT_DESC, oldValue, this.getShortDescription());
        }
    }

    /** return a description of the plugin
     *  @return a String
     */
    public String getDescription()
    {   return description; }

    /** initialize the description of the plugin
     *  @param description a String
     */
    public void setDescription(String description)
    {
        if ( description == null )
            throw new IllegalArgumentException("description could not be null");
        
        if ( ! this.getDescription().equals(shortDescription) )
        {   
            String oldValue = this.getDescription();
            this.description = description;
            
            this.firePropertyChange(PROPERTY_LONG_DESC, oldValue, this.getDescription());
        }
    }

    /** return the category of the plugin
     *  @return a ModuleCategory
     */
    public ModuleCategory getCategory()
    {   return category; }

    /** initialize the category of the plugin
     *  @param category a ModuleCategory
     */
    public void setCategory(ModuleCategory category)
    {   
        ModuleCategory cat = category;
        if ( cat == null )
        {   cat = ModuleCategory.SYSTEM; }
        
        if ( ! cat.equals(this.getCategory()) )
        {   
            ModuleCategory oldCat = this.getCategory();
            this.category = cat;
            this.firePropertyChange(PROPERTY_CATEGORY, oldCat, this.getCategory());
        }
    }

    @Override
    public boolean equals(Object t)
    {
	boolean equals = super.equals(t);
	
	if ( equals && t != null && this.getClass().equals(t.getClass()) )
	{
	    /** muse be in the same repository and have the same name */
	    PluginStructure other = (PluginStructure)t;
	    
	    if ( this.getPluginId() == null )
	    {
		if ( other.getPluginId() != null )
		{
		    equals = false;
		}
	    }
	    else
	    {
		equals = this.getPluginId().equals(other.getPluginId());
	    }
	    
	    if ( equals )
	    {
		if ( this.getRepository() == null )
		{
		    if ( other.getRepository() != null )
		    {
			equals = false;
		    }
		}
		else
		{
		    equals = this.getRepository().equals(other.getRepository());
		}
	    }
	}
	
	return equals;
    }
}
