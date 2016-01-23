/* 
 * Siberia browser : siberia plugin defining a simple web browser
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
package org.siberia.ui.swing.text;
 
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.StyleSheet;
import org.siberia.exception.ResourceException;
import org.siberia.GraphicalResources;
import org.siberia.ui.swing.text.document.HTMLControllableDocument;
import org.siberia.ui.swing.text.document.HTMLControllableDocument.LoadingDocumentListener;
import org.siberia.ResourceLoader;
import org.siberia.SiberiaBrowserPlugin;

/**
 * JEditorPane that allows HTML navigation
 *
 * @author alexis
 */
public class HtmlViewer extends JPanel implements HyperlinkListener, 
                                                  ActionListener
{
    /** swing component to view document */
    private JEditorPane    editor          = new JEditorPane();
    
    /* url text entry */
    private JTextField     urlTextField    = new JTextField();
    
    /* previous */
    private JButton        prevButton      = new JButton();
    
    /* next */
    private JButton        nextButton      = new JButton();
    
    private JProgressBar   progressBar     = new JProgressBar();
    
    /** vector of visited url */
    private Vector<SingleURL> linkHistory  = new Vector<SingleURL>();
    
    /** current index in the history links vector */
    private int            linkIndex       = 0;
 
    /** build a browser with the strign representation of the url to use
     *  @param url the first url to use
     */
    public HtmlViewer(String url) 
    {	
        super(new BorderLayout());
        
        /* interaction panel */
        JPanel inputPanel = new JPanel (new FormLayout("pref, pref, 5px, pref, fill:pref:grow", "pref"));
        CellConstraints cc = new CellConstraints();
        
        JLabel label = new JLabel ("URL : ");
        
        try
        {   this.prevButton.setIcon( ResourceLoader.getInstance().getIconNamed(SiberiaBrowserPlugin.PLUGIN_ID + ";1::img/Left.png") );
            this.nextButton.setIcon( ResourceLoader.getInstance().getIconNamed(SiberiaBrowserPlugin.PLUGIN_ID + ";1::img/Right.png") );
        }
        catch(ResourceException e){ e.printStackTrace(); }
        
        Dimension dimButton = new Dimension(23, 23);
        this.prevButton.setPreferredSize(dimButton);
        this.nextButton.setPreferredSize(dimButton);
        this.prevButton.setBorderPainted(false);
        this.nextButton.setBorderPainted(false);
        
        this.prevButton.addActionListener(this);
        this.nextButton.addActionListener(this);
        
        inputPanel.add(this.prevButton, cc.xy(1, 1));
        inputPanel.add(this.nextButton, cc.xy(2, 1));
        inputPanel.add(label, cc.xy(4, 1));
        inputPanel.add(this.urlTextField, cc.xy(5, 1));
    
        /** viewing panel */
        JScrollPane scrollPane = new JScrollPane(this.editor);
        
        JPanel statusBar = new JPanel(new BorderLayout());
//        this.progressBar.setIndeterminate(true);
        statusBar.add(this.progressBar, BorderLayout.EAST);
    
        this.add (inputPanel, BorderLayout.NORTH);
        this.add (scrollPane, BorderLayout.CENTER);
        this.add (statusBar, BorderLayout.SOUTH);
    
        HTMLControllableKit editorKit = new HTMLControllableKit();
        editorKit.addLoadingDocumentListener(new LoadingDocumentListener()
        {
            public void loadingStarted()
            {   //System.out.println("started");
                HtmlViewer.this.progressBar.setValue(0);
            }

            public void loadingFinished()
            {   //System.out.println("finished");
                HtmlViewer.this.progressBar.setValue(100);
            }
        });
        this.editor.setEditorKit(editorKit);
        this.editor.setEditable(false);
    
        this.editor.addHyperlinkListener(this);
    
        this.urlTextField.addActionListener(this);
        
        /** load the default page */
        this.linkHistory.add(new SingleURL(url));
        this.setPage(url);
        this.updateButtonsState();
        System.out.println("constructor");
        this.displayHistory();
    }
    
    /** display the url kept in memory */
    private void displayHistory()
    {   if ( this.linkHistory != null )
        {   System.out.println("affichage de l'historique ( current : " + this.linkIndex + ")");
            for(int i = 0; i < this.linkHistory.size(); i++)
                System.out.println("\t" + i + " : " + this.linkHistory.get(i).getUrl().toString());
        }
    }
 
    /** build a browser */
    public HtmlViewer() 
    {   this("http://www.google.fr"); }
    
    /** tell the viewer to start loading */
    public void start()
    {
        this.reloadCurrentPage();
        this.updateButtonsState();
    }
    
    /** reload the currentPage */
    private void reloadCurrentPage()
    {   this.setPage(this.linkHistory.get(this.linkIndex).getUrl()); }
    
    /** replace the current SingleURL with a corresponding FrameURL by giving parameters
     *  @param target the target to place new page
     *  @param a new Url to add
     *  @param event the event which causes the target frame to change its content
     */
    private void extendsCurrentPageWithFrame(String target, String url, HTMLFrameHyperlinkEvent event)
    {   FrameURL frameUrl = this.extendsCurrentPageWithFrame();
        
        frameUrl.setTarget(target);
        
        frameUrl.addSubURL(url, event);
    }
    
    /** replace the current SingleURL with a corresponding FrameURL */
    private FrameURL extendsCurrentPageWithFrame()
    {   SingleURL current = this.linkHistory.get(this.linkIndex);
        
        if ( current instanceof FrameURL ) return (FrameURL)current;
        
        FrameURL frameUrl = new FrameURL(current);
        
        frameUrl.addSubURL(current.getUrl(), null);
        
        /** replace the URL representation */
        this.linkHistory.remove(this.linkIndex);
        this.linkHistory.add(this.linkIndex, frameUrl);
        System.out.println("extendsCurrentPageWithFrame");
        this.displayHistory();
        
        return frameUrl;
    }
    
    /** change the document by giving a string representation of an url
     *  @param url a String representation of an url
     *  @return true if all succeeds
     */
    private boolean setPage(String url)
    {   
        if ( url == null )
        {   this.editor.setDocument(this.editor.getEditorKit().createDefaultDocument());
            return true;
        }
        
        try 
        {   /* change the document */
            URL link = new URL(url);
            this.editor.setPage( link );
            this.urlTextField.setText(url);
            return true;
        }
        catch(IOException ex)
        {   
            try
            {   url = "http://" + url;
                URL link = new URL(url);
                this.editor.setPage( link );
                this.urlTextField.setText(url);
                return true;
            }
            catch(Exception e){ }
        }
        
        /* show error message */
        Component parent = this;
        while( ! (parent instanceof JFrame) && parent != null )
        {   System.out.println("\tparent : " + parent);
            parent = parent.getParent();
        }
        // ((JFrame)parent).getContentPane()
        JOptionPane.showInternalMessageDialog(GraphicalResources.getInstance().getMainFrame(),
                                              url.toString() + " was not fund",
                                              "Error",
                                              JOptionPane.ERROR_MESSAGE);
        
        return false;
        
    }
        
    /** load a document
     *  @param url a string which will be used to build an url
     */
    public void loadPage(String url)
    {   if ( ! this.setPage(url) ) return;
        
        this.addURL(url);
    }
    
    /** add url to history
     *  @param url a String representation of an URL
     */
    private void addURL(String url)
    {   this.linkIndex ++;
        for(int i = this.linkIndex; i < this.linkHistory.size(); i++)
            this.linkHistory.removeElementAt(this.linkIndex);
        
        this.linkHistory.add(new SingleURL(url));
        System.out.println("addURL");
        this.displayHistory();
        
        this.updateButtonsState();
    }
    
    /** return the current page
     *  @return a SingleURL
     */
    public SingleURL getCurrentURL()
    {   return this.linkHistory.get(this.linkIndex); }
    
    /** tell if the current page contains frames
     *  @return true if the current page contains frames
     */
    public boolean isCurrentURLWithFrames()
    {   return (this.linkHistory.get(this.linkIndex) instanceof FrameURL); }
    
    /** update the state of the button */
    private void updateButtonsState()
    {   boolean couldDoNext = false;
        if ( this.isCurrentURLWithFrames() )
        {   couldDoNext = ((FrameURL)this.getCurrentURL()).canGoNext();
            
            if ( ! couldDoNext )
                couldDoNext = (this.linkIndex != this.linkHistory.size() - 1);
        }
        else
            couldDoNext = (this.linkIndex != this.linkHistory.size() - 1);
        
        if ( couldDoNext != this.nextButton.isEnabled() )
            this.nextButton.setEnabled(couldDoNext);
        
        boolean couldDoPrev = false;
        if ( this.isCurrentURLWithFrames() )
        {   couldDoPrev = ((FrameURL)this.getCurrentURL()).canGoBack();
            
            if ( ! couldDoPrev )
                couldDoPrev = (this.linkIndex != 0);
        }
        else
            couldDoPrev = (this.linkIndex != 0);
        
        if ( couldDoPrev != this.prevButton.isEnabled() )
            this.prevButton.setEnabled(couldDoPrev);
    }
    
    /* #########################################################################
     * ##################### HyperlinkListener implementation ##################
     * ######################################################################### */
    
    /** called when an hyperlink has been activated */
    public void hyperlinkUpdate (HyperlinkEvent event) 
    {
        if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
        {
            if (event instanceof HTMLFrameHyperlinkEvent) 
            {   
                if ( ! ((HTMLFrameHyperlinkEvent)event).getTarget().equals("_top") )
                {
                    HTMLDocument doc = (HTMLDocument)editor.getDocument();

                    if ( this.isCurrentURLWithFrames() )
                    {   ((FrameURL)this.getCurrentURL()).addSubURL(event.getURL().toString(), ((HTMLFrameHyperlinkEvent)event)); }
                    else
                    {   this.extendsCurrentPageWithFrame(((HTMLFrameHyperlinkEvent)event).getTarget(),
                                                         event.getURL().toString(),
                                                         (HTMLFrameHyperlinkEvent)event);
                    }

                    System.err.println("ca contient des frames");

                    ((FrameURL)this.getCurrentURL()).displayCurrentPage();
                    
                    this.updateButtonsState();
                }
                else
                    this.loadPage( urlTextField.getText() );
            }
            else
                this.loadPage( urlTextField.getText() );
            
            /* display the current url */
            this.urlTextField.setText (event.getURL().toString());
        }
    }
    
    /* #########################################################################
     * ###################### ActionListener implementation ####################
     * ######################################################################### */
    
    // M�thode appele apres une modification de la saisie
    public void actionPerformed (ActionEvent event)
    {   
        Object source = event.getSource();
        
        boolean stateChange = false;
        
        if ( source == this.urlTextField )
            this.loadPage( urlTextField.getText() );
        else if ( source == this.nextButton )
        {   
            if ( this.isCurrentURLWithFrames() )
            {   FrameURL frameUrl = (FrameURL)this.getCurrentURL();
                
                if ( frameUrl.canGoNext() )
                {   frameUrl.goNext();
                    frameUrl.displayCurrentPage();
                }
                else
                {   this.setPage( this.linkHistory.get(++ this.linkIndex).getUrl() );
                    System.out.println("actionPerformed");
                    this.displayHistory();
                }
            }
            else
            {   this.setPage( this.linkHistory.get(++ this.linkIndex).getUrl() );
                System.out.println("actionPerformed");
                this.displayHistory();
            }
            
            stateChange = true;
        }
        else if ( source == this.prevButton )
        {   
            if ( this.isCurrentURLWithFrames() )
            {   FrameURL frameUrl = (FrameURL)this.getCurrentURL();
                
                if ( frameUrl.canGoBack() )
                {   frameUrl.goBack();
                    frameUrl.displayCurrentPage();
                }
                else
                {   this.setPage( this.linkHistory.get(-- this.linkIndex).getUrl() );
                    System.out.println("actionPerformed");
                    this.displayHistory();
                }
            }
            else
            {   SingleURL previousURL = this.linkHistory.get(-- this.linkIndex);
                System.out.println("actionPerformed");
                this.displayHistory();
                
                if ( previousURL instanceof FrameURL )
                {   this.setPage( ((FrameURL)previousURL).getRoot().getUrl() );
                    System.out.println("load root page");
                    try
                    {
                    Thread.sleep(2000); }
                    catch(Exception e){ }
                    if ( ((FrameURL)previousURL).getPageCount() > 1 )
                    {   System.out.println("y'a plus d'une page");
                        ((FrameURL)previousURL).displayCurrentPage();
                    }
                }
                else
                    this.setPage( previousURL.getUrl() );
            }
            
            stateChange = true;
        }
        
        if ( stateChange )
            this.updateButtonsState();
            
    }
  
    // Methode main () d'exemple de mise en oeuvre.
    // Utilisation : java DocumentViewer
    public static void main (String[] args)
    {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new HtmlViewer());
        frame.setSize (400, 300);
        frame.setVisible(true);
    }
    
    /** object that describe a simple URL */
    private class SingleURL
    {   
        private String url = null;
        
        public SingleURL(String url)
        {   this.setUrl(url); }

        public String getUrl()
        {   return url; }

        public void setUrl(String url)
        {   this.url = url; }
    }
    
    /** object that describe an URL that contains frame */
    private class FrameURL extends SingleURL
    {   
        private String                                  url         = null;
        
        private String                                  target      = null;
        
        private Vector<SingleURL>                       subURLs     = null;
        
        private Map<SingleURL, HTMLFrameHyperlinkEvent> urlMaps     = null;
        
        private int                                     subURLIndex = -1;
        
        public FrameURL(String url)
        {   super(url); }
        
        public FrameURL(SingleURL url)
        {   super(url.getUrl()); }

        public String getTarget()
        {   return target; }

        public void setTarget(String target)
        {   this.target = target; }
        
        public void addSubURL(String url, HTMLFrameHyperlinkEvent event)
        {   if ( this.subURLs == null )
                this.subURLs = new Vector<SingleURL>();
            if ( this.urlMaps == null )
                this.urlMaps = new HashMap<SingleURL, HTMLFrameHyperlinkEvent>();
            
            
            for(int i = this.subURLIndex + 1; i < this.subURLs.size(); i++)
                this.subURLs.removeElementAt(this.subURLIndex + 1);
            
            SingleURL sUrl = new SingleURL(url);
            
            this.subURLs.add(sUrl);
            this.urlMaps.put(sUrl, event);
            
            this.subURLIndex ++;
        }
        
        public SingleURL getRoot()
        {   return this.subURLs.get(0); }
        
        public int getPageCount()
        {   return this.subURLs.size(); }
        
        public boolean canGoBack()
        {   return this.subURLIndex != 0; }
        
        public boolean canGoNext()
        {   return (this.subURLIndex != this.subURLs.size() -1); }
        
        public void goBack()
        {   this.subURLIndex --; }
        
        public void goNext()
        {   this.subURLIndex ++; }
        
        public void displayCurrentPage()
        {   
            /** process frame */
            if ( HtmlViewer.this.editor.getDocument() instanceof HTMLDocument )
            {
                HTMLDocument doc = (HTMLDocument)HtmlViewer.this.editor.getDocument();
                SingleURL sURL = this.subURLs.get(this.subURLIndex);
                
                HTMLFrameHyperlinkEvent event = this.urlMaps.get(sURL);
                
                if ( event != null )
                    doc.processHTMLFrameHyperlinkEvent(event);
                else
                {   /* force to reinitialize !! */
                    HtmlViewer.this.setPage(null);
                    
                    HtmlViewer.this.setPage(sURL.getUrl());
                }
            }
        }
    }
    
    /* #########################################################################
     * ######### overwritten kit to provide a controllable document ############
     * ######################################################################### */
    
    private class HTMLControllableKit extends HTMLEditorKit
    {
        /** listener when document is loaded */
        private List<LoadingDocumentListener> loadingListeners = null;

        /**
         * Create an uninitialized text storage model
         * that is appropriate for this type of editor.
         *
         * @return the model
         */
        public Document createDefaultDocument()
        {   StyleSheet styles = getStyleSheet();
            StyleSheet ss = new StyleSheet();

            ss.addStyleSheet(styles);

            HTMLControllableDocument doc = new HTMLControllableDocument(ss);
            
            /** add all listeners */
            if ( this.loadingListeners != null )
            {
                for(int i = 0; i < this.loadingListeners.size(); i++)
                    doc.addLoadingDocumentListener(this.loadingListeners.get(i));
            }
            
            doc.setParser(getParser());
            doc.setAsynchronousLoadPriority(6);
            doc.setTokenThreshold(10); // 100
            return doc;
        }
    
        /**
         * add a loadingDocument listener to the document
         *  @param listener an LoadingDocumentListener
         */
        public void addLoadingDocumentListener(LoadingDocumentListener listener)
        {   if ( this.loadingListeners == null )
                this.loadingListeners = new ArrayList<LoadingDocumentListener>();

            this.loadingListeners.add(listener);
        }

        /**
         * remove a loadingDocument listener to the document
         *  @param listener an LoadingDocumentListener
         */
        public void removeLoadingDocumentListener(LoadingDocumentListener listener)
        {   if ( this.loadingListeners == null )
                return;

            this.loadingListeners.remove(listener);
        }
    }
}

