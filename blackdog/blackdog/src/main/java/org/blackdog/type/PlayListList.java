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
package org.blackdog.type;

import org.siberia.type.SibList;
import org.siberia.type.annotation.bean.Bean;

/**
 *
 * List of PlayList
 *
 * @author alexis
 */
@Bean(  name="PlayList list",
        internationalizationRef="org.blackdog.rc.i18n.type.PlayListList",
        expert=true,
        hidden=true,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public class PlayListList extends SibList
{
    
    /** Creates a new instance of PlayListList */
    public PlayListList()
    {
        super();
        
        this.setAllowedClass(PlayList.class);
        this.setAcceptSubClassesItem(false);
    }
    
    /** add the main library
     *  @param library a specific PlayList
     */
    public void addLibrary(ScannablePlayList library)
    {        
        this.setAcceptSubClassesItem(true);
        
        this.add(0, library);
        
        this.setAcceptSubClassesItem(false);
    }
    
}
