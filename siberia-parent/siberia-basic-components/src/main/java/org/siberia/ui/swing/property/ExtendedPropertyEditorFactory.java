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

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;
import com.l2fprod.common.beans.editor.IntegerPropertyEditor;
import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
import java.beans.PropertyEditor;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import org.apache.log4j.Logger;
import org.siberia.ResourceLoader;
import org.siberia.env.PluginResources;
import org.siberia.env.SiberExtension;
import org.siberia.exception.ResourceException;

/**
 *
 * Custom EditorFactory
 *
 * @author alexis
 */
public class ExtendedPropertyEditorFactory extends PropertyEditorRegistry
{   
    /** id of the extension point that allow to add a property editor */
    private static final String PROPERTY_EDITOR_EXTENSION_POINT = "PropertyEditor";
    
    /** logger */
    private static       Logger logger                          = Logger.getLogger(ExtendedPropertyEditorFactory.class);
    
    /** Creates a new instance of InheritancePropertyRendererFactory */
    public ExtendedPropertyEditorFactory()
    {   }
    
    public synchronized PropertyEditor getEditor(Property property)
    {
        PropertyEditor editor = super.getEditor(property);
        
        if ( editor == null && ! property.getType().equals(Object.class) )
        {   editor = this.getEditor(property.getType().getSuperclass()); }
        
        return editor;
    }
    
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
        Set<SiberExtension> extensions = PluginResources.getInstance().getExtensions(PROPERTY_EDITOR_EXTENSION_POINT);
        if ( extensions != null )
        {
            Iterator<SiberExtension> it = extensions.iterator();
            while(it.hasNext())
            {   SiberExtension extension = it.next();

                String objectClassnames = extension.getStringParameterValue("class");
                String editorClassname = extension.getStringParameterValue("editorClass");

                if ( objectClassnames != null )
                {   if ( editorClassname != null )
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
				    {   Class objectClass = ClassUtils.getClass(objectClassname);
					Class editorClass = ResourceLoader.getInstance().getClass(editorClassname);

					this.registerEditor(objectClass, editorClass);
					if ( logger.isDebugEnabled() )
					{
					    logger.debug("registerEditor " + objectClass + ", " + editorClass);
					}
				    }
				    catch(ResourceException e)
				    {   logger.error("error when processing a '" + PROPERTY_EDITOR_EXTENSION_POINT +
							    "' extension point", e);
				    }
				}
			    }
			}
                    }
                    else
                    {   logger.error("unable to consider extension point '" + PROPERTY_EDITOR_EXTENSION_POINT + "' : " +
                                            "class parameter is null for '" + objectClassnames + "'");
                    }
                }
                else
                {   logger.error("unable to consider extension point '" + PROPERTY_EDITOR_EXTENSION_POINT + "' : " +
                                            "class parameter is null for editorClass='" + editorClassname + "'");
                }
            }
        }
    }
}
