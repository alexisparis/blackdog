package org.siberia.type;

import java.awt.Font;
import java.beans.PropertyVetoException;
import org.siberia.SiberiaGraphicsPlugin;
import org.siberia.type.annotation.bean.Bean;

/**
 *
 * Class representing a font 
 *
 * @author alexis
 */
@Bean(  name="font",
        internationalizationRef="org.siberia.rc.i18n.type.SibFont",
        expert=false,
        hidden=false,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public class SibFont extends SibBaseType<Font>
{
    /** default size */
    public static final int DEFAULT_SIZE = 8;
    
    /** Creates a new instance of ColdFont */
    public SibFont()
    {   super();
        
        try
        {   this.setValue(new Font(Font.SERIF, Font.PLAIN, DEFAULT_SIZE)); }
        catch (PropertyVetoException ex)
        {   ex.printStackTrace(); }
    }
    
    /** 
     * added to resolve introspection error when using classes bean habilities 
     *
     * initialize the value of the instance
     *  @param value 
     */
    public void setValue(Font value) throws PropertyVetoException
    {
        super.setValue(value);
    }
    
    /** 
     * added to resolve introspection error when using classes bean habilities
     *
     *  initialize the value of the instance
     *  @param value 
     */
    public Font getValueImpl() throws PropertyVetoException
    {   return this.getValue(); }
}
