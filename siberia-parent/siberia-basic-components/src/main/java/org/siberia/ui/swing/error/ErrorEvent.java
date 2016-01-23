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
package org.siberia.ui.swing.error;

import java.util.EventObject;
import java.util.Map;
import java.util.logging.Level;
import org.jdesktop.swingx.error.ErrorInfo;

/**
 *
 * Describe an error
 *
 * @author alexis
 */
public class ErrorEvent extends EventObject
{
    /** title */
    private String              title           = null;
    
    /** message */
    private String		message		= null;
    
    /** detailed message */
    private String		detailedMessage = null;
    
    /** category */
    private String		category	= null;
    
    /** level */
    private Level		level		= null;
    
    /** throwable */
    private Throwable		throwable	= null;
    
    /** state */
    private Map<String, String> state		= null;
    
    /*	create a new ErrorEvent
     *	@param source
     *	@param title the title
     *  @param msg the basic message of the error
     *	@param category the category of error
     *  @param throwable a Throwable
     *  @param level any Level (Level.SEVERE, Level.WARNING, etc).
     *                              If null, then the level will be set to SEVERE.
     */
    public ErrorEvent(Object source, String title, String msg, String category,
		      Throwable throwable, Level level)
    {
	this(source, title, msg, null, category, throwable, level);
    }
    
    /*	create a new ErrorEvent
     *	@param source
     *	@param title the title
     *  @param msg the basic message of the error
     *  @param detailedMsg the detailed message of the error
     *	@param category the category of error
     *  @param throwable a Throwable
     *  @param level any Level (Level.SEVERE, Level.WARNING, etc).
     *                              If null, then the level will be set to SEVERE.
     */
    public ErrorEvent(Object source, String title, String msg, String detailedMsg, String category,
		      Throwable throwable, Level level)
    {
	this(source, title, msg, detailedMsg, category, throwable, level, null);
    }
    
    /*	create a new ErrorEvent
     *	@param source
     *	@param title the title
     *  @param msg the basic message of the error
     *  @param detailedMsg the detailed message of the error
     *	@param category the category of error
     *  @param throwable a Throwable
     *  @param level any Level (Level.SEVERE, Level.WARNING, etc).
     *                              If null, then the level will be set to SEVERE.
     *	@param state the state of the application at the time the incident occured.
     *                              The standard System properties are automatically added to this
     *                              state, and thus do not need to be included. This value may be null.
     *                              If null, the resulting map will contain only the System properties.
     *                              If there is a value in the map with a key that also occurs in the
     *                              System properties (for example: sun.java2d.noddraw), then the
     *                              developer supplied value will be used. In other words, defined
     *                              parameters override standard ones. In addition, the keys
     *                              "System.currentTimeMillis" and "isOnEDT" are both defined
     *                              automatically.
     */
    public ErrorEvent(Object source, String title, String msg, String detailedMsg, String category,
		      Throwable throwable, Level level, Map<String, String> state)
    {
	super(source);
	
	this.title           = title;
	this.message	     = msg;
	this.detailedMessage = detailedMsg;
	this.category	     = category;
	this.throwable	     = throwable;
	this.level	     = level;
	this.state	     = state;
    }
    
    /** return the title of the event
     *	@return a String
     */
    public String getTitle()
    {
	return this.title;
    }
    
    /** return the message of the event
     *	@return a String
     */
    public String getMessage()
    {
	return this.message;
    }
    
    /** return the detailed message of the event
     *	@return a String
     */
    public String getDetailedMessage()
    {
	return this.detailedMessage;
    }
    
    /** return the category of error
     *	@return a String
     */
    public String getCategory()
    {
	return this.category;
    }
    
    /** return the level of the error
     *	@return a Level
     */
    public Level getLevel()
    {
	return this.level;
    }
    
    /** return the throwable
     *	@return a Throwable
     */
    public Throwable getThrowable()
    {
	return this.throwable;
    }
    
    /** return the state
     *	@return a Map
     */
    public Map<String, String> getState()
    {
	return this.state;
    }
    
    /** create an ErrorInfo from event
     *	@return an ErrorInfo
     */
    public ErrorInfo createErrorInfo()
    {
	/** if the detailed message is not null, the throwable is not taken into account :-/ */
	return new ErrorInfo(this.getTitle(), this.getMessage(), (this.getThrowable() == null ? this.getDetailedMessage() : null), this.getCategory(),
			     this.getThrowable(), this.getLevel(), this.getState());
    }
    
}
