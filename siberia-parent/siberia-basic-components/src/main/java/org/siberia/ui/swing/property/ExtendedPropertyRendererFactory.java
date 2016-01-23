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
package org.siberia.ui.swing.property;

import com.l2fprod.common.propertysheet.PropertyRendererRegistry;
import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.Logger;
import org.siberia.ResourceLoader;
import org.siberia.env.PluginResources;
import org.siberia.env.SiberExtension;
import org.siberia.exception.ResourceException;
import org.siberia.ui.swing.property.number.NumberDefaultPropertyRenderer;

/**
 *
 * Custom RendererFactory
 *
 * @author alexis
 */
public class ExtendedPropertyRendererFactory extends PropertyRendererRegistry
{   
    /** id of the extension point that allow to add a property renderer */
    private static final String PROPERTY_RENDERER_EXTENSION_POINT = "PropertyRenderer";
    
    /** id of the extension point that allow to add a property editor */
    private static final String PROPERTY_EDITOR_EXTENSION_POINT = "PropertyEditor";
    
    /** logger */
    private static       Logger logger                          = Logger.getLogger(ExtendedPropertyRendererFactory.class);
    
    /** Creates a new instance of InheritancePropertyRendererFactory */
    public ExtendedPropertyRendererFactory()
    {   }
    
    /**
     * Adds default renderers. This method is called by the constructor
     * but may be called later to reset any customizations made through
     * the <code>registerRenderer</code> methods. <b>Note: if overriden,
     * <code>super.registerDefaults()</code> must be called before
     * plugging custom defaults. </b>
     */
    @Override
    public void registerDefaults()
    {
        super.registerDefaults();
        
        /** use extension point for A to add custom renderers */
        Set<SiberExtension> extensions = PluginResources.getInstance().getExtensions(PROPERTY_RENDERER_EXTENSION_POINT);
        if ( extensions != null )
        {
            Iterator<SiberExtension> it = extensions.iterator();
            while(it.hasNext())
            {   SiberExtension extension = it.next();

                String objectClassnames   = extension.getStringParameterValue("class");
                String rendererClassname = extension.getStringParameterValue("rendererClass"); // editorClass

                if ( objectClassnames != null )
                {   if ( rendererClassname != null )
                    {   
			String[] array = objectClassnames.split(",");
			
			if ( array != null )
			{
			    for(int i = 0; i < array.length; i++)
			    {
				String objectClassname = array[i];
				
				if ( objectClassname != null )
				{
				    try
				    {   Class objectClass   = ClassUtils.getClass(objectClassname);
					Class rendererClass = ResourceLoader.getInstance().getClass(rendererClassname);

					this.registerRenderer(objectClass, rendererClass);
					
					if ( logger.isDebugEnabled() )
					{
					    logger.info("register renderer" + objectClass + ", " + rendererClass);
					}
				    }
				    catch(ResourceException e)
				    {   logger.error("error when processing a '" + PROPERTY_RENDERER_EXTENSION_POINT +
							    "' extension point", e);
				    }
				}
			    }
			}
                    }
                    else
                    {   logger.error("unable to consider extension point '" + PROPERTY_RENDERER_EXTENSION_POINT + "' : " +
                                            "class parameter is null for '" + objectClassnames + "'");
                    }
                }
                else
                {   logger.error("unable to consider extension point '" + PROPERTY_RENDERER_EXTENSION_POINT + "' : " +
                                            "class parameter is null for rendererClass='" + rendererClassname + "'");
                }
            }
        }
    }
}
