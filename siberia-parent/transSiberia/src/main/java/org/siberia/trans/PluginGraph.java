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
package org.siberia.trans;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBException;
import org.apache.log4j.Logger;
import org.siberia.trans.exception.InvalidBuildDependencyException;
import org.siberia.trans.exception.InvalidRepositoryException;
import org.siberia.trans.exception.ResourceNotFoundException;
import org.siberia.trans.exception.UnsatisfiedDependencyException;
import org.siberia.trans.type.RepositoryUtilities;
import org.siberia.trans.type.plugin.PluginBuild;
import org.siberia.trans.type.plugin.PluginDependency;
import org.siberia.trans.type.repository.SiberiaRepository;
import org.siberia.utilities.math.Matrix;

/**
 *
 * Structure allowing to manage plugins dependencies.
 * It provide methods to create a Matrix representation of the graph which helps
 * to determine if there are no cycles in plugin dependencies.
 *
 * @author alexis
 */
public class PluginGraph
{
    /** logger */
    private Logger			       logger       = Logger.getLogger(PluginGraph.class);
    
    /** list of plugin that allow to give a unique index for plugin build
     *	and to be able to have a list that contains plugin environment with all needed dependencies
     */
    private List<PluginBuild>                  builds       = new ArrayList<PluginBuild>(40);
    
    /** map linking a PluginBuild and a set of PluginBuild which are its dependencies
     *	this map could not contains PluginBuild which does not have any dependencies
     */
    private Map<PluginBuild, Set<PluginBuild>> dependencies = new HashMap<PluginBuild, Set<PluginBuild>>(40);
    
    /** trasSiberia */
    private TransSiberia                       trans        = null;
    
    /** Creates a new instance of PluginGraph
     *	@param transSiberia a TransSiberia
     *
     *	@exception IllegalArgumentException if transSiberia is null
     */
    public PluginGraph(TransSiberia transSiberia)
    {	
	if ( transSiberia == null )
	{
	    throw new IllegalArgumentException("provide a non null TransSiberia");
	}
	
	this.trans = transSiberia;
    }
    
    /** clear the graph */
    public void clear()
    {
	if ( this.builds != null )
	{
	    this.builds.clear();
	}
	if ( this.dependencies != null )
	{
	    this.dependencies.clear();
	}
    }
    
    /** return the list of PluginBuild registered
     *	@return a List of PluginBuild
     */
    List<PluginBuild> getRegisteredBuilds()
    {
	return this.builds;
    }
    
