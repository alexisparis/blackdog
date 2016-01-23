/* 
 * Siberia types : siberia plugin defining structures managed by siberia platform
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
package org.siberia.type.annotation.bean;

/**
 *
 * @author alexis
 */
public class BeanConstants
{   
    /** key defining the display name of a Bean */
    public static  final String BEAN_NAME                 = "name";
    
    /** key defining the display name of a Bean */
    public static  final String BEAN_DISPLAY_NAME         = "displayName";
    
    /** key defining the description of a Bean */
    public static  final String BEAN_DESCRIPTION          = "description";
    
    /** key defining the short description of a Bean */
    public static  final String BEAN_SHORT_DESCRIPTION    = "shortDescription";
    
    /** key defining the icon mono 16 path of a Bean */
    public static  final String BEAN_ICON_MONO_16         = "iconMono16";
    
    /** key defining the icon mono 32 path of a Bean */
    public static  final String BEAN_ICON_MONO_32         = "iconMono32";
    
    /** key defining the icon color 16 path of a Bean */
    public static  final String BEAN_ICON_COLOR_16        = "iconColor16";
    
    /** key defining the icon color 32 path of a Bean */
    public static  final String BEAN_ICON_COLOR_32        = "iconColor32";
    
    /** key defining the plugin icon mono 16 path of a Bean */
    public static  final String BEAN_PLUGIN_ICON_MONO_16  = "pluginIconMono16";
    
    /** key defining the plugin icon mono 32 path of a Bean */
    public static  final String BEAN_PLUGIN_ICON_MONO_32  = "pluginIconMono32";
    
    /** key defining the plugin icon color 16 path of a Bean */
    public static  final String BEAN_PLUGIN_ICON_COLOR_16 = "pluginIconColor16";
    
    /** key defining the plugin icon color 32 path of a Bean */
    public static  final String BEAN_PLUGIN_ICON_COLOR_32 = "pluginIconColor32";
    
    /** key defining if a Bean is considered as expert */
    public static  final String BEAN_EXPERT               = "expert";
    
    /** key defining if a Bean is considered as hidden */
    public static  final String BEAN_HIDDEN               = "expert";
    
    /** key defining if a Bean is considered as preferred */
    public static  final String BEAN_PREFERRED            = "preferred";
    
    /** key defining if a Bean property is considered as bounded */
    public static  final String BEAN_PROPERTY_BOUND       = "bound";
    
    /** key defining if a Bean property is considered as constrained */
    public static  final String BEAN_PROPERTY_CONSTRAINED = "constrained";
    
    /** Creates a new instance of BeanConstants */
    private BeanConstants()
    {   }
    
}
