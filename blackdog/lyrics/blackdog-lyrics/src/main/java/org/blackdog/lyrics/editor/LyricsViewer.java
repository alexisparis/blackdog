/*
 * blackdog lyrics : define editor and systems to get lyrics for a song
 *
 * Copyright (C) 2008 Alexis PARIS
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.blackdog.lyrics.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.html.HTMLEditorKit;
import org.apache.log4j.Logger;
import org.blackdog.BlackdogLyricsPlugin;
import org.blackdog.lyrics.type.Lyrics;
import org.blackdog.lyrics.type.LyricsRetrievedStatus;
import org.siberia.ResourceLoader;
import org.siberia.editor.AbstractEditor;
import org.siberia.editor.annotation.Editor;
import org.siberia.exception.ResourceException;
import org.siberia.type.SibType;
import org.siberia.utilities.awt.ColorTranslator;

/**
 * 
 * Lyrics viewer
 * 
 * @author alexis
 */
@Editor(relatedClasses={org.blackdog.lyrics.type.Lyrics.class},
                  description="Lyrics viewer",
                  name="Lyrics viewer",
                  launchedInstancesMaximum=-1)
public class LyricsViewer extends AbstractEditor
{
    /** logger */
    private transient Logger       logger                = Logger.getLogger(LyricsViewer.class);
    
    /** JEditorPane */
    private JEditorPane            editorPane            = null;
    
    /** scroll */
    private JScrollPane            scroll                = null;
    
    /** main panel of the viewer */
    private JPanel                 panel                 = null;
    
    /** JLabel that display message */
    private JLabel                 messageLabel          = null;
    
    /** icon retrieve in process */
    private Icon                   retrieveInProcessIcon = null;
    
    /** icon retrieve error */
    private Icon                   retrieveErrorIcon     = null;
    
    /** property change listener */
    private PropertyChangeListener listener              = null;
    
    /** create a new LyricsViewer */
    public LyricsViewer()
    {
	super();
	
	try
	{
	    this.retrieveInProcessIcon = ResourceLoader.getInstance().getIconNamed(BlackdogLyricsPlugin.PLUGIN_ID + ";1::img/retrieving_lyrics.png");
	}
	catch(ResourceException e)
	{
	    logger.error("unabel to get lyrics retrieve in process icon", e);
	}
	try
	{
	    this.retrieveErrorIcon = ResourceLoader.getInstance().getIconNamed(BlackdogLyricsPlugin.PLUGIN_ID + ";1::img/unretrieved_lyrics.png");
	}
	catch(ResourceException e)
	{
	    logger.error("unabel to get lyrics retrieve error icon", e);
	}
	
	this.listener = new PropertyChangeListener()
	{
	    public void propertyChange(PropertyChangeEvent evt)
	    {   
		if ( evt.getSource() == getInstance() )
		{   
		    if ( Lyrics.PROPERTY_HTML_CONTENT.equals(evt.getPropertyName()) )
		    {   
			String content = (String)evt.getNewValue();

			updateViewer(content, getInstance().getRetrieveStatus());
		    }
		    else if ( Lyrics.PROPERTY_RETRIEVED.equals(evt.getPropertyName()) )
		    {   
			if ( evt.getNewValue() instanceof LyricsRetrievedStatus )
			{
			    updateViewer(getInstance().getHtmlContent(), (LyricsRetrievedStatus)evt.getNewValue());
			}
		    }
		}
	    }
	};
    }
    
    /** set the SibType instance associated with the editor
     *  @param instance instance of SibType
     **/
    @Override
    public void setInstance(SibType instance)
    {   
        if ( instance != null && ! (instance instanceof Lyrics ) )
            throw new IllegalArgumentException("UpdateManager editor only support instance of " + Lyrics.class);
        
        if ( this.getInstance() != null )
        {   this.getInstance().removePropertyChangeListener(this.listener); }
        
        super.setInstance(instance);
	
        if ( this.getInstance() != null )
        {   this.getInstance().addPropertyChangeListener(this.listener); }
	
	this.updateViewer();
    }
    
    /** return the current Lyrics
     *	@return a Lyrics
     */
    @Override
    public Lyrics getInstance()
    {
	Lyrics lyrics = null;
	
	SibType type = super.getInstance();
	
	if ( type instanceof Lyrics )
	{
	    lyrics = (Lyrics)type;
	}
	
	return lyrics;
    }
    
