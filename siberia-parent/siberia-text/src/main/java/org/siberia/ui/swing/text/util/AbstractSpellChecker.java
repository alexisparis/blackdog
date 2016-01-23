package org.siberia.ui.swing.text.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.siberia.type.SibCollection;
import org.siberia.type.SibList;
import org.siberia.exception.ResourceException;
import org.siberia.type.lang.SibImport;
import org.siberia.type.lang.LangageElement;
import org.siberia.type.service.ServiceProvider;
import org.siberia.type.service.Task;
import org.siberia.type.service.event.TaskListener;
import org.siberia.ResourceLoader;
import org.siberia.TypeInformationProvider;
import org.siberia.type.event.ContentChangeEvent;


/**
 *
 * Abstract spell checker
 *
 * @author alexis
 */
public abstract class AbstractSpellChecker implements SpellChecker,
                                                      ActionListener,
                                                      TaskListener
{
    /** static element identifying what if displayed in the JPopupMenu */
    private static int DISPLAY_TABLE     = 0;
    private static int DISPLAY_WAIT      = 1;
    private static int DISPLAY_NO_RESULT = 2;
    
    private static final int LINE_COUNT = 8;

    private JPopupMenu     spellBox          = null;
    
    protected JTable       resultTable       = null;
    
    private JScrollPane    scrollPane        = null;
    
    private boolean        isRunning         = false;
    
    private Component      invoker           = null;
    
    private MethodTableModel model           = null;
    
    private String         sentenceDelimiter = null;
    
    private String         oldInput          = null;
    
    private SibList      list              = null;
    
    private JLabel         waitingLabel      = null;
    
    private JLabel         noResultLabel     = null;
    
    private int            displayType       = -1;
    
    private Timer          waitingTimer      = null;
    
    /** listener which will be warned when a choice will be made in the table using mouse */
    private ActionListener actionListener    = null;
    
    /** Creates a new instance of JavaSpellChecker */
    public AbstractSpellChecker()
    {   this.resultTable = new JTable();
        this.resultTable.setGridColor(Color.WHITE);
        this.resultTable.setPreferredScrollableViewportSize(new Dimension(300, this.resultTable.getRowHeight() * LINE_COUNT));
        this.resultTable.setFocusable(false);
        
        this.resultTable.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {   if ( e.getClickCount() >= 2 && e.getButton() == MouseEvent.BUTTON1 )
                {   if ( AbstractSpellChecker.this.actionListener != null )
                    {   AbstractSpellChecker.this.actionListener.actionPerformed(null); }
                }
            }
            
        });
        
        this.list = new SibList();
        
        this.model = new MethodTableModel();
        this.model.configure(this.list);
        
        this.resultTable.setModel(this.model);
        
        this.resultTable.getColumnModel().getColumn(0).setPreferredWidth(20);
        this.resultTable.getColumnModel().getColumn(0).setMaxWidth(20);
        
        this.waitingLabel  = new JLabel("<html><font size=\"-2\" color=\"blue\"><i>Wait...</i></color></html>");
        this.noResultLabel = new JLabel("<html><font size=\"-2\" color=\"red\"><i>No result</i></color></html>");
        
        Border border = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
        this.waitingLabel.setBorder(border);
        this.noResultLabel.setBorder(border);
        
        this.waitingTimer = new Timer(1000, this);
        this.waitingTimer.setRepeats(false);
        
        this.getServiceProvider().addTaskListener(this);
        
    }
    
    public void setSentenceDelimiter(String delimiter)
    {   this.sentenceDelimiter = delimiter; }
    
    public String getSentenceDelimiter()
    {   return this.sentenceDelimiter; }
    
    public boolean isRunning()
    {   return this.isRunning; }
    
    public void setInvoker(Component component)
    {   this.invoker = component; }
    
    /** return a ServiceProvider needed by the spellChecker
     *  @return a ServiceProvider needed by the spellChecker
     */
    protected abstract ServiceProvider getServiceProvider();
    
    /** update the content of the JPopupMenu according to the context
     *  @param waitTime indicates if the spellChecker is in wait mode
     */
    private void updateGraphicalContext(boolean waitTime)
    {   
//        System.out.println("nombre de composant dans la popup : " + this.spellBox.getComponentCount());
        if ( this.spellBox == null ) return;
//        System.out.println("displayType : " + this.displayType);
        if ( waitTime )
        {   if ( this.displayType == DISPLAY_WAIT )
            {   if ( ! this.spellBox.isVisible() )
                    this.spellBox.setVisible(true);
                return;
            }
            else
            {   if ( this.displayType == DISPLAY_TABLE )
                    this.spellBox.remove(this.scrollPane);
                else if ( this.displayType == DISPLAY_NO_RESULT )
                    this.spellBox.remove(this.noResultLabel);
                this.spellBox.add(this.waitingLabel);
                
                this.displayType = DISPLAY_WAIT;
            }
        }
        else
        {   if ( this.displayType == DISPLAY_WAIT )
            {   this.spellBox.remove(this.waitingLabel); }
            
            /* is there any result ? */
            if ( this.model.getRowCount() > 0 )
            {   if ( this.displayType == DISPLAY_NO_RESULT )
                    this.spellBox.remove(this.noResultLabel);
                this.spellBox.add(this.scrollPane, 0);
                this.displayType = DISPLAY_TABLE;
            }
            else
            {   if ( this.displayType == DISPLAY_TABLE )
                    this.spellBox.remove(this.scrollPane);
                this.spellBox.add(this.noResultLabel, 0);
                this.displayType = DISPLAY_NO_RESULT;
            }
        }
        
        if ( ! this.spellBox.isVisible() )
            this.spellBox.setVisible(true);
        
        this.updateSize();
    }
    
    /** add methods to the list */
