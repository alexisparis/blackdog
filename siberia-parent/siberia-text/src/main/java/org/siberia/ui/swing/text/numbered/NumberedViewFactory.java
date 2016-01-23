package org.siberia.ui.swing.text.numbered;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/**
 *
 * @author alexis
 */
public class NumberedViewFactory implements ViewFactory
{   
    private boolean showLineNumbers = false;
    
    public NumberedViewFactory(boolean showLineNumbers)
    {   super();
        
        this.showLineNumbers = showLineNumbers;
    }
    
    public View create(Element elem)
    {   String kind = elem.getName();
        if (kind != null)
        {   if (kind.equals(AbstractDocument.ContentElementName))
            {   //new Exception().printStackTrace();
                return new LabelView(elem); }
            else if (kind.equals(AbstractDocument.ParagraphElementName))
            {   return new NumberedParagraphView(elem, this.showLineNumbers); }
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
}
