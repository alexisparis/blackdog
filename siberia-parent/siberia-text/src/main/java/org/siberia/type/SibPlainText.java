package org.siberia.type;

import org.siberia.SiberiaTextPlugin;
import org.siberia.type.annotation.bean.Bean;
/**
 * Representation of a plain text
 *
 * @author alexis
 */
@Bean(  name="plain text",
        internationalizationRef="org.siberia.rc.i18n.type.SibPlainText",
        expert=false,
        hidden=false,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public class SibPlainText extends SibText
{
    /** create a new instanceof ColdPlainText */
    public SibPlainText()
    {   super();
        this.setSupportStyle(false);
    }
}
