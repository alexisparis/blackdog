/*
 * PageableTableTest.java
 * JUnit based test
 *
 * Created on February 28, 2007, 9:22 PM
 */

package org.siberia.ui.swing.table;

import javax.swing.event.TableModelListener;
import junit.framework.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.siberia.ui.swing.table.model.*;

/**
 *
 * @author alexis
 */
public class PageableTableTest extends TestCase {
    
    /** model */
    private TableModel emptyModel    = null;
    private TableModel simpleModel   = null;
    private TableModel extendedModel = null;
    
    public PageableTableTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
    
    /** method that create a TableModel
     *  that contains four rows [1, 2], [3, 4], [5, 6], [7, 8] with column names ["a", "b"]
     */
    private TableModel getSimpleModel()
    {   if ( simpleModel == null )
            simpleModel = new DefaultTableModel(new Object[][]{{1, 2}, {3, 4}, {5, 6}, {7, 8}}, new Object[]{"a", "b"});
        return simpleModel; 
    }
    
    /** method that create a TableModel
     *  that contains zero rows with column names ["a", "b"]
     */
    private TableModel getEmptyModel()
    {   if ( emptyModel == null )
            emptyModel = new DefaultTableModel(null, new Object[]{"a", "b", "c", "d"});
        return emptyModel; 
    }
    
    /** method that create a TableModel
     *  that contains 100 lines with column names ["a", "b"]
     */
    private TableModel getExtendedModel()
    {   if ( extendedModel == null )
        {   extendedModel = new TableModel() {
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
            };
        }
        return extendedModel; 
    }

    /**
     * Test of setDisplayRowNumber method, of class org.siberia.ui.swing.table.model.PageableTableModelWrapper.
     */
    public void testSetDisplayRowNumber()
    {
//        PageableTableModelWrapper wrapper = new PageableTableModelWrapper();
//        wrapper.setWrappedModel(this.getSimpleModel());
        PageableTable wrapper = new PageableTable();
        wrapper.setModel(this.getSimpleModel());
        
//        wrapper.setDisplayRowNumber(true);
        
//        assertTrue(wrapper.isRowNumberDisplayed());
        
//        wrapper.setDisplayRowNumber(false);
        
//        assertFalse(wrapper.isRowNumberDisplayed());
        
//        wrapper.setDisplayRowNumber(true);
        
//        assertTrue(wrapper.isRowNumberDisplayed());
    }

    /**
     * Test of setMaximumDisplayedRows method, of class org.siberia.ui.swing.table.model.PageableTableModelWrapper.
     */
    public void testSetMaximumDisplayedRows()
    {
        PageableTable wrapper = new PageableTable();
        wrapper.setModel(this.getSimpleModel());
        
        wrapper.setMaximumDisplayedRows(2);
        assertEquals(2, wrapper.getMaximumDisplayedRows());
        
        try
        {   wrapper.setMaximumDisplayedRows(0);
            assertTrue(false);
        }
        catch(Exception e)
        {   assertTrue(true); }
        
        try
        {   wrapper.setMaximumDisplayedRows(-2);
            assertTrue(false);
        }
        catch(Exception e)
        {   assertTrue(true); }
        
        wrapper.setMaximumDisplayedRows(10);
        assertEquals(10, wrapper.getMaximumDisplayedRows());
        
        try
        {   wrapper.setMaximumDisplayedRows(-1);
            assertTrue(true);
        }
        catch(Exception e)
        {   assertTrue(false); }
    }

