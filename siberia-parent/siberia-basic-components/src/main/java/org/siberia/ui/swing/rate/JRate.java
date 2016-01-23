/* 
 * Siberia basic components : siberia plugin defining components supporting siberia types
 *
 * Copyright (C) 2008 Alexis PARIS
 * Project Lead:  Alexis Paris
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
package org.siberia.ui.swing.rate;

/*
 * JSongRate.java
 *
 * Created on 24 février 2008, 14:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.LabelUI;

/**
 *
 * @author root
 */
public class JRate extends JComponent
{
    /** enumeration defining the mode of rate */
    public static enum RateMode
    {
	ON_5,
	ON_10;
    }
    
    /** property border star color */
    public static final String PROPERTY_STAR_BORDER_COLOR     = "star-border-color";
    
    /** property star selected color */
    public static final String PROPERTY_STAR_SELECTED_COLOR   = "star-selected-color";
    
    /** property star unselected color */
    public static final String PROPERTY_STAR_UNSELECTED_COLOR = "star-unselected-color";
    
    /** property star width */
    public static final String PROPERTY_STAR_WIDTH            = "star-width";
    
    /** property star gap */
    public static final String PROPERTY_STAR_GAP              = "star-gap";
    
    /** property rate mode */
    public static final String PROPERTY_RATE_MODE             = "rate-mode";
    
    /** property rate */
    public static final String PROPERTY_RATE                  = "rate";
    
    /** border color */
    private Color    borderColor         = null;
    
    /** selected star color */
    private Color    selectedStarColor   = Color.RED;
    
    /** unselected star color */
    private Color    unselectedStarColor = Color.LIGHT_GRAY;
    
    /** star width */
    private int      starWidth           = 20;
    
    /** star gap */
    private int      starGap             = 1;
    
    /** star */
    private Star     star                = null;
    
    /** set the rate mode */
    private RateMode mode                = RateMode.ON_10;
    
    /* value of the rate in [0, 10] */
    private Short    rate                = null;
    
    private int[]    ratios              = null;
    
    /** Creates a new instance of JSongRate */
    public JRate()
    {
	setOpaque(true);
	
	this.createStar();
	
	this.addMouseListener(new MouseAdapter()
	{
	    public void mouseReleased(MouseEvent e)
	    {
		if ( isEnabled() )
		{
		    Short s = computeValueForLocation(e.getX());

		    if ( s != null )
		    {
			if ( s.shortValue() < 0 )
			{
			    s = 0;
			}
			else if ( s.shortValue() > 10 )
			{
			    s = 10;
			}
		    }

		    setRate(s);
		}
	    }
	});
	
	this.updateUI();
    }

    @Override
    public void updateUI()
    {
	super.updateUI();
	
//	/** apply selected color and unselected color */
//	UIDefaults defaults = UIManager.getLookAndFeelDefaults();
//	Iterator it = defaults.keySet().iterator();
//	
//	while(it.hasNext())
//	{
//	    Object key = it.next();
//	    System.out.println("\t" + key + " --> " + defaults.get(key));
//	}
    }
    
    /** determine the value to apply if a clicked is processed at the given x position
     *	@param x 
     *	@return a Short
     */
    private Short computeValueForLocation(int x)
    {
	Short result = null;
	
	System.out.println("position : " + x);
	
	int _x = x - 1;
	
	if ( this.ratios == null )
	{
	    this.ratios = new int[3];

	    this.ratios[0] = (int)( ((double)this.getStarWidth()) * 1 / 3.0);
	    this.ratios[1] = (int)( ((double)this.getStarWidth()) * 2 / 3.0);
	    this.ratios[2] = this.getStarWidth();
	}
	
	for(int i = 0; i < 5 && result == null; i++)
	{
	    System.out.println("_x : " + _x);
	    for(int j = 0; this.ratios != null && j < this.ratios.length && result == null; j++)
	    {
		if ( _x < this.ratios[j] )
		{
		    result = (short)(2 * (i));
		    
		    if ( j == 1 )
		    {
			result = new Short( (short)(result.shortValue() + (short)1) );
		    }
		    else if ( j == 2 )
		    {
			result = new Short( (short)(result.shortValue() + (short)2) );
		    }
		    
		    System.out.println("j " + j + " result : " + result);
		}
	    }
	    
	    if ( this.ratios == null || this.ratios.length == 0 )
	    {
		break;
	    }
	    else
	    {
		_x -= this.ratios[this.ratios.length - 1] + this.getStarGap();
	    }
	}
	
	if ( result == null )
	{
	    result = 10;
	}

	return result;
    }
    
    /** get the rate mode
     *	@return a RateMode
     */
    public RateMode getRateMode()
    {
	return this.mode;
    }
    
