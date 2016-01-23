package org.siberia.ui.editor;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.ref.SoftReference;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import org.siberia.SiberiaIntrospector;
import org.siberia.type.SibList;
import org.siberia.type.SibString;
import org.siberia.type.SibType;
import org.siberia.type.info.BeanInfoCategory;
import org.siberia.ui.swing.TextFieldVerifier;
import org.siberia.ui.swing.property.ExtendedProperty;
import org.siberia.ui.swing.property.ExtendedPropertySheetPanel;
import org.siberia.ui.swing.property.ExtendedPropertySheetTable;
import org.siberia.ui.swing.property.ExtendedPropertySheetTableModel;
import org.siberia.ui.swing.table.SibListTablePanel;
import org.siberia.ui.swing.table.TablePanel;
import org.siberia.ui.swing.table.NumberedPageableTable;
import org.siberia.ui.swing.table.SibTypeListTable;
import org.siberia.ui.swing.property.ExtendedProperty;
import org.siberia.ui.swing.table.model.SibTypeListTableModel;

/**
 *
 * @author  alexis
 */
public abstract class DebugTablePanel<T extends TablePanel> extends javax.swing.JPanel
{
    private T pageablePanel = null;
    
    /** soft reference to a SiberiaIntrospector */
    private SoftReference<SiberiaIntrospector> introspectorRef       = new SoftReference<SiberiaIntrospector>(null);
    
