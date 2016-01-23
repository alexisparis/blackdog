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
package org.siberia.bar.factory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import org.siberia.bar.customizer.BarCustomizer;
import org.siberia.bar.customizer.DefaultMenuBarCustomizer;
import org.siberia.bar.customizer.MenuBarCustomizer;
import org.siberia.xml.schema.bar.AbstractElement;
import org.siberia.xml.schema.bar.ActionableShortcutedElement;
import org.siberia.xml.schema.bar.MenuType;
import org.siberia.xml.schema.bar.Menubar;
import org.siberia.xml.schema.bar.OrderedElement;
import org.siberia.xml.schema.bar.SeparatorElement;
import org.siberia.xml.schema.bar.TypeMenu;

/**
 *
 * merge a list of menubar
 *
 * @author alexis
 */
abstract class AbstractMenubarMerger<U, T extends BarCustomizer>
{
    /** logger */
    private static Logger     logger     = Logger.getLogger(AbstractMenubarMerger.class);
    
    /** list of items to merge */
    private   List<U>         items      = null;

    /** hashtable containigs the shortcuts assigned */
    protected Set<String>     shortcuts  = null;
    
    /** menubar customizer */
    private   T               customizer = null;

    /**
     * create a new AbstractMenubarMerger
     * 
     * @param list a list of menubar to merge. the order of the list is important because, the first menubar is<br>
     *  the menubar with the weakest priority.
     * @param customizer a MenuBarCustomizer
     */
    public AbstractMenubarMerger(List<U> list, T customizer)
    {   this.items  = (list == null ? Collections.EMPTY_LIST : list);
        this.shortcuts = new HashSet<String>();
	
	if ( customizer == null )
	{   throw new IllegalArgumentException("customizer shall be provided"); }
	
	this.customizer = customizer;
    }
    
    /** return the list of items to merge
     *	@return a List
     */
    protected List<U> getItemsToMerge()
    {
	return this.items;
    }
    
    /** return the customizer to use
     *	@return a T
     */
    protected T getCustomizer()
    {	return this.customizer; }

