/* 
 * Siberia editor : siberia plugin defining editor framework
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
package org.siberia.editor;

import org.siberia.type.SibType;

/**
 *
 * Provide some services for Editors
 *
 * @author alexis
 */
public class EditorUtilities
{    
    /** Creates a new instance of EditorUtilities */
    private EditorUtilities()
    {   }
    
    /** indicates if the given editor could be used to edit the given object
     *  @param editor an Editor
     *  @param type a SibType
     *  @return true if the editor support the given type
     */
    public static boolean supports(Editor editor, SibType type)
    {   boolean result = false;
        
        if ( editor != null && type != null )
        {   
            org.siberia.editor.annotation.Editor annotation = (org.siberia.editor.annotation.Editor)editor.getClass().getAnnotation(org.siberia.editor.annotation.Editor.class);
            
            if ( annotation != null )
            {   Class[] relatedClasses = annotation.relatedClasses();
                
		if ( relatedClasses != null )
		{
		    for(int i = 0; i < relatedClasses.length; i++)
		    {
			Class relatedClass = relatedClasses[i];
			
			if ( relatedClass != null && relatedClass.isAssignableFrom(type.getClass()) )
			{   
			    result = true;
			    break;
			}
		    }
		}
            }
        }
        
        return result;
    }
    
}
