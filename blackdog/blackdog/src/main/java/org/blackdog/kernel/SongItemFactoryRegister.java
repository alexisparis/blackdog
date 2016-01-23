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
package org.blackdog.kernel;

import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import org.blackdog.BlackdogPlugin;
import org.blackdog.BlackdogTypesPlugin;
import org.siberia.ResourceLoader;
import org.siberia.env.PluginResources;
import org.siberia.exception.ResourceException;
import org.siberia.utilities.cache.GenericCache2;
import org.blackdog.type.factory.SongItemFactory;
import org.siberia.env.SiberExtension;
/**
 *
 * Register for SongItem factory
 *
 * @author alexis
 */
public class SongItemFactoryRegister extends GenericCache2<String, SongItemFactory>
{
    
    /** Creates a new instance of SongItemFactoryRegister */
    public SongItemFactoryRegister()
    {   }
    
    /** method that create an elemet according to the key
     *  @param key the corresponding key
     *  @param parameters others parameters
     *  @return the object that has benn created
     */
    public SongItemFactory create(String key, Object... parameters)
    {
        String defaultSongItemFactoryClassname  = null;
        String specificSongItemFactoryClassname = null;
        
        /** read extensions point and determine the factory to use */
        Set<SiberExtension> extensions = PluginResources.getInstance().getExtensions(BlackdogTypesPlugin.SONG_ITEM_FACTORY_EXTENSION_POINT_ID);
        if ( extensions != null )
        {   Iterator<SiberExtension> it = extensions.iterator();
            while(it.hasNext())
            {   SiberExtension currentExtension = it.next();
                
                if ( this.isDefaultFactory(currentExtension) )
                {   if ( defaultSongItemFactoryClassname == null )
                    {   defaultSongItemFactoryClassname = currentExtension.getStringParameterValue("class"); }
                }
                else
                {   if ( this.isSpecificFactory(currentExtension, key) )
                    {   specificSongItemFactoryClassname = currentExtension.getStringParameterValue("class");
                        
                        break;
                    }
                }
            }
        }
        
        SongItemFactory factory = null;
        
        if ( specificSongItemFactoryClassname != null )
        {   
            try
            {   Class c = ResourceLoader.getInstance().getClass(specificSongItemFactoryClassname);
                factory = (SongItemFactory)c.newInstance();
            }
            catch(Exception e)
            {   e.printStackTrace(); }
        }
        
        if ( factory == null && defaultSongItemFactoryClassname != null )
        {   
            try
            {   Class c = ResourceLoader.getInstance().getClass(defaultSongItemFactoryClassname);
                factory = (SongItemFactory)c.newInstance();
            }
            catch(Exception e)
            {   e.printStackTrace(); }
        }
        
        return factory;
    }
    
    /** determine if the given SiberExtension represents a default SongItem factory
     *  @param extension a SiberExtension
     *  @return true if the given SiberExtension represents a default SongItem factory
     */
    private boolean isDefaultFactory(SiberExtension extension)
    {
        boolean result = false;
        
        if ( extension != null )
        {
            String allowedExtensions = extension.getStringParameterValue("extensions");
            
            if ( allowedExtensions != null && allowedExtensions.toLowerCase().indexOf("default") != -1 )
            {   result = true; }
        }
        
        return result;
        
    }
    
    /** determine if the given SiberExtension convey specifically to the given extension.
     *  warning : this method does not test that the extension represents a default SongItem so, if extexnsions = 'default,mp3'
     *      and ext = 'mp3' then, the method will return true
     *  @param extension a SiberExtension
     *  @param ext the String extension NOT beginning by '.'
     *  @return true if the given SiberExtension convey spécifically to the given extension
     */
    private boolean isSpecificFactory(SiberExtension extension, String ext)
    {
        boolean result = false;
        
        if ( extension != null && ext != null && ext.trim().length() > 0 )
        {
            String allowedExtensions = extension.getStringParameterValue("extensions");
            
            if ( allowedExtensions != null )
            {
                StringTokenizer tokenizer = new StringTokenizer(allowedExtensions, ",");
                
                while ( tokenizer.hasMoreTokens() )
                {   String currentExt = tokenizer.nextToken();
                    
                    if ( currentExt != null )
                    {
                        if ( currentExt.toLowerCase().equals( ext.toLowerCase() ) )
                        {   result = true;
                            break;
                        }
                    }
                }
            }
        }
        
        return result;
    }
    
}