    /** set the rate mode
     *	@param mode a RateMode
     */
    public void setRateMode(RateMode mode)
    {
	if ( mode == null )
	{
	    throw new IllegalArgumentException("rate mode could not be null");
	}
	
	if ( ! this.getRateMode().equals(mode) )
	{
	    RateMode oldMode = this.getRateMode();
	    
	    this.mode = mode;
	 
	    this.firePropertyChange(PROPERTY_RATE_MODE, oldMode, this.getRateMode());
	    
	    this.repaint();
	}
    }
    
    /** get the rate value
     *	@return a Short
     */
    public Short getRate()
    {
	return this.rate;
    }
    
    /** set the rate value
     *	@param value a Short
     */
    public void setRate(Short value)
    {
	if ( value != null && (value < 0 || value > 10) )
	{
	    throw new IllegalArgumentException("the value must be null or between [1, 10]");
	}
	
	boolean equals = false;
	
	if ( value == null )
	{
	    if ( this.getRate() == null )
	    {
		equals = true;
	    }
	}
	else
	{
	    if ( this.getRate() != null )
	    {
		equals = value.shortValue() == this.getRate().shortValue();
	    }
	}
	
	if ( ! equals )
	{
	    Short oldRate = this.getRate();
	    
	    this.rate = value;
	    
	    this.firePropertyChange(PROPERTY_RATE, oldRate, this.getRate());
	    
	    this.repaint();
	}
    }
    
    /** draw a star at a given position
     *	@param g a Graphics
     *	@param star a Star
     *	@param x the x position
     *	@param y the y position
     *	@param borderColor the color of the border, null to not paint border
     *	@param firstColor the color for the first part of the star
     *	@param secondColor the color for the first part of the star
     */
    private void paintStar(Graphics g, Star star, int x, int y, Color borderColor, Color firstColor, Color secondColor)
    {
	if ( g != null && star != null )
	{
	    Color initialColor = g.getColor();
	    
	    Polygon polygon = star.createFirstPolygon(x, y);

	    g.setColor(firstColor);

	    g.fillPolygon(polygon);

	    if ( borderColor != null )
	    {
		g.setColor(borderColor);
		g.drawPolygon(polygon);
	    }

	    polygon = star.createSecondPolygon(x, y);

	    g.setColor(secondColor);

	    g.fillPolygon(polygon);

	    if ( borderColor != null )
	    {
		g.setColor(borderColor);
		g.drawPolygon(polygon);
		g.setColor(firstColor);
		g.drawLine(star.getFirstMedianePoint().x, star.getFirstMedianePoint().y, 
			   star.getSecondMedianePoint().x, star.getSecondMedianePoint().y);
	    }
	    
	    g.setColor(initialColor);
	}
    }

    /** return the color use for the border of the stars
     *	@return a Color
     */
    public Color getBorderStarColor()
    {
	return borderColor;
    }

    /** initialize the color use for the border of the star
     *	@param borderColor a Color
     */
    public void setBorderStarColor(Color borderColor)
    {
	boolean equals = false;
	
	if ( this.getBorderStarColor() == null )
	{
	    if ( borderColor == null )
	    {
		equals = true;
	    }
	}
	else
	{
	    equals = this.getBorderStarColor().equals(borderColor);
	}
	
	if ( ! equals )
	{
	    Color oldColor = this.getBorderStarColor();
	    
	    this.borderColor = borderColor;
	    
	    this.firePropertyChange(PROPERTY_STAR_BORDER_COLOR, oldColor, this.getBorderStarColor());
	    
	    this.repaint();
	}
    }

    /** return the color use to paint selected part of starts
     *	@return a Color
     */
    public Color getSelectedStarColor()
    {
	return selectedStarColor;
    }

    /** initialize the color use to paint selected part of starts
     *	@return a Color
     */
    public void setSelectedStarColor(Color selectedStarColor)
    {
	if ( selectedStarColor == null )
	{
	    throw new IllegalArgumentException("selected star color must not be null");
	}
	
	boolean equals = false;
	
	if ( this.getSelectedStarColor() == null )
	{
	    if ( selectedStarColor == null )
	    {
		equals = true;
	    }
	}
	else
	{
	    equals = this.getSelectedStarColor().equals(selectedStarColor);
	}
	
	if ( ! equals )
	{
	    Color oldColor = this.getSelectedStarColor();
	    
	    this.selectedStarColor = selectedStarColor;
	    
	    this.firePropertyChange(PROPERTY_STAR_SELECTED_COLOR, oldColor, this.getSelectedStarColor());
	    
	    this.repaint();
	}
    }

    /** return the color use to paint unselected part of starts
     *	@return a Color
     */
    public Color getUnselectedStarColor()
    {
	return unselectedStarColor;
    }

