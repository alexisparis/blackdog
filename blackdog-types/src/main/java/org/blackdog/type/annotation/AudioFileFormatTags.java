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
package org.blackdog.type.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Annotation that allow to declare the tags specific to an AudioItem
 * that could be present on an AudioFileFormat
 *
 * @author alexis
 */
@Retention( RetentionPolicy.RUNTIME )
@Target(
{ElementType.TYPE
} )
public @interface AudioFileFormatTags
{
    /** return the name of the tags attached to audio length
     *  @return an array of String representing the AudioFileformat tags representing audio length
     */
    public String[] audioLengthTags(  ) default
    {
    }
    ;
    /** return the name of the tags attached to audio bytes length
     *  @return an array of String representing the AudioFileformat tags representing audio bytes length
     */
    public String[] audioBytesLengthTags(  ) default
    {
    }
    ;
}
