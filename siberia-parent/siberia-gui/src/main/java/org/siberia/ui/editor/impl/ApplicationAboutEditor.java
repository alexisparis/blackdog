/* 
 * Siberia gui : siberia plugin defining basics of graphical application 
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
package org.siberia.ui.editor.impl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.siberia.editor.AbstractEditor;
import org.siberia.editor.annotation.Editor;
import org.siberia.type.ApplicationAbout;
import org.siberia.type.SibType;
import org.siberia.SiberiaIntrospector;
import org.siberia.type.info.BeanInfoCategory;

/**
 *
 * @author alexis
 */
@Editor(relatedClasses={org.siberia.type.ApplicationAbout.class},
                  description="Editor for instances of application about",
                  name="About editor",
                  launchedInstancesMaximum=1)
public class ApplicationAboutEditor extends AbstractEditor
{
    /** ImagePanel */
    private ImagePanel              panel            = null;
    
    /** JLabel */
    private JLabel                  label            = null;
    
    /** soft reference to a BeanInfo related to ApplicationAbout */
    private SoftReference<BeanInfo> beanInfoRef      = new SoftReference<BeanInfo>(null);
    
    /** property change Listener on the instance
     *	that allow to refresh editor content
     */
    private PropertyChangeListener  propertyListener = null;
    
