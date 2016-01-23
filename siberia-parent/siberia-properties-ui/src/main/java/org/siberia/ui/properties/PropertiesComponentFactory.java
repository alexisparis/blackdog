/* 
 * Siberia properties ui : siberia plugin defining components to edit properties
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
package org.siberia.ui.properties;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Collections;
import java.util.Set;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import org.apache.log4j.Logger;
import org.siberia.properties.XmlCategory;
import org.siberia.properties.XmlProperty;
import org.siberia.exception.ResourceException;
import org.siberia.ResourceLoader;
import org.siberia.env.SiberExtension;
import org.siberia.env.PluginResources;

/**
 *
 * @author alexis
 */
public class PropertiesComponentFactory
{
    /** name of the extension for property renderer */
    private static final String PROPERTY_RENDERER_POINT = "PropertiesRenderer";
    
    /** name of the extension for property editor */
    private static final String PROPERTY_EDITOR_POINT   = "PropertiesEditor";
    
    /** name of the attribute representing the nature of renderer or editor */
    private static final String NATURE_ATTR_NAME        = "nature";
    
    /** name of the attribute representing the class of renderer or editor */
    private static final String CLASS_ATTR_NAME         = "class";
    
    /** logger */
    private static Logger logger = Logger.getLogger(PropertiesComponentFactory.class);
    
    /** map for Property renderer */
    private static Map<String, Class>    rendererClasses = null;
    
    /** map for Property editor */
    private static Map<String, Class>    editorClasses   = null;
    
    /** Creates a new instance of PropertiesRendererFactory */
    protected PropertiesComponentFactory()
    {   }
    
    /** method that force the configuration of the editors or renderers
     *  @param en an PropertyComponentEnumeration
     */
    private static void initialize(PropertyComponentEnumeration en)
    {
        Set<SiberExtension> extensions = PluginResources.getInstance().getExtensions(PROPERTY_EDITOR_POINT);
        if ( extensions != null )
        {
            Iterator<SiberExtension> it = extensions.iterator();
            while(it.hasNext())
            {   SiberExtension currentExtension = it.next();

                if ( currentExtension != null )
                {   /* create a descriptor */
                    String nature = currentExtension.getStringParameterValue(NATURE_ATTR_NAME);
                    String cl     = currentExtension.getStringParameterValue(CLASS_ATTR_NAME);
                    
                    if ( nature != null && cl != null )
                    {   try
                        {   Class c = ResourceLoader.getInstance().getClass(cl);
                            
                            if ( en == PropertyComponentEnumeration.RENDERER )
                            {   registerRenderer(nature, c); }
                            else if ( en == PropertyComponentEnumeration.EDITOR )
                            {   registerEditor(nature, c); }
                        }
                        catch(ResourceException e)
                        {   logger.error("unable to load class '" + cl + "'", e); }
                    }
                    else
                    {   logger.error("unable to register renderer for nature='" + nature +"' for class='" + cl + "'"); }
                }
            }
        }
        
        if ( en == PropertyComponentEnumeration.EDITOR && editorClasses == null )
            editorClasses = Collections.EMPTY_MAP;
        else if ( en == PropertyComponentEnumeration.RENDERER && rendererClasses == null )
            rendererClasses = Collections.EMPTY_MAP;
    }
    
    /** create a label according to property properties
     *  @param property an instance of XmlProperty
     *  @return a JLabel
     */
    public static JLabel createLabel(XmlProperty property)
    {   JLabel label = null;
        if ( property != null )
        {   label = new JLabel(property.getLabel());
            label.setHorizontalTextPosition(SwingConstants.LEFT);
            try
            {   label.setIcon(ResourceLoader.getInstance().getIconNamed(property.getIcon())); }
            catch(ResourceException e)
            {   }
            
            if ( property.getDescription() != null )
            {   if ( property.getDescription().trim().length() > 0 )
                    label.setToolTipText(property.getDescription());
            }
            label.setBackground(Color.GREEN);
        }
        return label;
    }
    
