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

import org.blackdog.type.device.AbstractDevice;
import org.siberia.type.SibList;
import org.siberia.type.annotation.bean.Bean;

/**
 *
 * List of AbstractDevice
 *
 * @author alexis
 */
@Bean(  name="Device list",
        internationalizationRef="org.blackdog.rc.i18n.type.DeviceList",
        expert=true,
        hidden=true,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public class DeviceList extends SibList
{
    
    /** Creates a new instance of RadioList */
    public DeviceList()
    {
        super();
        
        this.setAllowedClass(AbstractDevice.class);
    }
    
}
