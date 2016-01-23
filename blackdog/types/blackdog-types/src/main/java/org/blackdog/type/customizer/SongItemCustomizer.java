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
package org.blackdog.type.customizer;

import org.blackdog.report.TagsUpdateReport;

import org.blackdog.type.SongItem;

/**
 *
 * Entity that is able to complete the state of a SongItem.
 *
 *  For example : a customizer can be added to complete the duration of a SongItem
 *                  according to AudioFileFormat informations.
 *
 * @author alexis
 */
public interface SongItemCustomizer
{
    /** complete the information of the given SongItem
     *        @param report a TagsUpdateReport
     *        @param item a SongItem
     */
    public void customize( TagsUpdateReport report, SongItem item );
}
