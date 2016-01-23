
package org.siberia.type;

import java.awt.Color;
import java.beans.PropertyVetoException;
import org.siberia.SiberiaGraphicsPlugin;
import org.siberia.type.annotation.bean.Bean;

/**
 * class representing an html color
 *
 *  @author alexis
 */
@Bean(  name="color",
        internationalizationRef="org.siberia.rc.i18n.type.SibColor",
        expert=false,
        hidden=false,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public class SibColor extends SibBaseType<Color>
{
    /** create a new instanceof ColdString */
    public SibColor()
    {   super(); }
    
    /** 
     * added to resolve introspection error when using classes bean habilities 
     *
     * initialize the value of the instance
     *  @param value 
     */
    public void setValue(Color value) throws PropertyVetoException
    {
        super.setValue(value);
    }
    
    /** 
     * added to resolve introspection error when using classes bean habilities
     *
     *  initialize the value of the instance
     *  @param value 
     */
    public Color getValueImpl() throws PropertyVetoException
    {   return this.getValue(); }
    
    /** return true if the value is valid
     *  @param value a value
     *  @return true if the value is valid
     */
    private boolean isValid(String value)
    {   boolean valid = false;
        if ( value != null )
        {   String trimmed = value.trim();
            if ( trimmed.length() == 7 )
            {   if ( trimmed.charAt(0) == '#' )
                {   for(int i = 1; i < trimmed.length(); i++)
                    {   char c = trimmed.charAt(i);
                        if ( c < '0' || c > 'F' )
                            return false;
                    }
                    valid = true;
                }
            }
        }
        return valid;
    }

//    /** initialize the value of the instance
//     *  @param value 
//     */
//    public void setValue(String value)
//    {   /** verify if the value is valid */
//        boolean currentValid = this.isValid();
//        boolean nextValid    = this.isValid(value);
//        
//        if ( nextValid )
//        {   super.setValue(value); }
//        else // stay on the current value if it is valid else set default value
//        {   if ( ! currentValid )
//                super.setValue(defaultValue());
//        }
//    }
//
//    /** return the value of the instance ( this value is always validated )
//     *  @return an instance of String
//     */
//    public String getValue()
//    {   boolean currentValid = this.isValid();
//        if ( ! currentValid )
//            this.setValue(defaultValue());
//        return super.getValue();
//    }
    
    /** return an integer in [0, 255] representing the red composant
     *  @return an integer in [0, 255] representing the red composant
     */
    public int getRed()
    {   int value = 0;
        if( this.getValue() != null )
            value = this.getValue().getRed();
        return value;
    }
    
//    /** set the red composant
//     *  @param red an integer in [0, 255] representing the red composant
//     */
//    public void setRed(int red) throws PropertyVetoException
//    {   Color color = this.getValue();
//        if ( color == null )
//            color = new Color(red, 0, 0);
//        this.get
//        if (  )
//        this.setColorValue(0, red); }
    
    /** return an integer in [0, 255] representing the green composant
     *  @return an integer in [0, 255] representing the green composant
     */
    public int getGreen()
    {   int value = 0;
        if( this.getValue() != null )
            value = this.getValue().getGreen();
        return value;
    }
    
//    /** set the green composant
//     *  @param green an integer in [0, 255] representing the green composant
//     */
//    public void setGreen(int green) throws PropertyVetoException
//    {   this.setColorValue(1, green); }
    
    /** return an integer in [0, 255] representing the blue composant
     *  @return an integer in [0, 255] representing the blue composant
     */
    public int getBlue()
    {   int value = 0;
        if( this.getValue() != null )
            value = this.getValue().getBlue();
        return value;
    }
    
//    /** set the blue composant
//     *  @param blue an integer in [0, 255] representing the blue composant
//     */
//    public void setBlue(int blue) throws PropertyVetoException
//    {   this.setColorValue(2, blue); }
}
