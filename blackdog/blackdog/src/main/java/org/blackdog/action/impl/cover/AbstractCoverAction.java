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
package org.blackdog.action.impl.cover;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import org.blackdog.ui.cover.CoverPanel;
import org.blackdog.ui.cover.CoverResearchEditor;
import org.siberia.ui.action.GenericAction;

/**
 *
 * Action linked to a cover search editor
 *
 * @author alexis
 */
public abstract class AbstractCoverAction extends GenericAction implements CoverAction,
									   PropertyChangeListener
{
    /** weak reference on the cover editor */
    private WeakReference<CoverResearchEditor> editorRef     = new WeakReference<CoverResearchEditor>(null);
    
    /** reference to the cover panel currently displayed */
    private WeakReference<CoverPanel>          coverPanelRef = null;
    
    /** Creates a new instance of AbstractCoverAction */
    public AbstractCoverAction()
    {
    }
    
    /** set the cover editor
     *	@param editor a CoverResearchEditor
     */
    public void setCoverSearchEditor(CoverResearchEditor editor)
    {
	this.editorRef = new WeakReference<CoverResearchEditor>(editor);
    }
    
    /** return the cover editor
     *	@return a CoverResearchEditor
     */
    public CoverResearchEditor getCoverSearchEditor()
    {
	return this.editorRef.get();
    }
    
    /** indicate to the action that a new CoverPanel is being displayed
     *	@param panel a CoverPanel
     */
    public void currentCoverChanged(CoverPanel panel)
    {
	CoverPanel old = this.getCurrentCoverPanel();
	if ( old != null )
	{
	    old.removePropertyChangeListener(CoverPanel.PROPERTY_URL, this);
	}
	
	this.coverPanelRef = new WeakReference<CoverPanel>(panel);
	
	if ( panel != null )
	{
	    panel.addPropertyChangeListener(CoverPanel.PROPERTY_URL, this);
	}
    }
    
    /** return the reference to the current cover panel being displayed
     *	@return a CoverPanel
     */
    protected CoverPanel getCurrentCoverPanel()
    {
	CoverPanel panel = null;
	
	if ( this.coverPanelRef != null )
	{
	    panel = this.coverPanelRef.get();
	}
	
	return panel;
    }

    /**
     * This method gets called when a bound property is changed.
     * 
     * @param evt A PropertyChangeEvent object describing the event source 
     *   	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt)
    {
	if ( evt.getSource() instanceof CoverPanel )
	{
	    this.currentCoverChanged( (CoverPanel)evt.getSource() );
	}
    }
}
