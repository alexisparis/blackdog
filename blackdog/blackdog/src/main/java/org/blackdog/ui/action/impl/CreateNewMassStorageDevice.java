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
package org.blackdog.ui.action.impl;

import org.blackdog.kernel.MusikKernelResources;
import org.blackdog.type.device.MassStorageDevice;
import org.siberia.type.SibList;
import org.siberia.ui.action.impl.AddingTypeAction;

/**
 *
 * Allow to create a new mass storage device
 *
 * @author alexis
 */
public class CreateNewMassStorageDevice extends AddingTypeAction<SibList>
{
    
    /**
     * Creates a new instance of CreateNewMassStorageDevice
     */
    public CreateNewMassStorageDevice()
    {   super();
        
        this.setTypes(MusikKernelResources.getInstance().getAudioResources().getDeviceList());
    }
    
    /** method that allow the wizard to avoid the choice of the class to instantiate by proposing the given method
     *  @return a Class
     */
    public Class getClassToPropose()
    {
        return MassStorageDevice.class;
    }
    
}
