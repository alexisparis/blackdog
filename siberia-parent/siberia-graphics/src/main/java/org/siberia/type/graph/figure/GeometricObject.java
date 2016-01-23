package org.siberia.type.graph.figure;

import java.beans.PropertyVetoException;
import org.siberia.type.graph.VisualObject;
import org.siberia.type.SibInteger;
import org.siberia.type.SibType;

/**
 *  Class representing a figure that could be caracterized by an origin and an height and width
 *
 *  @author alexis
 */
public abstract class GeometricObject extends VisualObject
{
    /** x origin */
    private SibInteger x;
    
    /** y origin */
    private SibInteger y;
    
    /** height */
    private SibInteger height;
    
    /** width */
    private SibInteger width;
    
        
//        this.x      = new SibInteger(this, "x", 0);
//        this.y      = new SibInteger(this, "y", 0);
//        this.height = new SibInteger(this, "height", 0);
//        this.width  = new SibInteger(this, "width", 0);
//    
//        this.addChildElement(this.x);
//        this.addChildElement(this.y);
//        this.addChildElement(this.width);
//        this.addChildElement(this.height);

    /** create a new instance of GeometricObject
     *  @param parent the parent of this instance
     */
    public GeometricObject()
    {   super(); }
    
    /** return for an abstract figure the X min which it takes to display correctly
     *  @return the X min which it takes to display correctly
     **/
    public int getXMin()
    {   return ((Integer)this.x.getValue()).intValue() + ((Integer)this.width.getValue()).intValue(); }
    
    
    /** return for an abstract figure the Y min which it takes to display correctly
     *  @return the Y min which it takes to display correctly
     **/
    public int getYMin()
    {   return ((Integer)this.y.getValue()).intValue() + ((Integer)this.height.getValue()).intValue(); }
    
    /** set the origin of the figure
     *  @param x an integer
     *  @param y an integer
     */
    public void setOrigin(int x, int y)
    {   
        try
        {   this.x.setValue(x);
            this.y.setValue(y);
        }
        catch(PropertyVetoException e)
        {   e.printStackTrace(); }
    }
    
    /** set the origin of the figure
     *  @param x an SibInteger
     *  @param y an SibInteger
     */
    public void setOrigin(SibInteger x, SibInteger y)
    {   this.setOrigin(x == null ? 0 : x.getValue().intValue(), y == null ? 0 : y.getValue().intValue()); }
    
    /** return a new VisualObject which proportions changed
     *  @param ratioX the X ratio for the proportions
     *  @param ratioY the Y ratio for the proportions
     **/
    public abstract VisualObject createWithProportions(float ratioX, float ratioY);
    
    /** set the size of the figure
     *  @param width an integer
     *  @param height an integer
     */
    public void setSize(int width, int height)
    {   
        try
        {   this.width.setValue(width);
            this.height.setValue(height);
        }
        catch(PropertyVetoException e)
        {   e.printStackTrace(); }
    }
    /** set the size of the figure
     *  @param width an SibInteger
     *  @param height an SibInteger
     */
    public void setSize(SibInteger width, SibInteger height)
    {   this.setSize(width == null ? 0 : width.getValue().intValue(), height == null ? 0 : height.getValue().intValue()); }
    
    /** return the x position of the figure
     *  @return an SibInteger
     */
    public SibInteger getX()
    {   return this.x; }
    
    /** return the y position of the figure
     *  @return an SibInteger
     */
    public SibInteger getY()
    {   return this.y; }
    
    /** return the width position of the figure
     *  @return an SibInteger
     */
    public SibInteger getWidth()
    {   return this.width; }
    
    /** return the height position of the figure
     *  @return an SibInteger
     */
    public SibInteger getHeight()
    {   return this.height; }
    
    /** create another instance
     *  @return a clone
     */
    public Object clone()
    {   GeometricObject other = (GeometricObject)super.clone();
        
        other.setOrigin((SibInteger)this.getX().clone(),(SibInteger)this.getY().clone());
        other.setSize((SibInteger)this.getWidth().clone(),(SibInteger)this.getHeight().clone());
        
        return other;
    }
    
    public boolean equals(Object t)
    {   if ( ! super.equals(t) ) return false;
        if ( t != null )
        {   if ( t instanceof GeometricObject )
            {   GeometricObject other = (GeometricObject)t;
                if ( this.getX().equals(other.getX()) )
                {   if ( this.getY().equals(other.getY()) )
                    {   if ( this.getWidth().equals(other.getWidth()) )
                        {   if ( this.getHeight().equals(other.getHeight()) )
                            {   return true; }
                        }
                    }
                }
            }
        }
        return false;
    }
}
