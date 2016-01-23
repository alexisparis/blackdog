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
package org.siberia.ui.swing.property;

import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertySheetTable;
import com.l2fprod.common.propertysheet.PropertySheetTableModel;
import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.lang.reflect.Method;
import java.util.ResourceBundle;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import org.apache.log4j.Logger;
import org.siberia.type.SibType;
import org.siberia.ui.swing.dialog.SwingWorkerDialog;

/**
 *
 * Extension to PropertySheetTable
 *set
 * @author alexis
 */
public class ExtendedPropertySheetTable extends PropertySheetTable implements PropertyVetoListener
{   
  private static final int HOTSPOT_SIZE = 18;
  
  private static final String TREE_EXPANDED_ICON_KEY = "Tree.expandedIcon";
  private static final String TREE_COLLAPSED_ICON_KEY = "Tree.collapsedIcon";
  private static final String TABLE_BACKGROUND_COLOR_KEY = "Table.background";
  private static final String TABLE_FOREGROUND_COLOR_KEY = "Table.foreground";
  private static final String TABLE_SELECTED_BACKGROUND_COLOR_KEY = "Table.selectionBackground";
  private static final String TABLE_SELECTED_FOREGROUND_COLOR_KEY = "Table.selectionForeground";
  private static final String PANEL_BACKGROUND_COLOR_KEY = "Panel.background";
  
    /** logger */
    private Logger                           logger              = Logger.getLogger(ExtendedPropertySheetTable.class);
    
    /** Object actually managed by the model */
    private Object                           object              = null;
    
    /** string to consider to renderer multiple values when managing severall types */
    private String                           multipleValuesLabel = null;
    
    private TableCellRenderer                nameRenderer        = null;

  // Colors used by renderers
  private Color categoryBackground;
  private Color categoryForeground;
  private Color propertyBackground;
  private Color propertyForeground;
  private Color selectedPropertyBackground;
  private Color selectedPropertyForeground;
  private Color selectedCategoryBackground;
  private Color selectedCategoryForeground;
    
    /** Creates a new instance of ExtendedPropertySheetTable */
    public ExtendedPropertySheetTable()
    {   super(new ExtendedPropertySheetTableModel());
        
        /** set custom factories */
        this.setRendererFactory(new ExtendedPropertyRendererFactory());
        this.setEditorFactory(new ExtendedPropertyEditorFactory());
	
	nameRenderer = new NameRenderer();
    }
    
  private void initDefaultColors() {    
    this.categoryBackground = UIManager.getColor(PANEL_BACKGROUND_COLOR_KEY);
    this.categoryForeground = UIManager.getColor(TABLE_FOREGROUND_COLOR_KEY).darker().darker().darker();
    
    this.selectedCategoryBackground = categoryBackground.darker();
    this.selectedCategoryForeground = categoryForeground;
    
    this.propertyBackground = UIManager.getColor(TABLE_BACKGROUND_COLOR_KEY);
    this.propertyForeground = UIManager.getColor(TABLE_FOREGROUND_COLOR_KEY);
    
    this.selectedPropertyBackground = UIManager
      .getColor(TABLE_SELECTED_BACKGROUND_COLOR_KEY);
    this.selectedPropertyForeground = UIManager
      .getColor(TABLE_SELECTED_FOREGROUND_COLOR_KEY);
    
    setGridColor(categoryBackground);
  }
    