    /** initialize the color use to paint unselected part of starts
     *	@return a Color
     */
    public void setUnselectedStarColor(Color unselectedStarColor)
    {
	if ( unselectedStarColor == null )
	{
	    throw new IllegalArgumentException("unselected star color must not be null");
	}
	
	boolean equals = false;
	
	if ( this.getUnselectedStarColor() == null )
	{
	    if ( unselectedStarColor == null )
	    {
		equals = true;
	    }
	}
	else
	{
	    equals = this.getUnselectedStarColor().equals(unselectedStarColor);
	}
	
	if ( ! equals )
	{
	    Color oldColor = this.getUnselectedStarColor();
	    
	    this.unselectedStarColor = unselectedStarColor;
	    
	    this.firePropertyChange(PROPERTY_STAR_UNSELECTED_COLOR, oldColor, this.getUnselectedStarColor());
	    
	    this.repaint();
	}
    }

    /** return the width apply to the star
     *	@return an integer
     */
    public int getStarWidth()
    {
	return starWidth;
    }

    /** initilize the width apply to the star
     *	@return an integer
     */
    public void setStarWidth(int starWidth)
    {
	if ( starWidth <= 0 )
	{
	    throw new IllegalArgumentException("star width should be greater than 0");
	}
	
	if ( starWidth != this.getStarWidth() )
	{
	    int oldWidth = this.getStarWidth();
	    
	    this.starWidth = starWidth;
	    
	    this.firePropertyChange(PROPERTY_STAR_WIDTH, oldWidth, this.getStarWidth());
	    
	    this.ratios = null;
	    
	    this.createStar();
	    
	    this.repaint();
	}
    }

    /** return the gap apply between the star
     *	@return an integer
     */
    public int getStarGap()
    {
	return starGap;
    }

    /** initialize the gap apply between the star
     *	@param starGap an integer
     */
    public void setStarGap(int starGap)
    {
	if ( starGap < 0 )
	{
	    throw new IllegalArgumentException("star gap should be greater or equals to 0");
	}
	
	if ( starGap != this.getStarWidth() )
	{
	    int oldGap = this.getStarGap();
	    
	    this.starGap = starGap;
	    
	    this.firePropertyChange(PROPERTY_STAR_GAP, oldGap, this.getStarGap());
	    
	    this.repaint();
	}
    }
    
    /** create the Star according to the properties of the component */
    private void createStar()
    {
	this.star = new Star(this.getStarWidth());
    }

    @Override
    protected void paintComponent(Graphics g)
    {
	super.paintComponent(g);
	
	/* paint background */
	if ( this.isOpaque() )
	{
	    Color initColor = g.getColor();
	    
	    g.setColor(this.getBackground());
	    
	    g.fillRect(0, 0, this.getWidth(), this.getHeight());
	    
	    g.setColor(initColor);
	}
	
	if ( this.getRate() != null )
	{
	    int position = 1;
	    
	    for(int i = 0; i < 5; i++)
	    {
		Color firstPart = this.getUnselectedStarColor();
		Color secondPart = this.getUnselectedStarColor();
		
		int value = this.getRate().shortValue();
		if ( value > (2 * i) )
		{
		    firstPart = this.getSelectedStarColor();
		}
		if ( value > (2 * i) + 1 )
		{
		    secondPart = this.getSelectedStarColor();
		}
		
		if ( ! this.isEnabled() )
		{
		    firstPart  = firstPart.brighter().brighter();
		    secondPart = secondPart.brighter().brighter();
		}
		
		this.paintStar(g, this.star, position, 0, this.getBorderStarColor(), firstPart, secondPart);
		
		position = position + this.getStarWidth() + this.getStarGap();
	    }
	}
    }
    
    class Star
    {
	int x1, y1;
	int x2, y2;
	int x3, y3;
	int x4, y4;
	int x5, y5;
	int x6, y6;
	int x7, y7;
	int x8, y8;
	int x9, y9;
	int x10, y10;
	