    /** ########################################################################
     *  ############################# RENDERERS ################################
     *  ######################################################################## */ 
    
    /** register a new PropertyRenderer
     *  @param nature a String id associated with the renderer
     *  @param rendererClass a class that implements PropertyRenderer
     */
    public static void registerRenderer(String nature, Class rendererClass)
    {   if ( rendererClass != null )
        {   if ( PropertyRenderer.class.isAssignableFrom(rendererClass) )
            {   if ( rendererClasses == null )
                    rendererClasses = new HashMap<String, Class>();
                Class c = rendererClasses.get(nature);
                if ( c != null )
                    logger.warn("already contains a kind of renderer associated with id '" + nature + "', overwritten.");
                rendererClasses.put(nature, rendererClass);
            }
            else
                logger.warn("impossible to register that is not implementing " + PropertyRenderer.class.getName());
        }
    }
    
    /** unregister a new PropertyRenderer
     *  @param nature a String id associated with the parser
     */
    public static void unregisterRenderer(String nature)
    {   if ( rendererClasses != null )
        {   rendererClasses.remove(nature); }
    }
    
    /** return a renderer kind for a given property
     *  @param property a XmlProperty
     *  @return a PropertyRenderer that render the given property
     */
    public static PropertyRenderer rendererProperty(XmlProperty property)
    {   PropertyRenderer renderer = null;
        if ( property != null )
        {   if ( rendererClasses == null )
            {   initialize(PropertyComponentEnumeration.RENDERER); }
            
            Class c = rendererClasses.get(property.getRenderer());

            if ( PropertyRenderer.class.isAssignableFrom(c) )
            {   
                try
                {   renderer = (PropertyRenderer)c.newInstance();
                    renderer.setProperty(property);
                }
                catch (Exception ex)
                {   logger.error("unable to create property renderer for nature='" + property.getNature() + "'", ex); }
            }
        }
        
        return renderer;
    }
    
    /** ########################################################################
     *  ############################## EDITORS #################################
     *  ######################################################################## */ 
    
    /** register a new PropertyEditor
     *  @param nature a String id associated with the editor
     *  @param editorClass a class that implements PropertyEditor
     */
    public static void registerEditor(String nature, Class editorClass)
    {   if ( editorClass != null )
        {   if ( PropertyEditor.class.isAssignableFrom(editorClass) )
            {   if ( editorClasses == null )
                    editorClasses = new HashMap<String, Class>();
                Class c = editorClasses.get(nature);
                if ( c != null )
                    logger.warn("already contains a kind of editor associated with id '" + nature + "', overwritten.");
                editorClasses.put(nature, editorClass);
            }
            else
                logger.warn("impossible to register that is not implementing " + PropertyEditor.class.getName());
        }
    }
    
    /** unregister a new PropertyEditor
     *  @param nature a String id associated with the parser
     */
    public static void unregisterEditor(String nature)
    {   if ( editorClasses != null )
        {   editorClasses.remove(nature); }
    }
    
    /** return an editor kind for a given property
     *  @param property a XmlProperty
     *  @return a PropertyEditor that edit the given property
     */
    public static PropertyEditor editProperty(XmlProperty property)
    {   PropertyEditor editor = null;
        if ( property != null )
        {   if ( editorClasses == null )
            {   initialize(PropertyComponentEnumeration.EDITOR); }
            
            Class c = editorClasses.get(property.getEditor());

            if ( c != null && PropertyEditor.class.isAssignableFrom(c) )
            {   
                try
                {   editor = (PropertyEditor)c.newInstance();
                    editor.setProperty(property);
                }
                catch (Exception ex)
                {   logger.error("unable to create property editor for nature='" + property.getNature() + "'", ex); }
            }
        }
        
        return editor;
    }
    
    /** enumeration that describe editor and renderer */
    private enum PropertyComponentEnumeration
    {
        RENDERER(),
        EDITOR();
        
        /** create a new PropertyComponentEnumeration
         *  @param value
         */
        private PropertyComponentEnumeration()
        {   }
    }
}
