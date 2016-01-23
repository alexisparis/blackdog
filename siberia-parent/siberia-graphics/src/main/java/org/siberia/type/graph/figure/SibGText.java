package org.siberia.type.graph.figure;

import java.beans.PropertyVetoException;
import org.siberia.type.SibFont;
import org.siberia.type.graph.VisualObject;
import org.siberia.type.SibInteger;
import org.siberia.type.SibString;
import org.siberia.type.AbstractSibType;

public class SibGText extends FillableEnclosingGeometricObject
{
    /** Static EMPTY text */
    public static final SibGText EMPTY_TEXT = new SibGText();
    
    /** text of the graphical object */
    private SibString  text             = null;
    
    /** font to apply */
    private SibFont    font             = null;
    
    /** font size to apply */
    private SibInteger fontSize         = null;
    
    /** code associated with the text */
    private String       code             = null;
    
    /** indicates if the text is to be invisible if the text to display has a length equal to 0 */
    private boolean      invisibleIfEmpty = true;

    /** create a new Text */
    public SibGText()
    {   super(); }
    
    /** return a new VisualObject which proportions changed
     *  @param ratioX the X ratio for the proportions
     *  @param ratioY the Y ratio for the proportions
     **/
    public VisualObject createWithProportions(float ratioX, float ratioY)
    {   SibGText newTxt = (SibGText)super.createWithProportions(ratioX, ratioY);
        
        try
        {
            newTxt.setText(this.getText().getValue());
            newTxt.setInvisibleIfEmpty(this.isInvisibleIfEmpty());
            newTxt.setCode(this.getCode());
            newTxt.setFontSize(this.getFontSize().getValue().intValue());
        }
        catch(PropertyVetoException e)
        {   e.printStackTrace(); }
        
//        newTxt.getFont().select(this.getFont().getSelectedElements());
        return newTxt;
    }

    /** return the font for the text
     *  @return an instance of SibFont
     */
    public SibFont getFont()
    {   return this.font; }

    /** initialize the font of the text
     *  @param font an SibFont
     */
    public void setFont(String font)
    {   
//        if ( font != null )
//            this.font.select(font);
    }

    /** return an SibInteger representing the font size
     *  @return an SibInteger representing the font size
     */
    public SibInteger getFontSize()
    {   return this.fontSize; }

    /** initialize the font size
     *  @param fontSize an integer
     */
    public void setFontSize(int fontSize) throws PropertyVetoException
   {   this.fontSize.setValue(fontSize); }
    
    /** return an SibString representing the text
     *  @return an SibString representing the text
     */
    public SibString getText()
    {   return this.text; }
    
    /** initialize the text of this instance
     *  @param text a String representing the text
     */
    public void setText(String text) throws PropertyVetoException
    {   this.text.setValue(text); }

    /** return an String representing the code
     *  @return an String representing the code
     */
    public String getCode()
    {   return code; }

    /** initialize the code
     *  @param code a String representing the code
     */
    public void setCode(String code) throws PropertyVetoException
    {   this.code = code; }

    /** indicates if the text is to be invisible if the text to display has a length equal to 0
     *  @return true if the text is to be invisible if the text to display has a length equal to 0
     */
    public boolean isInvisibleIfEmpty()
    {   return invisibleIfEmpty; }

    /** set if the text is to be invisible if the text to display has a length equal to 0
     *  @param invisibleIfEmpty true if the text is to be invisible if the text to display has a length equal to 0
     */
    public void setInvisibleIfEmpty(boolean invisibleIfEmpty) throws PropertyVetoException
    {   this.invisibleIfEmpty = invisibleIfEmpty; }
    
    /** create another instance
     *  @return a clone
     */
    public Object clone()
    {   SibGText other = (SibGText)super.clone();
        
        try
        {   other.setInvisibleIfEmpty(this.isInvisibleIfEmpty());
            other.setCode(this.getCode());
//        other.getFont().select(this.getFont().getSelectedElements());
            other.setFontSize(this.getFontSize().getValue().intValue());
            
            other.setEnclosingFigure(this.isEnclosingFigure());
        }
        catch (PropertyVetoException ex)
        {   ex.printStackTrace(); }
        
        return other;
    }
    
    public boolean equals(Object t)
    {   if ( ! super.equals(t) ) return false;
        if ( t != null )
        {   if ( t instanceof SibGText )
            {   SibGText other = (SibGText)t;
                if ( this.getCode().equals(other.getCode()) )
                {   if ( this.isInvisibleIfEmpty() == other.isInvisibleIfEmpty() )
                    {   if ( this.getFont().equals(other.getFont()) )
                        {   if ( this.getFontSize().equals(other.getFontSize()) )
                            {   return true; }
                        }
                    }
                }
            }
        }
        return false;
    }
}
