/*
 * @(#)JavaContext.java	1.2 98/05/04
 *
 * Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package org.siberia.ui.swing.text.java;
import java.awt.*;
import javax.swing.text.*;
import org.siberia.ui.swing.text.UndoableEditorPane;
import org.siberia.ui.swing.text.numbered.ForcableView;
/**
 * A collection of styles used to render java text.  
 * This class also acts as a factory for the views used 
 * to represent the java documents.  Since the rendering 
 * styles are based upon view preferences, the views need
 * a way to gain access to the style settings which is 
 * facilitated by implementing the factory in the style 
 * storage.  Both functionalities can be widely shared across
 * java document views.
 *
 * @author   Timothy Prinzing
 * @version  1.2 05/04/98
 */
public class JavaContext extends StyleContext implements ViewFactory
{   
    // --- variables -----------------------------------------------
    /**
     * The styles representing the actual token types.
     */
    Style[] tokenStyles;
    
    /**
     * Constructs a set of styles to represent java lexical 
     * tokens.  By default there are no colors or fonts specified.
     */
    public JavaContext() {
	super();
	Style root = getStyle(DEFAULT_STYLE);
	tokenStyles = new Style[JavaToken.MaximumScanValue + 1];
	JavaToken[] tokens = JavaToken.all;
	int n = tokens.length;
	for (int i = 0; i < n; i++) {
	    JavaToken t = tokens[i];
	    Style parent = getStyle(t.getCategory());
	    if (parent == null) {
		parent = addStyle(t.getCategory(), root);
	    }
	    Style s = addStyle(null, parent);
	    s.addAttribute(JavaToken.JavaTokenAttribute, t);
	    tokenStyles[t.getScanValue()] = s;
	}
    }
    
    /** return the token corresponding to the code
     *  @param code a code
     *  @return a Token
     */
    public JavaToken getTokenForCode(int code)
    {   
        JavaToken current = null;
        for(int i = 0; i < JavaToken.all.length; i++)
        {   if ( JavaToken.all[i].getScanValue() == code )
            {   current = JavaToken.all[i];
                break; }
        }
        
        return current;
    }
    
    /**
     * Fetch the foreground color to use for a lexical
     * token with the given value.
     * 
     * @param code attribute set from a token element
     *  that has a Token in the set.
     */
    public Color getForeground(int code)
    {   JavaToken t = this.getTokenForCode(code);
        if ( t != null )
            return t.getColorFor();
        else
            return Color.BLACK;
    }
    
    /**
     * Fetch the font to use for a lexical
     * token with the given scan value.
     */
    public Font getFont(int code)
    {   JavaToken t = this.getTokenForCode(code);
        if ( t != null )
            return t.getFontFor();
        else
            return JavaToken.DEFAULT_FONT;
    }
    
    /**
     * Fetches the attribute set to use for the given
     * scan code.  The set is stored in a table to
     * facilitate relatively fast access to use in 
     * conjunction with the scanner.
     */
    public Style getStyleForScanValue(int code) {
        if (code < tokenStyles.length) {
	    return tokenStyles[code];
	}
	return null;
    }
    // --- ViewFactory methods -------------------------------------
    
    public View create(Element elem)
    {   String kind = elem.getName();
        if (kind != null)
        {   if (kind.equals(AbstractDocument.ContentElementName))
            {   //new Exception().printStackTrace();
                return new LabelView(elem); }
            else if (kind.equals(AbstractDocument.ParagraphElementName))
            {   return new JavaView(elem); }
            else if (kind.equals(AbstractDocument.SectionElementName))
            {   //new Exception().printStackTrace();
                return new BoxView(elem, View.Y_AXIS); }
            else if (kind.equals(StyleConstants.ComponentElementName))
            {   //new Exception().printStackTrace();
                return new ComponentView(elem); }
            else if (kind.equals(StyleConstants.IconElementName))
            {   //new Exception().printStackTrace();
                return new IconView(elem); }
        }
        return new LabelView(elem);
    }
    
    /**
     * View that uses the lexical information to determine the
     * style characteristics of the text that it renders.  This
     * simply colorizes the various tokens and assumes a constant
     * font family and size.
     */
    class JavaView extends WrappedPlainView implements ForcableView
    {
	
	JavaDocument.Scanner lexer;
	boolean lexerValid;
        
	/**
	 * Construct a simple colorized view of java
	 * text.
	 */
	JavaView(Element elem) {
	    super(elem);
	    JavaDocument doc = (JavaDocument) getDocument();
	    lexer = doc.createScanner();
	    lexerValid = false;
	}
        
        public void setSize(float w, float h)
        {   super.setSize(w, h); }
        
	/**
	 * Renders using the given rendering surface and area 
	 * on that surface.  This is implemented to invalidate
	 * the lexical scanner after rendering so that the next
	 * request to drawUnselectedText will set a new range
	 * for the scanner.
	 *
	 * @param g the rendering surface to use
	 * @param a the allocated region to render into
	 *
	 * @see View#paint
	 */
        public void paint(Graphics g, Shape a) {
	    super.paint(g, a);
	    lexerValid = false;
	}
    
        /** tell if the line numbers is to be displayed
         *  @return a boolean
         */
        public boolean isLineNumbersDisplayed()
        {   return true; }

