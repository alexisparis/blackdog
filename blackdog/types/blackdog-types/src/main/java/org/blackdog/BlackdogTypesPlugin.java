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
package org.blackdog;


/**
 *
 * Static declarations about the plugin blackdog-types
 *
 * @author alexis
 */
public class BlackdogTypesPlugin
{
    /** id of the plugin */
    public static final String PLUGIN_ID = "blackdog-types";

    /** id of the extensions points that represents SongItem factory */
    public static final String SONG_ITEM_FACTORY_EXTENSION_POINT_ID = "SongItemFactory";

    /** id of the extensions points that represents SongItem customizers */
    public static final String SONG_ITEM_CUSTOMIZER_EXTENSION_POINT_ID = "SongItemCustomizer";

    /** id of the extensions points that represents supported extension */
    public static final String AUDIO_EXTENSION_SUPPORTED_EXTENSION_POINT_ID = "AudioExtensionSupported";

    /** id of the extensions points that represents audio entagger */
    public static final String AUDIO_ENTAGGER_EXTENSION_POINT_ID = "AudioEntagger";

    /** Creates a new instance of BlackdogTypesPlugin */
    protected BlackdogTypesPlugin(  )
    {
    }
}
