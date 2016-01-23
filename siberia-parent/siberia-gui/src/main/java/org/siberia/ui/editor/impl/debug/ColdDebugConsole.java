/* 
 * Siberia gui : siberia plugin defining basics of graphical application 
 *
 * Copyright (C) 2008 Alexis PARIS
 * Project Lead:  Alexis Paris
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
package org.siberia.ui.editor.impl.debug;
//package org.siberia.ui.editor.impl.debug;
//
//import bsh.BshClassManager;
//import bsh.Interpreter;
//import bsh.NameSpace;
//import com.jgoodies.forms.layout.CellConstraints;
//import com.jgoodies.forms.layout.FormLayout;
//import java.awt.event.ActionEvent;
//import java.awt.event.InputEvent;
//import java.awt.event.KeyEvent;
//import javax.swing.AbstractAction;
//import javax.swing.Action;
//import javax.swing.JLabel;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.KeyStroke;
//import org.siberia.ui.editor.EditorAnnotation;
//import org.siberia.type.ColdURL;
//import org.siberia.ui.editor.AbstractEditor;
//import org.siberia.ui.editor.AbstractEditor.EditorBuildingFailedException;
//import org.siberia.ui.UserInterface;
//import org.siberia.ui.swing.text.UndoableEditorPane;
//import org.siberia.ui.swing.text.java.JavaEditorKit;
//import org.siberia.ui.swing.text.java.completion.JavaSpellChecker;
//
///**
// *
// * Editor which allow to use reflect API to debug
// *
// * @author alexis
// */
//@EditorAnnotation(relatedClass=org.siberia.debug.type.ColdDebugSession.class,
//                  description="Editor for instances of debug session",
//                  name="Interactive console")
//public class ColdDebugConsole extends AbstractEditor
//{
//    private UndoableEditorPane command = null;
//    
//    private JLabel      results        = null;
//    
//    private Interpreter interpreter    = null;
//    
//    /** Creates a new instance of ColdDebugConsole */
//    public ColdDebugConsole()
//    {   super(new ColdURL(null, "", "http://www.google.fr"), false, false);
//        
//        this.setLayoutType(AbstractEditor.BOX_LAYOUT);
//        
//        this.getInstance().setName("Debug console");
//        
//        this.interpreter = new Interpreter();
//        
//        this.command     = new UndoableEditorPane(false);
//        this.command.setEditorKit(new JavaEditorKit());
//        JavaSpellChecker spellCheck = new JavaSpellChecker();
//        spellCheck.setInvoker(UserInterface.getInstance().getFrame());
//        this.command.setSpellChecker(spellCheck);
//        
//        JScrollPane scroll = new JScrollPane(this.command);
//        this.command.linkWithScrollPane(scroll);
//        
//        Action interpret = new AbstractAction()
//        {   
//            public void actionPerformed(ActionEvent e)
//            {   System.out.println("interpret");
//                Object result = ColdDebugConsole.this.interpret();
//                ColdDebugConsole.this.results.setText(result == null ? "null" : result.toString() + ", class : " + result.getClass()); }
//        };
//        this.command.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK), "interpret");
//        this.command.getActionMap().put("interpret", interpret);
//        
//        this.results     = new JLabel();
//        
//        JPanel panel = new JPanel(new FormLayout("fill:pref:grow", "fill:pref:grow, 3px, max(300;pref)"));
//        CellConstraints cc = new CellConstraints();
//        panel.add(this.results, cc.xy(1, 1, cc.FILL, cc.FILL));
//        panel.add(scroll, cc.xy(1, 3, cc.LEFT, cc.FILL));
//        
//        this.configureContent(panel);
//        
//        try
//        {
//            this.buildingCompleted();
//        }
//        catch(EditorBuildingFailedException e){ e.printStackTrace(); }
//    }
//    
//    /** launch interpretation */
//    private Object interpret()
//    {   
//        try
//        {   BshClassManager manager = new BshClassManager();
//            manager.setClassLoader(this.getClass().getClassLoader());
//            NameSpace nameSpace = new NameSpace(manager, "classLoader");
////            nameSpace.setVariable("kernelResources", KernelResources.getInstance(), false);
//            nameSpace.setVariable("mainWindow", UserInterface.getInstance(), false);
//            return this.interpreter.eval(this.command.getText(), nameSpace);
//        }
//        catch(Exception eE)
//        {   //eE.printStackTrace();
//            return "";
//        }
//    }
//    
//    public void load()
//    {   /* do nothing */ }
//    
//    public void save()
//    {   /* do nothing */ }
//    
//    public void initializeEditor()
//    {   /* do nothing */ }
//    
//}
