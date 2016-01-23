/* 
 * Siberia image searcher : siberia plugin defining image searchers
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
package org.siberia;

/**
 *
 * Static declarations about the plugin siberia-image-searcher
 *
 * @author alexis
 */
public class SiberiaImageSearcherPlugin
{
    /** id of the plugin */
    public static final String PLUGIN_ID			    = "siberia-image-searcher";
    
    /** path to the resources directory */
    public static final String RC_PATH				    = "org/siberia/rc";
    
    /** extension point that define image searcher */
    public static final String EXTENSION_POINT_IMAGE_SEARCHER	    = "ImageSearcher";
    
    /** parameter name of the extension point image searcher */
    public static final String EXTENSION_POINT_IMAGE_SEARCHER_NAME  = "name";
    
    /** parameter class of the extension point image searcher */
    public static final String EXTENSION_POINT_IMAGE_SEARCHER_CLASS = "class";
    
    /**
	 * Creates a new instance of SiberiaImageSearcherPlugin
	 */
    protected SiberiaImageSearcherPlugin()
    {   }
    
}