    /** return the resulting merged menubar
     *  @return a Menubar or null if there was nothing to merge
     */
    public Menubar merge()
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering merge()");
	}
	Menubar bar = null;
        if ( items != null )
        {   if ( items.size() == 1 )
	    {
		Object o = items.get(0);
		if ( o instanceof Menubar )
		{
		    bar = (Menubar)o;
		}
	    }
            else if ( items.size() > 1 )
            {   /* start with the last two ones and go back */
		Object o = this.items.get(items.size() - 1);
		
		if ( o instanceof Menubar )
		{
		    bar = (Menubar)o;

		    if ( this.getCustomizer().shouldConsider(bar) )
		    {
			this.assignShortcuts(bar, true);

			for(int i = items.size() - 2; i >= 0; i--)
			{   
			    Object current = items.get(i);
			    
			    if ( current instanceof Menubar )
			    {
				bar = this.merge(bar, (Menubar)current);
			    }
			}
		    }
		}
            }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting merge()");
	}

        return bar;
    }

    /** return the resulting merged menubar
     *  @param master the master menubar
     *  @param servant the servant menubar
     *  @return a Menubar or null if there was nothing to merge
     */
    protected Menubar merge(Menubar master, Menubar servant)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering merge(Menubar, Menubar)");
	}
	Menubar result = null;
        if ( master == null )
        {   if ( servant != null )
            {   result = servant; }
        }
        else
        {   if ( servant != null && servant.getMenus() != null && servant.getMenus().getMenu() != null )
            {
                /* merge two non null items fill the structure of master with servant elements */
                for(int i = 0; i < servant.getMenus().getMenu().size(); i++)
                {   /* if it already contains a menu named like current servant menu, then merge them */
                    MenuType currentMenu = (MenuType)servant.getMenus().getMenu().get(i);
                    if ( currentMenu != null )
                    {   Object item = this.getItem(master, currentMenu.getLabel(), currentMenu.getI18Nref(), MenuType.class);
                        if ( item != null )
                        {   this.merge( (MenuType)item, currentMenu ); }
                        else 
                        {   /* add the current servant menu to the list of menu of the master bar */
                            master.getMenus().getMenu().add(currentMenu);
                        }
                    }
                }
            }
            result = master;
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting merge(Menubar, Menubar)");
	}
        return result;
    }

    /** merge two menu into master one
     *  @param masterMenu the master menu non null
     *  @param servantMenu the servant menu non null
     */
    protected void merge(MenuType masterMenu, MenuType servantMenu)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering merge(MenuType, MenuType)");
	}
	
	boolean seemsSame = this.equalsAbstractElts(masterMenu, servantMenu);
	
	if ( seemsSame )
        {   
            /* loop on all first elements of the servantMenu */

            List subItems = servantMenu.getMenuOrItemOrCheck();
            if ( subItems != null )
            {   Iterator it = subItems.iterator();
                while(it.hasNext())
                {   Object o = it.next();

                    if ( o != null )
                    {   if ( o instanceof MenuType )
                        {   /* search for a menu in the master sub items */
                            MenuType servantSubMenu = (MenuType)o;
                            MenuType masterSubMenu = (MenuType)this.getItem(masterMenu, servantMenu.getLabel(), servantMenu.getI18Nref(), MenuType.class);
                            if ( masterSubMenu != null )
                            {   this.merge(masterSubMenu, servantSubMenu); }
                            else
                            {   masterMenu.getMenuOrItemOrCheck().add(servantSubMenu);
                                this.assignShortcuts(servantSubMenu, true);
                            }
                        }
                        else if ( o instanceof SeparatorElement )
                        {   masterMenu.getMenuOrItemOrCheck().add((OrderedElement)o); }
                        else if ( o instanceof AbstractElement )
                        {   AbstractElement subItem = (AbstractElement)o;
                            /* search for a sub item in the servant list that is named like subItem
                             * 
                             * if we find a checkItem named 't' and if tmaster already contains a comboItem named 't'
                             *  then the checkItem won't be added to the resulting structure
                             */
                            AbstractElement masterSubItem = (AbstractElement)this.getItem(masterMenu,
                                                                                          subItem.getLabel(),
											  subItem.getI18Nref(),
                                                                                          AbstractElement.class);
                            if ( masterSubItem == null )
                            {   masterMenu.getMenuOrItemOrCheck().add((OrderedElement)o);
                                this.assignShortcuts(o, false);
                            }
                        }
                    }
                }
            }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting merge(MenuType, MenuType)");
	}
    }

    /** method that fill the shortcut set with an object ( this object should be instance of ActionableShortcutedMenuItem
     *  but it does nothing if it is an normal Object ). the shortcut of the element if it exists is set to "" if an existing element
     *  already uses it. if the object is a menu, then 
     *  @param objet an Object
     *  @param recur true if the method should call itself when the object is a container of menuItem
     */
    protected void assignShortcuts(Object object, boolean recur)
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("entering assignShortCuts(Object, boolean)");
	}
	if ( object != null )
        {   if ( recur )
            {   List list = null;
                if ( object instanceof Menubar )
                {   
		    Menubar bar = (Menubar)object;
		    if ( bar.getMenus() != null )
		    {
			list = bar.getMenus().getMenu();
		    }
		}
                else if( object instanceof MenuType )
                {   
		    list = ((MenuType)object).getMenuOrItemOrCheck();
		}
                else if( object instanceof TypeMenu )
                {   
		    TypeMenu menuType = (TypeMenu)object;
		    if ( menuType.getItems() != null )
		    {
			list = menuType.getItems().getMenuOrItemOrCheck();
		    }
		}

                if ( list != null )
                {   Iterator it = list.iterator();
                    while(it.hasNext())
                    {   this.assignShortcuts(it.next(), recur); }
                }
            }
            /* it an ActionableShortcutedMenuItem ? */
            if ( object instanceof ActionableShortcutedElement )
            {   /* try to add the shortcut and if already presents, remove it */
                String shortcut = ((ActionableShortcutedElement)object).getShortcut();
                if ( shortcut != null )
                {   shortcut = shortcut.trim();
                    if ( shortcut.length() > 0 )
                    {   if ( ! this.shortcuts.add(shortcut) )
                        {   ((ActionableShortcutedElement)object).setShortcut("");
                            logger.warn("the item '" + ((ActionableShortcutedElement)object).getLabel() + "' could not get shortcut '" +
                                                shortcut + "' because it's already assignated");
                        }
                    }
                }                            
            }
        }
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("exiting assignShortCuts(Object, boolean)");
	}
    }
    
    /** return true if the two items seems equals
     *	@param elt1 an AbstractElement
     *	@param elt2 an AbstractElement
     *	@return a boolean
     */
    private boolean equalsAbstractElts(AbstractElement elt1, AbstractElement elt2)
    {
	boolean seemsSame = false;
	
	if ( elt1 == null )
	{
	    if ( elt2 == null )
	    {
		seemsSame = true;
	    }
	}
	else
	{
	    seemsSame = this.equalsAbstractElts(elt1, (elt2 == null ? null : elt2.getLabel()),
						      (elt2 == null ? null : elt2.getI18Nref()));
	}
	
	return seemsSame;
    }
    
    /** return true if the two items seems equals
     *	@param elt an AbstractElement
     *  @param label the label of an element
     *  @param i18nRef the internationalization reference of an element
     *	@return a boolean
     */
    private boolean equalsAbstractElts(AbstractElement elt, String label, String i18nRef)
    {
	boolean seemsSame = false;

	if ( elt != null )
	{
	    if ( elt.getLabel() != null )
	    {
		seemsSame = elt.getLabel().equals(label);
	    }

	    if( ! seemsSame )
	    {
		if ( elt.getI18Nref() != null )
		{
		    seemsSame = elt.getI18Nref().equals(i18nRef);
		}
	    }
	}
	
	return seemsSame;
    }

    /** return the item named name instance of the given class
     *  @param parent where to search for the item ( not recursively ) a Menubar or a Menu
     *  @param label the label of an element
     *  @param i18nRef the internationalization reference of an element
     *  @param allowed class
     *  @return a corresponding item or null if nothing was found
     */
    protected Object getItem(Object parent, String label, String i18nRef, Class allowed)
    {   Object o = null;
        if ( parent != null && (parent instanceof Menubar || parent instanceof MenuType || parent instanceof TypeMenu) )
        {   List l = null;
            if ( parent instanceof MenuType )
	    {
                l = ((MenuType)parent).getMenuOrItemOrCheck();
	    }
            else if ( parent instanceof Menubar )
	    {
		Menubar bar = (Menubar)parent;
		if ( bar.getMenus() != null )
		{
		    l = bar.getMenus().getMenu();
		}
	    }
            else if ( parent instanceof TypeMenu )
	    {
		TypeMenu typeMenu = (TypeMenu)parent;
		if ( typeMenu.getItems() != null )
		{
		    l = typeMenu.getItems().getMenuOrItemOrCheck();
		}
	    }
	    
            if ( l != null )
            {   Iterator it = l.iterator();
                while(it.hasNext())
                {   Object current = it.next();
		    
		    if ( current != null )
		    {
			if ( current instanceof AbstractElement )
			{
			    AbstractElement elt = (AbstractElement)current;
			    
			    boolean seemsSame = this.equalsAbstractElts(elt, label, i18nRef);

			    if ( allowed.isAssignableFrom(current.getClass()) && seemsSame )
			    {   o = current;
				break;
			    }
			}
		    }
                }
            }
        }
        return o;
    }
}
