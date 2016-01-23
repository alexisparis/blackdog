package org.siberia.type.graph.figure;

import java.awt.Color;
import java.beans.PropertyVetoException;
import org.siberia.type.SibBoolean;
import org.siberia.type.SibColor;
import org.siberia.type.SibString;
import org.siberia.type.SibType;

/**
 *
 * Class which provide fillable support
 *
 * @author alexis
 */
public class FillableSupport implements Fillable
{
    /** is filled */
    private SibBoolean isFillable = null;
    
    /** fill color */
    private SibColor   fillColor  = null;
    
    /** Creates a new instance of FillableSupport
     *  @param parent an SibType instance which will represent the parent of inner elements 
     */
    public FillableSupport(SibType parent)
    {   this.isFillable = new SibBoolean();
        
        this.isFillable.setParent(parent);
        try
        {   this.isFillable.setName("fillable"); }
        catch(PropertyVetoException e)
        {   e.printStackTrace(); }
        
        try
        {   this.isFillable.setValue(true); }
        catch(PropertyVetoException e)
        {   e.printStackTrace(); }
        
        this.fillColor  = new SibColor();
        
        this.fillColor.setParent(parent);
        try
        {   this.fillColor.setName("fill color"); }
        catch(PropertyVetoException e)
        {   e.printStackTrace(); }
        
        try
        {   this.fillColor.setValue(Color.BLACK); }
        catch(PropertyVetoException e)
        {   e.printStackTrace(); }
    }
    
    /** set the parent of the inner element
     *  @param parent an SibType instance which will represent the parent of inner elements 
     */
    public void setParent(SibType parent)
    {   this.isFillable.setParent(parent);
        this.fillColor.setParent(parent);
    }
    
    /** return the fill color 
     *  @return an instance of Sibcolor
     */
    public SibColor getFillColor()
    {   return this.fillColor; }

    /** initialize the fill color 
     *  @param color a color
     */
    public void setFillColor(Color color) throws PropertyVetoException
    {   this.fillColor.setValue(color); }
    
    /** set fill hability
     *  @param fill true if the figure can be filled
     */
    public void setFillable(boolean fill) throws PropertyVetoException
    {   this.isFillable.setValue(fill); }
    
    /** return fill hability
     *  @return true if the figure can be filled
     */
    public boolean isFillable()
    {   return ( (Boolean)this.isFillable.getValue() ).booleanValue(); }
    
}
