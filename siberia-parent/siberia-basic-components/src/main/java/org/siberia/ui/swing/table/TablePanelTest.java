/* 
 * Siberia basic components : siberia plugin defining components supporting siberia types
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
package org.siberia.ui.swing.table;

import javax.swing.JFrame;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author alexis
 */
public class TablePanelTest {
    
    /** Creates a new instance of TablePanelTest */
    public TablePanelTest() {
    }
    
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        TablePanel panel = new TablePanel();
        
        panel.setModel(new TableModel()
        {
            public void addTableModelListener(TableModelListener l)
            {   }

            public Class<?> getColumnClass(int columnIndex)
            {   return String.class; }

            public int getColumnCount()
            {   return 2; }

            public String getColumnName(int columnIndex)
            {   if ( columnIndex == 0 )
                    return "a";
                else if ( columnIndex == 1 )
                    return "b";
                return "";
            }

            public int getRowCount()
            {   return 100; }

            public Object getValueAt(int rowIndex, int columnIndex)
            {   return "o-" + rowIndex + "-" + columnIndex; }

            public boolean isCellEditable(int rowIndex, int columnIndex)
            {   return false; }

            public void removeTableModelListener(TableModelListener l)
            {   }

            public void setValueAt(Object aValue, int rowIndex, int columnIndex)
            {   }
            
        });
        
        frame.getContentPane().add(panel);
        
        frame.pack();
        frame.setVisible(true);
    }
    
}
