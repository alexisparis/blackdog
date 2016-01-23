package org.siberia.type.graph.figure;

import java.awt.Color;
import java.beans.PropertyVetoException;
import org.siberia.type.graph.VisualObject;
import org.siberia.type.graph.SibGPoint;

import java.util.*;
import org.siberia.type.SibColor;
import org.siberia.type.graph.EnclosingFigure;
import org.siberia.type.SibList;
import org.siberia.type.SibType;
import org.siberia.type.AbstractSibType;

public class SibGPolygon extends VisualObject implements Fillable,
                                                         EnclosingFigure
{
    /** list of points */
    public  SibList       points      = null;
    
    /** fill support */
    private FillableSupport fillSupport = null;
    
    /** is enclosing figure ? */
    private boolean enclosingFigure = false;
    
    public SibGPolygon()
    {   super();
        
        this.points     = new SibList();
        this.points.setParent(this);
        
        try
        {
            this.points.setName("points");
        }
        catch(PropertyVetoException e)
        {   e.printStackTrace(); }
        
        this.points.setAllowedClass(SibGPoint.class);
        
        this.addChildElement(this.points);
        
        this.fillSupport = new FillableSupport(this);
    }
    
    /** return for an abstract figure the X min which it takes to display correctly
     *  @return the X min which it takes to display correctly
     **/
    public int getXMin()
    {   int min = 0;
        if ( this.points != null )
        {   try
            {   min = ((SibGPoint)this.points.get(0)).getX().getValue().intValue(); }
            catch(IndexOutOfBoundsException e){ return min; }

            for (int i  = 1; i < this.points.size(); i++)
            {   if( ((SibGPoint)this.points.get(i)).getX().getValue().intValue() > min )
                    min = ((SibGPoint)this.points.get(i)).getX().getValue().intValue();
            }
        }
        return min;
    }
    
    
    /** return for an abstract figure the Y min which it takes to display correctly
     *  @return the Y min which it takes to display correctly
     **/
    public int getYMin()
    {   int min = 0;
        if ( this.points != null )
        {   try
            {   min = ((SibGPoint)this.points.get(0)).getY().getValue().intValue(); }
            catch(IndexOutOfBoundsException e){ return min; }

            for (int i  = 1; i < this.points.size(); i++)
            {   if( ((SibGPoint)this.points.get(i)).getY().getValue().intValue() > min )
                    min = ((SibGPoint)this.points.get(i)).getY().getValue().intValue();
            }
        }
        return min;
    }
    
    /** return a new VisualObject which proportions changed
     *  @param ratioX the X ratio for the proportions
     *  @param ratioY the Y ratio for the proportions
     **/
    public VisualObject createWithProportions(float ratioX, float ratioY)
    {   SibGPolygon newPol = (SibGPolygon)this.clone();
        
        for (int i = 0; i < this.points.size(); i++)
        {   SibGPoint current = (SibGPoint)this.points.get(i);
            SibGPoint point = new SibGPoint();
            try
            {   point.setX((int)(current.getX().getValue().intValue() * ratioX));
                point.setY((int)(current.getY().getValue().intValue() * ratioY));
            }
            catch(PropertyVetoException e)
            {   e.printStackTrace(); }
            
            newPol.appendPoint(point);
        }
        
        try
        {   newPol.setBorderColor(this.getBorderColor().getValue());
            newPol.setLineWidth(this.getLineWidth().getValue().intValue());
            newPol.setFillable(this.isFillable());
            newPol.setFillColor(this.getFillColor().getValue());
        }
        catch (PropertyVetoException ex)
        {   ex.printStackTrace(); }
        
        return newPol;
    }
    
    /** return the list of points
     *  @return an SibList containing Point
     */
    public SibList getPoints()
    {   return this.points; }
    
    /** return an iterator over the points of the polygon
     *  @return an iterator over the points of the polygon
     */
    public Iterator<? extends SibType> points()
    {   return this.points.iterator(); }
    
    /** add a new point
     *  @param pt a new point
     */
    public boolean appendPoint(SibGPoint pt)
    {   return this.points.add(pt); }
    
    /** add a new point
     *  @param x the x point position
     *  @param y the y point position
     */
    public boolean appendPoint(int x, int y)
    {   
//        SibGPoint aux = new SibGPoint(this.points);
        SibGPoint aux = new SibGPoint();
        try
        {   aux.setX(x);
            aux.setY(y);
        }
        catch (PropertyVetoException ex)
        {   ex.printStackTrace(); }
        
        return this.points.add(aux);
    }
    
    /** return an array containing all x positions of the point owned by the polygon
     *  @return an array containing all x positions of the point owned by the polygon
     */
    public int[] getXs()
    {   int[] Xs = null;
        if ( this.points != null )
        {   Xs = new int[this.points.size()];
            for (int i = 0; i < this.points.size(); i++)
            {   Xs[i] = ( (SibGPoint) this.points.get(i) ).getX().getValue().intValue(); }
        }
        
        return Xs;
    }
    
    /** return an array containing all y positions of the point owned by the polygon
     *  @return an array containing all y positions of the point owned by the polygon
     */
    public int[] getYs()
    {   int[] Ys = null;
        if ( this.points != null )
        {   Ys = new int[this.points.size()];
            for (int i = 0; i < this.points.size(); i++)
            {   Ys[i] = ( (SibGPoint) this.points.get(i) ).getY().getValue().intValue(); }
        }
        
        return Ys;
    }
    
    /** return the number of points owned by the polygon
     *  @return the number of points owned by the polygon
     */
    public int getPointCount()
    {   return (this.points == null ? 0 : this.points.size()); }
    
    /** remove a point
     *  @param pt the point to remove
     */
    public boolean removePoint(SibGPoint pt)
    {   return this.points.remove(pt); }
    
    /** create another instance
     *  @return a clone
     */
    public Object clone()
    {   SibGPolygon other = (SibGPolygon)super.clone();
        
        Iterator it = this.points();
        if ( points != null )
        {   Object current =  null;
            while(it.hasNext())
            {   current = it.next();
                if ( current instanceof SibGPoint )
                {   other.appendPoint((SibGPoint)((SibGPoint)current).clone()); }
            }
        }
        try
        {   other.setFillable(this.isFillable());
            other.setFillColor(this.getFillColor().getValue());
        }
        catch(PropertyVetoException e)
        {   e.printStackTrace(); }
        
        other.setEnclosingFigure(this.isEnclosingFigure());
        
        return other;
    }
    
    public boolean equals(Object t)
    {   if ( ! super.equals(t) ) return false;
        if ( t != null )
        {   if ( t instanceof SibGPolygon )
            {   SibGPolygon other = (SibGPolygon)t;
                
                /** compare point */
                if ( this.getPointCount() == other.getPointCount() )
                {   if ( this.getPoints().equals(other.getPoints()) )
                    {   if ( this.isFillable() == other.isFillable() )
                        {   if ( this.isFillable() )
                            {   if ( this.getFillColor().equals(other.getFillColor()) )
                                    return true;
                            }
                            else
                                return true;
                        }
                    }
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
     *  @param color an instance of String representing an html color
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
