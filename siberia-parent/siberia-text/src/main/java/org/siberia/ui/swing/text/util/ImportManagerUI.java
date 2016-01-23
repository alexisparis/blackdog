package org.siberia.ui.swing.text.util;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.siberia.type.service.AbstractImportManager;
import org.siberia.type.service.ImportManager;
import org.siberia.type.service.importmanager.JavaImportManager;
import org.siberia.type.service.parser.ScriptJavaParser;

/**
 *
 * Dialog that manages import management
 *
 * @author alexis
 */
public class ImportManagerUI extends JDialog
{
    /** import manager */
    private ImportManager          importManager = null;
    
    /** indicates if the results must be used at end */
    private boolean                hasCanceled   = false;
    
    /** map of class name and graphical representation ( JComboBox) */
    private Map<String, JComboBox> mappings      = null;
    
    /** Creates a new instance of ImportManagerUI */
    public ImportManagerUI(Frame frame, ImportManager manager)
    {   super(frame, "Imports management", true);
        
        this.importManager = manager;
    }
    
    /** build graphically the appearance of resolve symbols manager */
    public void buildAndShow(String text)
    {   if ( this.importManager == null ) return;
        
        List<String> unresolvedSymbols = this.importManager.getParser().getUnresolvedSymbols(text);
        
        this.addWindowListener(new WindowAdapter()
        {   
            public void windowClosed(WindowEvent e)
            {   ImportManagerUI.this.hasCanceled = true; }
            
        });
        
        FormLayout lay = new FormLayout("pref, fill:pref:grow", "");
        JPanel panel = new JPanel(lay);
        CellConstraints cc = new CellConstraints();
        
        if ( unresolvedSymbols == null ) return;
        
        JComboBox currentCombo = null;
        if ( this.importManager.getServiceProvider() != null )
        {
            String currentSymbol = null;
            int index = 1;
            for(Iterator<String> symbols = unresolvedSymbols.iterator(); symbols.hasNext();)
            {   currentSymbol = symbols.next();
                /* build the corresponding list of packages */
                List<String> packageList = this.importManager.getServiceProvider().getPackagesNameContainingClass(currentSymbol);
                
                if ( packageList == null) continue;
                
                if ( packageList.size() == 0 ) continue;
                if ( packageList.size() == 1 )
                {   this.importManager.addImportPart(currentSymbol, packageList.get(0));
                    continue;
                }
                
                /* update layout */
                lay.appendRow(new RowSpec("pref"));

                currentCombo = new JComboBox(packageList.toArray());
                
                /** feed Map */
                if ( this.mappings == null )
                    this.mappings = new HashMap<String, JComboBox>();
                
                this.mappings.put(currentSymbol, currentCombo);
                
                panel.add(new JLabel("<html><b><code>" + currentSymbol + "</code></b> </html>"), cc.xy(1, index));
                panel.add(currentCombo, cc.xy(2, index, cc.FILL, cc.FILL));
                
                index += 1;
            }
            
            if ( index == 1 )
                return;
            
            /* add valid and cancel buttons */
            JButton valid  = new JButton("OK");
            valid.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {   ImportManagerUI.this.setVisible(false); }
            });
            
            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {   ImportManagerUI.this.setVisible(false);
                    ImportManagerUI.this.hasCanceled = true;
                }
            });
            
            JPanel panelValid = new JPanel(new FormLayout("pref, 4px, pref", "pref"));
            panelValid.add(valid, cc.xy(1, 1));
            panelValid.add(cancel, cc.xy(3, 1));
            
            lay.appendRow(new RowSpec("5px"));
            lay.appendRow(new RowSpec("pref"));
            
            panel.add(panelValid, cc.xywh(1, index + 1, 2, 1, cc.RIGHT, cc.DEFAULT));
        }
        
        this.setContentPane(panel);
        
        int x = ((int)Toolkit.getDefaultToolkit().getScreenSize().getWidth()) - this.getWidth();
        int y = ((int)Toolkit.getDefaultToolkit().getScreenSize().getHeight()) - this.getHeight();
        this.setLocation(x/2, y/2);
        
        this.pack();
        this.setVisible(true);
        
        if ( ! this.hasCanceled() )
        {   /* complete the information of the import manager */
            String currentKey = null;
            for(Iterator<String> keys = this.mappings.keySet().iterator(); keys.hasNext();)
            {   currentKey = keys.next();
                
                this.importManager.addImportPart(currentKey, this.mappings.get(currentKey).getSelectedItem().toString());
            }
        }
    }
    
    /** returns true if the dialog has been canceled
     *  @return true if the dialog has been canceled
     */
    public boolean hasCanceled()
    {   return this.hasCanceled; }
    
    public static void main(String[] args)
    {   JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().add(new JButton("va te faire enculer"));
        
        AbstractImportManager a = new JavaImportManager();
        a.setParser(new ScriptJavaParser());
        
        ImportManagerUI u = new ImportManagerUI(frame, a);
        
        frame.pack();
        frame.setVisible(true);
        
        u.buildAndShow("import java.lang.String;String a; URL i;");
    }
    
}