	public Star(int width)
	{
	    int x_middle = width / 2;
	    int y_middle = width / 2;
	    
	    this.x1 = width / 2;
	    this.y1 = 0;
	    
	    double valueX = 0;
	    
	    /* compute point 3 */
	    double coeff18 = - Math.tan( 18 * Math.PI / 180.0 );
	    double coeff18_2 = Math.pow(coeff18, 2);
	    
	    valueX = - (width / 2) *  Math.sqrt( ( 1f / (1 + coeff18_2) ) );
	    
	    this.x3 = (int)(valueX + (width / 2));
	    this.y3 = (int) (coeff18 * valueX);
	    this.y3 -= (int)(width / 2);
	    this.y3 = - this.y3;
	    
	    int x = (int)((this.x1 - this.x3) / 2);
	    int y = (int)((this.y3 - this.y1) / 2);
	    
	    x = (int)(( x_middle - ((double)x)) / 2) + x;
	    y = (int)(( y_middle - ((double)y)) / 2) + y;
	    
	    this.x2 = x;
	    this.y2 = y;
	    
	    /* compute point 5 */
	    double sin54   = Math.tan( 54 * Math.PI / 180.0 );
	    double sin54_2 = Math.pow(sin54, 2);
	    
	    valueX = - (width / 2) * Math.sqrt( ( 1f / (1 + sin54_2) ) );
	    
	    this.x5 = (int)(valueX + (width / 2));
	    this.y5 = (int) (sin54 * valueX);
	    this.y5 -= (int)(width / 2);
	    this.y5 = - this.y5;
	    
	    x = (int)((this.x5 - this.x3) / 2) + this.x3;
	    y = (int)((this.y5 - this.y3) / 2) + this.y3;
	    
	    x = (int)(( x_middle - x) / 2) + x;
	    y = (int)(( y_middle - y) / 2) + y;
	    
	    this.x4 = x;
	    this.y4 = y;
	    
	    this.x7 = width - this.x5;
	    this.y7 = this.y5;
	    
	    this.x8 = width - this.x4;
	    this.y8 = this.y4;
	    
	    this.x9 = width - this.x3;
	    this.y9 = this.y3;
	    
	    this.x10 = width - this.x2;
	    this.y10 = this.y2;
	    
	    x = (int)((this.x7 - this.x5) / 2) + this.x5;
	    y = this.y5;
	    
	    x = (int)(( x_middle - x) / 2) + x;
	    y = (int)(( y_middle - y) / 2) + y;
	    
	    this.x6 = x;
	    this.y6 = y;
	    
//	    System.out.println("1 : " + this.x1 + ", " + this.y1);
//	    System.out.println("2 : " + this.x2 + ", " + this.y2);
//	    System.out.println("3 : " + this.x3 + ", " + this.y3);
//	    System.out.println("4 : " + this.x4 + ", " + this.y4);
//	    System.out.println("5 : " + this.x5 + ", " + this.y5);
//	    System.out.println("6 : " + this.x6 + ", " + this.y6);
	}
	
	/** return the first point for the mediane
	 *  @return a Point
	 */
	public Point getFirstMedianePoint()
	{
	    return new Point(this.x1, this.y1 + 1);
	}
	
	/** return the second point for the mediane
	 *  @return a Point
	 */
	public Point getSecondMedianePoint()
	{
	    return new Point(this.x6, this.y6 - 1);
	}
	
	/** create a Polygon representing the first part of the star
	 *  @param decX
	 *  @param decY
	 *  @return a polygon
	 */
	public Polygon createFirstPolygon(int decX, int decY)
	{
	    Polygon poly = new Polygon();
	    
	    poly.addPoint(this.x1 + decX, this.y1 + decY);
	    poly.addPoint(this.x2 + decX, this.y2 + decY);
	    poly.addPoint(this.x3 + decX, this.y3 + decY);
	    poly.addPoint(this.x4 + decX, this.y4 + decY);
	    poly.addPoint(this.x5 + decX, this.y5 + decY);
	    poly.addPoint(this.x6 + decX, this.y6 + decY);
	    
//	    poly.addPoint(this.x1 + decX, this.y1 + decY);
		
	    return poly;
	}
	
	/** create a Polygon representing the second part of the star
	 *  @param decX
	 *  @param decY
	 *  @return a polygon
	 */
	public Polygon createSecondPolygon(int decX, int decY)
	{
	    Polygon poly = new Polygon();
	    
	    poly.addPoint(this.x1 + decX, this.y1 + decY);
	    poly.addPoint(this.x6 + decX, this.y6 + decY);
	    poly.addPoint(this.x7 + decX, this.y7 + decY);
	    poly.addPoint(this.x8 + decX, this.y8 + decY);
	    poly.addPoint(this.x9 + decX, this.y9 + decY);
	    poly.addPoint(this.x10 + decX, this.y10 + decY);
	    
//	    poly.addPoint(this.x1 + decX, this.y1 + decY);
		
	    return poly;
	}
	
    }
    
    public static void main(String[] args)
    {
	JFrame frame = new JFrame();
	frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	
	JPanel panel = new JPanel(new GridLayout(19, 2));
	
	for(int i = 0; i <= 10; i++)
	{
	    panel.add(new JLabel("" + i));
	    
	    JRate rate = new JRate();
	    rate.setRate((short)i);
	    
	    if ( i % 2 == 0 )
	    {
		rate.setEnabled(false);
	    }
	    
	    panel.add(rate);
	}
	
	frame.getContentPane().add(panel);
	
	frame.setSize(250, 400);
	frame.setVisible(true);
	
    }
    
}

