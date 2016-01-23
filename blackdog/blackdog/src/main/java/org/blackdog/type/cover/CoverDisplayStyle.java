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
package org.blackdog.type.cover;

import java.lang.ref.SoftReference;
import java.util.ResourceBundle;

/**
 *
 * Defines cover display style
 *
 * @author alexis
 */
public enum CoverDisplayStyle
{   
    SCALED("scaled"),
    SCALED_KEEP_RATIO("scaled_keep_ratio"),
    TILED("tiled"),
    CENTERED("centered");
    
    /** code */
    private String code = null;
    
    /**
     * soft reference to the ResourceBundle linked to CoverDisplayStyle
     */
    private static SoftReference<ResourceBundle> rbReference = new SoftReference<ResourceBundle>(null);
    
    private CoverDisplayStyle(String code)
    {   this.code = code; }
    
    /** return the code of the category
     *	@return the code
     */
    public String getCode()
    {
	return this.code;
    }
    
    /** return the label
     *  @return the label
     */
    public String label()
    {   
	ResourceBundle rb = this.rbReference.get();
	
        if ( rb == null )
        {   
	    rb = ResourceBundle.getBundle("org.blackdog.type.cover.CoverDisplayStyle");
            this.rbReference = new SoftReference<ResourceBundle>(rb);
        }
        
        return rb.getString(this.code);
    }
}