    /** ensure that the foreground color of the editorPane is that of the editorpane */
    private void modifyEditorPaneForegroundColor()
    {
	if ( this.editorPane != null )
	{
	    HTMLEditorKit editorKit = new HTMLEditorKit();
	    
	    Color c = this.editorPane.getForeground();
	    editorKit.getStyleSheet().addRule("BODY {color: " + ColorTranslator.getStringRepresentationOf(c) + "}"); //FF0000
	    
	    String text = this.editorPane.getText();
	    
	    this.editorPane.setEditorKit(editorKit);
	    
	    this.editorPane.setText(text);
	}
    }
    
    /** return the component that render the editor
     *  @return a Component
     */
    public Component getComponent()
    {   
	if ( this.panel == null )
	{
	    this.panel = new JPanel();
	    
	    this.editorPane = new LyricsPane();
	    
	    this.editorPane.setEditable(false);
	    
	    this.editorPane.updateUI();
//	    this.modifyEditorPaneForegroundColor();

	    this.scroll = new JScrollPane(this.editorPane);

	    panel.setLayout(new GridBagLayout());
	    
	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.gridx = 1;
	    gbc.gridy = 1;
	    gbc.anchor = gbc.CENTER;
	    gbc.fill = gbc.BOTH;
	    gbc.weightx = 1.0f;
	    gbc.weighty = 1.0f;
	    
	    panel.add(this.scroll, gbc);
	    
	    this.messageLabel = new JLabel();
	    this.messageLabel.setAlignmentX(0.5f);
	    this.messageLabel.setAlignmentY(0.5f);
	    this.messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
	    this.messageLabel.setVerticalAlignment(SwingConstants.CENTER);
	    this.messageLabel.setVisible(false);
	    panel.add(this.messageLabel, gbc);
	    
	    panel.setComponentZOrder(this.messageLabel, 0);
	    panel.setComponentZOrder(this.scroll, 1);
	    
	    this.updateViewer();
	}
	
	return this.panel;
    }
    
    /** update viewer editor pane
     *	with the given content
     */
    private void updateViewer()
    {
	Lyrics lyrics = this.getInstance();
	
	this.updateViewer( lyrics == null ? "" : lyrics.getHtmlContent(),
			   lyrics == null ? LyricsRetrievedStatus.UNRETRIEVED : lyrics.getRetrieveStatus() );
    }
    
    /** update viewer editor pane
     *	with the given content
     *	@param content
     *	@param status a LyricsRetrievedStatus
     */
    private void updateViewer(final String content, final LyricsRetrievedStatus status)
    {
	if ( logger != null && logger.isDebugEnabled() )
	{
	    logger.debug("updating viewer with content length=" + (content == null ? 0 : content.length()) + 
			 ", status=" + status);
	}
	
	if ( this.editorPane != null && this.messageLabel != null )
	{
	    Runnable runnable = new Runnable()
	    {
		public void run()
		{
		    String _content = (content == null ? "" : content);

		    if ( ! LyricsRetrievedStatus.RETRIEVED.equals(status) )
		    {
			_content = "";
		    }
		    
		    editorPane.setText( _content );
		    
		    /* invoke later else, the scroll does not go up */
		    if ( scroll != null )
		    {
			Runnable scrollRunnable = new Runnable()
			{
			    public void run()
			    {
				scroll.getVerticalScrollBar().setValue(0);
			    }
			};
			
			SwingUtilities.invokeLater(scrollRunnable);
		    }
		    
		    if ( LyricsRetrievedStatus.CURRENTLY_IN_PROCESS.equals(status) )
		    {
			messageLabel.setText(status.label());
			messageLabel.setIcon(retrieveInProcessIcon);
			messageLabel.setVisible(true);
		    }
		    else if ( LyricsRetrievedStatus.UNRETRIEVED.equals(status) )
		    {
			messageLabel.setText(status.label());
			messageLabel.setIcon(retrieveErrorIcon);
			messageLabel.setVisible(true);
		    }
		    else
		    {
			messageLabel.setVisible(false);
			messageLabel.setText("");
			messageLabel.setIcon(null);
		    }
		}
	    };

	    if ( SwingUtilities.isEventDispatchThread() )
	    {
		runnable.run();
	    }
	    else
	    {
		SwingUtilities.invokeLater(runnable);
	    }
	}
    }
    
}
