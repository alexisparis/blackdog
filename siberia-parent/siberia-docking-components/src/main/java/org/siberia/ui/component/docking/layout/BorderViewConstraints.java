/*
 * Siberia docking window : define an editor support based on Infonode docking window framework
 *
 * Copyright (C) 2008 Alexis PARIS
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.siberia.ui.component.docking.layout;

/**
 *
 * Define a ViewPlacement that can be used for example in a BorderViewLayout
 *
 * @author alexis
 */
public class BorderViewConstraints implements ViewConstraints
{
    /** NORTH constant */
    public static final int NORTH  = 0;
    
    /** SOUTH constant */
    public static final int SOUTH  = 1;
    
    /** CENTER constant */
    public static final int CENTER = 2;
    
    /** EAST constant */
    public static final int EAST   = 0;
    
    /** WEST constant : the west is the best */
    public static final int WEST   = 1;
    
    /** NONE : generally used when an alignement has been taken into account */
    public static final int NONE   = -1;
    
    /* definitions of the static Placements */
    
    /* Default Placement (center, center)*/
    public static final BorderViewConstraints DEFAULT_PLACEMENT    = new BorderViewConstraints(CENTER, CENTER);
    
    /* NORTH Placement */
    public static final BorderViewConstraints NORTH_PLACEMENT      = new BorderViewConstraints(CENTER, NORTH);
    
    /* SOUTH Placement */
    public static final BorderViewConstraints SOUTH_PLACEMENT      = new BorderViewConstraints(CENTER, SOUTH);
    
    /* EAST Placement */
    public static final BorderViewConstraints EAST_PLACEMENT       = new BorderViewConstraints(EAST, CENTER);
    
    /* WEST Placement */
    public static final BorderViewConstraints WEST_PLACEMENT       = new BorderViewConstraints(WEST, CENTER);
    
    /* NORTH-EAST Placement */
    public static final BorderViewConstraints NORTH_EAST_PLACEMENT = new BorderViewConstraints(EAST, NORTH);
    
    /* NORTH-WEST Placement */
    public static final BorderViewConstraints NORTH_WEST_PLACEMENT = new BorderViewConstraints(WEST, NORTH);
    
    /* SOUTH-EAST Placement */
    public static final BorderViewConstraints SOUTH_EAST_PLACEMENT = new BorderViewConstraints(EAST, SOUTH);
    
    /* SOUTH-WEST Placement */
    public static final BorderViewConstraints SOUTH_WEST_PLACEMENT = new BorderViewConstraints(WEST, SOUTH);
    
    /** vertical alignement */
    private int vAlign = CENTER;
    
    /** horizontal alignement */
    private int align  = CENTER;
    
    /** Creates a new instance of BorderViewPlacement
     *  @param horizontalAlignement : <br>
     *              <ul><li>EAST</li><li>CENTER</li><li>WEST</li></ul>
     *  @param verticalAlignement : <br>
     *              <ul><li>NORTH</li><li>CENTER</li><li>SOUTH</li></ul>
     */
    public BorderViewConstraints(int align, int vAlign)
    {   this.vAlign = vAlign;
        this.align  = align;
    }
    
    /** return the vertical alignement
     *  @return one of <ul><li>NORTH</li><li>CENTER</li><li>SOUTH</li></ul>
     */
    public int getVerticalAlignement()
    {   return this.vAlign; }
    
    /** return the vertical alignement
     *  @return one of <ul><li>NORTH</li><li>CENTER</li><li>SOUTH</li></ul>
     */
    public int getHorizontalAlignement()
    {   return this.align; }
    
    /** set the vertical alignement
     *  @param align one of <ul><li>NORTH</li><li>CENTER</li><li>SOUTH</li></ul>
     */
    public void setVerticalAlignement(int align)
    {   this.vAlign = align; }
    
    /** set the vertical alignement
     *  @param align one of <ul><li>NORTH</li><li>CENTER</li><li>SOUTH</li></ul>
     */
    public void setHorizontalAlignement(int align)
    {   this.align = align; }
    
    /** clear horizontal alignement */
    public void inhibitHorizontalAlignement()
    {   this.align = NONE; }
    
    /** clear vertical alignement */
    public void inhibitVerticalAlignement()
    {   this.vAlign = NONE; }
    
    /** return a clone of the given object
     *  @return an Object
     */
    public Object clone()
    {   return new BorderViewConstraints(this.getHorizontalAlignement(), this.getVerticalAlignement()); }
    
    public String toString()
    {   StringBuffer buffer = new StringBuffer();
        
        buffer.append("(" + this.getClass().getSimpleName() + " : horizontal=");
        
        switch(this.getHorizontalAlignement())
        {
            case(BorderViewConstraints.NONE)   : buffer.append("none");
                                               break;
            case(BorderViewConstraints.CENTER) : buffer.append("center");
                                               break;
            case(BorderViewConstraints.EAST)   : buffer.append("east");
                                               break;
            case(BorderViewConstraints.WEST)   : buffer.append("west");
                                               break;
        }
        buffer.append(",vertical=");
        
        switch(this.getVerticalAlignement())
        {
            case(BorderViewConstraints.NONE)   : buffer.append("none");
                                               break;
            case(BorderViewConstraints.CENTER) : buffer.append("center");
                                               break;
            case(BorderViewConstraints.NORTH)  : buffer.append("north");
                                               break;
            case(BorderViewConstraints.SOUTH)  : buffer.append("south");
                                               break;
        }
        
        buffer.append(")");
        
        return buffer.toString();
    }
    
}