    /** register a PluginBuild and its dependencies
     *	@param build a PluginBuild
     */
    public void registerBuild(PluginBuild build) throws InvalidRepositoryException,
							ResourceNotFoundException,
							InvalidBuildDependencyException,
							UnsatisfiedDependencyException,
							IOException,
							JAXBException
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling registerBuild(" + build + ")");
	}
	
	List<PluginBuild>                  _builds       = new ArrayList<PluginBuild>(10);
	Map<PluginBuild, Set<PluginBuild>> _dependencies = new HashMap<PluginBuild, Set<PluginBuild>>(10);
	
	this.registerBuild(build, _builds, _dependencies);
	
	/** complete builds and dependencies */
	if ( _builds != null )
	{
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("builds size : " + _builds.size());
	    }
	    
	    for(int i = 0; i < _builds.size(); i++)
	    {
		PluginBuild curBuild = _builds.get(i);
		
		if ( this.builds.contains(curBuild) )
		{
		    logger.info("builds already contains : " + curBuild);
		}
		else
		{
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("adding " + curBuild + " to the list of build");
		    }
		    this.builds.add(curBuild);
		}
	    }
	}
	
	Iterator<Map.Entry<PluginBuild, Set<PluginBuild>>> it = _dependencies.entrySet().iterator();
	while(it.hasNext())
	{
	    Map.Entry<PluginBuild, Set<PluginBuild>> currentEntry = it.next();
	    
	    if ( currentEntry != null && currentEntry.getKey() != null )
	    {
		if ( ! this.dependencies.containsKey(currentEntry.getKey()) )
		{
		    this.dependencies.put(currentEntry.getKey(), currentEntry.getValue());
		}
		else
		{
		    Set<PluginBuild> actualDependencies = this.dependencies.get(currentEntry.getKey());
		    
		    actualDependencies.addAll(currentEntry.getValue());
		}
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of registerBuild(" + build + ")");
	}
    }
    
    /**
     * register a PluginBuild and its _dependencies
     * 
     * @param build a PluginBuild
     * @param _builds the list of PluginBuild to feed
     * @param _dependencies the map to feed
     */
    public void registerBuild(PluginBuild build, List<PluginBuild> _builds, Map<PluginBuild, Set<PluginBuild>> _dependencies)
						 throws InvalidRepositoryException,
							UnsatisfiedDependencyException,
							ResourceNotFoundException,
							InvalidBuildDependencyException,
							IOException,
							JAXBException
    {
	if ( build != null )
	{
	    boolean alreadyContains = _builds.contains(build);

	    if ( ! alreadyContains )
	    {
		SiberiaRepository repository = build.getRepository();
		
		if ( repository == null )
		{
		    throw new InvalidRepositoryException(null);
		}
		else
		{
		    Iterator<PluginDependency> dependencies = RepositoryUtilities.getBuildDependencies(build).iterator();
		    
		    /** for each dependency, search for the most appropriate plugin */
		    while(dependencies.hasNext())
		    {
			PluginDependency dependency = dependencies.next();
			
			if ( dependency != null )
			{
			    PluginBuild dependentBuild = this.trans.resolveDependency(dependency, repository, true);
			    
			    if ( dependentBuild == null )
			    {
				/* no build found --> cannot register ... */
				throw new UnsatisfiedDependencyException(build, dependency);
				
			    }
			    else
			    {
				/* register dependency build */
				this.registerBuild(dependentBuild, _builds, _dependencies);
				
				Set<PluginBuild> set = _dependencies.get(build);
				
				if ( set == null )
				{
				    set = new HashSet<PluginBuild>(10);
				    _dependencies.put(build, set);
				}
				
				set.add(dependentBuild);
			    }
			}
		    }
		}
		
		_builds.add(build);
	    }
	}
    }
    
    /** unregister a PluginBuild and its dependencies<br>
     *	calling this method could provoke the unregistration of the builds that have build as a dependency
     *	@param build a PluginBuild
     */
    private void unregisterBuild(PluginBuild build)
    {
	if ( this.builds != null )
	{
	    this.builds.remove(build);
	}
	if ( this.dependencies != null )
	{
	    this.builds.remove(build);
	}
	
	Iterator<Map.Entry<PluginBuild, Set<PluginBuild>>> it = this.dependencies.entrySet().iterator();
	while(it.hasNext())
	{
	    Map.Entry<PluginBuild, Set<PluginBuild>> current = it.next();
	    
	    if ( current != null )
	    {
		if ( current.getValue() != null && current.getValue().contains(build) )
		{
		    this.unregisterBuild(current.getKey());
		}
	    }
	}
    }
    
    /** return the PluginBuild at the given location
     *	@param index an integer
     *	@return a PluginBuild
     */
    public PluginBuild getBuildAt(int index)
    {
	return this.builds.get(index);
    }
    
    /** return the number of Build of the graph 
     *	@return an integer
     */
    public int getBuildCount()
    {
	return this.builds.size();
    }
    
    /** create the adjacence matrix that represents the graph of registered plugin<br>
     *	this matrix should no longer be used if some new builds were registered or some builds unregistered<br>
     *	@return a Matrix
     */
    public Matrix createAdjacenceMatrix()
    {
	Matrix result = null;
	
	if ( this.builds != null && this.builds.size() > 0 )
	{
	    /** the row and column index is related to the position of a PluginBuild in the builds list */
	    result = new Matrix(this.builds.size());
	    
	    result.fill(0);
	    
	    /** loop on all entry of dependencies to feed the matrix */
	    Iterator<Map.Entry<PluginBuild, Set<PluginBuild>>> it = this.dependencies.entrySet().iterator();
	    
	    while(it.hasNext())
	    {
		Map.Entry<PluginBuild, Set<PluginBuild>> entry = it.next();
		
		if ( entry != null )
		{
		    int rowIndex = this.builds.indexOf(entry.getKey());
		    
		    if ( rowIndex >= 0 )
		    {
			Set<PluginBuild> set = entry.getValue();
			
			if ( set != null )
			{
			    Iterator<PluginBuild> depBuilds = set.iterator();
			    
			    while(depBuilds.hasNext())
			    {
				PluginBuild currentDepBuild = depBuilds.next();
				
				if ( currentDepBuild != null )
				{
				    int columnIndex = this.builds.indexOf(currentDepBuild);
				    
				    if ( columnIndex >= 0 )
				    {
					result.set(rowIndex, columnIndex, 1);
				    }
				}
			    }
			}
		    }
		}
	    }
	    
	}
	
	return result;
    }
    
    /** print states on standard output */
    public void print()
    {
	this.print(System.out);
    }
    
    /** print states
     *	@param stream a PrintStream
     */
    public void print(PrintStream stream)
    {
	stream.println("##############");
	stream.println("graph states : ");
	
	stream.println();
	stream.println("registered builds : ");
	for(int i = 0; i < this.builds.size(); i++)
	{
	    PluginBuild build = this.builds.get(i);
	    stream.println("\ti=" + (build == null ? null : build.getName() + " (" + build.getVersion() + ")"));
	}
	
	stream.println();
	stream.println("dependencies : ");
	Iterator<PluginBuild> it = this.dependencies.keySet().iterator();
	while(it.hasNext())
	{
	    StringBuffer buffer = null;
	    
	    PluginBuild build = it.next();
	    if ( build != null )
	    {
		Set<PluginBuild> set = this.dependencies.get(build);
		
		if ( set != null )
		{
		    Iterator<PluginBuild> it2 = set.iterator();
		    
		    while(it2.hasNext())
		    {
			if ( buffer == null )
			{
			    buffer = new StringBuffer();
			    buffer.append( "\t" + (build == null ? null : build.getName() + " (" + build.getVersion() + ")") );
			    buffer.append(" depends on  ");
			}
			
			PluginBuild dependence = it2.next();
			
			buffer.append( (dependence == null ? null : dependence.getName() + " (" + dependence.getVersion() + ")") + " " );
		    }
		}
	    }
	    
	    if ( buffer != null )
	    {
		stream.println(buffer.toString());
	    }
	}
	
	stream.println();
	Matrix m = this.createAdjacenceMatrix();
	stream.println("matrix : ");
	m.print(stream);
	
	stream.println("##############");
    }
    
    
}
