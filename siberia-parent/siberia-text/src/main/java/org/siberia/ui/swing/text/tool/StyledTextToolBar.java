package org.siberia.ui.swing.text.tool;

import org.siberia.ui.swing.text.UndoableEditorPane;



/**
 *
 * Toolbar for styled text edition
 *
 * @author alexis
 */
public class StyledTextToolBar extends AbstractTextToolBar
{
    
    /** Creates a new instance of PlainTextToolBar */
    public StyledTextToolBar(UndoableEditorPane editorPane)
    {   super(editorPane);
        
        this.setCommentingSupported(false);
        
        this.build();
    }
    
}