    /**
     * Test of setCurrentPage method, of class org.siberia.ui.swing.table.model.PageableTableModelWrapper.
     */
    public void testSetCurrentPage()
    {
        PageableTable wrapper = new PageableTable();
        wrapper.setModel(this.getSimpleModel());
        
        wrapper.setMaximumDisplayedRows(1);
        
        assertEquals(0, wrapper.getCurrentPage());
        
        wrapper.setCurrentPage(1);
        
        assertEquals(1, wrapper.getCurrentPage());
        
        wrapper.setCurrentPage(-1);
        assertEquals(0, wrapper.getCurrentPage());
        
        wrapper.setCurrentPage(56);
        assertEquals(3, wrapper.getCurrentPage());
        
        wrapper.setMaximumDisplayedRows(2);
        
        wrapper.setCurrentPage(1);
        assertEquals(1, wrapper.getCurrentPage());
        
        wrapper.setCurrentPage(-1);
        assertEquals(0, wrapper.getCurrentPage());
        
        wrapper.setCurrentPage(56);
        assertEquals(1, wrapper.getCurrentPage());
    }

    /**
     * Test of convertRowIndexToWrapping method, of class org.siberia.ui.swing.table.model.PageableTableModelWrapper.
     */
    public void testConvertRowIndexToWrapping()
    {   PageableTable wrapper = new PageableTable();
        
        wrapper.setMaximumDisplayedRows(4);
        
        assertEquals(3, wrapper.convertRowIndexToWrapping(3));
        assertEquals(3, wrapper.convertRowIndexToWrapping(7));
        assertEquals(0, wrapper.convertRowIndexToWrapping(8));
        assertEquals(1, wrapper.convertRowIndexToWrapping(1));
        assertEquals(0, wrapper.convertRowIndexToWrapping(0));
        
        wrapper.setMaximumDisplayedRows(1);
        
        assertEquals(0, wrapper.convertRowIndexToWrapping(3));
        assertEquals(0, wrapper.convertRowIndexToWrapping(7));
        assertEquals(0, wrapper.convertRowIndexToWrapping(8));
        assertEquals(0, wrapper.convertRowIndexToWrapping(1));
        assertEquals(0, wrapper.convertRowIndexToWrapping(0));
        
        wrapper.setMaximumDisplayedRows(5);
        
        assertEquals(3, wrapper.convertRowIndexToWrapping(3));
        assertEquals(2, wrapper.convertRowIndexToWrapping(7));
        assertEquals(3, wrapper.convertRowIndexToWrapping(8));
        assertEquals(4, wrapper.convertRowIndexToWrapping(9));
        assertEquals(0, wrapper.convertRowIndexToWrapping(10));
    }

    /**
     * Test of convertRowIndexToWrapped method, of class org.siberia.ui.swing.table.model.PageableTableModelWrapper.
     */
    public void testConvertRowIndexToWrapped()
    {   
        PageableTable wrapper = new PageableTable();
        wrapper.setModel(this.getExtendedModel());
        
        wrapper.setMaximumDisplayedRows(4);
        wrapper.setCurrentPage(0);
        
        assertEquals(3, wrapper.convertRowIndexToWrapped(3));
        assertEquals(2, wrapper.convertRowIndexToWrapped(2));
        assertEquals(1, wrapper.convertRowIndexToWrapped(1));
        assertEquals(0, wrapper.convertRowIndexToWrapped(0));
        
        wrapper.setCurrentPage(1);
        
        assertEquals(7, wrapper.convertRowIndexToWrapped(3));
        assertEquals(6, wrapper.convertRowIndexToWrapped(2));
        assertEquals(5, wrapper.convertRowIndexToWrapped(1));
        assertEquals(4, wrapper.convertRowIndexToWrapped(0));
        
        wrapper.setMaximumDisplayedRows(10);
        
        assertEquals(13, wrapper.convertRowIndexToWrapped(3));
        assertEquals(12, wrapper.convertRowIndexToWrapped(2));
        assertEquals(11, wrapper.convertRowIndexToWrapped(1));
        assertEquals(10, wrapper.convertRowIndexToWrapped(0));
        
        wrapper.setCurrentPage(5);
        wrapper.setMaximumDisplayedRows(5);
        
        assertEquals(28, wrapper.convertRowIndexToWrapped(3));
        assertEquals(27, wrapper.convertRowIndexToWrapped(2));
        assertEquals(26, wrapper.convertRowIndexToWrapped(1));
        assertEquals(25, wrapper.convertRowIndexToWrapped(0));
    }

