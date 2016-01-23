/* =============================================================================
 * Siberia launcher
 * =============================================================================
 *
 * Project Lead:  Alexis Paris
 *
 * (C) Copyright 2008, by Alexis Paris.
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
package org.siberia;

import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import org.java.plugin.boot.Application;
import org.java.plugin.boot.ApplicationPlugin;

/**
 *
 * @author alexis
 */
public class SiberiaLauncher
{   
    /** soft reference to the application plugin */
    public static SoftReference<Application> applicationRef = null;
    
    /**
     * Call this method to start/stop application.
     * @param args command line arguments, not interpreted by this method but
     *             passed to
     *             {@link ApplicationPlugin#initApplication(ExtendedProperties, String[])}
     *             method
     */
    public static void main(final String[] args)
    {   
	try
	{   org.java.plugin.boot.Boot.main(args);
	    
	    if ( applicationRef != null )
	    {
		Application app = applicationRef.get();
		
		if ( app != null )
		{
		    /* acces via introspection to avoid a dependency from launcher to siberia-platform :-( */
		    
		    try
		    {
			Method m = app.getClass().getMethod("applicationStarted", (Class[])null);
			
			if ( m != null )
			{
			    m.invoke(app, (Object[])null);
			}
		    }
		    catch(Exception e)
		    {
			e.printStackTrace();
		    }
		}
	    }
	}
	catch(Exception e)
	{   e.printStackTrace(); }
	
    }
    
}
