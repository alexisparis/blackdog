package org.siberia.type.graph.figure;

import java.beans.PropertyVetoException;
import org.siberia.type.AbstractSibType;
import org.siberia.type.graph.VisualObject;
import org.siberia.type.graph.SibGPoint;

/** 
 *  class representing a line
 *
 *  @author alexis
 */
public class SibGLine extends VisualObject
{
    /** A point */
    private SibGPoint pointA;
    
    /** B point */
    private SibGPoint pointB;
    
    /** create a new instance of Line */
    public SibGLine()
    {   super(); }
    
    /** return for an abstract figure the X min which it takes to display correctly
     *  @return the X min which it takes to display correctly
     **/
    public int getXMin()
    {   return java.lang.Math.max(this.pointA.getX().getValue().intValue(),
                                  this.pointB.getX().getValue().intValue()); }
    
    
    /** return for an abstract figure the Y min which it takes to display correctly
     *  @return the Y min which it takes to display correctly
     **/
    public int getYMin()
    {   return java.lang.Math.max(this.pointA.getY().getValue().intValue(),
                                  this.pointB.getY().getValue().intValue()); }
    
    /** return a new VisualObject which proportions changed
     *  @param ratioX the X ratio for the proportions
     *  @param ratioY the Y ratio for the proportions
     **/
    public VisualObject createWithProportions(float ratioX, float ratioY)
    {   SibGLine newLine = (SibGLine)this.clone();
        
        newLine.setPointA((int)(this.getPointA().getX().getValue().intValue() * ratioX),
                          (int)(this.getPointA().getY().getValue().intValue()* ratioY));
        newLine.setPointB((int)(this.getPointB().getX().getValue().intValue() * ratioX),
                          (int)(this.getPointB().getY().getValue().intValue() * ratioY));
        return newLine;
    }
    
    /** return the A point
     *  @return an instance of SibGPoint
     */
    public SibGPoint getPointA()
    {   return this.pointA; }
    
    /** return the B point
     *  @return an instance of SibGPoint
     */
    public SibGPoint getPointB()
    {   return this.pointB; }
    
    /** initialize A point
     *  @param p an instance of SibGPoint
     */
    public void setPointA(SibGPoint p)
    {   if ( p != null )
            this.setPointA(p.getX().getValue().intValue(), p.getY().getValue().intValue());
    }
    
    /** initialize B point
     *  @param p an instance of SibGPoint
     */
    public void setPointB(SibGPoint p)
    {   if ( p != null )
            this.setPointB(p.getX().getValue().intValue(), p.getY().getValue().intValue());
    }
    
    /** initialize A point
     *  @param x the x position of point A
     *  @param y the y position of point A
     */
    public void setPointA(int x, int y)
    {   
        try
        {   this.pointA.setX(x);
            this.pointA.setY(y);
        }
        catch (PropertyVetoException ex)
        {   ex.printStackTrace(); }
    }
    
    /** initialize B point
     *  @param x the x position of point B
     *  @param y the y position of point B
     */
    public void setPointB(int x, int y)
    {   
        try
        {   this.pointB.setX(x);
            this.pointB.setY(y);
        }
        catch (PropertyVetoException ex)
        {   ex.printStackTrace(); }
    }
    
    /** add a point to the line<br>
     *  if point A is not initialized, then the given x and y positions will be assigned to point A, else<br>
     *  x and y represent point B position
     *  @param x the x position
     *  @param y the y position
     */
    public boolean addPoint(int x, int y)
    {   if (this.pointA.isAssignated())
        {   if (this.pointB.isAssignated()) return false;
            this.setPointB(x, y);
            return true;
        }
        this.setPointA(x, y);
        return true;
    }
}
