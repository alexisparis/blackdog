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
package org.siberia.ui.swing.text.document;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLDocument.HTMLReader;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.StyleSheet;

/**
 *
 * @author alexis
 */
public class HTMLControllableDocument extends HTMLDocument
{
    /** listener when document is loaded */
    private List<LoadingDocumentListener> loadingListeners = null;
    
    public HTMLControllableDocument(StyleSheet styles)
    {   super(styles); }

    public HTMLEditorKit.ParserCallback getReader(int pos)
    {
        Object desc = getProperty(Document.StreamDescriptionProperty);
        if (desc instanceof URL) { 
            setBase((URL)desc);
        }
        HTMLReader reader = new HTMLControllableReader(pos);
        return reader;
    }

    HTMLEditorKit.ParserCallback getReader(int pos, int popDepth,
                                           int pushDepth,
                                           HTML.Tag insertTag,
                                           boolean insertInsertTag) {
        Object desc = getProperty(Document.StreamDescriptionProperty);
        if (desc instanceof URL) { 
            setBase((URL)desc);
        }
        HTMLReader reader = new HTMLControllableReader(pos, popDepth, pushDepth,
                                           insertTag);//, insertInsertTag);//, false, true);
        return reader;
    }
    
    public void processHTMLFrameHyperlinkEvent(HTMLFrameHyperlinkEvent event)
    {   System.out.println("appel a processHTMLFrameHyperlinkEvent");
        HTMLControllableDocument.this.fireLoadingDocumentStarted();
        super.processHTMLFrameHyperlinkEvent(event);
        HTMLControllableDocument.this.fireLoadingDocumentFinished();
        System.out.println("fin d'appel a processHTMLFrameHyperlinkEvent");
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
    
    /** fire load start event to the listener */
    private void fireLoadingDocumentStarted()
    {   if ( this.loadingListeners == null )
            return;
        
        for(int i = 0; i < this.loadingListeners.size(); i++)
            this.loadingListeners.get(i).loadingStarted();
    }
    
    /** fire load finished event to the listener */
    private void fireLoadingDocumentFinished()
    {   if ( this.loadingListeners == null )
            return;
        
        for(int i = 0; i < this.loadingListeners.size(); i++)
            this.loadingListeners.get(i).loadingFinished();
    }

    private class HTMLControllableReader extends HTMLDocument.HTMLReader
    {
        public HTMLControllableReader(int offset)
        {   super(offset); }

        public HTMLControllableReader(int offset, int popDepth, int pushDepth, HTML.Tag insertTag)
        {   super(offset, popDepth, pushDepth, insertTag); }

        // -- HTMLEditorKit.ParserCallback methods --------------------

        /**
         * The last method called on the reader.  It allows
         * any pending changes to be flushed into the document.  
         * Since this is currently loading synchronously, the entire
         * set of changes are pushed in at this point.
         */
        public void flush() throws BadLocationException
        {   System.out.println("appel a flush");
            HTMLControllableDocument.this.fireLoadingDocumentStarted();
            super.flush();
            HTMLControllableDocument.this.fireLoadingDocumentFinished();
            System.out.println("fin d'appel a flush");
        }
    }
    
    /* #########################################################################
     * ################# LoadingDocumentListener class #########################
     * ######################################################################### */
    
    public static interface LoadingDocumentListener
    {   
        public void loadingStarted();
        
        public void loadingFinished();
    }
}
