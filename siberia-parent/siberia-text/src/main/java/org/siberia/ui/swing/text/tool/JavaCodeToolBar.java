package org.siberia.ui.swing.text.tool;

import org.siberia.ui.swing.text.UndoableEditorPane;



/**
 *
 * Toolbar for java code edition
 *
 * @author alexis
 */
public class JavaCodeToolBar extends AbstractTextToolBar
{
    
    /** Creates a new instance of PlainTextToolBar */
    public JavaCodeToolBar(UndoableEditorPane editorPane)
    {   super(editorPane);
        
        this.setJustificationSupported(false);
        this.setStyleSupported(false);
        this.setTextColorizationSupported(false);
        this.setCommentingSupported(true);
        
        this.build();
    }
    
}
