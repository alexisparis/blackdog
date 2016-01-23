/* 
 * Siberia properties ui : siberia plugin defining components to edit properties
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
//package org.siberia.ui.properties.editor;
//
//import java.awt.Dimension;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.io.File;
//import javax.swing.JButton;
//import javax.swing.JComponent;
//import javax.swing.JFileChooser;
//import org.siberia.properties.ColdXmlProperty;
//import org.siberia.exception.ResourceException;
//import org.siberia.GraphicalResources;
//import org.siberia.ResourceLoader;
//import org.siberia.properties.exception.PropertiesException;
//
///**
// *
// * property renderer allowing to choose file
// *
// * @author alexis
// */
//public class FileChooserPropertyRenderer extends TextFieldPropertyEditor
//{   
//    /** button */
//    private JButton fileChooseButton = null;
//    
//    /** Creates a new instance of TextFieldPropertyRenderer */
//    public FileChooserPropertyRenderer()
//    {   super();
//        
//        this.setTextFieldColumn(28);
//    }
//    
//    /** build the extending components
//     *  @return an array of components
//     */
//    protected JComponent[] getExtendedComponents()
//    {   JComponent[] components = super.getExtendedComponents();
//        JComponent[] returnedComponents = components;
//        if ( this.fileChooseButton == null )
//        {   this.fileChooseButton = new JButton();
//            
//            try
//            {
//                this.fileChooseButton.setIcon(ResourceLoader.getInstance().getIconNamed("/org/siberia/rc/img/SmallOpen.png"));
//            }
//            catch(ResourceException e)
//            {   e.printStackTrace(); }
//            
//            this.fileChooseButton.setPreferredSize(new Dimension(20, 20));
//            
//            if ( this.getProperty() != null )
//            {   
//                this.fileChooseButton.addActionListener(new ActionListener()
//                {
//                    public void actionPerformed(ActionEvent e)
//                    {   JFileChooser chooser = new JFileChooser();
//                        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//                        chooser.setMultiSelectionEnabled(false);
//                        chooser.setSelectedFile(new File(textField.getText()));
//                        int result = chooser.showOpenDialog(GraphicalResources.getInstance().getMainFrame());
//                        if ( result == JFileChooser.APPROVE_OPTION )
//                        {   
//                            try
//                            {   getProperty().setValue(chooser.getSelectedFile().getAbsolutePath()); }
//                            catch(Exception ex)
//                            {   ex.printStackTrace(); }
//                        }
//                    }
//                });
//                
//                returnedComponents = new JComponent[components.length + 1];
//                System.arraycopy(components, 0, returnedComponents, 0, components.length);
//                returnedComponents[returnedComponents.length - 1] = this.fileChooseButton;
//            }
//        }
//        return returnedComponents;
//    }
//}
