package org.siberia.type.graph.figure;

import java.beans.PropertyVetoException;
import org.siberia.type.SibInteger;
import org.siberia.type.SibType;
import org.siberia.type.graph.SibGPoint;

public class SibGOval extends FillableEnclosingGeometricObject
{   
    /** create a new Oval */
    public SibGOval()
    {   super(); }

    /** return a point representing the "center" of the oval<br>
     *  WARNING : any change which will occurs on the center of the oval will affect point position
     *  @return a point
     */
    public SibGPoint getCenter()
    {   SibGPoint aux = new SibGPoint();
        try
        {
            aux.setX(this.getX());
            aux.setY(this.getY());
        }
        catch(PropertyVetoException e)
        {   e.printStackTrace(); }
        
        return  aux;
    }

    /** set the center of the oval
     *  @param x an integer
     *  @param y an integer
     */
    public void setCenter(int x, int y)
    {   this.setOrigin(x, y); }

    /** set the center of the oval
     *  @param x an SibInteger
     *  @param y an SibInteger
     */
    public void setCenter(SibInteger x, SibInteger y)
    {   this.setCenter(x == null ? 0 : x.getValue().intValue(), y == null ? 0 : y.getValue().intValue()); }

    /** set the diameter of the oval
     *  @param w an integer
     *  @param h an integer
     */
    public void setDiameters(int w, int h)
    {   this.setSize(w, h); }
    
    /** set the diameter of the oval
     *  @param w an SibInteger
     *  @param h an SibInteger
     */
    public void setDiameters(SibInteger w, SibInteger h)
    {   this.setDiameters(w == null ? 0 : w.getValue().intValue(), h == null ? 0 : h.getValue().intValue()); }

    /** return the value of the horizontal diameter
     *  @return the value of the horizontal diameter
     */
    public int getHorizontalDiameter()
    {   return this.getWidth().getValue().intValue(); }

    /** return the value of the vertical diameter
     *  @return the value of the vertical diameter
     */    
    public int getVerticalDiameter()
    {   return this.getHeight().getValue().intValue(); }
        
    /** return true if the oval represent a circle
     *  @return true if it represent a circle
     */
    public boolean isCircle()
    {   return this.getHeight() == this.getWidth(); }
}
