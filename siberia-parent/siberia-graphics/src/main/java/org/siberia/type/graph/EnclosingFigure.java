package org.siberia.type.graph;

/**
 *
 * Interface for king of figure that can be considered as THE enclosinf fig <br>
 * of a group of figures
 * 
 * @author alexis
 */
public interface EnclosingFigure
{
    /** return true if the figure is an enclosing figure
     *  @return true if the figure is an enclosing figure
     */
    public boolean isEnclosingFigure();
 
    /** set if the figure is an enclosing figure
     *  @param enclosingFigure true if the figure is an enclosing figure
     */   
    public void setEnclosingFigure(boolean enclosingFigure);
    
}
