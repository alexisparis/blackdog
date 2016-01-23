/* 
 * Siberia resources : siberia plugin to facilitate resource loading
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
package org.siberia;

import javax.swing.JFrame;
import org.apache.log4j.Logger;

/**
 *
 *  Entity that is able to give references concerning the graphical user interface
 *
 * @author alexis
 */
public class GraphicalResources
{       
    /* the singleton */
    private static final  GraphicalResources instance = new GraphicalResources();
    
    /** logger */
    public  static        Logger             logger   = Logger.getLogger(GraphicalResources.class);
    
    /** main frame of the application */
    private               JFrame             frame    = null;
    
    /** Creates a new instance of GraphicalResources */
    private GraphicalResources()
    {   /* do nothing */ }
    
    /** singleton getter **/
    public static GraphicalResources getInstance()
    {   return instance; }
    
    /** set the reference to the main frame
     *  @param frame a JFrame
     */
    public void setMainFrame(JFrame frame)
    {   this.frame = frame; }
    
    /** return a reference on the main frame of the application
     *  @return an instance of JFrame
     */
    public JFrame getMainFrame()
    {   return this.frame; }

}
