package org.siberia.type;

import org.siberia.SiberiaTextPlugin;
import org.siberia.type.annotation.bean.Bean;
/**
 *  Representation of a styled text (rtf)
 *
 *  @author alexis
 */
@Bean(  name="styled text",
        internationalizationRef="org.siberia.rc.i18n.type.SibStyledText",
        expert=false,
        hidden=false,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public class SibStyledText extends SibText
{
    /** create a new instanceof ColdStyledText */
    public SibStyledText()
    {   super();
        this.setSupportStyle(true);
    }
}
