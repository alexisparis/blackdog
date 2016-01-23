/* =============================================================================
 * Siberia bars
 * =============================================================================
 *
 * Project Lead:  Alexis Paris
 *
 * (C) Copyright 2007, by Alexis Paris.
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
package org.siberia.bar.customizer;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;
import org.siberia.bar.action.NullAction;
import org.siberia.bar.menu.ColdMenu;
import org.siberia.xml.schema.bar.ButtonType;
import org.siberia.xml.schema.bar.CheckType;
import org.siberia.xml.schema.bar.SeparatorSizedElement;
import org.siberia.xml.schema.bar.ActionableElement;
import org.siberia.xml.schema.bar.ActionableShortcutedElement;
import org.siberia.xml.schema.bar.CheckMenuItem;
import org.siberia.xml.schema.bar.ComboType;
import org.siberia.xml.schema.bar.MenuType;
import org.siberia.xml.schema.bar.MenuItem;
import org.siberia.xml.schema.bar.Menubar;
import org.siberia.xml.schema.bar.OrderedElement;
import org.siberia.xml.schema.bar.ParameterType;
import org.siberia.xml.schema.bar.SeparatorElement;
import org.siberia.xml.schema.bar.Toolbar;
import org.siberia.xml.schema.bar.TypeMenu;
import org.siberia.xml.schema.bar.AbstractElement;

/**
 *
 * Generic implementation of a customizer
 *  that generate the following component for the given related object :
 *
 *  <ul>
 *	<li>ColdMenu for MenuType</li>
 *	<li>JMenuItem for MenuItem</li>
 *	<li>JCheckBoxMenuItem for CheckMenuItem</li>
 *	<li>JButton for ButtonType</li>
 *	<li>JCheckBox for CheckType</li>
 *  </ul>
 *
 *  it defines some protected methods that can be usefully overriden like :
 *  <ul>
 *	<li>createIcon(String iconPath)</li>
 *	<li>showAsInactive(AbstractButton button )</li>
 *	<li>createAction(String classNameRef, List parameters)</li>
 *	<li>customizeAction(Action action, List parameters)</li>
 *	<li>createAction(String classNameRef)</li>
 *	<li>applyMnemonic(Action action, String mnemonic)</li>
 *  </ul>
 *
 * @author alexis
 */
public class DefaultBarCustomizer<E> extends AbstractBarCustomizer<E>
{   
    /** logger */
    private Logger logger = Logger.getLogger(DefaultBarCustomizer.class);
    
    /** Creates a new instance of DefaultBarCustomizer */
    public DefaultBarCustomizer()
    {	}
    
    /** return true if the given item should be taken into acount
     *	@param e 
     *	@return true if the item should be taken into account
     */
    public boolean shouldConsider(E e)
    {	return true; }
    
