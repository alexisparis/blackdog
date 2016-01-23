/* 
 * Siberia binding : siberia plugin defining persistence services
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
package org.siberia.binding.impl;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.siberia.binding.FileBindingManager;
import org.siberia.binding.constraint.FileBindingConstraint;
import org.siberia.binding.exception.LoadException;
import org.siberia.binding.exception.SaveException;

/**
 *
 * BindingManager that support files to bind SibType instance
 *
 * @author alexis
 */
public abstract class AbstractFileBindingManager implements FileBindingManager
{
    /** soft reference to an Xml FileFilter */
    private SoftReference<FileFilter> filterReference        = new SoftReference<FileFilter>(null);
    
    /** set of extensions allowed */
    protected Set<String>             allowedExtensions      = null;
    
    /** description of allowed files */
    private String                  allowedFileDescription = null;
    
    /** create a new FileBindingManager */
    public AbstractFileBindingManager()
    {   this((String[])null); }
    
    /** create a new FileBindingManager
     *  @param extensions an array of allowed extensions
     */
    public AbstractFileBindingManager(String... extensions)
    {   
        if ( extensions != null )
        {   for(int i = 0; i < extensions.length; i++)
            {   String current = extensions[i];
                
                if ( current != null )
                {   if ( this.allowedExtensions == null )
                    {   this.allowedExtensions = new HashSet<String>(); }
                    
                    this.allowedExtensions.add(current);
                }
            }
        }
    }
    
    /** method that dispose the binding manager */
    public void dispose()
    {   /* do nothing */ }
    
    /** save objects
     *  @param types an array of Object
     *
     *  @exception SaveException if errors occured
     */
    public void store(Object... types) throws SaveException
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        
        if ( types != null )
        {   for(int i = 0; i < types.length; i++)
            {   Object type = types[i];
                
                int result = chooser.showSaveDialog(null);

                if ( result == JFileChooser.APPROVE_OPTION )
                {   File f = chooser.getSelectedFile();

                    if ( ! f.exists() )
                    {   boolean createSuccessful = false;
                        Exception ex = null;
                        try
                        {   createSuccessful = f.createNewFile(); }
                        catch(IOException e)
                        {   ex = e; }

                        if ( ! createSuccessful )
                        {   throw new SaveException(ex); }
                    }

                    this.store(type, f);
                }
            }
        }
    }
    
    /** load objects
     *  @param constraints an array of BindingConstraint that allow to filter objects to load
     *	@return an array of Objects resulting from the loading process
     *
     *  @exception LoadException if errors occured
     */
    public Object[] load(FileBindingConstraint... constraints) throws LoadException
    {
	Object[] types = null;
        
	if ( constraints != null )
	{
	    types = new Object[constraints.length];
	    
	    for(int i = 0; i < constraints.length; i++)
	    {
		FileBindingConstraint currentCst = constraints[i];
		
		if ( currentCst != null )
		{
		    File f = currentCst.getFile();
		    
		    if ( f == null || ! f.exists() || ! f.isFile() )
		    {
			throw new LoadException(null);
		    }
		    
		    types[i] = this.load(f);
		}
	    }
	}
	
	return types;
    }
    
    /* #########################################################################
     * ########################### abstract methods ############################
     * ######################################################################### */
    
    /** methods to save the instance in a File
     *  @param type an Object
     *  @param file an existing File
     *
     *  @exception SaveException if errors occured
     */
    protected abstract void store(Object type, File file) throws SaveException;
    
    /** methods to save the instance in a File
     *  @param type an Object
     *  @param file an existing File
     *
     *  @exception LoadException if errors occured
     */
    protected abstract Object load(File file) throws LoadException;
    
    /* #########################################################################
     * ###################### FileFilter considerations ########################
     * ######################################################################### */
    
    /** return a set of extension to use to create the FileFilter
     *  @return a Set of String representing file extensions (".xml", ".pdf", etc..)
     */
    protected Set<String> getAllowedExtensions()
    {   return this.allowedExtensions; }

    /** return a description of which kind of files is allowed
     *  @return a String
     */
    public String getAllowedFileDescription()
    {   return allowedFileDescription; }

    /** initialize the description of which kind of files is allowed
     *  @param allowedFileDescription a String
     */
    public void setAllowedFileDescription(String allowedFileDescription)
    {   this.allowedFileDescription = allowedFileDescription; }
    
    /** abstract method which allow to create a customized FileFilter
     *  @return a FileFilter or null if no FileFilter shall be used for the type
     */
    protected FileFilter createFilter()
    {   FileFilter filter = this.filterReference.get();
        
        if ( filter == null )
        {   filter = new FileFilter()
            {
                public boolean accept(File pathname)
                {   boolean accept = false;
                    
                    Set<String> extensions = getAllowedExtensions();
                    if ( extensions != null && pathname != null )
                    {   Iterator<String> it = extensions.iterator();
                        
                        while(it.hasNext())
                        {   String currentExtension = it.next();
                            
                            if ( pathname.getAbsolutePath().endsWith(currentExtension) )
                            {   accept = true;
                                break;
                            }
                        }
                    }
                    
                    return accept;
                }

                /**
                 * The description of this filter. For example: "JPG and GIF Images"
                 * @see FileView#getName
                 */
                public String getDescription()
                {   return getAllowedFileDescription(); }
            };
            
            /** create reference */
            this.filterReference = new SoftReference<FileFilter>(filter);
        }
        
        return filter;
    }
}
