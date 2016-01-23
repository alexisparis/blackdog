package org.siberia;

import org.siberia.ResourceLoader;

/**
 *
 * Static declarations about the plugin Siberia-debug
 *
 * @author alexis
 */
public class SiberiaDebugPlugin extends org.java.plugin.Plugin
{
    /**
     * Creates a new instance of SiberiaDebugPlugin
     */
    public SiberiaDebugPlugin()
    {   }
    
    protected void doStart()
    {
	ResourceLoader.getInstance().setDebugEnabled(true);
    }
    
    protected void doStop() 
    {
	
    }
    
}
