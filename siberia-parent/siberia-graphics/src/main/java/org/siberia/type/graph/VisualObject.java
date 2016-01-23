package org.siberia.type.graph;

import java.awt.Color;
import java.beans.PropertyVetoException;
import org.siberia.type.SibColor;
import org.siberia.type.SibInteger;
import org.siberia.type.SibString;
import org.siberia.type.AbstractSibType;
import org.siberia.type.SibType;

/**
 *  Main class for all syntaxic definition of a graphical figure
 *
 *  @author alexis
 */
public abstract class VisualObject extends AbstractSibType implements ResizableObject
{  
    /** tag associated with the visual object */
    private SibString    tag              = null;
    
    /** border color */
    private SibColor     borderColor      = null;
    
    /** line width */
    protected SibInteger lineWidth        = null;
    
    /** indicates if it must grow vertically */
    private boolean        growVertically   = true;
    
    /** indicates if it must grow horizontally */
    private boolean        growHorizontally = true;
    
    /** indicates if it is floating */
    private boolean        isFloating       = true;
    
        
//        this.tag = new SibString(this, "tag", "");
//        this.borderColor = new SibColor(this, "border color", "#FFFFFF");
//        this.lineWidth = new SibInteger(this, "line width", 2);
    
    /**create a new VisualObject */
    public VisualObject()
    {   super(); }

    /*  return a String representation of the value
     *  @return a String representation of the value
     */
    public String valueAsString()
    {   return null; }
    
    /** return for an abstract figure the X min which it takes to display correctly
     *  @return the X min which it takes to display correctly
     **/
    public abstract int getXMin();
    
    
    /** return for an abstract figure the Y min which it takes to display correctly
     *  @return the Y min which it takes to display correctly
     **/
    public abstract int getYMin();
    
    /** return a new VisualObject which proportions changed
     *  @param ratioX the X ratio for the proportions
     *  @param ratioY the Y ratio for the proportions
     **/
    public abstract VisualObject createWithProportions(float ratioX, float ratioY);
    
    /** return the tag of the visual object
     *  @return an instance of SibString representing the tag
     */
    public SibString getTag()
    {   return this.tag; }

    /** initialize the tag of the visual object
     *  @param tag an instance of String representing the tag
     */
    public void setTag(String tag) throws PropertyVetoException
    {   this.tag.setValue(tag); }
    
    /** return the border color of the visual object
     *  @return an instance of SibColor representing the border color
     */
    public SibColor getBorderColor()
    {   return this.borderColor; }

    /** initialize the border color of the visual object
     *  @param color a Color
     */
    public void setBorderColor(Color color) throws PropertyVetoException
    {   this.borderColor.setValue(color); }

    /** return the line width of the visual object
     *  @return an instance of SibInteger representing the line width
     */
    public SibInteger getLineWidth()
    {   return this.lineWidth; }

    /** initialize the line width of the visual object
     *  @param value an integer representing the line width
     */
    public void setLineWidth(int value) throws PropertyVetoException
    {   this.lineWidth.setValue(value); }
    
    public boolean isValid()
    {   return true; }
    
    /* #########################################################################
     * ################# ResizableObject implementation ########################
     * ######################################################################### */
    
    /** indicates if the origin point is floating
     *  @return true if the origin point is floating
     */
    public boolean isFloating()
    {   return this.isFloating; }
    
    /** set if the origin point is floating
     *  @param floating true if the origin point is floating
     */
    public void setFloating(boolean floating)
    {   this.isFloating = floating; }
    
    /** indicates if it have to grow horizontally
     *  @return true if it have to grow horizontally
     */
    public boolean isHorizontallyGrowing()
    {   return this.growHorizontally; }
    
    /** set if it have to grow horizontally
     *  @param grow true if it have to grow horizontally
     */
    public void setHorizontallyGrowing(boolean grow)
    {   this.growHorizontally = grow; }
    
    /** indicates if it have to grow vertically
     *  @return true if it have to grow vertically
     */
    public boolean isVerticalylGrowing()
    {   return this.growVertically; }
    
    /** set if it have to grow vertically
     *  @param grow true if it have to grow vertically
     */
    public void setVerticallyGrowing(boolean grow)
    {   this.growVertically = grow; }
    
    /** create another instance
     *  @return a clone
     */
    public Object clone()
    {   VisualObject other = (VisualObject)super.clone();
        
        try
        {
            other.setBorderColor(this.getBorderColor().getValue());
            other.setFloating(this.isFloating());
            other.setHorizontallyGrowing(this.isHorizontallyGrowing());
            other.setLineWidth(this.getLineWidth().getValue().intValue());
            other.setTag(this.getTag().getValue());
            other.setVerticallyGrowing(this.isVerticalylGrowing());
        }
        catch(PropertyVetoException e)
        {   e.printStackTrace(); }
        
        return other;
    }
    
    public boolean equals(Object t)
    {   if ( ! super.equals(t) ) return false;
        if ( t != null )
        {   if ( t instanceof VisualObject )
            {   VisualObject other = (VisualObject)t;
                if ( this.getTag().equals(other.getTag()) )
                {   if ( this.getBorderColor().equals(other.getBorderColor()) )
                    {   if ( this.getLineWidth().equals(other.getLineWidth()) )
                        {   if ( this.isFloating() == other.isFloating() )
                            {   if ( this.isHorizontallyGrowing() == other.isHorizontallyGrowing() )
                                {   if ( this.isVerticalylGrowing() == other.isVerticalylGrowing() )
                                        return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
}