    /** try to launch edition on the value column related to the given property
     *	@param propertyName the name of a property
     *	@return true if the edition was successfully launched
     */
    public boolean editAtProperty(String propertyName)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling editAtProperty(" + propertyName + ")");
	}
	
	boolean result = false;
	
	ExtendedPropertySheetTableModel model = this.getExtendedTableModel();
	
	if ( model == null )
	{
	    logger.warn("model is null --> could not edit");
	}
	else
	{
	    int rowModelIndex = model.getRowForProperty(propertyName);
	    
	    if ( rowModelIndex >= 0 && rowModelIndex < model.getRowCount() )
	    {
		int convertedrowIndex = this.convertRowIndexToView(rowModelIndex);
		
		if ( convertedrowIndex >= 0 && convertedrowIndex < this.getRowCount() )
		{
		    result = this.editCellAt(convertedrowIndex, model.VALUE_COLUMN);
		    
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("editCellAt(" + convertedrowIndex + ", " + model.VALUE_COLUMN + ") on " + this + " returns " + result);
		    }
		}
		else
		{
		    logger.warn("row view index is " + convertedrowIndex + " but must be in [0, " + this.getRowCount() + "[");
		}
	    }
	    else
	    {
		logger.warn("row model index is " + rowModelIndex + " but must be in [0, " + model.getRowCount() + "[");
	    }
	}
	
	return result;
    }

    /* Override setModel to add the table as PropertyVetoListener for the table */
    @Override
    public void setModel(TableModel newModel)
    {   
        ExtendedPropertySheetTableModel model = this.getExtendedTableModel();
        if ( model != null )
        {   model.removePropertyVetoListener(this); }
        
        super.setModel(newModel);
        
        model = this.getExtendedTableModel();
        if ( model != null )
        {   model.addPropertyVetoListener(this); }
    }
    
    private void _setValueAt(Object aValue, int row, int column)
    {
	super.setValueAt(aValue, row, column);
    }

    @Override
    public void setValueAt(final Object aValue, final int row, final int column)
    {
	SibType[] types = this.getExtendedTableModel().getObjects();
	
	SwingWorkerDialog _dialog = null;
	
	try
	{
	    boolean applyModifications = true;
	    
	    boolean warnUser = false;
	    
	    if ( types != null && types.length > 1 )
	    {
		warnUser = true;
	    }
	    
	    if ( warnUser )
	    {
		ResourceBundle rb = ResourceBundle.getBundle(ExtendedPropertySheetTable.class.getName());
		
		String title = rb.getString("dialog.multipleModifications.title");
		StringBuffer buffer = new StringBuffer();
		buffer.append(rb.getString("dialog.multipleModifications.message"));
		
		String motif = "{items_count}";
		int motifPosition = buffer.indexOf(motif);
		if ( motifPosition > -1 )
		{
		    buffer.delete(motifPosition, motifPosition + motif.length());
		    buffer.insert(motifPosition, (types == null ? "0" : Integer.toString(types.length)));
		}
		
		int answer = JOptionPane.showConfirmDialog(this, buffer.toString(), title, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		
		if ( answer != JOptionPane.YES_OPTION )
		{
		    applyModifications = false;
		}
	    }
	    
	    if ( applyModifications )
	    {
		if ( types != null && types.length > 5 )
		{
		    final SwingWorkerDialog dialog = new SwingWorkerDialog(SwingUtilities.getWindowAncestor(this), true);
		    _dialog = dialog;
		    dialog.setDifferWorkerExecutionEnabled(false);
		    dialog.setWorker(new SwingWorker()
		    {
			protected Object doInBackground() throws Exception
			{
			    try
			    {
				_setValueAt(aValue, row, column);
			    }
			    catch(Exception e)
			    {
				logger.error("got exception while calling setValueAt(" + aValue + ", " + row + ", " + column + ")", e);
			    }

			    this.setProgress(100);

			    return null;
			}
		    });

		    dialog.setLocationRelativeTo(null);

		    ResourceBundle rb = ResourceBundle.getBundle(ExtendedPropertySheetTable.class.getName());

		    dialog.getLabel().setText(rb.getString("dialog.applyModifications.text"));

		    dialog.getProgressBar().setIndeterminate(true);

		    dialog.display();
		}
		else
		{
		    super.setValueAt(aValue, row, column);
		}
	    }
	}
	finally
	{
//	    if ( _dialog != null && _dialog.isVisible() )
//	    {
//		_dialog.setVisible(false);
//		_dialog.dispose();
//	    }
	}
    }

    @Override
    public Object getValueAt(int row, int column)
    {
	Object o = super.getValueAt(row, column);
	
	if ( o instanceof ExtendedPropertySheetTableModel.DifferentValues )
	{
	    /** special case for enum, i cannot return null which is an invalid value
	     *	so, we return the first item of the enumeration
	     *
	     *	if i return directly null, my EnumComboBoxModel will not know which class
	     *	is the enum and won't be able to initialize its combo model !!
	     */
	    com.l2fprod.common.propertysheet.PropertySheetTableModel.Item item = this.getSheetModel().getPropertySheetElement(row);
	    if ( item == null )
	    {
		o = null;
	    }
	    else
	    {
		Property property = item.getProperty();
		
		if ( property == null )
		{
		    o = null;
		}
		else
		{
		    Class c = property.getType();
			
		    if ( c == null )
		    {
			o = null;
		    }
		    else
		    {
			if ( Enum.class.isAssignableFrom(c) )
			{
			    try
			    {
				Method m = c.getMethod("values", new Class[]{});
				Object[] enumItems = (Object[])m.invoke(null, new Object[]{});

				/** try to take the first item */
				if ( enumItems == null || enumItems.length == 0 )
				{
				    o = null;
				}
				else
				{
				    o = enumItems[0];
				}
			    }
			    catch(Exception e)
			    {
				e.printStackTrace();
				o = null;
			    }
			}
			else
			{
			    o = null;
			}
		    }
		}
	    }
	}
	
	return o;
    }
    
    /** return the string related to cell that have severall values when managing severall types
     *	@return a String
     */
    private String getMultipleValuesLabel()
    {
	if ( this.multipleValuesLabel == null )
	{
	    ResourceBundle rb = ResourceBundle.getBundle(this.getClass().getName());

	    if ( rb == null )
	    {
		this.multipleValuesLabel = "<Multiple values>";
	    }
	    else
	    {
		this.multipleValuesLabel = rb.getString("multiplevalues.label");
	    }
	}
	
	return this.multipleValuesLabel;
    }

    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
    {
	Component c = super.prepareRenderer(renderer, row, column);
	
	Object value = super.getValueAt(row, column);
	if ( c instanceof JLabel && value instanceof ExtendedPropertySheetTableModel.DifferentValues )
	{   
	    ((JLabel)c).setText(this.getMultipleValuesLabel());
	    ((JLabel)c).setHorizontalAlignment(SwingConstants.CENTER);
	}
	
	return c;
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column)
    {
	TableCellRenderer renderer = null;
	
	Object o = super.getValueAt(row, column);
	
	if ( o instanceof ExtendedPropertySheetTableModel.DifferentValues )
	{
	    /** use the TableCellRenderer to use for String */
	    renderer = this.getDefaultRenderer(String.class);
	}
	if ( column == 0 )
	{
	    renderer = this.nameRenderer;
	}
	if ( renderer == null )
	{
	    renderer = super.getCellRenderer(row, column);
	}
	
	return renderer;
    }

    public void updateUI()
    {
	setRendererFactory(new ExtendedPropertyRendererFactory());
	setEditorFactory(new ExtendedPropertyEditorFactory());
	
	TableCellRenderer renderer = this.nameRenderer;
        Component component = null;
        if (renderer instanceof Component) {
            component = (Component)renderer;
        }

        if (component != null)
	{   SwingUtilities.updateComponentTreeUI(component); }
	
	super.updateUI();
	
	initDefaultColors();
    }
    
    /** method that returns the table model
     *  @return an instance of ExtendedPropertySheetTableModel
     */
    public ExtendedPropertySheetTableModel getExtendedTableModel()
    {   ExtendedPropertySheetTableModel extendedModel = null;
        TableModel model = this.getModel();
        if ( model instanceof ExtendedPropertySheetTableModel )
            extendedModel = (ExtendedPropertySheetTableModel)model;
        return extendedModel;
    }
	
    /** return true if the page allow to specify the given property
     *  @param propertyName the name of the property
     *  @return true if the page allow to specify the given property
     */
    public boolean allowToSpecifyProperty(String propertyName)
    {
	boolean result = false;
	
	if ( propertyName != null )
	{
	    ExtendedPropertySheetTableModel model = this.getExtendedTableModel();
	    if ( model != null )
	    {
		Property[] properties = model.getProperties();

		if ( properties != null )
		{
		    for(int i = 0; i < properties.length; i++)
		    {
			Property current = properties[i];

			if ( current != null )
			{
			    if ( propertyName.equals(current.getName()) )
			    {
				result = true;
				break;
			    }
			}
		    }
		}
	    }
	}

	return result;
    }

    public String getToolTipText(MouseEvent event)
    {
        String tooltip = super.getToolTipText(event);
        
        if ( tooltip == null )
        {   int row    = this.rowAtPoint(event.getPoint());
            int column = this.columnAtPoint(event.getPoint());
            
            if ( row > -1 )
            {   
		if ( column == 0 )
		{
		    Property prop = null;
		    if ( row >= 0 && this.getRowCount() > row )
		    {
			Item item = ((ExtendedPropertySheetTableModel)this.getModel()).getPropertySheetElement(row);
			
			if ( item != null )
			{
			    prop = item.getProperty();
			}
		    }
		    if (prop != null)
		    {
			StringBuffer buffer = new StringBuffer();
			
			buffer.append("<html><b>" + (prop.getDisplayName() == null?"":prop.getDisplayName()));
			buffer.append("</b><br>");
			buffer.append(prop.getShortDescription() == null ? "" : prop.getShortDescription());
			buffer.append("</html>");
			
			tooltip = buffer.toString();
		    }
		}
		else if ( column > 0 )
		{
		    Object value = this.getModel().getValueAt(row, column);

		    if ( value instanceof ExtendedPropertySheetTableModel.DifferentValues )
		    {
			tooltip = this.getMultipleValuesLabel();
		    }
		    else
		    {
			tooltip = String.valueOf(value);
		    }
		}
            }
        }
        
        return tooltip;
    }
    
    /* #########################################################################
     * ################# PropertyVetoListener implementation ###################
     * ######################################################################### */
    
    /** method that is called when the model catch a PropertyVetoException
     *  @param model the model that catched an exception of kind PropertyVetoException
     *  @param the current object managed by the model
     *  @param item the Item that is responsible
     *  @param exception a PropertyVetoException
     */
    public void vetoException(ExtendedPropertySheetTableModel model,
                              Object object,
                              Item item,
                              PropertyVetoException exception)
    {
        /** use message provided by VetoableChangeListener to indicate to the user the reason why
         *  change could not be applied
         */
        JOptionPane.showMessageDialog(SwingUtilities.getRoot(this), (exception == null ? "Cannot change value" : exception.getMessage()),
                                      "Error", JOptionPane.ERROR_MESSAGE);
    }
    
  private class NameRenderer extends DefaultTableCellRenderer {

    private CellBorder border;
    
    public NameRenderer() {
      border = new CellBorder();
    }
    
    private Color getForeground(boolean isProperty, boolean isSelected) {
      return (isProperty ? (isSelected ? selectedPropertyForeground : propertyForeground) :
        (isSelected ? selectedCategoryForeground : categoryForeground));
    }

    private Color getBackground(boolean isProperty, boolean isSelected) {
      return (isProperty ? (isSelected ? selectedPropertyBackground : propertyBackground) :
        (isSelected ? selectedCategoryBackground : categoryBackground));
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
      super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
      PropertySheetTableModel.Item item = (Item) value;

      // shortcut if we are painting the category column
      if (column == PropertySheetTableModel.VALUE_COLUMN && !item.isProperty()) {
        setBackground(getBackground(item.isProperty(), isSelected));
        setText("");
        return this;
      }
      
      setBorder(border);

      // configure the border
      border.configure((PropertySheetTable)table, item);
      
      setBackground(getBackground(item.isProperty(), isSelected));
      setForeground(getForeground(item.isProperty(), isSelected));
      
      setEnabled(isSelected || !item.isProperty() ? true : item.getProperty().isEditable());
      setText(item.getName());

      return this;
    }
  }

  private static class ExpandedIcon implements Icon {
    public void paintIcon(Component c, Graphics g, int x, int y) {
      Color backgroundColor = c.getBackground();

      if (backgroundColor != null)
        g.setColor(backgroundColor);
      else g.setColor(Color.white);
      g.fillRect(x, y, 8, 8);
      g.setColor(Color.gray);
      g.drawRect(x, y, 8, 8);
      g.setColor(Color.black);
      g.drawLine(x + 2, y + 4, x + (6), y + 4);
    }
    public int getIconWidth() {
      return 9;
    }
    public int getIconHeight() {
      return 9;
    }
  }

  private static class CollapsedIcon extends ExpandedIcon {
    public void paintIcon(Component c, Graphics g, int x, int y) {
      super.paintIcon(c, g, x, y);
      g.drawLine(x + 4, y + 2, x + 4, y + 6);
    }
  }
  private static class CellBorder implements Border {
    
    private int indentWidth; // space before hotspot
    private boolean showToggle;
    private boolean toggleState;
    private Icon expandedIcon;
    private Icon collapsedIcon;
    private Insets insets = new Insets(1, 0, 1, 1);
    private boolean isProperty;
    
    public CellBorder() {
      expandedIcon = (Icon)UIManager.get(TREE_EXPANDED_ICON_KEY);
      collapsedIcon = (Icon)UIManager.get(TREE_COLLAPSED_ICON_KEY);
      if (expandedIcon == null) {
        expandedIcon = new ExpandedIcon();
      }
      if (collapsedIcon == null) {
        collapsedIcon = new CollapsedIcon();
      }
    }

    public void configure(PropertySheetTable table, Item item) {      
      isProperty = item.isProperty();      
      toggleState =  item.isVisible();
      showToggle = item.hasToggle();
      
      indentWidth = 0;//getIndent(table, item);      
      insets.left = indentWidth + (showToggle?HOTSPOT_SIZE:0) + 2;;
    }
    
    public Insets getBorderInsets(Component c) {
      return insets;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width,
        int height) {      
      if (!isProperty) {
        Color oldColor = g.getColor();      
        g.setColor(c.getBackground());
        g.fillRect(x, y, x + HOTSPOT_SIZE - 2, y + height);
        g.setColor(oldColor);
      }
      
      if (showToggle) {
        Icon drawIcon = (toggleState ? expandedIcon : collapsedIcon);
        drawIcon.paintIcon(c, g,
          x + indentWidth + (HOTSPOT_SIZE - 2 - drawIcon.getIconWidth()) / 2,
          y + (height - drawIcon.getIconHeight()) / 2);
      }
    }

    public boolean isBorderOpaque() {
      return true;
    }
    
  }
}