//    public abstract List<LangageElement> modifyContent(String input);
    
    /** method which allow to modify the list */
    public void updateList(String input)
    {       
        //System.out.println("updateList");
        if ( input.startsWith("\n") )
            input = input.substring(1);
            
        boolean shouldConfigure = false;
        
        if ( this.oldInput == null )
            shouldConfigure = true;
        else if ( ! input.startsWith(this.oldInput) )
            shouldConfigure = true;
        
        if ( input.endsWith(".") )
            shouldConfigure = true;
        
        if ( shouldConfigure )
        {   
            if ( this.waitingTimer.isRunning() )
                this.waitingTimer.stop();
            this.waitingTimer.start();
            
            this.updateGraphicalContext(true);
        
            if ( this.getServiceProvider().isRunning() )
            {   this.getServiceProvider().shouldReinit();
                this.list.clear();
                this.getServiceProvider().configureResearch(input, this.list);
                return;
            }    

            this.list.clear();
            this.getServiceProvider().configureResearch(input, this.list);

            Thread t = new Thread(this.getServiceProvider());
            t.start();
        }
        else
        {   int index = input.lastIndexOf(".");
            String processedInput = input.substring(index + 1);
            
            this.model.filter(this.oldInput, processedInput);
        
            this.updateSize();
        }
        
        this.oldInput = input;
    }
    
    /** tell the spellChecker about a parsing result
     *  @param map a map<String, Class>
     */
    public void setParsingResults(Map<String, Class> map)
    {   if ( this.getServiceProvider() != null )
            this.getServiceProvider().setParserResult(map);
    }
    
    /** set the import used
     *  @param imports a list of SibImport
     */
    public void setImports(List<SibImport> imports)
    {   if ( this.getServiceProvider() != null )
            this.getServiceProvider().setImports(imports);
    }
    
    /** return the current selected object */
    public String getCurrentWord()
    {   if ( this.resultTable != null )
        {   LangageElement result = this.model.getElementAt(this.resultTable.getSelectedRow());
            if ( result == null )
                return null;
            
            return result.getCompletionObject();
        }
        
        return null;
    }
    
    /** set the actionListener that will be warned when a choice will be made */
    public void setActionListener(ActionListener actionListener)
    {   this.actionListener = actionListener; }
    
    public void run(String input, Point point)
    {   //input = "";
        if ( this.invoker == null ) return;
        
        String critere = input.substring(input.lastIndexOf('.') + 1);
        
        if ( this.spellBox == null )
        {   this.spellBox = new JPopupMenu()
            {
                protected void firePopupMenuWillBecomeInvisible()
                {   super.firePopupMenuWillBecomeInvisible();

                    AbstractSpellChecker.this.isRunning = false;
                }
            };

            this.resultTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            /** select first row */
            if ( this.resultTable.getModel().getRowCount() > 0 )
                this.resultTable.getSelectionModel().setSelectionInterval(0, 0);

            this.spellBox.getActionMap().put("Quit", new AbstractAction()
                {
                    public void actionPerformed(ActionEvent e)
                    {   AbstractSpellChecker.this.stop(); }
                });
            this.spellBox.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Quit");

            this.spellBox.setFocusable(false);
            this.spellBox.setBorderPainted(false);
        }

        if ( this.spellBox.getInvoker() != this.invoker )
            this.spellBox.setInvoker(this.invoker);
        
        this.scrollPane = new JScrollPane(this.resultTable);
        
//        this.updateGraphicalContext(false);

//        if ( this.spellBox.getComponentCount() == 0 )
//        {   this.spellBox.add(new JScrollPane(this.resultTable)); }

        this.spellBox.show(this.invoker, point.x, point.y);

        this.isRunning = true;
        
        this.updateList(input);
    }
    
    public void nextPage()
    {   if ( this.spellBox != null )
        {   int posSel = this.resultTable.getSelectionModel().getMaxSelectionIndex();
            
            if ( posSel < this.resultTable.getModel().getRowCount() -  1)
            {   posSel = Math.min(posSel + LINE_COUNT, this.resultTable.getModel().getRowCount() - 1);
                this.resultTable.getSelectionModel().setSelectionInterval(posSel, posSel);
                
                this.resultTable.scrollRectToVisible(this.resultTable.getCellRect(posSel, 0, true));
            }
        }
    }
    
    public void previousPage()
    {   if ( this.spellBox != null )
        {   int posSel = this.resultTable.getSelectionModel().getMaxSelectionIndex();
            
            if ( posSel > 0 )
            {   posSel  = Math.max(posSel - LINE_COUNT, 0);
                this.resultTable.getSelectionModel().setSelectionInterval(posSel, posSel);
                
                this.resultTable.scrollRectToVisible(this.resultTable.getCellRect(posSel, 0, true));
            }
        }
    }
    
    public void selectNext()
    {   if ( this.spellBox != null )
        {   int posSel = this.resultTable.getSelectionModel().getMinSelectionIndex();
            
            if ( posSel < this.resultTable.getModel().getRowCount() -  1)
            {   posSel ++; }
            else posSel = 0;
            
            this.resultTable.getSelectionModel().setSelectionInterval(posSel, posSel);

            Rectangle rec = this.resultTable.getCellRect(posSel, 0, true);
            
            this.resultTable.scrollRectToVisible(rec);
        }
    }
    
    public void selectPrevious()
    {   if ( this.spellBox != null )
        {   int posSel = this.resultTable.getSelectionModel().getMaxSelectionIndex();
            
            if ( posSel > 0 )
            {   posSel --; }
            else
            {   posSel = this.resultTable.getRowCount() - 1; }
                
            this.resultTable.getSelectionModel().setSelectionInterval(posSel, posSel);

            this.resultTable.scrollRectToVisible(this.resultTable.getCellRect(posSel, 0, true));
        }
    }
    
    public void stop()
    {   if ( this.spellBox != null )
        {   if ( this.spellBox.isVisible() )
            {   this.spellBox.setVisible(false);
                this.oldInput = null;
            }
            
            this.spellBox.remove(this.scrollPane);
            this.spellBox.remove(this.waitingLabel);
            this.spellBox.remove(this.noResultLabel);
        }
    }
    
    public void updatePosition(Point p)
    {   if ( this.spellBox != null )
        {   if ( this.spellBox.isVisible() )
            {   Point pt = (Point)p.clone();
                pt.translate(this.spellBox.getInvoker().getLocationOnScreen().x, 
                             this.spellBox.getInvoker().getLocationOnScreen().y);
                this.spellBox.setLocation(pt);
            }
        }
    }
    
    public synchronized void updateSize()
    {   /* update width according to row count */
        Dimension dim = null;
        
        if ( this.displayType == DISPLAY_TABLE )
        {   int lineCount = Math.min(LINE_COUNT, this.model.getRowCount());
            dim = new Dimension(300, this.resultTable.getRowHeight() * lineCount);
        }
        else
        {   int width = Math.max(this.waitingLabel.getSize().width, this.noResultLabel.getSize().width);
            int height = Math.max(this.waitingLabel.getSize().height, this.noResultLabel.getSize().height);
            dim = new Dimension(width, height);
        }
        
        this.resultTable.setPreferredScrollableViewportSize(dim);
        
        Dimension dim2 = new Dimension(dim.width, dim.height + 5);
        this.scrollPane.setSize(dim2);
        this.spellBox.setSize(dim2);
        this.spellBox.setPreferredSize(dim2);
        this.spellBox.setMaximumSize(dim2);
        
//        System.out.println("dimension : " + dim);
        this.spellBox.revalidate();
        this.spellBox.repaint();
    }
    
    /* #########################################################################
     * ################### ActionListener implementation #######################
     * ######################################################################### */
    
    public void actionPerformed(ActionEvent e)
    {   /** timer has finished */
        if ( e.getSource() == this.waitingTimer )
        {   this.updateGraphicalContext(false); }
    }
    
    /* #########################################################################
     * #################### TaskListener implementation ########################
     * ######################################################################### */
    
    /** acts when the task finished */
    public void taskFinished(Task task)
    {   if ( task == this.getServiceProvider() )
        {   if ( this.waitingTimer.isRunning() )
                this.waitingTimer.stop();
            this.updateGraphicalContext(false); }
    }
    
    /** ########################################################################
     *  ######################### own table model ##############################
     *  ######################################################################## */
    
    /** list model to represent methods & attributes */
    private class MethodTableModel implements TableModel,
                                              PropertyChangeListener
    {
        /** list of listeners **/
        private List<TableModelListener> listeners = new ArrayList<TableModelListener>();
        
        /** content of the list **/
        private SibList           content;
       
        public void configure(SibList list)
        {    this.content = list;
             
             this.content.addPropertyChangeListener(this);
        }
        
        public void reset()
        {   if ( this.content != null )
            {   this.content.clear();
                this.fireTableChanged(new TableModelEvent(this));
            }
        }
    
        public void propertyChange(PropertyChangeEvent evt)
        {   
            if ( evt.getSource() == this.content && evt.getPropertyName().equals("content") )
            {   TableModelEvent event = null;
                
                if ( evt instanceof ContentChangeEvent )
                {
                    
                    int mode = ((ContentChangeEvent)evt).getMode() == ContentChangeEvent.ADD ? 
                                                TableModelEvent.INSERT : TableModelEvent.DELETE;
                    int[] rowChanged = ((ContentChangeEvent)evt).getPosition();
                    if ( rowChanged != null )
                    {   for(int i = 0; i < rowChanged.length; i++)
                        {   event = new TableModelEvent(this, i, i, TableModelEvent.ALL_COLUMNS, mode);
                            this.fireTableChanged(event);
                        }
                    }
                    
                
//                if ( AbstractSpellChecker.this.displayType == DISPLAY_TABLE )
//                    AbstractSpellChecker.this.updateSize();
                }
                this.fireTableChanged(new TableModelEvent(this));
            }
        }
        
        public void filter(String oldInput, String newInput)
        {   LangageElement currentElement = null;
            for(int i = 0; i < this.content.size(); i++)
            {   currentElement = (LangageElement)this.content.get(i);
                
                if ( ! currentElement.getName().startsWith(newInput) )
                {   this.content.remove(i);
                    
                    i--;
                }
            }
                
            this.fireTableChanged(new TableModelEvent(this));
            
            AbstractSpellChecker.this.updateSize();
        }
        
        public Class getColumnClass(int columnIndex)
        {   if ( columnIndex == 0 ) return Icon.class;
            else                    return String.class;
        }
        
        public String getColumnName(int columnIndex)
        {   return null; }
        
        public int getColumnCount()
        {   return 2; }
        
        public int getRowCount()
        {   return this.content == null ? 0 : this.content.size(); }
        
        public boolean isCellEditable(int rowIndex, int columnIndex)
        {   return false; }
        
        public LangageElement getElementAt(int index)
        {   if ( index < 0 || index >= this.content.size() )
                return null;
            
            return (LangageElement)this.content.get(index);
        }
       
        public Object getValueAt(int rowIndex, int columnIndex)
        {    if ( columnIndex == 0 )
             {   LangageElement value = (LangageElement)this.content.get(rowIndex);
                try
                {   String resPath = TypeInformationProvider.getIconResource(value);
                    if ( resPath != null )
                    {   return ResourceLoader.getInstance().getIconNamed(resPath); }
                }
                catch (ResourceException ex)
                {   ex.printStackTrace(); }
                 
                return null;
             }
             else
                 return ((LangageElement)this.content.get(rowIndex)).toHtml();
        }
       
        public void setValueAt(Object value, int rowIndex, int columnIndex)
        {    }

        /**
         * Adds a listener to the table that's notified each time a change
         * to the data model occurs.
         * @param l the <code>TableModelListener</code> to be added
         */  
        public void addTableModelListener(TableModelListener l)
        {    this.listeners.add(l); }
       
        /**
         * Removes a listener from the list that's notified each time a 
         * change to the data model occurs.
         * @param l the <code>TableModelListener</code> to be removed
         */  
        public void removeTableModelListener(TableModelListener l)
        {    this.listeners.remove(l); }
    
        /** send a table model event to all registered listeners **/
        public void fireTableChanged(TableModelEvent e)
        {   Iterator it = this.listeners.iterator();
            while(it.hasNext())
            {   ((TableModelListener)it.next()).tableChanged(e); }
        }
    }
    
}
