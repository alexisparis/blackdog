package org.siberia.substance;

import org.jvnet.substance.SubstanceLookAndFeel;

/**
 *
 * @author alexis
 */
public class BusinessLookAndFeel extends SubstanceLookAndFeel
{
    
    /** Creates a new instance of BusinessLookAndFeel */
    public BusinessLookAndFeel()
    {   super();
        
        this.setSkin(new org.jvnet.substance.skin.BusinessSkin());
    }
    
}
