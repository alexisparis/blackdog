package org.siberia.type.graph;

import java.beans.PropertyVetoException;
import org.siberia.type.SibInteger;
import org.siberia.type.AbstractSibType;

/**
 *
 * class defining an instance which has the information about a SibGPoint
 *
 * @author alexis
 */
public class SibGPoint extends VisualObject
{
    /** x position */
    private SibInteger x           = null;
    
    /** y position */
    private SibInteger y           = null;
    
    /** is x initialized */
    private boolean      assignatedX = false;
    
    /** is y initialized */
    private boolean      assignatedY = false;
    
    /** Creates a new instance of SibGPoint */
    public SibGPoint()
    {   super(); }
    
    /** return for an abstract figure the X min which it takes to display correctly
     *  @return the X min which it takes to display correctly
     **/
    public int getXMin()
    {   return ((Integer)this.x.getValue()).intValue(); }
    
    
    /** return for an abstract figure the Y min which it takes to display correctly
     *  @return the Y min which it takes to display correctly
     **/
    public int getYMin()
    {   return ((Integer)this.y.getValue()).intValue(); }
    
    /** return a new VisualObject which proportions changed
     *  @param ratioX the X ratio for the proportions
     *  @param ratioY the Y ratio for the proportions
     **/
    public VisualObject createWithProportions(float ratioX, float ratioY)
    {   SibGPoint other = new SibGPoint();
        try
        {   other.setX((int)(this.getX().getValue().intValue() * ratioX));
            other.setY((int)(this.getY().getValue().intValue() * ratioY));
        }
        catch(PropertyVetoException e)
        {   e.printStackTrace(); }
        
        return other;
    }
    
    /** indicates if position of the SibGPoint have been initialized
     *  @return true if the SibGPoint position has been initialized
     */
    public boolean isAssignated()
    {   return this.assignatedX && this.assignatedY; }
    
    /** initialize the x position
     *  @param x an SibInteger
     */
    public void setX(SibInteger x) throws PropertyVetoException
    {   this.setX(x.getValue().intValue()); }
    
    /** initialize the y position
     *  @param y an SibInteger
     */
    public void setY(SibInteger y) throws PropertyVetoException
    {   this.setY(y.getValue().intValue()); }
    
    /** initialize the x position
     *  @param x an integer
     */
    public void setX(int x) throws PropertyVetoException
    {   this.assignatedX = true;
        this.x.setValue(x);
    }
    
    /** initialize the y position
     *  @param y an integer
     */
    public void setY(int y) throws PropertyVetoException
    {   this.assignatedY = true;
        this.y.setValue(y);
    }
    
    /** return the x position
     *  @return an instance of SibInteger representing the x position
     */
    public SibInteger getX()
    {   return this.x; }
    
    /** return the y position
     *  @return an instance of SibInteger representing the y position
     */
    public SibInteger getY()
    {   return this.y; }
    
    /** create another instance
     *  @return a clone
     */
    public Object clone()
    {   SibGPoint other = (SibGPoint)super.clone();
        
        try
        {   other.setX((SibInteger)this.getX().clone());
            other.setY((SibInteger)this.getY().clone());
        }
        catch (PropertyVetoException ex)
        {   ex.printStackTrace(); }
        
        return other;
    }
    
    public boolean equals(Object t)
    {   if ( ! super.equals(t) ) return false;
        if ( t != null )
        {   if ( t instanceof SibGPoint )
            {   SibGPoint other = (SibGPoint)t;
                if ( this.getX().equals(other.getX()) )
                {   if ( this.getY().equals(other.getY()) )
                    {   return true; }
                }
            }
        }
        return false;
    }
    
}
