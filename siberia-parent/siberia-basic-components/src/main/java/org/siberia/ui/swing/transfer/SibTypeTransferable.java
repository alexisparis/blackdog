/* 
 * Siberia basic components : siberia plugin defining components supporting siberia types
 *
 * Copyright (C) 2008 Alexis PARIS
 * Project Lead:  Alexis Paris
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
package org.siberia.ui.swing.transfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import org.siberia.ResourceLoader;
import org.siberia.type.SibType;

/**
 *
 * Transferable implementation for SibTypes
 *
 * @author alexis
 */
public class SibTypeTransferable implements Transferable
{
    private static DataFlavor[] flavors = null;
    
    static
    {	try
        {   flavors = new DataFlavor[]
            {	new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + 
                               "; class=" + SibTypeList.class.getName(), "Siberian types", ResourceLoader.getInstance().getPluginClassLoader("blackdog")),
                DataFlavor.stringFlavor
            };
        }
        catch(Exception e)
        {   e.printStackTrace(); }
    }
    
    /** list of SibType */
    private SibTypeList types = null;

    public SibTypeTransferable(SibTypeList types)
    {
	this.types = types;
    }

    /**
     * Returns whether or not the specified data flavor is supported for
     * this object.
     * 
     * @param flavor the requested flavor for the data
     * @return boolean indicating whether or not the data flavor is supported
     */
    public boolean isDataFlavorSupported(DataFlavor flavor)
    {   
	boolean result = false;
        for (int i = 0; i < flavors.length && ! result; i++)
	{   if (flavor.equals(flavors[i]))
	    {	result = true; }
	}
	return result;
    }

    /**
     * Returns an object which represents the data to be transferred.  The class 
     * of the object returned is defined by the representation class of the flavor.
     * 
     * 
     * @param flavor the requested flavor for the data
     * @exception IOException                if the data is no longer available
     *              in the requested flavor.
     * @exception UnsupportedFlavorException if the requested data flavor is
     *              not supported.
     * @see DataFlavor#getRepresentationClass
     */
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
    {   
	Object result = null;
	
	if (flavor.equals(flavors[0]))
        {
            StringBuffer buffer = new StringBuffer();
            
            if ( this.types != null )
            {
                for(int i = 0; i < this.types.size(); i++)
                {
                    SibType current = this.types.get(i);
                    
                    if ( i > 0 )
                    {
                        buffer.append(", ");
                    }
                    
                    if ( current == null )
                    {
                        buffer.append("null");
                    }
                    else
                    {
                        buffer.append(current.toString());
                    }
                }
            }
            
	    result = buffer.toString();
	}
        else if (flavor.equals(flavors[1]))
        {
            result = this.types;
	}
	
	if ( result == null )
        {   throw new UnsupportedFlavorException(flavor); }
	
	return result;
    }

    /**
     * Returns an array of DataFlavor objects indicating the flavors the data 
     * can be provided in.  The array should be ordered according to preference
     * for providing the data (from most richly descriptive to least descriptive).
     * 
     * @return an array of data flavors in which this data can be transferred
     */
    public DataFlavor[] getTransferDataFlavors()
    {
	return (DataFlavor[])flavors.clone();
    }
}
