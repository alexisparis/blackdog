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
package org.blackdog.type.device;

import org.blackdog.type.ScannablePlayList;
import org.blackdog.type.scan.ScanProperties;
import org.blackdog.type.scan.ScannedDirectory;

import org.siberia.base.LangUtilities;
import org.siberia.base.file.Directory;

import org.siberia.type.SibDirectory;
import org.siberia.type.annotation.bean.BeanProperty;

import java.beans.PropertyVetoException;

/**
 *
 * Abstract representation of a device
 *
 * @author alexis
 */
public abstract class AbstractDevice
    extends ScannablePlayList
{
    /** property mount point */
    public static final String PROPERTY_MOUNT_POINT = "mount-point";

    /** directory represented the mount point of the device */
    @BeanProperty( 
            name = PROPERTY_MOUNT_POINT, internationalizationRef = "org.blackdog.rc.i18n.property.AbstractDevice_mountpoint", expert = false, hidden = false, preferred = true, bound = true, constrained = true, writeMethodName = "setMountPoint", writeMethodParametersClass = 
    {
        Directory.class}
    , readMethodName = "getMountPoint", readMethodParametersClass = 
    {
    }

         )
    private Directory mountPoint = null;

    /** Creates a new instance of AbstractDevice */
    public AbstractDevice(  )
    {
    }

    /** set the mount point of the device
     *  @param directory a Directory
     */
    public void setMountPoint( Directory directory )
                       throws PropertyVetoException
    {
        boolean equals = false;

        if ( directory == null )
        {
            if ( this.getMountPoint(  ) == null )
            {
                equals = true;
            }
        } else
        {
            if ( this.getMountPoint(  ) != null )
            {
                equals =
                    LangUtilities.equals( this.getMountPoint(  ).getAbsolutePath(  ),
                                          directory.getAbsolutePath(  ) );
            }
        }

        if ( ! equals )
        {
            this.fireVetoableChange( PROPERTY_MOUNT_POINT,
                                     this.getMountPoint(  ),
                                     directory );

            this.checkReadOnlyProperty( PROPERTY_MOUNT_POINT,
                                        this.getMountPoint(  ),
                                        directory );

            Directory oldDir = this.getMountPoint(  );

            this.mountPoint = directory;

            this.firePropertyChange( PROPERTY_MOUNT_POINT,
                                     oldDir,
                                     this.getMountPoint(  ) );
        }
    }

    /** return the mount point of the device
     *  @return a Directory
     */
    public Directory getMountPoint(  )
    {
        return this.mountPoint;
    }

    /**
     * return the ScanProperties related to this item
     *
     *
     * @return a ScanProperties
     */
    public final ScanProperties getScanProperties(  )
    {
        ScanProperties retValue = new ScanProperties(  );

        Directory dir = this.getMountPoint(  );

        if ( dir != null )
        {
            ScannedDirectory scannedDir = new ScannedDirectory(  );

            try
            {
                scannedDir.setValue( dir );
            } catch ( PropertyVetoException ex )
            {
                ex.printStackTrace(  );
            }

            retValue.getScannedDirectories(  ).add( scannedDir );
        }

        return retValue;
    }
}
