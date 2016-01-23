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
package org.siberia.trans.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import javax.xml.bind.JAXBException;
import org.siberia.xml.schema.pluginarch.ObjectFactory;
import org.siberia.xml.schema.pluginarch.Repositories;
import org.siberia.xml.schema.pluginarch.Repository;
import org.siberia.xml.schema.pluginarch.Module;
import org.siberia.xml.schema.pluginarch.UrlRepository;
import org.siberia.utilities.xml.exception.SchemaValidationNotSupportedException;


/**
 *
 * Class that allow marshalling and unmarshalling file related to existing siberia xsd files
 *
 * @author alexis
 */
public class JAXBLoader extends org.siberia.utilities.xml.JAXBLoader
{       
    /** soft reference on an ObjectFactory */
    private SoftReference<ObjectFactory> factoryRef = new SoftReference<ObjectFactory>(null);
    
    /** create a new JAXBLoader */
    public JAXBLoader()
    {   super(JAXBLoader.class.getClassLoader()); }
    
    /* #########################################################################
     * ######################### Plugin unmarshaller ###########################
     * ######################################################################### */

    /** load repositories declaration
     *  @param stream an InputStream
     *  @return a Repositories
     */
    public Repositories loadRepositories(InputStream stream) throws JAXBException
    {   Object result = unmarshal(Repositories.class, stream);
        if ( result instanceof Repositories )
            return (Repositories)result;
        return null;
    }
    
    /** method that return the url of the xsd file that declare the given class
     *  this method is used to be able to check the validity of an xml file
     *  @param jaxbClass a class
     *  @return the url of an xsd file or null
     */
    protected URL getXsdUrlForClass(Class jaxbClass)
    {
        return this.getClass().getClassLoader().getResource("xsd/PluginArchitecture.xsd");
    }
    
    /** check that the given stream contains a valid Repositories declaration
     *  @param stream an InputStream
     *  @return true if the given stream contains a valid Repositories declaration
     */
    public boolean checkRepositoriesValidity(InputStream stream)
    {
        boolean result = false;
        
        try
        {   
            result = checkValidity(Repositories.class, stream);
        }
        catch(SchemaValidationNotSupportedException e)
        {
            e.printStackTrace();
        }
        
        return result;
    }
    
    /** save repositories declaration
     *  @param repositories a Repositories
     *  @param file a File
     */
    public void saveRepositories(Repositories repositories, File file) throws JAXBException, IOException
    {   this.marshal(Repositories.class, repositories, file); }
    
    /** save module declaration
     *  @param module a Module
     *  @param file a File
     */
    public void saveModule(Module module, File file) throws JAXBException, IOException
    {   this.marshal(Module.class, module, file); }
    
    /** save repository declaration
     *  @param repository a Repository
     *  @param file a File
     */
    public void saveRepository(Repository repository, File file) throws JAXBException, IOException
    {   this.marshal(Repository.class, repository, file); }

    /** create an empty Repositories declaration in the given file
     *  @param file a File
     */
    public void createDefaultRepositories(File file) throws JAXBException, IOException
    {
	this.createDefaultRepositories(file, true);
    }

    /** create an empty Repositories declaration in the given file
     *  @param file a File
     *	@param useDefaultRepositories true to indicate that if the configuration file is missing, then 
     *		the configuration file have to be generated with the default repositories
     */
    public void createDefaultRepositories(File file, boolean useDefaultRepositories) throws JAXBException, IOException
    {   ObjectFactory factory = this.factoryRef.get();
        if ( factory == null )
        {   factory = new ObjectFactory();
            this.factoryRef = new SoftReference<ObjectFactory>(factory);
        }
        
        Repositories reps = factory.createRepositories();
        
	if ( useDefaultRepositories )
	{
	    UrlRepository decl = factory.createUrlRepository();
	    decl.setValue("http://alexis.paris.perso.cegetel.net/siberia/");
	    reps.getUrlRepository().add(decl);
	}
            
        this.marshal(Repositories.class, reps, file);
    }

    /** load a repository declaration
     *  @param stream an InputStream
     *  @return a Repository
     */
    public Repository loadRepository(InputStream stream) throws JAXBException
    {   Object result = unmarshal(Repository.class, stream);
        if ( result instanceof Repository )
            return (Repository)result;
        return null;
    }

    /** load a Module
     *  @param stream an InputStream
     *  @return a Module
     */
    public Module loadModule(InputStream stream) throws JAXBException
    {   Object result = unmarshal(Module.class, stream);
        if ( result instanceof Module )
            return (Module)result;
        return null;
    }

}
