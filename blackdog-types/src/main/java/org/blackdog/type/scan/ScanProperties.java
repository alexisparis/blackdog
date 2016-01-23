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
package org.blackdog.type.scan;

import org.siberia.type.SibList;
import org.siberia.type.annotation.bean.Bean;

import java.beans.PropertyVetoException;
import java.util.ResourceBundle;

/**
 *
 * Object that represents the properties for a scan
 *
 * @author alexis
 */
@Bean( 
        name = "Scan properties", internationalizationRef = "org.blackdog.rc.i18n.type.ScanProperties", expert = false, hidden = false, preferred = true, propertiesClassLimit = Object.class, methodsClassLimit = Object.class
     )
public class ScanProperties
    extends SibList
{
    /** SibList containing SibDirectory that represents the scanned directories */
    private ScannedDirectories scannedDirectories = null;

    /** SibList containing SibDirectory that represents the ignored directories */
    private IgnoredDirectories ignoredDirectories = null;

    /** Creates a new instance of ScanProperties */
    public ScanProperties(  )
    {
        super(  );

        ResourceBundle rb = ResourceBundle.getBundle( ScanProperties.class.getName(  ) );

        this.setAllowedClass( SibList.class );

        this.scannedDirectories = new ScannedDirectories(  );
        this.ignoredDirectories = new IgnoredDirectories(  );

        this.add( this.scannedDirectories );
        this.add( this.ignoredDirectories );

        try
        {
            this.setName( rb.getString( "mainScanPropertiesName" ) );
            this.setContentItemAsChild( true );
            this.setCreateAuthorization( false );
            this.setRemoveAuthorization( false );

            this.scannedDirectories.setName( rb.getString( "scannedDirectoriesName" ) );
            this.scannedDirectories.setNameCouldChange( false );
            this.scannedDirectories.setRemovable( false );

            this.ignoredDirectories.setName( rb.getString( "ignoredDirectoriesName" ) );
            this.ignoredDirectories.setNameCouldChange( false );
            this.ignoredDirectories.setRemovable( false );
        } catch ( PropertyVetoException ex )
        {
            ex.printStackTrace(  );
        }
    }

    /** return a list containing the directory to scan
     *  @return a ScannedDirectories
     */
    public ScannedDirectories getScannedDirectories(  )
    {
        return this.scannedDirectories;
    }

    /** return a list containing the directory to scan
     *  @return a IgnoredDirectories
     */
    public IgnoredDirectories getIgnoredDirectories(  )
    {
        return this.ignoredDirectories;
    }
}