    /**
     * Test of getValueAt method, of class org.siberia.ui.swing.table.model.PageableTableModelWrapper.
     */
    public void testGetValueAt()
    {
        PageableTable wrapper = new PageableTable();
        wrapper.setModel(this.getExtendedModel());
        
        wrapper.setMaximumDisplayedRows(10);
        wrapper.setCurrentPage(0);
        
        assertEquals("o-0-0", wrapper.getValueAt(0, 0));
        assertEquals("o-0-1", wrapper.getValueAt(0, 1));
        assertEquals("o-3-0", wrapper.getValueAt(3, 0));
        assertEquals("o-3-1", wrapper.getValueAt(3, 1));
        assertEquals("o-9-0", wrapper.getValueAt(9, 0));
        assertNull(wrapper.getValueAt(10, 0));
        assertNull(wrapper.getValueAt(9, 3));
        
        wrapper.setMaximumDisplayedRows(10);
        wrapper.setCurrentPage(2);
        assertEquals("o-20-0", wrapper.getValueAt(0, 0));
        assertEquals("o-20-1", wrapper.getValueAt(0, 1));
        assertEquals("o-23-0", wrapper.getValueAt(3, 0));
        assertEquals("o-23-1", wrapper.getValueAt(3, 1));
        assertEquals("o-29-0", wrapper.getValueAt(9, 0));
        assertNull(wrapper.getValueAt(10, 0));
        assertNull(wrapper.getValueAt(9, 3));
    }

    /**
     * Test of getRowCount method, of class org.siberia.ui.swing.table.model.PageableTableModelWrapper.
     */
    public void testGetRowCount()
    {
        PageableTable wrapper = new PageableTable();
        wrapper.setModel(this.getSimpleModel());
        
        wrapper.setMaximumDisplayedRows(10);
        wrapper.setCurrentPage(0);
        
        assertEquals(4, wrapper.getRowCount());
        
        wrapper.setMaximumDisplayedRows(2);
        
        assertEquals(2, wrapper.getRowCount());
        
        wrapper.setModel(this.getEmptyModel());
        
        assertEquals(0, wrapper.getRowCount());
    }

    /**
     * Test of getColumnCount method, of class org.siberia.ui.swing.table.model.PageableTableModelWrapper.
     */
    public void testGetColumnCount()
    {
        PageableTable wrapper = new PageableTable();
        wrapper.setModel(this.getSimpleModel());
        
        assertEquals(2, wrapper.getColumnCount());
        
        wrapper.setModel(this.getEmptyModel());
        
        assertEquals(4, wrapper.getColumnCount());
        
        wrapper.setModel(this.getSimpleModel());
        
        assertEquals(2, wrapper.getColumnCount());
        
//        wrapper.setDisplayRowNumber(true);
        
//        assertEquals(3, wrapper.getColumnCount());
    }

