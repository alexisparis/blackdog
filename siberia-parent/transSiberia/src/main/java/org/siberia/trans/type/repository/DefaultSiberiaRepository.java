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
package org.siberia.trans.type.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.WeakHashMap;
import javax.xml.bind.JAXBException;
import org.apache.log4j.Logger;
import org.siberia.trans.exception.FileCheckException;
import org.siberia.trans.exception.InvalidPluginDeclaration;
import org.siberia.trans.exception.ResourceNotFoundException;
import org.siberia.trans.type.plugin.PluginBuild;
import org.siberia.trans.type.plugin.PluginStructure;
import org.siberia.type.annotation.bean.Bean;
import org.siberia.utilities.io.IOUtilities;
import org.siberia.utilities.security.check.CheckSum;
import org.siberia.utilities.task.TaskStatus;
import org.siberia.xml.schema.pluginarch.Module;

/**
 *
 * default implementation of a siberia plugin repository
 *
 * @author alexis
 */
@Bean(  name="default repository",
        internationalizationRef="org.siberia.rc.i18n.type.AbstractSiberiaRepository",
        expert=true,
        hidden=true,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public class DefaultSiberiaRepository extends AbstractSiberiaRepository
{   
    /** logger */
    private static Logger logger = Logger.getLogger(DefaultSiberiaRepository.class);
    
    /** weak hashmap linking url and local file
     *	it allow to avoid the download of a resource if it has already been downloaded
     */
    private Map<URL, File> cache = new WeakHashMap<URL, File>(40);
    
    /** Creates a new instance of DefaultSiberiaRepository
     *  @param url the url representing the repository
     */
    public DefaultSiberiaRepository()
    {   super(); }
    
    /**
	 * method that allow to copy the content of a remote file in a local temporary file
	 * 
	 * @param url the url of the file to copy to local
	 * @param status a TaskStatus
	 * @param check the method of check
	 * @return the result of the copy
	 * @exception FileCheckException if check is not null and checksum check failed
	 * @exception IOException if IOException occurs !!
	 * @exception ResourcResourceNotFoundException resource is not found
	 */
    protected File copyToLocal(URL url, TaskStatus status, CheckSum check) throws FileCheckException,
										  IOException,
										  ResourceNotFoundException
    {
	return this.copyToLocal(url, null, status, check, true);
    }
    
    /**
	 * method that allow to copy the content of a remote file at the given local location
	 * 
	 * @param url the url of the file to copy to local
	 * @param filePath the path where to copy the resulting file (null to create a temporary file)
	 * @param status a TaskStatus. could be null.
	 * @param check the method of check. if null, no check.
	 * @param failIfChecksumFailed true to indicate that a failed checksum must throw an exception of kind FileCheckException.
	 * 				    false to only provide error logs
	 * @return the result of the copy. the result is not marked to be deleted when jvm exit.
	 * @exception FileCheckException if failIfChecksumFailed and check is not null and checksum check failed
	 * @exception IOException if IOException occurs !!
	 * @exception ResourceNResourceNotFoundExceptionesource is not found
	 */
    protected File copyToLocal(URL url, String filePath, TaskStatus status, CheckSum check, boolean failIfChecksumFailed) throws FileCheckException,
																 IOException,
																 ResourceNotFoundException
    {
	if ( url == null )
	{
	    throw new IllegalArgumentException("the url could not be null");
	}
	
	File result = null;
        
        /** get the file representing the declaration of the repository */
	if ( filePath == null )
	{
	    String file = url.getFile();
	    
	    int lastSlashIndex     = file.lastIndexOf("/");
	    int lastBackSlashIndex = file.lastIndexOf("\\");
	    
	    file = file.substring( Math.max(lastSlashIndex + 1, lastBackSlashIndex + 1) );
	    
	    try
	    {	
		/** this path could not be accepted by all os, so try with an easier path
		 *  if error occur
		 */
		result = File.createTempFile("c_f_" + url.getProtocol() + "_" + 
				url.getHost() + "_" + file + "_", ".cop");
	    }
	    catch (IOException ex)
	    {
		/** try with an easier filepath */
		result = File.createTempFile("c_f_" + url.getProtocol() + "_" + 
				url.getHost() + "_" , ".cop");
	    }
	}
	else
	{
	    result = new File(filePath);
	}
	
	result.deleteOnExit();
	
	File parentFile = result.getParentFile();
	
	/** create file and parent if necessary */
	if ( ! parentFile.exists() )
	{
	    parentFile.mkdirs();
	}
	if ( ! result.exists() )
	{
	    result.createNewFile();
	}
	
	boolean checkFile = true;

	/** copy the content of the remote stream */
	try
	{
	    File cachedFile = this.cache.get(url);

	    if ( cachedFile == null )
	    {
		logger.debug("copying to local url content \"" + url + "\" to filePath : " + filePath + " with checksum : " + check);
		byte[] bytes = new byte[1*1024];
		IOUtilities.copy(url.openStream(), result, status, bytes);
	    }
	    else
	    {
		/** copy the content of cachedFile to result and avoid file check cause, it has been made before */
		if ( filePath != null )
		{
		    IOUtilities.copy(cachedFile, result);
		}
		else
		{
		    /* we use a temp file, no need to create another file */
		    result = cachedFile;
		}
		
		checkFile = false;
	    }
	}
	catch(FileNotFoundException e)
	{
	    throw new ResourceNotFoundException(e.getMessage());
	}

	if ( checkFile )
	{
	    if ( CheckSum.NONE.equals(check) )
	    {
		this.cache.put(url, result);
	    }
	    else
	    {
		logger.debug("performing check of the copy with checksum : " + check);

		/* search for the same file plus the checksum extension
		 *  if the file to download is http://perso.wanadoo.fr/alexis.paris/repository.xml
		 *  then we search for a signature file in http://perso.wanadoo.fr/alexis.paris/repository.xml.md5 if checksum is md5
		 */
		URL urlSign = new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getPath() + check.extension());

		File tmpFileSign = File.createTempFile("repository", ".xml" + check.extension());
		tmpFileSign.deleteOnExit();

		try
		{
		    IOUtilities.copy(urlSign.openStream(), tmpFileSign);
		}
		catch(FileNotFoundException e)
		{
		    ResourceNotFoundException resEx = new ResourceNotFoundException(e.getMessage());
		    resEx.setStackTrace(e.getStackTrace());
		    throw resEx;
		}

		if ( check.isValid(result, tmpFileSign) )
		{
		    /** feed map */
		    if ( result != null )
		    {
			this.cache.put(url, result);
		    }
		}
		else
		{
		    FileCheckException exception = new FileCheckException(result.getAbsolutePath(), "check (method=" + check + ") invalid for repository declaration of " + this);

		    if ( failIfChecksumFailed )
		    {
			throw exception;
		    }
		    else
		    {
			logger.error("checksum failed for file : '" + result + "' with signature file : '" + tmpFileSign + "' with checksum method : " + check,
				     exception);
		    }
		}
	    }
	}
	
	if ( result == null )
	{
            throw new ResourceNotFoundException(null);
	}
        
        return result;
    }
    
     
    
    /** copy the repository declaration at a given local location
     *  @param check a CheckSum
     *  @param status a TaskStatus
     *
     *  @return the temporarly file created
     */
    public File copyRepositoryDeclarationToLocalTemp(CheckSum check, TaskStatus status)
                                                                                  throws ResourceNotFoundException,
                                                                                         IOException,
                                                                                         FileCheckException
    {
	/** create the url that represents the repository file on this repository */
	String file = this.getURL().getPath();
	
	if ( ! file.endsWith("/") )
	{
	    file += "/";
	}
	file += "repository.xml";
	
	URL url = new URL(
		this.getURL().getProtocol(),
		this.getURL().getHost(),
		this.getURL().getPort(),
		file);
	
	return this.copyToLocal(url, status, check);
    }
    
    /** copy a plugin at a given local location
     *  @param plugin a PluginBuild
     *  @param status a TaskStatus
     *
     *  @return the temporarly file created
     */
    public File copyPluginToLocalTemp(PluginBuild plugin, TaskStatus status)
                                                                                  throws ResourceNotFoundException,
                                                                                         IOException,
                                                                                         FileCheckException,
											 InvalidPluginDeclaration
    {
	return this.copyToLocal(this.getBuildURL(plugin), status, plugin.getCheckType());
    }
    
    /** return the module declaration contains builds information
     *  @param plugin a PluginStructure
     *  @param status a TaskStatus
     *
     *  @return a Module
     */
    public Module getModuleDeclaration(PluginStructure plugin, TaskStatus status) throws ResourceNotFoundException,
                                                                                         IOException,
											 JAXBException
    {
	Module module = null;
	
	String path = this.getURL().getPath();
	
	URL url = new URL(this.getURL().getProtocol(),
			  this.getURL().getHost(),
			  this.getURL().getPort(),
			  path + File.separator + plugin.getDirectoryRelativePath() +
			  File.separator + plugin.getPluginDeclarationFilename());
	
	File f = null;
		
	try
	{
	    f = this.copyToLocal(url, status, CheckSum.NONE);
	}
	catch (FileCheckException ex)
	{
	    logger.error("unable to copy from " + url, ex);
	}

	org.siberia.trans.xml.JAXBLoader loader = new org.siberia.trans.xml.JAXBLoader();
	
	module = loader.loadModule(new FileInputStream(f));
	
	return module;
    }
    
}