        /** set if the line numbers is to be displayed
         *  @param lineVisible a boolean
         */
        public void setLineNumbersDisplayed(boolean lineVisible)
        {   /* do nothing */ }
    
        public void forceSetInsets()
        {   this.setInsets((short)0, (short)0, (short)0, (short)0); }
        
        protected void setInsets(short top, short left, short bottom, short right)
        {   short leftTmp = 0;
            
            if ( this.getContainer() != null )
            {   leftTmp = (short)(left+((UndoableEditorPane)this.getContainer()).getNumberWidth());
                
                Dimension dim = this.getContainer().getPreferredSize();
                dim.width -= ((UndoableEditorPane)this.getContainer()).getNumberWidth() + 10;
//                Dimnew Dimension()
                this.getContainer().setPreferredSize(dim);
            }
            else
                leftTmp = 15;
            
            super.setInsets(top, leftTmp, bottom, right);
        }
        
        public void paintChild(Graphics g, Rectangle r, int n)
        {   
            int k = 5;
            int previousLineCount = getPreviousLineCount();
            int numberX = r.x - getLeftInset();
            int numberY = r.y + r.height - k;
            short preferredWidth = this.getPreferredLineNumberColumnWidth(g);
            short numberWidth = ((UndoableEditorPane)this.getContainer()).getNumberWidth();
         
            if ( numberWidth != preferredWidth )
            {   System.err.println("my putain de process a la con");
                ((UndoableEditorPane)this.getContainer()).setNumberWidth(preferredWidth);
                if ( this.getContainer() != null )
                {   //this.getContainer().repaint();
                    if ( this.getContainer() instanceof UndoableEditorPane )
                        ((UndoableEditorPane)this.getContainer()).refreshEditorAppearance();
                }
            }
            
            Color color = g.getColor();
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(r.x - getLeftInset(), r.y, r.x - getLeftInset() + numberWidth - k, r.y + r.height);
            g.setColor(Color.WHITE);
            g.fillRect(r.x - getLeftInset(), r.y + r.height + 1, r.x - getLeftInset() + numberWidth - k, r.y + 2*r.height + 1);
            g.drawString(Integer.toString(previousLineCount + n + 1),
                                          numberX, numberY);
            g.setColor(color);
            super.paintChild(g, r, n);
        }
        
        public int getPreviousLineCount()
        {   int lineCount = 0;
            int count = this.getParagraphCount();
            for (int i = 0; i < count; i++)
            {   if (this.getParent().getView(i) == this)
                {   break; }
                else
                {   lineCount += this.getParent().getView(i).getViewCount(); }
            }
            return lineCount;
        }
        public int getParagraphCount()
        {   return this.getViewCount(); }
        
        public short getPreferredLineNumberColumnWidth(Graphics g)
        {   return (short)(g.getFontMetrics().charWidth('9') * ((int)(Math.log10((double)this.getParagraphCount())) + 2)); }
        
	/**
	 * Renders the given range in the model as normal unselected
	 * text.  This is implemented to paint colors based upon the
	 * token-to-color translations.  To reduce the number of calls
	 * to the Graphics object, text is batched up until a color
	 * change is detected or the entire requested range has been
	 * reached.
	 *
	 * @param g the graphics context
	 * @param x the starting X coordinate
	 * @param y the starting Y coordinate
	 * @param p0 the beginning position in the model
	 * @param p1 the ending position in the model
	 * @returns the location of the end of the range
	 * @exception BadLocationException if the range is invalid
	 */
        protected int drawUnselectedText(Graphics g, int x, int y, 
					 int p0, int p1) throws BadLocationException {
	    Document doc = getDocument();
	    Color lastColor = null;
            Font  lastFont  = null;
	    int mark = p0;
	    for (; p0 < p1; )
            {
		updateScanner(p0);
		int p = Math.min(lexer.getEndOffset(), p1);
		p = (p <= p0) ? p1 : p;
                
		Color fg  = getForeground(lexer.token);
//                Font font = getFont(lexer.token);
                
		if (fg != lastColor )//|| font != lastFont)
                {   // color change, flush what we have
		    g.setColor(lastColor);
//                    g.setFont(lastFont);
                    
		    Segment text = getLineBuffer();
		    doc.getText(mark, p0 - mark, text);
		    x = Utilities.drawTabbedText(text, x, y, g, this, mark);
		    mark = p0;
		}
		lastColor = fg;
//                lastFont  = font;
		p0 = p;
	    }
	    // flush remaining
	    g.setColor(lastColor);
	    Segment text = getLineBuffer();
	    doc.getText(mark, p1 - mark, text);
	    x = Utilities.drawTabbedText(text, x, y, g, this, mark);
	    return x;
	}
	/**
	 * Update the scanner (if necessary) to point to the appropriate
	 * token for the given start position needed for rendering.
	 */
	void updateScanner(int p) {
	    try {
		if (! lexerValid) {
		    JavaDocument doc = (JavaDocument) getDocument();
		    lexer.setRange(doc.getScannerStart(p), doc.getLength());
		    lexerValid = true;
		}
		while (lexer.getEndOffset() <= p) {
		    lexer.scan();
		}
	    } catch (Throwable e) {
		// can't adjust scanner... calling logic
		// will simply render the remaining text.
		//e.printStackTrace();
	    }
	}
    }
}
