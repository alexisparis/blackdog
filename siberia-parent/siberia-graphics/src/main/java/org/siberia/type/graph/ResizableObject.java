package org.siberia.type.graph;

import java.awt.Rectangle;

/**
 *
 * interface for figure, which are caracterized by coordinates,
 * and which has to be resized
 *
 * @author alexis
 */
public interface ResizableObject
{
    
    /** indicates if the origin point is floating
     *  @return true if the origin point is floating
     */
    public boolean isFloating();
    
    /** set if the origin point is floating
     *  @param floating true if the origin point is floating
     */
    public void setFloating(boolean floating);
    
    /** indicates if it have to grow horizontally
     *  @return true if it have to grow horizontally
     */
    public boolean isHorizontallyGrowing();
    
    /** set if it have to grow horizontally
     *  @param grow true if it have to grow horizontally
     */
    public void setHorizontallyGrowing(boolean grow);
    
    /** indicates if it have to grow vertically
     *  @return true if it have to grow vertically
     */
    public boolean isVerticalylGrowing();
    
    /** set if it have to grow vertically
     *  @param grow true if it have to grow vertically
     */
    public void setVerticallyGrowing(boolean grow);
    
}