    /** complete a JMenu according to a list of xml element
     *  @param menu a JComponent ( JMenu or JToolBar )
     *  @param item a list of xml items
     */
    public void feedComponent(final JComponent component, List items)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering feedComponent(JComponent, List)");
	}
	
	if ( component != null && items != null)
        {   Iterator it = items.iterator();
            while(it.hasNext())
            {   Object current = it.next();
                
                final Component item = this.createItem(component, current);
		
		if ( item != null )
		{   
		    if ( component instanceof JMenu && item instanceof JMenuItem )
		    {
			((JMenu)component).insert( (JMenuItem)item, ((JMenu)component).getMenuComponentCount() );
		    }
		    else
		    {
			component.add(item);
		    }
		}
            }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting feedComponent(JComponent, List)");
	}
    }
    
    /** apply a mnemonic on an action
     *  @param action an action
     *  @param mnemonic a mnemonic
     */
    public void applyMnemonic(Action action, String mnemonic)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering applyMnemonic(Action, String)");
	    logger.debug("calling applyMnemonic(" + action + ", " + mnemonic + ")");
	}
	if ( action != null )
        {   if ( mnemonic != null )
            {   if ( mnemonic.trim().length() >= 1 )
                {   
		    String _mnemonic = null;
		    try
                    {   
			_mnemonic = mnemonic.trim().substring(0, 1);
			
			Field f = KeyEvent.class.getDeclaredField("VK_" + _mnemonic);
                        if ( f != null )
			{
			    if ( logger.isDebugEnabled() )
			    {
				logger.debug("applying mnemonic " + f.getInt(f) + " for action with name " + action.getValue(Action.NAME));
			    }
                            action.putValue(Action.MNEMONIC_KEY, f.getInt(f));
			}
                    }
                    catch(Exception e)
                    {   
			logger.error("could not assign mnemonic '" + _mnemonic + "' for action " + action, e);
		    }
                }
            }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting applyMnemonic(Action, String)");
	}
    }
    
    /** return an instance of of the class represented by the given class name reference<br>
     *	the reference could be a classic classname of any kind of form if your customizer is able to load the 
     *	related class
     *	@param classNameRef a reference to a class
     *	@return an Action
     */
    protected Action createAction(String classNameRef)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering createAction(String)");
	    logger.debug("calling createAction(" + classNameRef + ")");
	}
	Action action = null;
	
        if ( classNameRef != null )
        {
	    try
	    {   Class c = this.getClass().getClassLoader().loadClass(classNameRef);
		action = (Action)c.newInstance();
	    }
	    catch (Exception ex)
	    {   
		logger.error("exception occured when trying to instanciate class '" + classNameRef + "'", ex);
	    }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting createAction(String)");
	}
	
	return action;
    }
    
    /** customize action
     *	@param action an Action
     *  @param parameters a list of parameter
     */
    protected void customizeAction(Action action, List parameters)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering customizeAction(Action, List)");
	}
	if ( action != null )
	{
	    /* apply parameters */
            if ( parameters != null )
            {   Iterator it = parameters.iterator();
                while(it.hasNext())
                {   Object current = it.next();
                    if ( current instanceof ParameterType )
                    {   ParameterType param = (ParameterType)current;
                        String name = param.getName();
                        if ( name.trim().length() > 0 )
                        {   /* find the method on the action named set + 'name of the parameter with first letter in uppercase */
                            String methodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);

                            try
                            {   Method m = action.getClass().getMethod(methodName, String.class);
                                m.invoke(action, param.getValue());
                            }
                            catch(Exception e)
                            {   
				logger.error("unable to use introspection on action : " + action + " with parameter named " + param.getName(), e);
			    }
                        }
                    }
                }
            }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting customizeAction(Action, List)");
	}
    }
    
    /** create an action by using the class name reference ( return a null action if the class could not be found )
     *	@param classNameRef a reference to a class
     *  @param parameters a list of parameter
     *  @return an action
     */
    public Action createAction(String classNameRef, List parameters)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering createAction(String, List)");
	    logger.debug("calling createAction(" + classNameRef + ", with " + (parameters == null ? 0 : parameters.size()) + " parameters)");
	}
	Action action = this.createAction(classNameRef);

        if ( action == null )
	{
            action = new NullAction();
	}
	
	this.customizeAction(action, parameters);
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("createAction(String, List) returns " + action);
	    logger.debug("exiting createAction(String, List)");
	}
	
	return action;
    }
    
    /** create a JMenu according to the given parameter
     *	@param menu a MenuType
     *	@return a JMenu
     */
    public JMenu createMenu(MenuType menu)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering createMenu(MenuType)");
	    logger.debug("calling createMenu(..) with menu : " + menu + " (i18nref=" + (menu == null ? null : menu.getI18Nref()) + 
									 ", action class=" + (menu == null ? null : menu.getActionClass()) + ")");
	}
	JMenu result = new ColdMenu();
	Action a = this.createAction(menu.getActionClass(), menu.getParameter());
	
	String         i18nCode = menu.getI18Nref();
	
	String label = menu.getLabel();
	
	if ( i18nCode != null )
	{
	    String value = this.getInternationalizedValue(i18nCode + ".label");
	    
	    if ( value != null )
	    {
		label = value;
	    }
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("applying name '" + label + "' for action " + a);
	}
	a.putValue(Action.NAME, label);
	
	String mnemonic = menu.getMnemonic();
	
	if ( i18nCode != null )
	{
	    String value = this.getInternationalizedValue(i18nCode + ".mnemonic");
	    
	    if ( value != null )
	    {
		mnemonic = value;
	    }
	}

	this.applyMnemonic(a, mnemonic);
	
	result.setAction(a);
	
	result.setFont(this.getFont());
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting createMenu(MenuType)");
	}
	
	this.feedComponent(result, menu.getMenuOrItemOrCheck());
	
	return result;
    }
    
    /** add an Abstract button to its parent component according to a SeparatorElement
     *	@param separator a SeparatorElement
     *	@param parent a Container
     */
    protected void addSeparator(SeparatorElement separator, Container parent)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering addSeparator(SeparatorElement, Container)");
	    logger.debug("calling addSeparator on container " + parent);
	}
	if ( parent instanceof JMenu )
	    ((JMenu)parent).addSeparator();
	else if ( parent instanceof JPopupMenu )
	    ((JPopupMenu)parent).addSeparator();
	else if ( parent instanceof JToolBar )
	    ((JToolBar)parent).addSeparator();
	else
	{
	    logger.warn("no separator could be added for component : " + parent);
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting addSeparator(SeparatorElement, Container)");
	}
    }
    
    /** add an Abstract button to its parent component according to a SeparatorElement
     *	@param separator a SeparatorSizedElement
     *	@param parent a Container
     */
    protected void addSizedSeparator(SeparatorSizedElement separator, Container parent)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering addSizedSeparator(SeparatorSizedElement, Container)");
	}
	if ( parent instanceof JToolBar )
	{   ((JToolBar)parent).addSeparator(
				new Dimension(separator.getWidth(), separator.getHeight()));
	}
	else
	{
	    logger.warn("no size separator could be added for component : " + parent);
	}
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting addSizedSeparator(SeparatorSizedElement, Container)");
	}
    }
    
    /** create an AbstractButton according to a MenuItem and its parent
     *	@param menuItem a MenuItem
     *	@param container a Container
     *	@return an AbstractButton
     */
    protected AbstractButton createMenuButton(MenuItem menuItem, Container container)
    {	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering createMenuButton(MenuItem, Container)");
	}
	AbstractButton button = new JMenuItem();
	if ( container instanceof JToolBar )
	{   logger.warn("adding a MenuItem to a JToolBar"); }
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("createMenuButton returns " + button);
	    logger.debug("exiting createMenuButton(MenuItem, Container)");
	}
	
	return button;
    }
    
    /** create an AbstractButton according to a CheckMenuItem and its parent
     *	@param menuItem a CheckMenuItem
     *	@param container a Container
     *	@return an AbstractButton
     */
    protected AbstractButton createCheckMenuButton(CheckMenuItem menuItem, Container container)
    {	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering createCheckMenuButton(CheckMenuItem, Container)");
	}
	AbstractButton button = new JCheckBoxMenuItem();
	if ( container instanceof JToolBar )
	{   logger.warn("adding a CheckMenuItem to a JToolBar"); }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("createCheckMenuButton returns " + button);
	    logger.debug("exiting createCheckMenuButton(CheckMenuItem, Container)");
	}
	
	return button;
    }
    
    /** create an AbstractButton according to a ButtonType and its parent
     *	@param button a ButtonType
     *	@param container a Container
     *	@return an AbstractButton
     */
    protected AbstractButton createButton(ButtonType button, Container container)
    {	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering createButton(ButtonType, Container)");
	}
	AbstractButton jbutton = new javax.swing.JButton();
	if ( ! (container instanceof JToolBar) )
	{   logger.warn("adding a ButtonType to a container that is not a JToolBar"); }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("createButton returns " + button);
	    logger.debug("exiting createButton(ButtonType, Container)");
	}
	
	return jbutton;
    }
    
    /** create an AbstractButton according to a CheckType and its parent
     *	@param check a CheckType
     *	@param container a Container
     *	@return an AbstractButton
     */
    protected AbstractButton createCheckButton(CheckType check, Container container)
    {	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering createCheckButton(CheckType, Container)");
	}
	AbstractButton button = new javax.swing.JCheckBox();
	if ( ! (container instanceof JToolBar) )
	{   logger.warn("adding a CheckType to a container that is not a JToolBar"); }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("createCheckButton returns " + button);
	    logger.debug("exiting createCheckButton(CheckType, Container)");
	}
	
	return button;
    }
    
    /** create an AbstractButton according to a ComboType and its parent
     *	@param combo a ComboType
     *	@param container a Container
     *	@return an AbstractButton
     */
    protected AbstractButton createComboButton(ComboType combo, Container container)
    {	
	AbstractButton button = null;
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering createComboButton(ComboType, Container)");
	}
	logger.error("combo not implemented");
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("createComboButton returns " + button);
	    logger.debug("exiting createComboButton(ComboType, Container)");
	}
	return button;
    }
    
    /** add a new graphical item to the bar
     *	@param parentComponent a Container
     *	@param item the xml item which allow to generate an AbstractButton
     *	@return an AbstractButton or null
     *
     *	do not add the component to the parent container
     */
    public AbstractButton createItem(Container parentComponent, Object current)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering createItem(Container, Object)");
	    logger.debug("calling createItem with " + current);
	}
	
	AbstractButton button   = null;
	
	if ( current instanceof MenuType )
	{
	    button = this.createMenu( (MenuType)current );
	}
	else
	{
	    Action         action   = null;

	    String         i18nCode = null;

	    if ( current instanceof AbstractElement )
	    {
		i18nCode = ((AbstractElement)current).getI18Nref();
	    }
	    
	    if ( logger.isDebugEnabled() )
	    {
		boolean set = (i18nCode != null);
		logger.debug("i18n reference set ? " + set + (set ? " with " + i18nCode : ""));
	    }

	    if ( current instanceof SeparatorElement )
	    {   
		this.addSeparator((SeparatorElement)current, parentComponent);
		return null;
	    }
	    else if ( current instanceof SeparatorSizedElement )
	    {   
		this.addSizedSeparator((SeparatorSizedElement)current, parentComponent);
		return null;
	    }

	    /* shortcuted element */
	    if ( current instanceof MenuItem )
	    {   
		button = this.createMenuButton((MenuItem)current, parentComponent);
	    }
	    else if ( current instanceof CheckMenuItem )
	    {   
		button = this.createCheckMenuButton((CheckMenuItem)current, parentComponent);
	    }

	    /* non shortcuted element */
	    else if ( current instanceof ButtonType )
	    {   
		button = this.createButton((ButtonType)current, parentComponent);
	    }
	    else if ( current instanceof CheckType )
	    {   
		button = this.createCheckButton((CheckType)current, parentComponent);
	    }
	    else if ( current instanceof ComboType )
	    {   
		button = this.createComboButton((ComboType)current, parentComponent);
	    }

	    /* create action */
	    if ( current instanceof ActionableElement )
	    {   ActionableElement item = (ActionableElement)current;
		String actionClass = item.getActionClass();

		action = createAction(actionClass, item.getParameter());

		boolean activeAction = true;

		if ( action != null)
		{   String iconPath = item.getIcon();

		    if ( i18nCode != null )
		    {
			String value = this.getInternationalizedValue(i18nCode + ".icon");
			if ( value != null )
			{
			    iconPath = value;
			}
		    }

		    // icon
		    if ( iconPath != null )
		    {   
			// BE CAREFUL : putting something that is not an icon, will provoke silent problems of
			//              construction of menubars and toolbars

			Icon icon = this.createIcon(iconPath);
			if ( icon != null )
			{
			    action.putValue(Action.SMALL_ICON, icon);
			}
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("applying icon : " + icon);
			}
		    }

		    // tooltip
		    String tooltip = item.getTooltip();

		    if ( i18nCode != null )
		    {
			String value = this.getInternationalizedValue(i18nCode + ".tooltip");
			if ( value != null )
			{
			    tooltip = value;
			}
		    }
		    if ( tooltip != null )
		    {
			action.putValue(Action.SHORT_DESCRIPTION, tooltip);
		    }
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("applying tooltip : " + tooltip);
		    }

		    // label
		    String label = item.getLabel();

		    if ( i18nCode != null )
		    {
			String value = this.getInternationalizedValue(i18nCode + ".label");
			if ( value != null )
			{
			    label = value;
			}
		    }
		    if ( label != null )
		    {
			action.putValue(Action.NAME, label);
		    }
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("applying label : " + label);
		    }

		    // Mnemonic
		    String mnemonic = item.getMnemonic();

		    if ( i18nCode != null )
		    {
			String value = this.getInternationalizedValue(i18nCode + ".mnemonic");
			if ( value != null )
			{
			    mnemonic = value;
			}
		    }
		    this.applyMnemonic(action, mnemonic);
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("applying mnemonic : " + mnemonic);
		    }

		    // shortcut
		    if ( current instanceof ActionableShortcutedElement && button instanceof JMenuItem )
		    {   
			String shortcut = ((ActionableShortcutedElement)current).getShortcut();

			if ( i18nCode != null )
			{
			    String value = this.getInternationalizedValue(i18nCode + ".shortcut");
			    if ( value != null )
			    {
				shortcut = value;
			    }
			}
			
			if ( shortcut != null )
			{
			    action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(shortcut));
			}
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("applying shortcut : " + shortcut);
			}
		    }

		    if ( action instanceof NullAction )
		    {   activeAction = false; }
		}
		else
		{   activeAction = false; }

		if ( button != null && ! activeAction )
		{   
		    this.showAsInactive(button);
		}
	    }

	    if ( button != null )
	    {   Font font = this.getFont();
		if ( font != null )
		{	button.setFont(font); }

		button.setAction(action);
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting createItem(Container, Object)");
	}
	
	return button;
    }
    
    /** called when no action has been found for a given component
     *	@param component an AbstractButton
     */
    protected void showAsInactive(AbstractButton component)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering showAsInactive(AbstractButton)");
	    logger.debug("calling showAsInactive for " + component);
	}
	if ( component != null )
	{
	    component.setForeground(java.awt.Color.RED); 
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting showAsInactive(AbstractButton)");
	}
    }
    
    /** create an icon according to the representation of an icon defined in the xml bar definition or 
     *	in an internationzalition resource
     *	@param iconRepresentation
     *	@return an Icon or null
     */
    protected Icon createIcon(String iconRepresentation)
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering createIcon(String)");
	    logger.debug("calling createIcon(" + iconRepresentation + ")");
	}
	Icon icon = null;
	
	if ( iconRepresentation != null )
	{
	    /* try to find it with the current ClassLoader */
	    URL url = this.getClass().getClassLoader().getResource(iconRepresentation);
	    
	    if ( url != null )
	    {
		icon = new ImageIcon(url);
	    }
	}
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("createIcon(String) returns " + icon);
	    logger.debug("exiting createIcon(String)");
	}
	
	return icon;
    }
    
}
