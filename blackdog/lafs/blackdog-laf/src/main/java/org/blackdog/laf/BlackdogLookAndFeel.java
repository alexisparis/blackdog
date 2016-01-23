/*
 * BlackdogLookAndFeel.java
 *
 * Created on 23 janvier 2008, 22:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.blackdog.laf;

import java.net.URL;
import org.blackdog.BlackdogLafPlugin;
import org.siberia.ExternalizedPlasticLookAndFeel;
import org.siberia.ResourceLoader;

/**
 *
 * @author alexis
 */
public class BlackdogLookAndFeel extends ExternalizedPlasticLookAndFeel
{
    /**
     * Creates a new instance of BlackdogLookAndFeel
     */
    public BlackdogLookAndFeel() throws Exception
    {
	super("Blackdog",
	      ResourceLoader.getInstance().getRcResource(BlackdogLafPlugin.PLUGIN_ID +";1::laf/BlackdogTheme.properties").openStream());
    }
    
}
