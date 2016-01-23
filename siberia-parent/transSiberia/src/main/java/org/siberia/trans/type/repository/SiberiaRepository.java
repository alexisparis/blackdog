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

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.bind.JAXBException;
import org.siberia.trans.exception.FileCheckException;
import org.siberia.trans.exception.InvalidPluginDeclaration;
import org.siberia.trans.exception.ResourceNotFoundException;
import org.siberia.trans.type.plugin.PluginBuild;
import org.siberia.trans.type.plugin.PluginStructure;
import org.siberia.type.SibType;
import org.siberia.type.annotation.bean.Bean;
import org.siberia.utilities.security.check.CheckSum;
import org.siberia.utilities.task.TaskStatus;
import org.siberia.xml.schema.pluginarch.Module;

/**
 *
 * Class that define a remote repository that contains siberia plugins
 *
 * @author alexis
 */
@Bean(  name="repository",
        internationalizationRef="org.siberia.rc.i18n.type.SiberiaRepository",
        expert=true,
        hidden=true,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public interface SiberiaRepository extends SibType
{
    /** property url */
    public static final String PROPERTY_URL        = "repositoryUrl";
    
    /** administrator mail */
    public static final String PROPERTY_ADMIN_MAIL = "adminMail";
    
    /** initialize the URL of the repository
     *  @param url the URL of the repository
     */
    public void setURL(URL url) throws PropertyVetoException;
    
    /** return the URL of the repository
     *  @return the URL of the repository
     */
    public URL getURL();
    
    /** initialize the mail of the repository administrator
     *  @param mail the mail of the repository administrator
     */
    public void setAdministratorMail(String mail) throws PropertyVetoException;
    
    /** return the mail of the repository administrator
     *  @return the mail of the repository administrator
     */
    public String getAdministratorMail();
    
    /** copy the repository declaration at a given local location
     *  @param check a CheckSum
     *  @param status a TaskStatus
     *
     *  @return the temporarly file created
     */
    public File copyRepositoryDeclarationToLocalTemp(CheckSum check, TaskStatus status)
                                                                                  throws ResourceNotFoundException,
                                                                                         IOException,
                                                                                         FileCheckException;
    
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
											 InvalidPluginDeclaration;
    
    /** return the module declaration contains builds information
     *  @param plugin a PluginStructure
     *  @param status a TaskStatus
     *
     *  @return a Module
     */
    public Module getModuleDeclaration(PluginStructure plugin, TaskStatus status)
                                                                                  throws ResourceNotFoundException,
                                                                                         IOException,
											 JAXBException;
    
    
    /** return the number of bytes of the given PluginBuild
     *	@param build a PluginBuild
     *	@return the number of bytes of the given PluginBuild
     */
    public int getBuildSize(PluginBuild build) throws InvalidPluginDeclaration,
						      IOException,
						      MalformedURLException;
    
}
