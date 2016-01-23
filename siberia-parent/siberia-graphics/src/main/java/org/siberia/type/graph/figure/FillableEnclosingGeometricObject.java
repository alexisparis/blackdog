package org.siberia.type.graph.figure;

import java.awt.Color;
import java.beans.PropertyVetoException;
import org.siberia.type.SibColor;
import org.siberia.type.SibType;
import org.siberia.type.graph.EnclosingFigure;
import org.siberia.type.graph.VisualObject;

public class FillableEnclosingGeometricObject extends GeometricObject implements Fillable,
                                                     EnclosingFigure
{
    /** fill support */
    protected FillableSupport fillSupport     = new FillableSupport(this);
    
    /** is enclosing figure */
    private   boolean         enclosingFigure = false;
    
    /** create a new FillableEnclosingGeometricObject */
    public FillableEnclosingGeometricObject()
    {   super(); }
    
    /** return a new VisualObject which proportions changed
     *  @param ratioX the X ratio for the proportions
     *  @param ratioY the Y ratio for the proportions
     **/
    public VisualObject createWithProportions(float ratioX, float ratioY)
    {   FillableEnclosingGeometricObject fig = (FillableEnclosingGeometricObject)this.clone();
        
        fig.setOrigin((int)(this.getX().getValue().intValue() * ratioX),
                         (int)(this.getY().getValue().intValue() * ratioY));
        fig.setSize((int)(this.getWidth().getValue().intValue() * ratioX),
                       (int)(this.getHeight().getValue().intValue() * ratioY));
        try
        {   fig.setFillable(this.isFillable()); }
        catch (PropertyVetoException ex)
        {   ex.printStackTrace(); }
        
        try
        {   fig.setFillColor(this.getFillColor().getValue()); }
        catch (PropertyVetoException ex)
        {   ex.printStackTrace(); }
        
        try
        {   fig.setBorderColor(this.getBorderColor().getValue()); }
        catch (PropertyVetoException ex)
        {   ex.printStackTrace(); }
        
        try
        {   fig.setLineWidth(this.getLineWidth().getValue().intValue()); }
        catch (PropertyVetoException ex)
        {   ex.printStackTrace(); }
        
        return fig;
    }
    
    /** return for an abstract figure the X min which it takes to display correctly
     *  @return the X min which it takes to display correctly
     **/
    public int getXMin()
    {   return this.getX().getValue().intValue() + (int) (this.getWidth().getValue().intValue()) / 2; }
    
    
    /** return for an abstract figure the Y min which it takes to display correctly
     *  @return the Y min which it takes to display correctly
     **/
    public int getYMin()
    {   return this.getY().getValue().intValue() + (int) (this.getHeight().getValue().intValue()) / 2; }
    
    /** create another instance
     *  @return a clone
     */
    public Object clone()
    {   FillableEnclosingGeometricObject other = (FillableEnclosingGeometricObject)super.clone();
        try
        {   other.setFillColor(this.getFillColor().getValue());
            other.setFillable(this.isFillable());
            other.fillSupport.setParent(other);
        }
        catch (PropertyVetoException ex)
        {   ex.printStackTrace(); }
        
        other.setEnclosingFigure(this.isEnclosingFigure());
        
        return other;
    }
    
    public boolean equals(Object t)
    {   if ( ! super.equals(t) ) return false;
        if ( t != null )
        {   if ( t instanceof SibGOval )
            {   SibGOval other = (SibGOval)t;
                if ( this.isFillable() == other.isFillable() )
                {   if ( this.isFillable() )
                    {   if ( this.getFillColor().equals(other.getFillColor()) )
                            return true;
                    }
                    else
                        return true;
                }
            }
        }
        return false;
    }
    
    /* #########################################################################
     * ###################### Fillable implementation ##########################
     * ######################################################################### */
    
    /** return the fill color 
     *  @return an instance of Sibcolor
     */
    public SibColor getFillColor()
    {   return this.fillSupport.getFillColor(); }

    /** initialize the fill color 
     *  @param color a color
     */
    public void setFillColor(Color color) throws PropertyVetoException
    {   this.fillSupport.setFillColor(color); }
    
    /** set fill hability
     *  @param fill true if the figure can be filled
     */
    public void setFillable(boolean fill) throws PropertyVetoException
    {   this.fillSupport.setFillable(fill); }
    
    /** return fill hability
     *  @return true if the figure can be filled
     */
    public boolean isFillable()
    {   return this.fillSupport.isFillable(); }
    
    /* #########################################################################
     * ################## EnclosingFigure implementation #######################
     * ######################################################################### */

    public boolean isEnclosingFigure()
    {   return enclosingFigure; }

    public void setEnclosingFigure(boolean enclosingFigure)
    {   this.enclosingFigure = enclosingFigure; }
    
}
