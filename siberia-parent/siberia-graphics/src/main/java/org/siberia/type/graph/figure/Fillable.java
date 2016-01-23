package org.siberia.type.graph.figure;

import java.awt.Color;
import java.beans.PropertyVetoException;
import org.siberia.type.SibColor;
/** 
 *  interface for object that has the hability to be filled
 *
 *  @author alexis
 */
public interface Fillable
{
    /** return the fill color 
     *  @return an instance of Sibcolor
     */
    public SibColor getFillColor();

    /** initialize the fill color 
     *  @param color a color
     */
    public void setFillColor(Color color) throws PropertyVetoException;
    
    /** set fill hability
     *  @param fill true if the figure can be filled
     */
    public void setFillable(boolean fill) throws PropertyVetoException;
    
    /** return fill hability
     *  @return true if the figure can be filled
     */
    public boolean isFillable();
    
}
