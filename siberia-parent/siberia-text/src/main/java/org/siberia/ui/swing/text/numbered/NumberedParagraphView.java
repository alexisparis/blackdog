/*
 * NumberedParagraphView.java
 *
 * Created on 25 juin 2005, 18:44
 */

package org.siberia.ui.swing.text.numbered;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.text.Element;
import javax.swing.text.ParagraphView;
import javax.swing.text.View;
import org.siberia.ui.swing.text.UndoableEditorPane;

/**
 *
 * @author alexis
 */
public class NumberedParagraphView extends ParagraphView implements ForcableView
{
    private boolean insetsActivated = true;
    
    public NumberedParagraphView(Element e)
    {   super(e);
        short top = 0;
        short left = 0;
        short bottom = 0;
        short right = 0;
        this.setInsets(top, left, bottom, right);
    }
    
    public NumberedParagraphView(Element e, boolean showLineNumbers)
    {   super(e);
        short top = 0;
        short left = 0;
        short bottom = 0;
        short right = 0;
        this.setLineNumbersDisplayed(showLineNumbers);
        this.setInsets(top, left, bottom, right);
    }
    
    /** tell if the line numbers is to be displayed
     *  @return a boolean
     */
    public boolean isLineNumbersDisplayed()
    {   return this.insetsActivated; }
    
    /** set if the line numbers is to be displayed
     *  @param lineVisible a boolean
     */
    public void setLineNumbersDisplayed(boolean lineVisible)
    {   this.insetsActivated = lineVisible; }
    
    public void forceSetInsets()
    {   
        if ( this.insetsActivated )
            this.setInsets((short)0, (short)0, (short)0, (short)0);
    }

    protected void setInsets(short top, short left, short bottom, short right)
    {   short leftTmp = 0;
        
        if ( this.insetsActivated )
        {   if ( this.getContainer() != null )
                leftTmp = (short)(left + ((UndoableEditorPane)this.getContainer()).getNumberWidth());
            else
                leftTmp = 15;
        }
        else
        {   leftTmp = left; }
        
//        System.out.println("left : " + leftTmp);
        
//        super.setInsets(top, leftTmp, bottom, right);
        super.setInsets(top, left, bottom, right);
    }

    public void paintChild(Graphics g, Rectangle r, int n)
    {   
        if ( ! this.insetsActivated )
        {   super.paintChild(g, r, n);
            return;
        }
        
        int previousLineCount = getPreviousLineCount();
        int numberX = r.x - getLeftInset();
        int numberY = r.y + r.height - 5;

        short preferredWidth = this.getPreferredLineNumberColumnWidth(g);
        
        short numberWidth = ((UndoableEditorPane)this.getContainer()).getNumberWidth();

        if ( numberWidth != preferredWidth )
        {   ((UndoableEditorPane)this.getContainer()).setNumberWidth(preferredWidth);

            if ( this.getContainer() != null )
            {   this.getContainer().repaint();

                if ( this.getContainer() instanceof UndoableEditorPane )
                    ((UndoableEditorPane)this.getContainer()).refreshEditorAppearance();
            }
        }

        Color color = g.getColor();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(r.x - getLeftInset(), r.y, r.x - getLeftInset() + numberWidth - 5, r.y + r.height);
        g.setColor(Color.WHITE);
        g.fillRect(r.x - getLeftInset(), r.y + r.height + 1, r.x - getLeftInset() + numberWidth - 5, r.y + 2*r.height + 1);
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
    {   View parent = this.getParent();
        return parent.getViewCount();
    }

    public short getPreferredLineNumberColumnWidth(Graphics g)
    {   return (short)(g.getFontMetrics().charWidth('9') * ((int)(Math.log10((double)this.getParagraphCount())) + 2)); }
}
