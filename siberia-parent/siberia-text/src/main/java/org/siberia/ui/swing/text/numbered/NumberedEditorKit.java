package org.siberia.ui.swing.text.numbered;
import javax.swing.text.ViewFactory;
import javax.swing.text.rtf.RTFEditorKit;

/**
 *
 * @author alexis
 */
public class NumberedEditorKit extends RTFEditorKit
{   
    private boolean showLineNumbers = false;
    
    public NumberedEditorKit(boolean showLineNumbers)
    {   super();
        this.showLineNumbers = showLineNumbers;
    }
    
    public NumberedEditorKit()
    {   this(true); }
    
    public ViewFactory getViewFactory()
    {   return new NumberedViewFactory(this.showLineNumbers); }
}
