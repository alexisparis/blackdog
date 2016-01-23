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
import java.util.Iterator;
import java.util.List;
import org.siberia.bar.customizer.DefaultMenuBarCustomizer;
import org.siberia.bar.customizer.DefaultPopupMenuCustomizer;
import org.siberia.bar.customizer.PopupMenuCustomizer;
import org.siberia.xml.schema.bar.AbstractElement;
import org.siberia.xml.schema.bar.MenuType;
import org.siberia.xml.schema.bar.OrderedElement;
import org.siberia.xml.schema.bar.SeparatorElement;
import org.siberia.xml.schema.bar.TypeMenu;

/**
 *
 * merge a list of type menu
 *
 * @author alexis
 */
class TypeMenuMerger extends AbstractMenubarMerger<TypeMenu, PopupMenuCustomizer>
{
    /** create a new TypeMenuMerger
     *  @param list a list of typeMenu to merge. the order of the list is important because, the first menubar is<br>
     *  the menubar with the weakest priority.
     */
    public TypeMenuMerger(List<TypeMenu> list)
    {	this(list, new DefaultPopupMenuCustomizer()); }
    
    /** create a new TypeMenuMerger
     *  @param list a list of typeMenu to merge. the order of the list is important because, the first menubar is<br>
     *  the menubar with the weakest priority.
     *	@param customizer a MenuBarCustomizer
     */
    public TypeMenuMerger(List<TypeMenu> list, PopupMenuCustomizer customizer)
    {   super(list, customizer == null ? new DefaultPopupMenuCustomizer() : customizer); }

    /** return the resulting merged menubar
     *  @return a Menubar or null if there was nothing to merge
     */
    public TypeMenu mergeType()
    {   TypeMenu menu = null;
        if ( getItemsToMerge() != null )
        {   if ( getItemsToMerge().size() == 1 )
                return getItemsToMerge().get(0);
            else if ( getItemsToMerge().size() > 1 )
            {   /* start with the last two ones and go back */
                menu = getItemsToMerge().get(getItemsToMerge().size() - 1);

                this.assignShortcuts(menu, true);

                for(int i = getItemsToMerge().size() - 2; i >= 0; i--)
                {   menu = this.merge(menu, getItemsToMerge().get(i)); }
            }
        }

        return menu;
    }

    /** return the resulting merged typeMenu
     *  @param master the master typeMenu
     *  @param servant the servant typeMenu
     *  @return a Menubar or null if there was nothing to merge
     */
    protected TypeMenu merge(TypeMenu master, TypeMenu servant)
    {   
        /* loop on all first elements of the servant */
        List subItems = null;
	if ( servant.getItems() != null )
	{
	    subItems = servant.getItems().getMenuOrItemOrCheck();
	}
        if ( subItems != null )
        {   Iterator it = subItems.iterator();
            while(it.hasNext())
            {   Object o = it.next();

                if ( o != null )
                {   if ( o instanceof MenuType )
                    {   /* search for a menu in the master sub items */
                        MenuType servantSubMenu = (MenuType)o;
                        MenuType masterSubMenu = (MenuType)this.getItem(master, servantSubMenu.getLabel(), servantSubMenu.getI18Nref(), MenuType.class);

                        if ( masterSubMenu != null )
                        {   this.merge(masterSubMenu, servantSubMenu); }
                        else
                        {   
			    master.getItems().getMenuOrItemOrCheck().add(servantSubMenu);
                            this.assignShortcuts(servantSubMenu, true);
                        }
                    }
                    else if ( o instanceof SeparatorElement )
                    {   master.getItems().getMenuOrItemOrCheck().add((OrderedElement)o); }
                    else if ( o instanceof AbstractElement )
                    {   AbstractElement subItem = (AbstractElement)o;
                        /* search for a sub item in the servant list that is named like subItem
                         * 
                         * if we find a checkItem named 't' and if tmaster already contains a comboItem named 't'
                         *  then the checkItem won't be added to the resulting structure
                         */
                        AbstractElement masterSubItem = (AbstractElement)this.getItem(master,
                                                                                      subItem.getLabel(),
										      subItem.getI18Nref(), 
                                                                                      AbstractElement.class);
                        if ( masterSubItem == null )
                        {   master.getItems().getMenuOrItemOrCheck().add((OrderedElement)o);
                            this.assignShortcuts(o, false);
                        }
                    }
                }
            }
        }
        return master;
    }
}  
