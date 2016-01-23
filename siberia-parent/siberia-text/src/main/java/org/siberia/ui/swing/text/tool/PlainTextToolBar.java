package org.siberia.ui.swing.text.tool;

import org.siberia.ui.swing.text.UndoableEditorPane;



/**
 *
 * Toolbar for plain text edition
 *
 * @author alexis
 */
public class PlainTextToolBar extends AbstractTextToolBar
{
    
    /** Creates a new instance of PlainTextToolBar */
    public PlainTextToolBar(UndoableEditorPane editorPane)
    {   super(editorPane);
        
        this.setStyleSupported(false);
        this.setIndentationSupported(false);
        this.setJustificationSupported(false);
        this.setTextColorizationSupported(false);
        this.setCommentingSupported(false);
        
        this.build();
    }
    
}
