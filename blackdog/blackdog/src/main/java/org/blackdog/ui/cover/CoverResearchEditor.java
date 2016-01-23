/*
 * blackdog : audio player / manager
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
package org.blackdog.ui.cover;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.blackdog.BlackdogPlugin;
import org.blackdog.action.impl.cover.CoverAction;
import org.blackdog.type.cover.CoverDisplayStyle;
import org.blackdog.type.cover.CoverSearch;
import org.blackdog.type.cover.event.CoverSearchEvent;
import org.blackdog.type.cover.event.CoverSearchItemEvent;
import org.blackdog.type.cover.event.CoverSearchListener;
import org.jdesktop.swingx.JXImagePanel;
import org.siberia.ResourceLoader;
import org.siberia.bar.factory.BarFactory;
import org.siberia.editor.AbstractEditor;
import org.siberia.editor.annotation.Editor;
import org.siberia.editor.launch.EditorLaunchContext;
import org.siberia.exception.ResourceException;
import org.siberia.type.SibType;
import org.siberia.editor.launch.DefaultEditorLaunchContext;
import org.siberia.editor.AuxiliaryEditor;
import org.siberia.ui.bar.PluginBarFactory;
import org.siberia.ui.bar.PluginBarProvider;
import org.siberia.ui.bar.customizer.PluginToolBarCustomizer;
import org.siberia.ui.swing.combo.EnumListCellRenderer;
import org.siberia.ui.swing.combo.model.EnumComboBoxModel;

/**
 *
 * Cover research editor
 *
 * @author alexis
 */
@Editor(relatedClasses={org.blackdog.type.cover.CoverSearch.class},
                  description="Cover search editor",
                  name="Cover search editor",
                  launchedInstancesMaximum=1)
