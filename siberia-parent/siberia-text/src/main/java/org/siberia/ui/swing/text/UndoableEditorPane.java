package org.siberia.ui.swing.text;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;
import javax.swing.text.View;
import org.siberia.type.service.ImportManager;
import org.siberia.type.service.Parser;
import org.siberia.ui.swing.text.java.JavaEditorKit;
import org.siberia.ui.swing.text.numbered.ForcableView;
import org.siberia.ui.swing.text.numbered.NumberedEditorKit;
import org.siberia.ui.swing.text.util.ImportManagerUI;
import org.siberia.ui.swing.text.util.SpellChecker;



/**
 *
 * @author alexis
 */
public class UndoableEditorPane extends JEditorPane implements UndoableEditListener,
                                                               CaretListener//,
//                                                               AdjustmentListener,
//                                                               ComponentListener
{
    /** reference to its own UndoManager */
    private UndoEditorManager undoManager     = null; // edit
    
    /** String representing the tab sequence */
    private String            tab             = "    "; // edit
    
    /** String representing a comment sequence */
    private String            commentSequence = ""; // edit
    
    private JScrollPane       scrollPane      = null;
    
    public static short       NUMBERS_WIDTH   = 0; // edit
    
    public static short       preferredWidth  = 0; // edit
    
    public int                scrollWidth     = 0; // edit
    
    protected SpellChecker    spellChecker    = null; // edit
    
    private   boolean         widthLimited    = false; // edit
    
    /** parser for the content of the pane */
    private   Parser          parser          = null;
    
    /** import manager */
    private ImportManager     importManager   = null;
    
    /** Creates a new instance of UndoableEditorPane */
    public UndoableEditorPane()
    {   this(false); }
    
    /** Creates a new instance of UndoableEditorPane */
    public UndoableEditorPane(boolean widthLimited)
    {   super();
        
        this.setWidthLimited(widthLimited);
        
        System.out.println("width limited ? " + widthLimited);
        
        /** install kit */
        this.setEditorKitForContentType("text/java", new JavaEditorKit());
        this.setEditorKitForContentType("text/plain", new NumberedEditorKit(false));
        
        this.undoManager = new UndoEditorManager();
        
        if ( this.getDocument() != null )
        {   this.getDocument().addUndoableEditListener( this ); }
        
        Action printSentence = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {   System.out.println("sentence : " + UndoableEditorPane.this.getCurrentSentence()); }
        };
        this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK), "pre");
        this.getActionMap().put("pre", printSentence);
    }
    
    public void setBounds(int x, int y, int w, int h)
    {
        super.setBounds(x, y, w, h);
    }
    
    public short getNumberWidth() // edit
    {   return this.NUMBERS_WIDTH; }
    
    public void setNumberWidth(short width) // edit
    {   this.NUMBERS_WIDTH = width; }
    
    public Parser getParser()
    {   return parser; }

    public void setParser(Parser parser)
    {   this.parser = parser;
        
        if ( this.importManager != null )
            this.importManager.setParser(this.parser);
    }

    public ImportManager getImportManager()
    {   return importManager; }

    public void setImportManager(ImportManager importManager)
    {   /* action is already in Action map */
        boolean actionAlreadyExists = false;
        if ( this.importManager != null )
        {   
            if ( importManager == null  )
                this.getInputMap().remove(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK/* | InputEvent.META_MASK*/));
            
            actionAlreadyExists = true;
        }
        
        this.importManager = importManager;
        
        if ( this.importManager != null)
        {   this.importManager.setParser(this.getParser());
            
            if ( ! actionAlreadyExists )
            {   /* we must feed the action map */
                this.getActionMap().put("importsManagement", new AbstractAction()
                {
                    public void actionPerformed(ActionEvent e)
                    {   if ( getImportManager() == null ) return;
                        
                        ImportManagerUI managerUI = new ImportManagerUI(/*MainWindow.getInstance()*/null, getImportManager());
                        
                        try
                        {   managerUI.buildAndShow(getDocument().getText(0, getDocument().getLength()));
                            
                            if ( ! managerUI.hasCanceled() )
                            {   getDocument().insertString(0, getImportManager().getResultImports(), null); }
                        }
                        catch(BadLocationException ex){  ex.printStackTrace(); }
                        
                        getImportManager().reset();

                    }
                });
            }
            this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK/* | InputEvent.META_MASK*/),
                                      "importsManagement");
        }   
    }
    
    public void linkWithScrollPane(JScrollPane scrollPane) // edit
    {   
        this.scrollPane = scrollPane;
        
        boolean alreadyAdded = false;
        
        for (int i = 0; i < this.getCaretListeners().length; i++)
        {   if ( this.getCaretListeners()[i] == this )
                alreadyAdded = true;
        }
         
        if ( ! alreadyAdded )
            this.addCaretListener(this);
    }
    
    private String getCurrentToken(String delimiter)
    {   int position = this.getCaretPosition();
        
        String currentChar = null;
        int startPosition = position - 1;
        
        try
        {   currentChar = this.getText(startPosition, 1);
            while( ( ! currentChar.equals(delimiter)) && startPosition > 0 )
            {   startPosition --;

                currentChar = this.getText(startPosition, 1);
            
            }
            int tmp = startPosition == 0 ? 0 : startPosition + 1;
            return this.getText(tmp, position - tmp);
        }
        catch(BadLocationException e) { return ""; }
    }
    
    private String getCurrentSentence()
    {   return this.getCurrentToken("\n"); }
    
    private String getCurrentWord()
    {   String currentSentence = this.getCurrentSentence();
        if ( currentSentence == null ) return null;
        
        int lastSpaceIndex = currentSentence.lastIndexOf(" ");
        
        return currentSentence.substring(lastSpaceIndex == -1 ? 0 : lastSpaceIndex);
    }
    
    public void setSpellChecker(SpellChecker spellChecker) // edit
    {   if ( spellChecker == null ) return;
        
        this.spellChecker = spellChecker;
        this.spellChecker.setInvoker(this);
        
        // call spell checker
        Action spellAction = new AbstractAction("Spell")
        {   public void actionPerformed(ActionEvent evt) 
            {   
                if ( UndoableEditorPane.this.spellChecker == null ) return;
                
                if ( ! UndoableEditorPane.this.spellChecker.isRunning() )
                {   
                    /* start parsing if it exists */
                    if ( getParser() != null )
                    {   
                        Map<String, Class> parsing = getParser().parse( getTextToCursor() );
                        
                        UndoableEditorPane.this.spellChecker.setParsingResults(parsing);
                        UndoableEditorPane.this.spellChecker.setImports(getParser().getImports());
                    }
                    
                    UndoableEditorPane.this.spellChecker.run(
                            UndoableEditorPane.this.getCurrentToken(UndoableEditorPane.this.spellChecker.getSentenceDelimiter()),
                            UndoableEditorPane.this.getCompletionPosition());
                }
            }
        };
        
        this.spellChecker.setActionListener(new ActionListener()
        {   
            public void actionPerformed(ActionEvent e)
            {   UndoableEditorPane.this.processSpelling(); }
        });
        
        this.getActionMap().put("Spell", spellAction);
        this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,  InputEvent.CTRL_MASK), "Spell");
        
        Object key = UndoableEditorPane.this.getInputMap().get(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
        
        Action selectNext = new SwitchingAction(this.getActionMap().get(key))
        {   
            public void actionPerformed(ActionEvent e)
            {   boolean doDefaultAction = true;
                
                if ( UndoableEditorPane.this.spellChecker != null )
                {   if ( UndoableEditorPane.this.spellChecker.isRunning() )
                        doDefaultAction = false;
                }
                
                if ( doDefaultAction )
                    this.getDefaultAction().actionPerformed(e);
                else
                    UndoableEditorPane.this.spellChecker.selectNext();
            }
        };
        
        key = UndoableEditorPane.this.getInputMap().get(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
        
        Action selectPrevious = new SwitchingAction(this.getActionMap().get(key))
        {   
            public void actionPerformed(ActionEvent e)
            {   boolean doDefaultAction = true;
                
                if ( UndoableEditorPane.this.spellChecker != null )
                {   if ( UndoableEditorPane.this.spellChecker.isRunning() )
                        doDefaultAction = false;
                }
                
                if ( doDefaultAction )
                    this.getDefaultAction().actionPerformed(e);
                else
                    UndoableEditorPane.this.spellChecker.selectPrevious();
            }
        };
        
        boolean alreadyAdded = false;
        
        for (int i = 0; i < this.getCaretListeners().length; i++)
        {   if ( this.getCaretListeners()[i] == this )
                alreadyAdded = true;
        }
         
        if ( ! alreadyAdded )
            this.addCaretListener(this);
    }
    
    /** return the position where the completion popup has to be displayed
     *  @param dot the dot
     *  @return a Point
     */
    public Point getCompletionPosition(int dot)
    {   Point point = null;
        if ( this.getCaret() != null )
        {   
            TextUI mapper = this.getUI();
	    Document doc = this.getDocument();
	    if ((mapper != null) && (doc != null)) {
		// determine the new location and scroll if
		// not visible.
		Rectangle newLoc;
		try {
		    newLoc = mapper.modelToView(UndoableEditorPane.this, dot, Position.Bias.Forward);
		} catch (BadLocationException e) {
		    newLoc = null;
		}
                point = newLoc.getLocation();
	    }
        }
        else
        {   point = (Point)this.getLocation().clone();
            point.translate(this.getInsets().left + this.NUMBERS_WIDTH, this.getInsets().top);
        }
        
        point.translate(this.getLocation().x,
                        this.getLocation().y + 14);
        return point;
    }
    
    /** return the position where the completion popup has to be displayed
     *  @return a Point
     */
    public Point getCompletionPosition()
    {   return this.getCompletionPosition(this.getCaret().getDot()); }
    
//    public boolean getScrollableTracksViewportWidth() // edit
//    {   return this.isWidthLimited(); }
//    
//    public Dimension getPreferredSize() // edit
//    {   Dimension d = super.getPreferredSize();
//        
//        if ( d.width < this.scrollWidth )//this.scrollPane.getPreferredSize().width )
//            d.width = this.scrollWidth;//this.scrollPane.getPreferredSize().width;
//        return d;
//    }
    
    public String getTab() // edit
    {   return this.tab; }
    
    public void setCommentSequence(String commentSequence) // edit
    {   this.commentSequence = commentSequence; }
    
    public String getCommentSequence() // edit
    {   return this.commentSequence; }
    
    public void refreshEditorAppearance() // edit
    {   /** refresh insets of all view */
        //System.out.println("refreshEditor : " + NUMBERS_WIDTH);
        View view = this.getUI().getRootView(this);
        
        for(int i = 0; i < view.getViewCount(); i++)
        {   View current = view.getView(i);
            if ( current instanceof ForcableView )
            {   if ( ((ForcableView)current).isLineNumbersDisplayed() )
                    ((ForcableView)current).forceSetInsets();
            }
            
            for (int j = 0; j < current.getViewCount(); j++)
            {   
                if ( current.getView(j) instanceof ForcableView )
                {   if ( ((ForcableView)current.getView(j)).isLineNumbersDisplayed() )
                        ((ForcableView)current.getView(j)).forceSetInsets();
                }
            }   
        }
    }
    
    /** return the given instance responsible of undo and redo action on JEditorPane
     *  @return an UndoEditorManager
     */
    public UndoEditorManager getUndoManager() // edit
    {   return this.undoManager; }
    
    /** link the editor with a new document
     *  @param doc a new document
     */
    public void setDocument(Document doc) // edit
    {   //System.out.println("setDocument : " + doc);
        if( this.getDocument() != null ) this.getDocument().removeUndoableEditListener( this );
        
        super.setDocument(doc);
        
        if( this.getDocument() != null ) this.getDocument().addUndoableEditListener( this );
        
        if ( this.getUndoManager() != null )
            this.getUndoManager().discardAllEdits();
        
//        if ( ! (this.getEditorKit() instanceof NumberedEditorKit) )
//            this.setEditorKit(new NumberedEditorKit());
    }
    
    public String getTextToCursor()
    {   try
        {   return this.getDocument().getText(0, this.getCaretPosition()); }
        catch(Exception e){ e.printStackTrace(); }
        
        return null;
    }
    
    /** return the length from the current paragraph to the caret position
     *  @return an integer
     */
    public int getCurrentParagraphLength() // edit
    {   int position = this.getCaretPosition();
        
        int positionTmp = position;
        
        int length = -1;
        
        while( positionTmp >= 0 )
        {   try
            {
                if ( this.getText(positionTmp, 1 ).equals("\n") )
                    return length;
            }
            catch(BadLocationException e){  return position; }
            positionTmp --;
            length ++;
        }
        return position;
    }
    
    /* #########################################################################
     * ################## UndoableEditListener implementation ##################
     * ######################################################################### */
    
    public void undoableEditHappened( UndoableEditEvent evt) 
    {   this.getUndoManager().addEdit(evt.getEdit()); }

    /* #########################################################################
     * ###################### CaretListener implementation #####################
     * ######################################################################### */
    
    public void caretUpdate(CaretEvent e)
    {           
        if ( this.spellChecker != null )
        {   if ( this.spellChecker.isRunning() )
            {   
                /* start parsing if it exists */
                
                if ( getParser() != null )
                {   Map<String, Class> parsing = this.getParser().parse( this.getTextToCursor() );

                    this.spellChecker.setParsingResults(parsing);
                    this.spellChecker.setImports(this.getParser().getImports());
                }
                
                this.spellChecker.updatePosition(UndoableEditorPane.this.getCompletionPosition(e.getDot()));
                this.spellChecker.updateList(this.getCurrentToken(this.spellChecker.getSentenceDelimiter()));
            }
        }
        
        if ( ! (this.getEditorKit() instanceof NumberedEditorKit) ) return;
        
        if ( this.scrollPane != null )
        {
            int viewPosX =  this.scrollPane.getViewport().getViewPosition().x;

            if ( viewPosX <= this.NUMBERS_WIDTH )
            {   //System.out.println("appel à refreshEditorAppearance .................");
    //            this.refreshEditorAppearance(); // en attente XXAP
            }
        }
    }

    public boolean isWidthLimited()
    {   return widthLimited; }

    public void setWidthLimited(boolean widthLimited)
    {   this.widthLimited = widthLimited; }
    
    /** use the spellChecker current selection, to complete the current document */
    private void processSpelling()
    {   String returnedValue = this.spellChecker.getCurrentWord();

        if ( returnedValue != null )
        {   String current = this.getCurrentWord();

            int lastPointPosition = current.lastIndexOf(".");
            if ( lastPointPosition != -1 )
                current = current.substring(lastPointPosition + 1);

            try
            {
                System.out.println("returnedValeu : " + returnedValue + ", current length : " + current.length());
                String insertString = returnedValue;

                if ( current.length() > insertString.length() )
                    insertString = "";
                else
                    insertString = insertString.substring(current.length());

                if ( insertString.length() != 0 )
                    this.getDocument().insertString(this.getCaret().getDot(), insertString, null);
            }
            catch(BadLocationException r){  r.printStackTrace(); }

        }

        this.spellChecker.stop();
    }
    
    /* #########################################################################
     * ####################### KeyEvent processing #############################
     * ######################################################################### */
    
    protected final void processMouseEvent(MouseEvent e)
    {   super.processMouseEvent(e);
        
//        System.out.println("processMouseEvent");
    }
    
    /** Overrides <code>processKeyEvent</code> to process events. **/
    protected final void processKeyEvent(KeyEvent e)
    {   boolean doDefault = true;
        if ( this.spellChecker != null )
        {   if ( this.spellChecker.isRunning() )
            {   doDefault = this.preSpellingProcessKeyEvent(e); }
            else
            {   doDefault = this.preProcessKeyEvent(e); }
        }
        
        if ( doDefault )
        {   
            /* save the current line */
            String currentLine = this.getCurrentSentence();
            
            super.processKeyEvent(e);
            
            if ( this.spellChecker != null )
            {   if ( this.spellChecker.isRunning() )
                {   this.postSpellingProcessKeyEvent(e, currentLine); }
                else
                {   this.postDefaultProcessKeyEvent(e, currentLine); }
            }
            
            
        }             
    }
    
    /** method for preAction on a key event
     *  @param e the KeyEvent
     *  @return true if the default keyEvent process is required
     */
    protected boolean preProcessKeyEvent(KeyEvent e)
    {   if ( e.getID() == KeyEvent.KEY_PRESSED )
        {
            /* manage tab actions according to getTab() result */
            if ( e.getKeyCode() == KeyEvent.VK_TAB )
            {   
                if ( e.getModifiers() == InputEvent.SHIFT_MASK )
                {   int tabSize = this.getTab().length();

                    /* if the previous tabSize letter are space, delete them */
                    try
                    {
                        if ( this.getText(this.getCaret().getDot() - tabSize, tabSize).trim().length() == 0 )
                            this.getDocument().remove(this.getCaret().getDot() - tabSize, tabSize);
                    }
                    catch(BadLocationException r){ }
                    
                    return false;
                }
                else if ( e.getModifiers() == 0 )
                {
                    try
                    {   this.getDocument().insertString(this.getCaret().getDot(), this.getTab(), null); }
                    catch(BadLocationException r){  r.printStackTrace(); }

                    return false;
                }
            }
        }
        
        return true;
    }
    
    /** method for preAction on a key event when the spellchecker is running
     *  @param e the KeyEvent
     *  @return true if the default keyEvent process is required
     */
    protected boolean preSpellingProcessKeyEvent(KeyEvent e)
    {
        if ( e.getModifiers() == 0 )
        {   
            if ( e.getID() == KeyEvent.KEY_PRESSED )
            {   
                if ( e.getKeyCode() == KeyEvent.VK_UP )
                {   this.spellChecker.selectPrevious();
                    return false;
                }
                else if ( e.getKeyCode() == KeyEvent.VK_DOWN )
                {   this.spellChecker.selectNext();
                    return false;
                }
                else if ( e.getKeyCode() == KeyEvent.VK_PAGE_UP )
                {   this.spellChecker.previousPage();
                    return false;
                }
                else if ( e.getKeyCode() == KeyEvent.VK_PAGE_DOWN )
                {   this.spellChecker.nextPage();
                    return false;
                }
                else if ( e.getKeyCode() == KeyEvent.VK_ENTER )
                {   this.processSpelling();

                    return false;
                }
            }
        }
        
        return true;
    }
    
    /** method for postAction on a key event
     *  @param e the KeyEvent
     *  @param currentLine the current sentence before default JEditorPane KeyEvent processing
     */
    protected void postDefaultProcessKeyEvent(KeyEvent e, String currentLine)
    {       
        /* POST ACTIONS */
        if ( e.getID() == KeyEvent.KEY_PRESSED )
        {   
            /* if return was pressed, adjust the caret position according to the start of the previous line */
            if ( e.getKeyCode() == KeyEvent.VK_ENTER )
            {   if ( currentLine == null ) return;

                StringBuffer buf = new StringBuffer();

                for(int i = 0; i < currentLine.length(); i++)
                {   if ( currentLine.charAt(i) == ' ' )
                        buf.append(' ');
                    else
                        break;
                }

                try
                {   this.getDocument().insertString(this.getCaret().getDot(), buf.toString(), null); }
                catch(BadLocationException r){  }

                buf = null;
            }
        }
    }
    
    /** method for postAction on a key event
     *  @param e the KeyEvent
     *  @param currentLine the current sentence before default JEditorPane KeyEvent processing
     */
    protected void postSpellingProcessKeyEvent(KeyEvent e, String currentLine)
    {   }
    
    /** private class for special actions */
    private abstract class SwitchingAction extends AbstractAction
    {
        private Action oldAction = null;
        
        public SwitchingAction(Action a)
        {   super();
            
            this.oldAction = a;
        }
        
        public Action getDefaultAction()
        {   return this.oldAction; }
    }
}
