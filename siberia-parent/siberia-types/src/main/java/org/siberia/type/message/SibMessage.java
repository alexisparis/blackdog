/* 
 * Siberia types : siberia plugin defining structures managed by siberia platform
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
package org.siberia.type.message;

import java.util.logging.Level;
import org.siberia.type.*;

/**
 *
 * SibType that contains a message
 *
 * @author alexis
 */
//@Bean(  name="SibMessage",
//        internationalizationRef="org.siberia.rc.i18n.type.SibWaitingMessage",
//        expert=true,
//        hidden=true,
//        preferred=true,
//        propertiesClassLimit=Object.class,
//        methodsClassLimit=Object.class
//      )
public abstract class SibMessage extends SibString
{
    /** Level INFO, SEVERE and WARNING are allowed */
    private Level level = Level.INFO;
    
    /**
     * Creates a new instance of SibMessage
     */
    public SibMessage()
    {	}
    
    /** initialize the level
     *	@param level a Level
     */
    protected void setMessageLevel(Level level)
    {
	if ( level == null || ( ! Level.INFO.equals(level) && 
				! Level.WARNING.equals(level) && 
				! Level.SEVERE.equals(level) ) )
	{
	    throw new IllegalArgumentException("allowed levels are " + Level.INFO + ", " +
								       Level.WARNING + ", " + 
								       Level.SEVERE);
	}
	
	this.level = level;
    }
    
    /** return the level of the message
     *	@return a Level INFO, WARNING OR SEVERE
     */
    public Level getMessageLevel()
    {
	return this.level;
    }
    
}
