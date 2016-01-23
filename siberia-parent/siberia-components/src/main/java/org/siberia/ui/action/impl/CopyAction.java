/* 
 * Siberia components : siberia plugin for graphical components
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
package org.siberia.ui.action.impl;

import java.awt.event.ActionEvent;
import java.util.List;
import org.apache.log4j.Logger;
import org.siberia.exception.ResourceException;
import org.siberia.ui.action.TypeReferenceAction;
import org.siberia.type.SibType;
import org.siberia.ResourceLoader;
import org.siberia.SiberiaComponentsPlugin;
import org.siberia.ui.action.TypeReferenceAction.NoItemAnymoreException;
import org.siberia.utilities.util.Parameter;

/**
 * Copy action
 *
 * @author alexis
 */
public class CopyAction extends TypeReferenceAction<SibType>
{
    /** logger */
    private Logger logger = Logger.getLogger(CopyAction.class);
    
    /** Creates a new instance of CopyAction
     *  @param instance an instance related to this action
     */
    public CopyAction(Object instance)
    {   super("Copy");
	
	String iconPath = SiberiaComponentsPlugin.PLUGIN_ID + ";1::img/Copy.png";
	
        try
        {   this.setIcon(ResourceLoader.getInstance().getIconNamed(iconPath));
        }
        catch(ResourceException e)
        {   
	    logger.error("unable to find icon '" + iconPath + "'", e);
	}
        
        this.setTypes((SibType)instance);
    }
    
    public void actionPerformed(ActionEvent e)
    {   
	logger.info("not implemented");
    }
    
    /** method which create the type if the reference has been broken
     *  this method is called by the getType when reference is broken
     *  @param list a list of Parameter
     *  @return an instanceof SibType
     *
     *  @exception NoItemAnymoreException when no item will be created for greater or equal index
     */
    protected SibType createType(List<Parameter> list, int index) throws NoItemAnymoreException
    {   throw new NoItemAnymoreException(); }
    
}
