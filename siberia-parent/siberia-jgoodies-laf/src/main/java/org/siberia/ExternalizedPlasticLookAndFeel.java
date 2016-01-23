/* 
 * Siberia jgoodies laf : siberia plugin defining jgoodies look and feels
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

import com.jgoodies.plaf.plastic.Plastic3DLookAndFeel;
import java.io.InputStream;


/**
 *
 * @author Alexis
 */
public class ExternalizedPlasticLookAndFeel extends Plastic3DLookAndFeel
{
    
    /** Creates a new instance of ExternalizedPlasticLookAndFeel
     *	@param themeName the name of the theme
     *	@param stream an InputStream that contains properties describing the theme
     */
    public ExternalizedPlasticLookAndFeel(String themeName, InputStream stream)
    {
        super();
	
	if ( stream != null )
	{
	    ExternalizedPlasticTheme theme = new ExternalizedPlasticTheme(themeName, stream);
	    
	    this.setMyCurrentTheme(theme);
	}
    }

    @Override
    public String getName()
    {
	String result = null;
	
	if ( this.getMyCurrentTheme() != null )
	{
	    result = this.getMyCurrentTheme().getName();
	}
	
	if ( result == null )
	{
	    result = super.getName();
	}
	
	return result;
    }
    
}
