package org.siberia.ui.editor.impl.text;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.ByteArrayOutputStream;
import org.siberia.type.SibText;

import org.siberia.editor.AdaptedEditor;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.siberia.type.SibCode;
import org.siberia.type.SibType;
import org.siberia.type.service.ImportManager;
import org.siberia.type.service.Parser;
import org.siberia.editor.AdaptedEditor;
import org.siberia.editor.AdaptedEditor.EditorBuildingFailedException;
import org.siberia.editor.annotation.Editor;
import org.siberia.ui.swing.text.UndoableEditorPane;
import org.siberia.ui.swing.text.java.completion.JavaSpellChecker;
import org.siberia.ui.swing.text.util.SpellChecker;
import org.siberia.ui.swing.text.tool.AbstractTextToolBar;
import org.siberia.ui.swing.text.tool.JavaCodeToolBar;
import org.siberia.ui.swing.text.tool.PlainTextToolBar;
import org.siberia.ui.swing.text.tool.StyledTextToolBar;

/**
 *
 * Editor for type inheriting from ColdText
 *
 * @author alexis
 */
@Editor(relatedClass=org.siberia.type.SibText.class,
                  description="Editor for instances of ColdText",
                  name="Text editor")
public class ColdTextEditor extends AdaptedEditor
{
    private Border    border;
    private JScrollPane scrollPane;
    private UndoableEditorPane editorPane;
    
    /** Creates a new instance of ColdTextEditor */
    public ColdTextEditor()
    {   this(null); }
    
    /** Creates a new instance of ColdTextEditor */
    public ColdTextEditor(SibText instance)
    {   super(instance, true, false); }

    /**
     * set the SibType instance associated with the editor
     * 
     * @param instance instance of SibType
     */
    @Override
    public void setInstance(SibType instance)
    {
        super.setInstance(instance);
        
        if ( this.instance instanceof SibText )
        {   SibText textInstance = (SibText)this.instance;
            
            //this.addFocusListener(this);
            this.border = new TitledBorder(this.instance.getName());

            this.setLayoutType(AdaptedEditor.BOX_LAYOUT);

//            System.out.println("automatic line return ? " + textInstance.isAutomaticLineReturn());
            this.editorPane = new UndoableEditorPane( false );//! textInstance.isAutomaticLineReturn());

            if ( instance instanceof SibCode )
            {   this.editorPane.setCommentSequence(((SibCode)instance).getCommentSequence());

                /* complete the parser */
                Class parserClass = ((SibCode)instance).getParserClass();
                if ( parserClass != null )
                {   try
                    {   Parser parser = (Parser)parserClass.newInstance();
                        this.editorPane.setParser(parser);
                    }
                    catch(Exception e){ e.printStackTrace(); }
                }

                if ( ((SibCode)instance).supportImportManagement() )
                {   /* set the import manager */
                    Class importManagerClass = ((SibCode)instance).getImportManagerClass();
                    if ( importManagerClass != null )
                    {   try
                        {   ImportManager manager = (ImportManager)importManagerClass.newInstance();

                            if ( this.editorPane.getParser() == null )
                                throw new RuntimeException("Try to assign an import manager without having build any Parser.");

                            /* no need to set the parser, it's done on UndoableEditorPane */

                            this.editorPane.setImportManager(manager);

                        }
                        catch(Exception e){ e.printStackTrace(); }
                    }
                }
            }

            AbstractTextToolBar toolbar = null;

            String type = textInstance.getType();
            this.editorPane.setContentType(type);

            EditorKit kit = null;
            Document document = null;

            if ( type.equals("text/java") )
            {   toolbar = new JavaCodeToolBar(this.editorPane); }
            else if( type.equals("text/plain") )
            {   if ( textInstance.supportStyle() )
                    toolbar = new StyledTextToolBar(this.editorPane);
                else
                    toolbar = new PlainTextToolBar(this.editorPane);
            }

            if ( textInstance.supportSpellCheck() )
            {   SpellChecker spellChecker = null;

                if ( type.equals("text/java") )
                    spellChecker = new JavaSpellChecker();

                this.editorPane.setSpellChecker(spellChecker);
            }

            this.editorPane.setEditable(true);
            this.editorPane.setText((String)((SibText)this.instance).getValue());

            this.configureContent(this.editorPane);

            this.setToolbar(toolbar);

            try
            {
                this.buildingCompleted();
            }
            catch(EditorBuildingFailedException e){ e.printStackTrace(); }

            // l'action undo
            Action undoAction = new AbstractAction("Undo")
            {   public void actionPerformed(ActionEvent evt) 
                {   try 
                    {   if (ColdTextEditor.this.editorPane.getUndoManager().canUndo())
                        {   ColdTextEditor.this.editorPane.getUndoManager().undo();
                            ((AbstractTextToolBar)ColdTextEditor.this.getToolbar()).updateUndoRedoState();
                        }
                    }
                    catch (CannotUndoException e) {}
                }
            };
            this.getPanel().getActionMap().put("Undo", undoAction);        
            this.getPanel().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control Z"), "Undo");

            // l'action undo
            Action redoAction = new AbstractAction("Redo")
            {   public void actionPerformed(ActionEvent evt) 
                {   try 
                    {   if (ColdTextEditor.this.editorPane.getUndoManager().canRedo())
                        {   ColdTextEditor.this.editorPane.getUndoManager().redo();
                            ((AbstractTextToolBar)ColdTextEditor.this.getToolbar()).updateUndoRedoState();
                        }
                    }
                    catch (CannotRedoException e) {}
                }
            };
            this.getPanel().getActionMap().put("Redo", redoAction);
            this.getPanel().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("control Y"), "Redo");

//            this.scrollPane.repaint();
//            toolbar.repaint();
            
        }
    }
    
    @Override
    public void save()
    {   
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        
        
        try
        {   this.editorPane.getEditorKit().write(stream,
                        this.editorPane.getDocument(), 0,
                        this.editorPane.getDocument().getLength());
        }
        catch(Exception ioe){ ioe.printStackTrace(); }
        
        byte[] b = stream.toByteArray();
        
        try
        {
//        for(int i = 0; i < b.length; i++)
//            System.out.print( (char)b[i]);
     
            ((SibText)this.getInstance()).setValue(stream.toString());
        }
        catch(PropertyVetoException e)
        {   e.printStackTrace(); }
    }
    
    /** method to update the graphical components in the editor
     *  according to the state of the relative associated entity
     */
    @Override
    public void load()
    {   
        this.editorPane.setText((String)((SibText)this.getInstance()).getValue());
        
//        StringReader reader = new StringReader((String)((ColdText)this.getInstance()).getValue());
//        
//        try
//        {   ((RTFEditorKit)this.editorPane.getEditorKit()).read(reader,
//                    this.editorPane.getDocument(), 0); }
//        catch(Exception ioe){ ioe.printStackTrace(); }
        
//        try
//        {
//            System.out.println(this.editorPane.getDocument().getText(0, this.editorPane.getDocument().getLength()));
//        }
//        catch(Exception e)
//        {   e.printStackTrace(); }
    }
}