    /** Creates new form DebugTablePanel */
    public DebugTablePanel()
    {
	initComponents();
	
	this.detailButton.addActionListener(new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
		try
		{
		    File f = File.createTempFile("tableDetail", ".txt");
		    PrintStream stream = new PrintStream(f);

		    pageablePanel.getTable().debug(stream);

		    stream.close();

		    Desktop.getDesktop().open(f);
		}
		catch (IOException ex)
		{
		    ex.printStackTrace();
		}
	    }
	});
	
	ButtonGroup group = new ButtonGroup();
	group.add(this.radioAdd);
	group.add(this.radioRemove);
	
	this.radioAdd.setSelected(true);
	
	this.spinnerFrom.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
	this.spinnerLength.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
	
	this.pageablePanel = this.createPageablePanel();
	
	this.buttonModifyList.addActionListener(new ActionListener()
	{
	    public void actionPerformed(ActionEvent e)
	    {
		TableModel model = pageablePanel.getTable().getModel();
		if ( model instanceof SibTypeListTableModel )
		{
		    SibList list = ((SibTypeListTableModel)model).getList();
		    
		    if ( list != null )
		    {
			System.out.println("list size before modification : " + list.size());

			int fromIndex = ((Number)spinnerFrom.getValue()).intValue() - 1;
			int length    = ((Number)spinnerLength.getValue()).intValue();

			if ( radioAdd.isSelected() )
			{
			    /** add length new items at fromIndex */
			    for(int i = 0; i < length; i++)
			    {
				SibString item = new SibString();
				
				try
				{   item.setName(item.getClass().getSimpleName() + "_" + i); }
				catch (PropertyVetoException ex)
				{   ex.printStackTrace(); }
				
				list.add(fromIndex + i, item);
			    }
			}
			else if ( radioRemove.isSelected() )
			{
			    for(int i = 0; i < length; i++)
			    {
				if ( fromIndex < list.size() )
				{
				    list.remove(fromIndex);
				}
				else
				{
				    break;
				}
			    }
			}
			
			System.out.println("list size after modification : " + list.size());
		    }
		}
	    }
	});
	
	((JSplitPane)horSplitPane).setTopComponent(this.pageablePanel);
	
	final ExtendedPropertySheetPanel propertyPanel = new ExtendedPropertySheetPanel(new ExtendedPropertySheetTable());
	
	verSplitPane.setTopComponent(propertyPanel);
	
	ListSelectionModel selectionModel = this.pageablePanel.getTable().getSelectionModel();
	selectionModel.addListSelectionListener(new ListSelectionListener()
	{
	    public void valueChanged(ListSelectionEvent e)
	    {
		if ( ! e.getValueIsAdjusting() )
		{
		    Object bean = null;
		    
		    if ( pageablePanel.getTable() instanceof SibTypeListTable )
		    {
			Object[] items = ((SibTypeListTable)pageablePanel.getTable()).getSelectedObjects();
			if ( items != null && items.length >= 1 )
			{
			    bean = items[0];
			}
			
			/** create the BeanInfo for the type and link the model with the current type */
			BeanInfo info = null;

			StringBuffer title = new StringBuffer();

			if ( bean instanceof SibType )
			{   info = getIntrospector().getBeanInfo((SibType)bean, BeanInfoCategory.ALL, true, true); }

			if ( info != null )
			{   
//			    System.out.println("bean info properties : ");
//			    PropertyDescriptor[] descs = info.getPropertyDescriptors();
//			    if ( descs != null )
//			    {
//				for(int i = 0; i < descs.length; i++)
//				{
//				    PropertyDescriptor current = descs[i];
//				    
//				    if ( current != null )
//				    {
//					System.out.println("\t" + current.getName() + " " + current.getDisplayName());
//				    }
//				}
//			    }
			    
			    propertyPanel.setBeanInfo(info);

			    if ( bean != null )
			    {   ExtendedPropertySheetTableModel propertyModel = ((ExtendedPropertySheetTable)propertyPanel.getTable()).getExtendedTableModel();
				if ( propertyModel != null )
				{   propertyModel.setObjects((SibType)bean);

				    propertyPanel.getTable().setPreferredScrollableViewportSize(new Dimension(100, 
										  Math.max(propertyModel.getRowCount() *
					    propertyPanel.getTable().getRowHeight(), 25)));

				    propertyPanel.readFromObject((SibType)bean);
				}
			    }
			}
			else
			{   propertyPanel.getTable().getSheetModel().setProperties(new ExtendedProperty[]{}); }
		    }
		}
	    }
	});
    }
    
    /** return a SiberiaIntrospector
     *  @return a SiberiaIntrospector
     */
    private SiberiaIntrospector getIntrospector()
    {   SiberiaIntrospector in = this.introspectorRef.get();
        if ( in == null )
        {   in = new SiberiaIntrospector();
            this.introspectorRef = new SoftReference<SiberiaIntrospector>(in);
        }
        
        return in;
    }
    
    /** create the TablePanel
     *	@return a TablePanel
     */
    protected abstract T createPageablePanel();
    
    /** returns the internal SibListTablePanel
     *	@return a SibListTablePanel
     */
    public T getTablePanel()
    {	return this.pageablePanel; }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        RadioGroup = new javax.swing.ButtonGroup();
        horSplitPane = new javax.swing.JSplitPane();
        tablePanel = new javax.swing.JPanel();
        toolsPanel = new javax.swing.JPanel();
        verSplitPane = new javax.swing.JSplitPane();
        PropertiesPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        detailButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        radioAdd = new javax.swing.JRadioButton();
        radioRemove = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        spinnerFrom = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        spinnerLength = new javax.swing.JSpinner();
        buttonModifyList = new javax.swing.JButton();

        horSplitPane.setDividerLocation(500);
        horSplitPane.setMinimumSize(new java.awt.Dimension(100, 100));
        horSplitPane.setPreferredSize(new java.awt.Dimension(412, 502));
        tablePanel.setMinimumSize(new java.awt.Dimension(400, 500));
        tablePanel.setPreferredSize(new java.awt.Dimension(400, 500));
        javax.swing.GroupLayout tablePanelLayout = new javax.swing.GroupLayout(tablePanel);
        tablePanel.setLayout(tablePanelLayout);
        tablePanelLayout.setHorizontalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 499, Short.MAX_VALUE)
        );
        tablePanelLayout.setVerticalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 590, Short.MAX_VALUE)
        );
        horSplitPane.setLeftComponent(tablePanel);

        verSplitPane.setDividerLocation(400);
        verSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        PropertiesPanel.setMinimumSize(new java.awt.Dimension(100, 400));
        PropertiesPanel.setPreferredSize(new java.awt.Dimension(100, 600));
        javax.swing.GroupLayout PropertiesPanelLayout = new javax.swing.GroupLayout(PropertiesPanel);
        PropertiesPanel.setLayout(PropertiesPanelLayout);
        PropertiesPanelLayout.setHorizontalGroup(
            PropertiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 194, Short.MAX_VALUE)
        );
        PropertiesPanelLayout.setVerticalGroup(
            PropertiesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 399, Short.MAX_VALUE)
        );
        verSplitPane.setTopComponent(PropertiesPanel);

        detailButton.setText("print details");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("modify list"));
        radioAdd.setText("add");
        radioAdd.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        radioAdd.setMargin(new java.awt.Insets(0, 0, 0, 0));

        radioRemove.setText("remove");
        radioRemove.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        radioRemove.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel1.setText("from row");

        jLabel2.setText("count");

        buttonModifyList.setText("do");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(radioAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(radioRemove))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(spinnerLength, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                            .addComponent(spinnerFrom, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE))))
                .addContainerGap())
            .addComponent(buttonModifyList, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radioRemove)
                    .addComponent(radioAdd))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(spinnerFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(spinnerLength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonModifyList))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(detailButton)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(detailButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );
        verSplitPane.setRightComponent(jPanel1);

        javax.swing.GroupLayout toolsPanelLayout = new javax.swing.GroupLayout(toolsPanel);
        toolsPanel.setLayout(toolsPanelLayout);
        toolsPanelLayout.setHorizontalGroup(
            toolsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(verSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
        );
        toolsPanelLayout.setVerticalGroup(
            toolsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(verSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE)
        );
        horSplitPane.setRightComponent(toolsPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(horSplitPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 707, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(horSplitPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PropertiesPanel;
    private javax.swing.ButtonGroup RadioGroup;
    private javax.swing.JButton buttonModifyList;
    private javax.swing.JButton detailButton;
    private javax.swing.JSplitPane horSplitPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton radioAdd;
    private javax.swing.JRadioButton radioRemove;
    private javax.swing.JSpinner spinnerFrom;
    private javax.swing.JSpinner spinnerLength;
    private javax.swing.JPanel tablePanel;
    private javax.swing.JPanel toolsPanel;
    private javax.swing.JSplitPane verSplitPane;
    // End of variables declaration//GEN-END:variables
    
}
