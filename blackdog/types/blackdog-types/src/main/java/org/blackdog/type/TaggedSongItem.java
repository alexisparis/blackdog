/*
 * blackdog types : define kind of items maanged by blackdog
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

import org.blackdog.report.TagsUpdateReport;

import org.siberia.type.SibType;
import org.siberia.type.annotation.bean.Bean;

import java.beans.PropertyVetoException;
import java.net.URL;

/**
 *
 * define a song item that supports tags.
 * indicating that such an item is dirty means that some of the properties
 * of the song like author, title, etc... have been modified.
 * this kind of item must provide a method to update the properties
 *
 * @author alexis
 */
@Bean( 
        name = "tagged song item", internationalizationRef = "org.blackdog.rc.i18n.type.TaggedSongItem", expert = false, hidden = true, preferred = true, propertiesClassLimit = Object.class, methodsClassLimit = Object.class
     )
public interface TaggedSongItem
    extends SongItem
{
    /** indicate if the song item is in a tags refresh process
     *        @return true if the song item is in a tags refresh process
     */
    public boolean isInTagsRefreshProcess(  );

    /** indicate if the song item is in a tags refresh process
     *        @param process true if the song item is in a tags refresh process
     */
    public void setInTagsRefreshProcess( boolean process );
}