    /**
     * Test of getColumnCount method, of class org.siberia.ui.swing.table.model.PageableTableModelWrapper.
     */
    public void testGetPageCount()
    {
        PageableTable wrapper = new PageableTable();
        wrapper.setModel(this.getSimpleModel());
        
        wrapper.setMaximumDisplayedRows(2);
        assertEquals(2, wrapper.getPageCount());
        
        wrapper.setMaximumDisplayedRows(3);
        assertEquals(2, wrapper.getPageCount());
        
        wrapper.setMaximumDisplayedRows(4);
        assertEquals(1, wrapper.getPageCount());
        
        wrapper.setMaximumDisplayedRows(1);
        assertEquals(4, wrapper.getPageCount());
        
        wrapper.setModel(this.getEmptyModel());
        
        wrapper.setMaximumDisplayedRows(2);
        assertEquals(1, wrapper.getPageCount());
        
        wrapper.setMaximumDisplayedRows(3);
        assertEquals(1, wrapper.getPageCount());
        
        wrapper.setMaximumDisplayedRows(4);
        assertEquals(1, wrapper.getPageCount());
        
        wrapper.setModel(this.getExtendedModel());
        
        wrapper.setMaximumDisplayedRows(1);
        assertEquals(100, wrapper.getPageCount());
        
        wrapper.setMaximumDisplayedRows(2);
        assertEquals(50, wrapper.getPageCount());
        
        wrapper.setMaximumDisplayedRows(10);
        assertEquals(10, wrapper.getPageCount());
    }

//    /**
//     * Test of getColumnName method, of class org.siberia.ui.swing.table.model.PageableTableModelWrapper.
//     */
//    public void testGetColumnName()
//    {
//        PageableTable wrapper = new PageableTable();
//        TableModel simpleModel = this.getSimpleModel();
//        wrapper.setModel(simpleModel);
//        
//        assertEquals(simpleModel.getColumnName(0), wrapper.getColumnName(0));
//        assertEquals(simpleModel.getColumnName(1), wrapper.getColumnName(1));
//        
//        wrapper.setDisplayRowNumber(true);
//        
//        assertEquals(simpleModel.getColumnName(0), wrapper.getColumnName(1));
//        assertEquals(simpleModel.getColumnName(1), wrapper.getColumnName(2));
//        
//        wrapper.setDisplayRowNumber(false);
//        
//        assertEquals(simpleModel.getColumnName(0), wrapper.getColumnName(0));
//        assertEquals(simpleModel.getColumnName(1), wrapper.getColumnName(1));
//    }
//
//    /**
//     * Test of getColumnClass method, of class org.siberia.ui.swing.table.model.PageableTableModelWrapper.
//     */
//    public void testGetColumnClass()
//    {
//        PageableTable wrapper = new PageableTable();
//        TableModel simpleModel = this.getSimpleModel();
//        wrapper.setModel(simpleModel);
//        
//        assertEquals(simpleModel.getColumnClass(0), wrapper.getColumnClass(0));
//        assertEquals(simpleModel.getColumnClass(1), wrapper.getColumnClass(1));
//        
//        wrapper.setDisplayRowNumber(true);
//        
//        assertEquals(simpleModel.getColumnClass(0), wrapper.getColumnClass(1));
//        assertEquals(simpleModel.getColumnClass(1), wrapper.getColumnClass(2));
//        
//        wrapper.setDisplayRowNumber(false);
//        
//        assertEquals(simpleModel.getColumnClass(0), wrapper.getColumnClass(0));
//        assertEquals(simpleModel.getColumnClass(1), wrapper.getColumnClass(1));
//    }

    /**
     * Test of isCellEditable method, of class org.siberia.ui.swing.table.model.PageableTableModelWrapper.
     */
    public void testIsCellEditable()
    {
//        PageableTableModelWrapper wrapper = new PageableTableModelWrapper();
//        TableModel simpleModel = this.getSimpleModel();
//        wrapper.setWrappedModel(simpleModel);
//        
//        assertEquals(simpleModel.getColumnClass(0), wrapper.getColumnClass(0));
//        assertEquals(simpleModel.getColumnClass(2), wrapper.getColumnClass(1));
//        
//        wrapper.setDisplayRowNumber(true);
//        
//        assertEquals(simpleModel.getColumnClass(0), wrapper.getColumnClass(1));
//        assertEquals(simpleModel.getColumnClass(2), wrapper.getColumnClass(2));
//        
//        wrapper.setDisplayRowNumber(false);
//        
//        assertEquals(simpleModel.getColumnClass(0), wrapper.getColumnClass(0));
//        assertEquals(simpleModel.getColumnClass(2), wrapper.getColumnClass(1));
    }
    
}
