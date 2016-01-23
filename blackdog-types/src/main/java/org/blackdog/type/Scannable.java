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

import org.blackdog.type.scan.ScanProperties;

import org.siberia.type.SibType;

/**
 *
 * Interface that declare an object that can be scanned
 *
 * @author alexis
 */
public interface Scannable
    extends SibType
{
    /** return true if the object can be scanned
     *  @return a boolean
     */
    public boolean isScannable(  );

    /** indicate if the object can be scanned
     *  @param scannable a boolean
     */
    public void setScannable( boolean scannable );

    /** return the ScanProperties related to this item
     *  @return a ScanProperties
     */
    public ScanProperties getScanProperties(  );
}
