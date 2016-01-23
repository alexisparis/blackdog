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

import java.awt.Container;
import java.awt.Font;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JComponent;
import org.siberia.bar.i18n.I18NResources;
import org.siberia.bar.i18n.I18nResolver;

/**
 *
 * Object that could be linked to a BarFactory to customize the way it will be created
 *
 *  E is Toolbar or Menubar or TypeMenu
 *
 * @author alexis
 */
public interface BarCustomizer<E>
{
    /** return true if successive separator should be avoided
     *	@return a boolean
     */
    public boolean avoidSuccessiveSeparators();
    
    /** return true if the given item should be taken into acount
     *	@param e 
     *	@return true if the item should be taken into account
     */
    public boolean shouldConsider(E e);
    
    /** apply a mnemonic on an action
     *  @param action an action
     *  @param mnemonic a mnemonic
     */
    public void applyMnemonic(Action action, String mnemonic);
    
    /** create an action by using the class name ( return a null action if the class could not be found )
     *  @param className the complete class name
     *  @param parameters a list of parameter
     *  @return an action
     */
    public Action createAction(String className, List parameters);
    
    /** add a new graphical item to the bar
     *	@param parentComponent a Container
     *	@param item the xml item which allow to generate an AbstractButton
     *	@return an AbstractButton or null
     *
     *	do not add the component to the parent container
     */
    public AbstractButton createItem(Container parentComponent, Object item);
    
    /** set the font to use
     *	@param font a Font
     */
    public void setFont(Font font);
    
    /** return the font to use
     *	@return a Font
     */
    public Font getFont();
    
    /** complete a JMenu according to a list of xml element
     *  @param menu a JComponent ( JMenu or JToolBar )
     *  @param item a list of xml items
     */
    public void feedComponent(JComponent component, List items);
    
    /* #########################################################################
     * ####################### I18N related methods ############################
     * ######################################################################### */
    
    /** set the I18nResolver related to this customizer
     *	@param resolver a I18nResolver
     */
    public void setI18NResolver(I18nResolver resolver);
    
    /** return the I18nResolver related to this customizer
     *	@return a I18nResolver
     */
    public I18nResolver getI18NResolver();
    
    /** register a I18N resources
     *	@param resources a I18NResources
     */
    public void registerI18NResources(I18NResources resources);
    
    /** unregister a I18N resources
     *	@param resources a I18NResources
     */
    public void unregisterI18NResources(I18NResources resources);
    
    /** clear internationalization resources */
    public void clearI18NResources();
}
