/* 
 * TransSiberia : siberia plugin allowing to update siberia pplications and download new plugins
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
package org.siberia.trans.type.plugin;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * declaration of a version
 *
 * @author alexis
 */
public class Version implements Comparable<Version>, Serializable
{
    /** unknwon version */
    public static final Version UNKNOWN_VERSION = new Version()
    {
	public void setMajor(int major){    }
	public int getMajor(){return -1;}
	public void setMinor(int minor){    }
	public int getMinor(){return -1;}
	public void setMicro(int micro){    }
	public int getMicro(){return -1;}
    };
    
    /** property major */
    public static final String              PROPERTY_MAJOR     = "major";
    
    /** property minor */
    public static final String              PROPERTY_MINOR     = "minor";
    
    /** property revision */
    public static final String              PROPERTY_REVISION  = "revision";
    
    /** list of separator accepted */
    private static Set<Character>           ALLOWED_SEPARATORS = Version.createAllowedSeparatorsList();
    
    /** major */
    private int                             major              = 0;
    
    /** minor */
    private int                             minor              = 0;
    
    /** revision */
    private int                             revision           = 0;
    
    /** property change support */
    private transient PropertyChangeSupport support            = new PropertyChangeSupport(this);
    
    /** Creates a new instance of Version */
    public Version()
    {   this.setMajor(0);
        this.setMinor(0);
        this.setRevision(0);
    }

    /** return the major number of the version
     *	<b>1</b>.2.3
     *  @return an integer
     */
    public int getMajor()
    {   return major; }

    /** set the major number of the version
     *	<b>1</b>.2.3
     *  @param major an integer
     *
     *  @exception IllegalArgumentException if major is less than 0
     */
    public void setMajor(int major)
    {   if ( major < 0 )
            throw new IllegalArgumentException("major is to be greater or equal to 0");
        int oldValue = this.getMajor();
        this.major = major;
        
        this.support.firePropertyChange(PROPERTY_MAJOR, oldValue, this.getMajor());
    }

    /** return the minor number of the version
     *	1.<b>2</b>.3
     *  @return an integer
     */
    public int getMinor()
    {   return minor; }

    /** set the minor number of the version
     *	1.<b>2</b>.3
     *  @param minor an integer
     *
     *  @exception IllegalArgumentException if minor is less than 0
     */
    public void setMinor(int minor)
    {   if ( minor < 0 )
            throw new IllegalArgumentException("minor is to be greater or equal to 0");
        int oldValue = this.getMinor();
        this.minor = minor;
        
        this.support.firePropertyChange(PROPERTY_MINOR, oldValue, this.getMinor());
    }

    /** return the revision number of the version
     *	1.2.<b>3</b>
     *  @return an integer
     */
    public int getRevision()
    {   return revision; }

    /** set the revision number of the version
     *
     *	1.2.<b>3</b>
     *  @param revision an integer
     *
     *  @exception IllegalArgumentException if revision is less than 0
     */
    public void setRevision(int revision)
    {   if ( revision < 0 )
            throw new IllegalArgumentException("revision is to be greater or equal to 0");
        int oldValue = this.getRevision();
        this.revision = revision;
        
        this.support.firePropertyChange(PROPERTY_REVISION, oldValue, this.getRevision());
    }

    /** return the micro number of the version
     *	1.2.<b>3</b>
     *  @return an integer
     *
     *	@see getRevision
     */
    public int getMicro()
    {   return this.getRevision(); }

    /** set the micro number of the version
     *
     *	1.2.<b>3</b>
     *  @param revision an integer
     *
     *  @exception IllegalArgumentException if micro is less than 0
     *	@see setRevision(int revision)
     */
    public void setMicro(int micro)
    {	this.setRevision(micro); }
    
    /** return true if the version indicates that the related product is stable
     *  @return true if the version indicates that the related product is stable
     */
    public boolean isStable()
    {   return (this.getMinor() % 2 == 0); }
    
    /** comparable implementation */
    public int compareTo(Version o)
    {   int result = 0;
        
        if ( o == null )
        {   result = 1; }
        else
        {   result = this.getMajor() - o.getMajor();
            if ( result == 0 )
            {   result = this.getMinor() - o.getMinor();
                if ( result == 0 )
                {   result = this.getRevision() - o.getRevision(); }
            }
        }
        
        return result;
    }
    
