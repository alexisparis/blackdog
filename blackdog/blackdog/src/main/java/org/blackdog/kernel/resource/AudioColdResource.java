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
package org.blackdog.kernel.resource;

import org.siberia.kernel.resource.DefaultColdResource;
import org.siberia.kernel.resource.ResourcePersistence;
import org.siberia.type.SibType;

/**
 *
 * @author alexis
 */
public class AudioColdResource extends DefaultColdResource
{   
    /** Creates a new instance of AudioColdResource */
    public AudioColdResource(SibType item, String label, ResourcePersistence persistenceType)
    {
	super(item, label, persistenceType);
    }
    
}