    /** Creates a new instance of ApplicationAboutEditor */
    public ApplicationAboutEditor()
    {
	panel = new ImagePanel();
	
	/* prepare the JEditorPane that will display software information */
	this.label = new JLabel();
	this.label.setForeground(Color.WHITE);
	
	panel.setLayout(new BorderLayout());
	
	panel.add(this.label, BorderLayout.SOUTH);
	
	this.propertyListener = new PropertyChangeListener()
	{
	    public void propertyChange(PropertyChangeEvent evt)
	    {
		refreshComponents();
	    }
	};
	
	this.label.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 0));
    }
    
    /** set the SibType instance associated with the editor
     *  @param instance instance of SibType
     **/
    @Override
    public void setInstance(SibType instance)
    {   
	SibType old = this.getInstance();
	
	super.setInstance(instance);
	
	if ( old != this.getInstance() )
	{
	    if ( old != null )
	    {
		old.removePropertyChangeListener(this.propertyListener);
	    }
	    
	    /** refresh components */
	    this.refreshComponents();
	    
	    if ( this.getInstance() != null )
	    {
		this.getInstance().addPropertyChangeListener(this.propertyListener);
	    }
	}
    }
    
    /** return a PropertyDescriptor of the given name for the BeanInfo of ApplicationAbout
     *	@param propertyName the name of the property
     *	@return a ProeprtyDescriptor
     */
    private PropertyDescriptor getPropertyDescriptor(String propertyName)
    {
	PropertyDescriptor descriptor = null;
	
	BeanInfo info = this.beanInfoRef.get();
	
	if ( info == null )
	{
	    SiberiaIntrospector introspector = new SiberiaIntrospector();
	    info = introspector.getBeanInfo(ApplicationAbout.class, BeanInfoCategory.ALL, true, true);
	    
	    this.beanInfoRef = new SoftReference<BeanInfo>(info);
	}
	
	if ( info != null )
	{
	    PropertyDescriptor[] descriptors = info.getPropertyDescriptors();
	    if ( descriptors != null )
	    {
		for(int i = 0; i < descriptors.length; i++)
		{
		    PropertyDescriptor current = descriptors[i];
		    
		    if ( current != null )
		    {
			if ( current.getName().equals(propertyName) )
			{
			    descriptor = current;
			    break;
			}
		    }
		}
	    }
	}
	
	return descriptor;
    }
    
    private void refreshComponents()
    {
	if ( this.getInstance() instanceof ApplicationAbout )
	{
	    ApplicationAbout about = (ApplicationAbout)this.getInstance();
	    
	    this.panel.setBackgroundImage(about.getImage());
	    
	    StringBuffer buffer = new StringBuffer(400);
	    
	    buffer.append("<html>");
	    buffer.append("<table>");
	    
	    String color = "#FFFFFF";
	    
	    PropertyDescriptor desc = null;
	    String displayName = null;
	    String propertyName = null;
	    
	    propertyName = ApplicationAbout.PROPERTY_SOFT_NAME;
	    desc = this.getPropertyDescriptor(propertyName);
	    displayName = (desc == null ? "" : desc.getDisplayName());
	    buffer.append("<tr><td><font color=\"" + color + "\"><b>" + displayName + "</b></font></td>" +
			      "<td><font color=\"" + color + "\"><b>:</b></td>" +
			      "<td><font color=\"" + color + "\"><b>" + about.getSoftwareName() + "</b></font></td></tr>");
	    
	    propertyName = ApplicationAbout.PROPERTY_VERSION;
	    desc = this.getPropertyDescriptor(propertyName);
	    displayName = (desc == null ? "" : desc.getDisplayName());
	    buffer.append("<tr><td><font color=\"" + color + "\"><b>" + displayName + "</b></font></td>" +
			      "<td><font color=\"" + color + "\"><b>:</b></td>" +
			      "<td><font color=\"" + color + "\"><b>" + about.getVersion() + "</b></font></td></tr>");
	    
	    propertyName = ApplicationAbout.PROPERTY_RELEASE_DATE;
	    desc = this.getPropertyDescriptor(propertyName);
	    displayName = (desc == null ? "" : desc.getDisplayName());
	    String formattedDate = "";
	    
	    try
	    {
		formattedDate = DateFormat.getDateInstance(DateFormat.LONG).format(about.getReleaseDate());
	    }
	    catch(Exception e)
	    {	e.printStackTrace(); }
	    
	    buffer.append("<tr><td><font color=\"" + color + "\"><b>" + displayName + "</b></font></td>" +
			      "<td><font color=\"" + color + "\"><b>:</b></td>" +
			      "<td><font color=\"" + color + "\"><b>" + formattedDate + "</b></font></td></tr>");
	    
	    propertyName = ApplicationAbout.PROPERTY_LICENSE;
	    desc = this.getPropertyDescriptor(propertyName);
	    displayName = (desc == null ? "" : desc.getDisplayName());
	    buffer.append("<tr><td><font color=\"" + color + "\"><b>" + displayName + "</b></font></td>" +
			      "<td><font color=\"" + color + "\"><b>:</b></td>" +
			      "<td><font color=\"" + color + "\"><b>" + about.getLicense() + "</b></font></td></tr>");
	    
	    propertyName = ApplicationAbout.PROPERTY_WEB_LINK;
	    desc = this.getPropertyDescriptor(propertyName);
	    displayName = (desc == null ? "" : desc.getDisplayName());
	    buffer.append("<tr><td><font color=\"" + color + "\"><b>" + displayName + "</b></font></td>" +
			      "<td><font color=\"" + color + "\"><b>:</b></td>" +
			      "<td><font color=\"" + color + "\"><b>" + about.getWebLink() + "</b></font></td></tr>");
	    
	    propertyName = ApplicationAbout.PROPERTY_AUTHORS;
	    desc = this.getPropertyDescriptor(propertyName);
	    displayName = (desc == null ? "" : desc.getDisplayName());
	    buffer.append("<tr><td><font color=\"" + color + "\"><b>" + displayName + "</b></font></td>" +
			      "<td><font color=\"" + color + "\"><b>:</b></td>" +
			      "<td><font color=\"" + color + "\"><b>");
	    
	    List<String> authors = about.getAuthors();
	    if ( authors != null )
	    {
		if ( authors.size() == 1 )
		{
		    buffer.append(authors.get(0));
		}
		else
		{
		    buffer.append("<ul>");
		    for(int i = 0; i < authors.size(); i++)
		    {
			buffer.append("<li>" + authors.get(i) + "</li>");
		    }
		    buffer.append("</ul>");
		}
	    }
	    buffer.append("</b></font></td></tr>");
	    
	    buffer.append("</table>");
	    buffer.append("</html>");
	    
	    this.label.setText(buffer.toString());
	}
	else
	{
	    this.panel.setBackgroundImage(null);
	    this.label.setText("");
	}
	
	
//	this.editorPane.getPreferredScrollableViewportSize();
    }
    
    /** return the component that render the editor
     *  @return a Component
     */
    public Component getComponent()
    {
	return panel;
    }
    
    private class ImagePanel extends JPanel
    {
	/** background image */
	private Image image = null;
	
	/** constructor */
	public ImagePanel()
	{   super();
	    
	    this.setDoubleBuffered(true);
	}
	
	/** return the image used as background
	 *  @return an Image
	 */
	public Image getBackgroundImage()
	{   return image; }
	
	/** initialize the image used as background
	 *  @param image an Image
	 */
	public void setBackgroundImage(Image image)
	{   this.image = image; }
	
	public void paintComponent(Graphics g)
	{
	    super.paintComponent(g);
	    
	    if ( this.getBackgroundImage() != null )
	    {   Dimension dim = this.getSize();
		
		g.drawImage(this.getBackgroundImage(), 0, 0, dim.width, dim.height, this);
	    }
	}
    }
    
}