public class CoverResearchEditor extends AbstractEditor implements AuxiliaryEditor,
								   CoverSearchListener,
								   ItemListener
{
    /** logger */
    private Logger                           logger                 = Logger.getLogger(CoverResearchEditor.class);
    
    /** CoverResearchPanel */
    private CoverResearchPanel               panel	            = null;
    
    /** resource bundle */
    private ResourceBundle                   rb                     = null;
    
    /** add a label in the toolbar */
    private JLabel                           positionLabel          = null;
    
    /** combo box to choose the manner in which images will be displayed */
    private JComboBox                        comboImageDisplayStyle = null;
    
    /** image indicating to wait */
    private Image                            waitingImage           = null;
    
    /* executor that called ImageIO read */
    private ExecutorService                  imageIOService         = Executors.newFixedThreadPool(10);
    
    /** list of weak reference of CoverAction */
    private List<WeakReference<CoverAction>> actionRefs             = new ArrayList<WeakReference<CoverAction>>();
    
    /** create a new CoverResearchEditor */
    public CoverResearchEditor()
    {
	super();
	
	this.rb = ResourceBundle.getBundle(CoverResearchEditor.class.getName());
	
	try
	{
	    this.waitingImage = ResourceLoader.getInstance().getImageNamed(BlackdogPlugin.PLUGIN_ID + ";1::img/retrieving_image.png");
	}
	catch(ResourceException e)
	{
	    logger.error("unable to load wait image", e);
	}
    }
    
    @Override
    public CoverSearch getInstance()
    {
	return (CoverSearch)super.getInstance();
    }
    
    /** delete cover
     *	@param panel a CoverPanel
     */
    public void deleteCover(CoverPanel panel) throws IOException, URISyntaxException
    {
	if ( this.panel != null && panel != null )
	{
	    if ( panel.getURL() != null )
	    {
		File f = new File(panel.getURL().getFile());
		
		if ( f.exists() )
		{
		    f.delete();
		}
	    }
	    
	    for(int i = 0; i < this.panel.getMainPanel().getComponentCount(); i++)
	    {
		Component c = this.panel.getMainPanel().getComponent(i);
		
		Component _c = c;

		CoverPanel cover = null;

		if ( _c instanceof JScrollPane )
		{
		    _c = ((JScrollPane)_c).getViewport().getView();
		}

		if ( _c instanceof CoverPanel )
		{
		    if ( _c == panel )
		    {
			this.panel.getMainPanel().remove(c);
			break;
		    }
		}
	    }
	    
	    this.currentCoverChanged();
	    this.updatePositionLabel();
	}
    }
    
    /** indicate to all Cover action registered that a new CoverPanel is being displayed */
    private void currentCoverChanged()
    {
	if ( this.panel != null )
	{
	    JPanel panel = this.panel.getMainPanel();
	    
	    if ( panel != null )
	    {
		for(int i = 0; i < panel.getComponentCount(); i++)
		{
		    Component c = panel.getComponent(i);
		    
		    if ( c != null && c.isVisible() )
		    {
			CoverPanel cover = null;
			
			if ( c instanceof JScrollPane )
			{
			    c = ((JScrollPane)c).getViewport().getView();
			}
			
			if ( c instanceof CoverPanel )
			{
			    cover = (CoverPanel)c;
			}
			
			this.currentCoverChanged(cover);
			
			break;
		    }
		}
	    }
	}
    }
    
    /** indicate to all Cover action registered that a new CoverPanel is being displayed
     *	@param panel a CoverPanel
     */
    private void currentCoverChanged(CoverPanel panel)
    {
	if ( this.actionRefs != null )
	{
	    for(int i = 0; i < this.actionRefs.size(); i++)
	    {
		WeakReference<CoverAction> currentActionRef = this.actionRefs.get(i);
		
		if ( currentActionRef != null )
		{
		    CoverAction action = currentActionRef.get();
		    
		    if ( action != null )
		    {
			action.currentCoverChanged(panel);
		    }
		}
	    }
	}
    }
    
    /** set the SibType instance associated with the editor
     *  @param instance instance of SibType
     **/
    @Override
    public void setInstance(SibType instance)
    {   
        if ( instance != null && ! (instance instanceof CoverSearch ) )
	{
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " editor only support instance of " + CoverSearch.class);
	}
        
	CoverSearch oldCs = this.getInstance();
	
	if ( oldCs != instance && oldCs != null )
	{
	    oldCs.removeCoverSearchListener(this);
	    
	    oldCs.setEnabled(false);
	}
	
	super.setInstance(instance);
	
	if ( oldCs != this.getInstance() && this.getInstance() != null )
	{
	    this.getInstance().addCoverSearchListener(this);
	    
	    
	    this.getInstance().setEnabled(true);
	}
    }
    
    /** return the combo box that allow to change the cover display type
     *	@return a JComBox
     */
    public JComboBox getDisplayTypeCombo()
    {
	return this.comboImageDisplayStyle;
    }
    
    /** return the image count label
     *	@return a Label
     */
    public JLabel getImagePositionLabel()
    {
	return this.positionLabel;
    }
    
    /** select the next image */
    public void showNextImage()
    {
	if ( this.panel != null )
	{
	    Runnable runnable = new Runnable()
	    {
		public void run()
		{
		    panel.getCardLayout().next(panel.getMainPanel());
		    currentCoverChanged();
		    updatePositionLabel();
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
    
    /** select the previous image */
    public void showPreviousImage()
    {
	if ( this.panel != null )
	{
	    Runnable runnable = new Runnable()
	    {
		public void run()
		{
		    panel.getCardLayout().previous(panel.getMainPanel());
		    currentCoverChanged();
		    updatePositionLabel();
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
    
    /** return the component that render the editor
     *  @return a Component
     */
    public Component getComponent()
    {   
	if ( this.panel == null )
        {   
	    this.panel = new CoverResearchPanel();
	    
	    /** initialize toolbar */
	    BarFactory barFactory = new BarFactory();
	    
	    PluginToolBarCustomizer customizer = new PluginToolBarCustomizer()
	    {
		/** return an instance of of the class represented by the given class name reference<br>
		 *	the reference could be a classic classname of any kind of form if your customizer is able to load the 
		 *	related class
		 *	@param classNameRef a reference to a class
		 *	@return an Action
		 */
		@Override
		protected Action createAction(String classNameRef)
		{
		    Action a = super.createAction(classNameRef);
		    
		    if ( a instanceof CoverAction )
		    {
			((CoverAction)a).setCoverSearchEditor(CoverResearchEditor.this);
			
			actionRefs.add(new WeakReference<CoverAction>( (CoverAction)a ));
		    }
		    
		    return a;
		}
	    };

	    PluginBarFactory.getInstance().configure(this.panel.getToolBar(),
						     new PluginBarProvider("toolbar.cover", PluginBarProvider.BarKind.TOOLBAR),
						     customizer, true);
	    
	    /** create a combo to choose in which style to display image */
	    this.comboImageDisplayStyle = new JComboBox();
	    this.comboImageDisplayStyle.setModel(new EnumComboBoxModel(CoverDisplayStyle.class));
	    this.comboImageDisplayStyle.setRenderer(new EnumListCellRenderer());

	    this.panel.getToolBar().addSeparator();
	    this.panel.getToolBar().add(comboImageDisplayStyle);

	    this.positionLabel = new JLabel();
	    this.panel.getToolBar().addSeparator();
	    this.panel.getToolBar().add(this.positionLabel);
	    updatePositionLabel();
	    
	    this.getDisplayTypeCombo().setSelectedItem(CoverDisplayStyle.SCALED_KEEP_RATIO);

	    this.getDisplayTypeCombo().addItemListener(this);

	    this.updateDisplayStyle( (CoverDisplayStyle)this.getDisplayTypeCombo().getSelectedItem() );
        }
        
        return this.panel;
    }
    
    /* #########################################################################
     * ################## ItemListener implementation ##########################
     * ######################################################################### */
    
    public void itemStateChanged(ItemEvent e)
    {
	if ( e.getSource() == this.getDisplayTypeCombo() )
	{
	    this.updateDisplayStyle( (CoverDisplayStyle)this.getDisplayTypeCombo().getSelectedItem() );
	}
    }
    
    /** convert a CoverDisplayStyle into a JXImagePanel.Style
     *	@param style a CoverDisplayStyle
     *	@return a JXImagePanel.Style
     */
    private JXImagePanel.Style convertStyle(CoverDisplayStyle style)
    {
	JXImagePanel.Style _jxImgStyle = null;
	
	if ( style != null )
	{
	    if ( CoverDisplayStyle.CENTERED.equals(style) )
	    {
		_jxImgStyle = JXImagePanel.Style.CENTERED;
	    }
	    else if ( CoverDisplayStyle.SCALED.equals(style) )
	    {
		_jxImgStyle = JXImagePanel.Style.SCALED;
	    }
	    else if ( CoverDisplayStyle.SCALED_KEEP_RATIO.equals(style) )
	    {
		_jxImgStyle = JXImagePanel.Style.SCALED_KEEP_ASPECT_RATIO;
	    }
	    else if ( CoverDisplayStyle.TILED.equals(style) )
	    {
		_jxImgStyle = JXImagePanel.Style.TILED;
	    }
	}
	
	if ( _jxImgStyle == null )
	{
	    _jxImgStyle = JXImagePanel.Style.SCALED;
	}
	
	return _jxImgStyle;
    }
    
    /** apply a CoverDisplayStyle to all Images actually displayed in the mainPanel
     *	@param style a CoverDisplayStyle
     */
    private void updateDisplayStyle(CoverDisplayStyle style)
    {
	if ( this.panel != null )
	{
	    final JXImagePanel.Style jxImgStyle = this.convertStyle(style);

	    if ( jxImgStyle != null && this.panel.getMainPanel() != null )
	    {
		Runnable runnable = new Runnable()
		{
		    public void run()
		    {
			for(int i = 0; i < panel.getMainPanel().getComponentCount(); i++)
			{
			    JXImagePanel xPanel = null;

			    Component c = panel.getMainPanel().getComponent(i);

			    if ( c instanceof JScrollPane )
			    {
				c = ((JScrollPane)c).getViewport().getView();
			    }
			    if ( c instanceof JXImagePanel )
			    {
				xPanel = (JXImagePanel)c;
			    }

			    if ( xPanel != null )
			    {
				if ( logger.isDebugEnabled() )
				{
				    logger.debug("setting style " + jxImgStyle + " to image panel : " + xPanel);
				}
				xPanel.setStyle(jxImgStyle);
			    }
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
    
    /* #########################################################################
     * ############### CoverSearchListener implementation ######################
     * ######################################################################### */
    
    /* Sent after the indices in the index0,index1 interval have been inserted in the data model.
     *	@param e a CoverSearchItemEvent
     */
    public void imageAdded(final CoverSearchItemEvent e)
    {
	if ( logger.isDebugEnabled() )
	{
	    if ( this.getInstance() == null )
	    {
		logger.debug("cover search is null");
	    }
	    else
	    {
		logger.debug("image added at : " + e.getIndex0() + " --> " + this.getInstance().getURLAt(e.getIndex0()));
	    }
	}
	
	if ( e != null )
	{
	    Runnable runnable = new Runnable()
	    {
		public void run()
		{
		    final URL url = getInstance().getURLAt(e.getIndex0());

		    if( url != null )
		    {
			/** add a new CoverPanel to the main panel */
			final CoverPanel imagePanel = new CoverPanel();
			imagePanel.setImage(waitingImage);
			imagePanel.setStyle(JXImagePanel.Style.CENTERED);
			
			final JScrollPane scroll = new JScrollPane(imagePanel);

			panel.getMainPanel().add(scroll, Integer.toString(e.getIndex0() + 1));
			
			currentCoverChanged();

			imagePanel.setToolTipText(url.toString());

			imageIOService.submit(new Runnable()
			{
			    public void run()
			    {
				boolean removePanel = false;
				
				try
				{
				    final Image img = ImageIO.read(url);

				    if ( img == null )
				    {
					removePanel = true;
				    }
				    else
				    {
					if ( logger.isDebugEnabled() )
					{
					    logger.debug("image io cache enabled ? " + ImageIO.getUseCache());
					}

					Runnable edtRunnable = new Runnable()
					{
					    public void run()
					    {   
						imagePanel.setStyle(convertStyle( (CoverDisplayStyle)getDisplayTypeCombo().getSelectedItem()) );
						imagePanel.setImage(url, img);
						
						currentCoverChanged();
						imagePanel.revalidate();
					    }
					};
					if ( SwingUtilities.isEventDispatchThread() )
					{
					    edtRunnable.run();
					}
					else
					{
					    SwingUtilities.invokeLater(edtRunnable);
					}
				    }
				}
				catch(IOException e)
				{
				    logger.warn("could not load image from '" + url + "'", e);
				    removePanel = true;
				}
				
				if ( removePanel )
				{
				    Runnable edtRunnable = new Runnable()
				    {
					public void run()
					{
					    panel.getMainPanel().remove(scroll);
					    updatePositionLabel();
					}
				    };
				    if ( SwingUtilities.isEventDispatchThread() )
				    {
					edtRunnable.run();
				    }
				    else
				    {
					SwingUtilities.invokeLater(edtRunnable);
				    }
				}
			    }
			});
		    }
		    
		    /** update position label */
		    updatePositionLabel();
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
    
    /** update the position label */
    private void updatePositionLabel()
    {
	Runnable runnable = new Runnable()
	{
	    public void run()
	    {
		if ( positionLabel != null )
		{
		    if ( panel == null )
		    {
			positionLabel.setText("");
		    }
		    else
		    {
			boolean visibleFound = false;
			int i = 0;
			for(; i < panel.getMainPanel().getComponentCount() && ! visibleFound; i++)
			{
			    Component c = panel.getMainPanel().getComponent(i);
			    
			    if ( c.isVisible() )
			    {
				visibleFound = true;
			    }
			}
			
			if ( visibleFound )
			{
			    positionLabel.setText((i) + " / " + panel.getMainPanel().getComponentCount());
			}
			else
			{
			    positionLabel.setText("");
			}
		    }
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
          
    /**
     * Sent after the indices in the index0,index1 interval have been removed from the data model.
     * 
     * @param e a CoverSearchItemEvent
     */
    public void imagesRemoved(final CoverSearchItemEvent e)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("receiving a remove images event");
	}
	
	if ( e != null )
	{
	    Runnable runnable = new Runnable()
	    {
		public void run()
		{
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("removing all content");
		    }
		    panel.getMainPanel().removeAll();
		    /* must call repaint, else, at least the current panel stay displayed */
		    panel.getMainPanel().repaint();
		    
		    currentCoverChanged(null);
		    
		    updatePositionLabel();
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
    
    /** indicate that the search is finished
     *	@param event a CoverSearchEvent
     */
    public void searchFinished(CoverSearchEvent event)
    {
	if ( this.panel != null && this.rb != null )
	{
	    Runnable runnable = new Runnable()
	    {
		public void run()
		{
		    panel.getMessageLabel().setText(rb.getString("search.finished.message"));
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
    
    /** indicate that the search has started
     *	@param event a CoverSearchEvent
     */
    public void searchStarted(CoverSearchEvent event)
    {
	if ( this.panel != null && this.rb != null )
	{
	    Runnable runnable = new Runnable()
	    {
		public void run()
		{
		    panel.getMessageLabel().setText(rb.getString("search.start.message"));
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
