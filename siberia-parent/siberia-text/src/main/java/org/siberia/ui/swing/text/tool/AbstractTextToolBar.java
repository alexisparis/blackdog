package org.siberia.ui.swing.text.tool;

import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.plaf.ToolBarUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.siberia.ui.swing.text.UndoableEditorPane;
import org.siberia.ui.swing.tool.AbstractToolBar;
import org.siberia.SiberiaTextPlugin;

/**
 *
 * Default toolbar for text editor
 *
 * @author alexis
 */
public abstract class AbstractTextToolBar extends AbstractToolBar implements ActionListener,
                                                                             CaretListener
{
    /** reference onto the editorPane */
    private UndoableEditorPane editorPane       = null;
    
    /** ##########################
     *  Functionalities indicators
     *  ########################## */
    
    /** indicates if Undo Redo is allowed */
    private boolean supportUndo          = true;
 
    /** indicates if Copy Cut Paste is allowed */   
    private boolean supportCopy          = true;
    
    /** indicates if Paragraph indentation is allowed */
    private boolean supportIndentation   = true;
    
    /** indicates if Text style is allowed */
    private boolean supportStyle         = true;
    
    /** indicates if Paragraph justification is allowed */
    private boolean supportJustification = true;
    
    /** indicates if text colorization is allowed */
    private boolean supportColorizedText = true;
    
    /** indicates if text commenting is allowed */
    private boolean supportCommentedText = true;
    
    /** ############
     *  Undo buttons
     *  ############ */
    private JButton undo       = null;
    private JButton redo       = null;
    
    /** ############
     *  Copy buttons
     *  ############ */
    private JButton copy       = null;
    private JButton cut        = null;
    private JButton paste      = null;
    
    /** #############
     *  style buttons
     *  ############# */
    private JToggleButton bold       = null;
    private JToggleButton italic     = null;
    private JToggleButton underlined = null;
    private JComboBox     fontType   = null;
    private JComboBox     fontSize   = null;
    
    /** ###################
     *  Indentation buttons
     *  ################### */
    private JButton indent     = null;
    private JButton unindent   = null;
    
    /** #####################
     *  justification buttons
     *  ##################### */
    private JButton left       = null;
    private JButton right      = null;
    private JButton center     = null;
    private JButton justify    = null;
    
    /** ###################
     *  Comment buttons
     *  ################### */
    private JButton comment    = null;
    private JButton uncomment  = null;
    
    /** Creates a new instance of ScriptToolBar which by default support all allowed tools :<br>
     *    - Undo / Redo.<br>
     *    - Copy / Cut / Paste.<br>
     *    - Indentation.<br>
     *    - Text style.<br>
     *    - Paragraph justifications.<br>
     *    - Text colorization.<br>
     */
    public AbstractTextToolBar(UndoableEditorPane editorPane)
    {   super(false);
        
        this.editorPane = editorPane;
    }
    
    /** method to build the toolbar according to the functionalities */
    protected void build()
    {
        if ( this.isStyleSupported() )
        {
            /** add Caret Listener */
            this.editorPane.addCaretListener(this);
            
            this.bold       = this.createToggleButton(SiberiaTextPlugin.PLUGIN_ID + ";1::img/bold_16.gif", "Bold", new StyledEditorKit.BoldAction());
            this.italic     = this.createToggleButton(SiberiaTextPlugin.PLUGIN_ID + ";1::img/italic_16.gif", "Italic", new StyledEditorKit.ItalicAction());
            this.underlined = this.createToggleButton(SiberiaTextPlugin.PLUGIN_ID + ";1::img/underlined_16.gif", "Underlined", new StyledEditorKit.UnderlineAction());
            
            GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] fontList = environment.getAvailableFontFamilyNames();
            
            this.fontType   = new JComboBox(fontList);
            this.fontType.addActionListener(this);
            String[] fontListSize = new String[]{"24", "22", "20", "18", "16", "14", "12", "11", "10", "9", "8", "7", "6", "5", "4"};
            this.fontSize   = new JComboBox(fontListSize);
            this.fontSize.addActionListener(this);
            this.bold.setBorderPainted(false);
            this.italic.setBorderPainted(false);
            this.underlined.setBorderPainted(false);
            this.fontSize.setFocusable(false);
            this.fontType.setFocusable(false);
            this.add(this.fontType);
            this.add(this.fontSize);
            this.add(this.bold);
            this.add(this.italic);
            this.add(this.underlined);
            this.addSeparator();
        }
        
        if ( this.isUndoSupported() )
        {
            Action undoAction = new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    try
                    {   AbstractTextToolBar.this.editorPane.getUndoManager().undo(); }
                    catch (CannotUndoException ex) { ex.printStackTrace(); };

                    AbstractTextToolBar.this.updateUndoRedoState();
                }
            };
            Action redoAction = new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {
                    try
                    {   AbstractTextToolBar.this.editorPane.getUndoManager().redo(); }
                    catch (CannotRedoException ex) { ex.printStackTrace(); };

                    AbstractTextToolBar.this.updateUndoRedoState();
                }
            };
            
            this.undo = this.createButton(SiberiaTextPlugin.PLUGIN_ID + ";1::img/undo_16.gif", "Undo", undoAction);
            this.redo = this.createButton(SiberiaTextPlugin.PLUGIN_ID + ";1::img/redo_16.gif", "Redo", redoAction);
            this.undo.setBorderPainted(false);
            this.redo.setBorderPainted(false);
            this.undo.setEnabled(false);
            this.redo.setEnabled(false);
            this.add(this.undo);
            this.add(this.redo);
            this.addSeparator();
        }
        
        if ( this.isCopySupported() )
        {
            Action copyAction = new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {   AbstractTextToolBar.this.editorPane.copy(); }
            };
            Action cutAction = new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {   AbstractTextToolBar.this.editorPane.cut(); }
            };
            Action pasteAction = new AbstractAction()
            {
                public void actionPerformed(ActionEvent e)
                {   AbstractTextToolBar.this.editorPane.paste(); }
            };
            
            this.copy  = this.createButton(SiberiaTextPlugin.PLUGIN_ID + ";1::img/copy_16.gif", "Copy", copyAction);
            this.cut   = this.createButton(SiberiaTextPlugin.PLUGIN_ID + ";1::img/cut_16.gif", "Cut", cutAction);
            this.paste = this.createButton(SiberiaTextPlugin.PLUGIN_ID + ";1::img/paste_16.gif", "Paste", pasteAction);
            this.copy.setBorderPainted(false);
            this.cut.setBorderPainted(false);
            this.paste.setBorderPainted(false);
            
            this.add(this.copy);
            this.add(this.cut);
            this.add(this.paste);
            this.addSeparator();
        }
        System.out.println("support indentation : " + this.supportIndentation);
        
        if ( this.isIndentationSupported() )
        {
            this.indent   = this.createButton(SiberiaTextPlugin.PLUGIN_ID + ";1::img/indent_16.gif", "Indent", new InsertTextOnLineAction(this.editorPane.getTab()));
            this.unindent = this.createButton(SiberiaTextPlugin.PLUGIN_ID + ";1::img/unindent_16.gif", "Unindent", new RemoveTextOnLineAction(this.editorPane.getTab()));
            this.indent.setBorderPainted(false);
            this.unindent.setBorderPainted(false);
            System.out.println("ajout de l'indentation");
            this.add(this.indent);
            this.add(this.unindent);
            this.addSeparator();
        }
        
        if ( this.isJustificationSupported() )
        {
            this.left    = this.createButton(SiberiaTextPlugin.PLUGIN_ID + ";1::img/left_16.gif", "Left", 
                                             new StyledEditorKit.AlignmentAction("", StyleConstants.ALIGN_LEFT));
            this.center  = this.createButton(SiberiaTextPlugin.PLUGIN_ID + ";1::img/center_16.gif", "Center", 
                                             new StyledEditorKit.AlignmentAction("", StyleConstants.ALIGN_CENTER));
            this.right   = this.createButton(SiberiaTextPlugin.PLUGIN_ID + ";1::img/right_16.gif", "Right", 
                                             new StyledEditorKit.AlignmentAction("", StyleConstants.ALIGN_RIGHT));
            this.justify = this.createButton(SiberiaTextPlugin.PLUGIN_ID + ";1::img/justify_16.gif", "Justify", 
                                             new StyledEditorKit.AlignmentAction("", StyleConstants.ALIGN_JUSTIFIED));
            this.left.setBorderPainted(false);
            this.center.setBorderPainted(false);
            this.right.setBorderPainted(false);
            this.justify.setBorderPainted(false);
            this.add(this.left);
            this.add(this.center);
            this.add(this.right);
            this.add(this.justify);
            this.addSeparator();
        }
        
        if ( this.isCommentingSupported() )
        {
            this.comment   = this.createButton(SiberiaTextPlugin.PLUGIN_ID + ";1::img/comment.gif", "Comment",
                    new InsertTextOnLineAction(this.editorPane.getCommentSequence()));
            this.uncomment = this.createButton(SiberiaTextPlugin.PLUGIN_ID + ";1::img/uncomment.gif", "Unindent",
                    new RemoveTextOnLineAction(this.editorPane.getCommentSequence()));
            this.comment.setBorderPainted(false);
            this.uncomment.setBorderPainted(false);
            this.add(this.comment);
            this.add(this.uncomment);
            this.addSeparator();
        }
        
    }
    
    public void updateUndoRedoState()
    {   
        this.undo.setEnabled(this.editorPane.getUndoManager().canUndo());
        this.redo.setEnabled(this.editorPane.getUndoManager().canRedo());
        
//        this.setModified( this.editorPane.getUndoManager().canUndo() );
    }
    
    /* #################################################
     * ####### Functionalities getters / setters #######
     * ################################################# */
    
    /** return true if undo / redo is supported */
    public boolean isUndoSupported()
    {   return this.supportUndo; }
    
    /** fix if undo / redo is supported
     *  @param undoSupported true if undo / redo is to be supported
     */
    public void setUndoSupported(boolean undoSupported)
    {   this.supportUndo = undoSupported; }
    
    /** return true if copy / cut / paste is supported */
    public boolean isCopySupported()
    {   return this.supportCopy; }
    
    /** fix if copy / cut / paste is supported
     *  @param copySupported true if copy / cut / paste is to be supported
     */
    public void setCopySupported(boolean copySupported)
    {   this.supportCopy = copySupported; }
    
    /** return true if indentation is supported */
    public boolean isIndentationSupported()
    {   return this.supportIndentation; }
    
    /** fix if indentation is supported
     *  @param indentationSupported true if indentation is to be supported
     */
    public void setIndentationSupported(boolean indentationSupported)
    {   this.supportIndentation = indentationSupported; }
    
    /** return true if style is supported */
    public boolean isStyleSupported()
    {   return this.supportStyle; }
    
    /** fix if style is supported
     *  @param styleSupported true if style is to be supported
     */
    public void setStyleSupported(boolean styleSupported)
    {   this.supportStyle = styleSupported; }
    
    /** return true if commenting is supported */
    public boolean isCommentingSupported()
    {   return this.supportCommentedText; }
    
    /** fix if commenting is supported
     *  @param commentSupported true if commenting is to be supported
     */
    public void setCommentingSupported(boolean commentSupported)
    {   this.supportCommentedText = commentSupported; }
    
    /** return true if justification is supported */
    public boolean isJustificationSupported()
    {   return this.supportJustification; }
    
    /** fix if justification is supported
     *  @param justificationSupported true if justification is to be supported
     */
    public void setJustificationSupported(boolean justificationSupported)
    {   this.supportJustification = justificationSupported; }
    
    /** return true if text colorization is supported */
    public boolean isTextColorizationSupported()
    {   return this.supportColorizedText; }
    
    /** fix if text colorization is supported
     *  @param textColorizationSupported true if text colorization is to be supported
     */
    public void setTextColorizationSupported(boolean textColorizationSupported)
    {   this.supportColorizedText = textColorizationSupported; }

    /**
     * Places a String into each selected lines the document. If there
     * is a selection, it is removed before the tab is added.
     */
    private class InsertTextOnLineAction extends AbstractAction
    {
        private String textToInsert = "";
        
        public InsertTextOnLineAction()
        {   /* do nothing */ }
        
        public InsertTextOnLineAction(String text)
        {   this.textToInsert = text; }
        
        public void setText(String text)
        {   this.textToInsert = text; }
        
        public void actionPerformed(ActionEvent e)
        {   System.out.println("InsertTextOnLineAction");
            UndoableEditorPane target = AbstractTextToolBar.this.editorPane;
            
            if (target != null)
            {
                if ((! target.isEditable()) || (! target.isEnabled()))
                {   UIManager.getLookAndFeel().provideErrorFeedback(target);
                    return;
                }
                System.out.println("target : " + target);
                if ( target.getDocument().getLength() == 0 ) return;
                
                int tmpPos = 0;
                
                try
                {
                    tmpPos = target.getDocument().getText(0, target.getSelectionStart()).lastIndexOf('\n');
                }
                catch(BadLocationException le){ le.printStackTrace(); }
                
                int startSelectionPos = tmpPos == -1 ? 0 : tmpPos;
                int endSelectionPos   = 0;
                
                if ( target.getSelectionEnd() != 0 )
                    endSelectionPos = target.getSelectionEnd();
                else
                {   /* we are at the beginning of the text */
                    try
                    {   if ( ! target.getText(0, 1).equals("\n") )
                            endSelectionPos ++;
                    }
                    catch(BadLocationException badle){ badle.printStackTrace(); }
                }
                
                int CRLFCount         = 0;
                
                System.out.println("start : " + startSelectionPos);
                System.out.println("end   : " + endSelectionPos);
                
                for(int i = startSelectionPos; i < endSelectionPos + (CRLFCount * this.textToInsert.length()); i++)
                {
                    target.getUndoManager().setGroupActionActivated(true);
                    try
                    {
                        System.out.println("i : " + i);
                        if ( target.getDocument().getText(i, 1).equals("\n") || i == 0 )
                        {   /* insert String */
                            System.out.println("\tok");
                            target.getDocument().insertString(i + ( i == 0 ? 0 : 1 ), this.textToInsert, null);
                            i += this.textToInsert.length();
                            CRLFCount ++;
                        }
                    }
                    catch(BadLocationException ble){ ble.printStackTrace(); }
                    
                    target.getUndoManager().setGroupActionActivated(false);
                }
            }
        }
    }

    /**
     * Removes a String into each selected lines the document. If there
     * is a selection, it is removed before the tab is added.
     */
    private class RemoveTextOnLineAction extends AbstractAction
    {
        private String textToInsert = "";
        
        public RemoveTextOnLineAction()
        {   /* do nothing */ }
        
        public RemoveTextOnLineAction(String text)
        {   this.textToInsert = text; }
        
        public void setText(String text)
        {   this.textToInsert = text; }
        
        public void actionPerformed(ActionEvent e)
        {   System.out.println("RemoveTextOnLineAction");
            
            UndoableEditorPane target = AbstractTextToolBar.this.editorPane;
            if (target != null)
            {
                if ((! target.isEditable()) || (! target.isEnabled()))
                {   UIManager.getLookAndFeel().provideErrorFeedback(target);
                    return;
                }
                if ( target.getDocument().getLength() == 0 ) return;
                
                int tmpPos = 0;
                
                try
                {
                    tmpPos = target.getDocument().getText(0, target.getSelectionStart()).lastIndexOf('\n');
                }
                catch(BadLocationException le){ le.printStackTrace(); }
                
                int startSelectionPos = tmpPos == -1 ? 0 : tmpPos;
                int endSelectionPos   = 0;
                
                if ( target.getSelectionEnd() != 0 )
                    endSelectionPos = target.getSelectionEnd();
                else
                {   /* we are at the beginning of the text */
                    try
                    {   if ( ! target.getText(0, 1).equals("\n") )
                            endSelectionPos ++;
                    }
                    catch(BadLocationException badle){ badle.printStackTrace(); }
                }
                
                int CRLFCount         = 0;
                
                for(int i = startSelectionPos; i < endSelectionPos - (CRLFCount * this.textToInsert.length()); i++)
                {
                    target.getUndoManager().setGroupActionActivated(true);
                    try
                    {
                        if ( target.getDocument().getText(i, 1).equals("\n") || i == 0)
                        {   /* insert a tab */
                            target.getDocument().remove(i + ( i == 0 ? 0 : 1 ), this.textToInsert.length());
                            CRLFCount ++;
                        }
                    }
                    catch(BadLocationException ble){ ble.printStackTrace(); }
                    
                    target.getUndoManager().setGroupActionActivated(false);
                }
            }
        }
    }
    
    public void actionPerformed(ActionEvent e)
    {   if ( e.getSource() == this.fontType || e.getSource() == this.fontSize )
        {   if ( this.editorPane.getDocument() instanceof StyledDocument )
            {   SimpleAttributeSet attrs = new SimpleAttributeSet();
                if ( e.getSource() == this.fontType )
                {   String fontType = (String)this.fontType.getSelectedItem();
                    StyleConstants.setFontFamily(attrs, fontType);
                }
                else if ( e.getSource() == this.fontSize )
                {   String fontSize = (String)this.fontSize.getSelectedItem();
                    try
                    {   StyleConstants.setFontSize(attrs, Integer.parseInt(fontSize)); }
                    catch(NumberFormatException es){ es.printStackTrace(); }
                }
                else return;
                
                ((StyledDocument)this.editorPane.getDocument()).setCharacterAttributes(
                        this.editorPane.getSelectionStart(),
                        this.editorPane.getSelectionEnd(),
                        attrs, false);
                this.editorPane.refreshEditorAppearance();
            }
        }
    }
    
    /** ########################################################################
     *  #################### CaretListener implementation ######################
     *  ######################################################################## */
    /**
     * Called when the caret position is updated.
     *
     * @param e the caret event
     */
    public void caretUpdate(CaretEvent e)
    {   
        /* update the state of some buttons */
        
        int start = this.editorPane.getSelectionStart();
        int end   = this.editorPane.getSelectionEnd();
        
        this.bold.setSelected(false);
        this.italic.setSelected(false);
        this.underlined.setSelected(false);
        
        MutableAttributeSet attr = null;
        
        if ( this.editorPane.getEditorKit() instanceof StyledEditorKit )
            attr = ((StyledEditorKit)this.editorPane.getEditorKit()).getInputAttributes();
        
        System.out.println("taille : " + StyleConstants.getFontSize(attr));
        try
        {
            this.fontSize.setSelectedItem( StyleConstants.getFontSize(attr) );
            this.fontType.setSelectedItem( StyleConstants.getFontFamily(attr) );
        }
        catch(Exception efd){ }
        
        if ( start == end ) return;
        
        /* get attributes from the selection and update styles elements */
        this.bold.setSelected(StyleConstants.isBold(attr));
        this.italic.setSelected(StyleConstants.isItalic(attr));
        this.underlined.setSelected(StyleConstants.isUnderline(attr));
    }
    
//    /** ########################################################################
//     *  ########################### UNDO ACTION ################################
//     *  ######################################################################## */
//    
//    private class UndoAction() implements
//    /** to undo an action */
//    public void undoAction()
//    {
//        try {
//            this.ediPane.getUndoManager().undo();
//        } 
//        catch (CannotUndoException ex) 
//        {
//            System.out.println("Unable to undo: " + ex);
//            ex.printStackTrace();
//        };
//        
//        updateDoState();       
//    }
}