    public boolean equals(Object o)
    {   boolean result = false;
        
        if ( o instanceof Version )
        {   Version other = (Version)o;
            
            result = this.compareTo(other) == 0;
        }
        
        return result;
    }

    @Override
    public int hashCode()
    {
	return this.getMajor() + this.getMinor() + this.getRevision();
    }
    
    public String toString()
    {   StringBuffer buffer = new StringBuffer();
        buffer.append(this.getMajor() + ".");
        buffer.append(this.getMinor() + ".");
        buffer.append(this.getRevision());
        
        return buffer.toString();
    }
    
    /** ########################################################################
     *  ####################### PropertyChange methods #########################
     *  ######################################################################## */
    
    /** add a new PropertyChangeListener
     *  @param listener a PropertyChangeListener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {   this.support.addPropertyChangeListener(listener); }
    
    /** add a new PropertyChangeListener
     *  @param propertyName the name of the property
     *  @param listener a PropertyChangeListener
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {   this.support.addPropertyChangeListener(propertyName, listener); }
    
    /** remove a new PropertyChangeListener
     *  @param listener a PropertyChangeListener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {   this.support.removePropertyChangeListener(listener); }
    
    /** remove a new PropertyChangeListener
     *  @param propertyName the name of the property
     *  @param listener a PropertyChangeListener
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
    {   this.support.removePropertyChangeListener(propertyName, listener); }
    
    /** ########################################################################
     *  ############################ static items ##############################
     *  ######################################################################## */
    
    /** create the set of allowed separators
     *  @return a set of Character (do not returns null )
     */
    private static Set<Character> createAllowedSeparatorsList()
    {   Set<Character> set = new HashSet<Character>(4);
        
        set.add('.');
        set.add('-');
        set.add('_');
        set.add(' ');
        
        return set;
    }
    
    /** method that parse a String to modify a Version
     *  @param s a String
     *  @param version the version to modify
     *
     *  @exception VersionFormatException
     */
    public static void parse(String s, Version version) throws VersionFormatException
    {   if ( version == null )
            throw new IllegalArgumentException("version must not be null");
        
        if ( s == null )
            throw new VersionFormatException(null);
        else
        {   int[] numbers = new int[]{0, 0, 0};

            int numbersIndex = 0;
            int currentIndex = 0;
            
            /* PENDING : a revoir pour etre plus efficace */
            StringBuffer buffer = new StringBuffer();

            for(int i = 0; i < s.length(); i++)
            {   char currentChar = s.charAt(i);

                boolean isSeparator = ALLOWED_SEPARATORS.contains(currentChar);
                boolean parseBuffer = false;

                if ( isSeparator )
                {   /* consecutive separators */
                    if ( buffer.length() > 0 )
                    {   /* parse the current number */
                        parseBuffer = true;
                    }
                }
                else
                {   buffer.append(currentChar);

                    /* it is the end of the String to parse */
                    if ( i == s.length() - 1 )
                        parseBuffer = true;
                }

                if ( parseBuffer )
                {   try
                    {   int value = Integer.parseInt(buffer.toString());

                        if ( numbersIndex > 2 )
                            throw new VersionFormatException(s);
                        numbers[numbersIndex++] = value;

                        /* clear buffer */
                        buffer = new StringBuffer();
                    }
                    catch(NumberFormatException e)
                    {   throw new VersionFormatException(s); }
                }
            }

            /** create and feed Version */
            version.setMajor(numbers[0]);
            version.setMinor(numbers[1]);
            version.setRevision(numbers[2]);
        }
    }
    
    /** method that parse a String to provide a Version
     *  @param s a String
     *  @return a Version
     *
     *  @exception VersionFormatException
     */
    public static Version parse(String s) throws VersionFormatException
    {   Version version = new Version();
        
        Version.parse(s, version);
        
        return version;
    }
    
    /** exception that is throwed when trying to parse an invalid string representation of a version */
    public static class VersionFormatException extends IllegalArgumentException
    {
        /** create a new VersionFormatException
         *  @param toParse the String that caused this exception to be created
         */
        public VersionFormatException(String toParse)
        {   super("unable to parse " + (toParse == null ? null : "'" + toParse + "'")); }
    }
    
}
