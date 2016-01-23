package org.siberia.substance;

import org.jvnet.substance.SubstanceLookAndFeel;

/**
 *
 * @author alexis
 */
public class SaharaLookAndFeel extends SubstanceLookAndFeel
{
    
    /** Creates a new instance of BusinessLookAndFeel */
    public SaharaLookAndFeel()
    {   super();
        
        this.setSkin(new org.jvnet.substance.skin.SaharaSkin());
    }
    
}
