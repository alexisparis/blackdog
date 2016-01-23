/* 
 * Siberia types : siberia plugin defining structures managed by siberia platform
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
package org.siberia.type;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.*;
import org.siberia.SiberiaTypesPlugin;
import org.siberia.type.annotation.bean.Bean;
import org.siberia.type.exception.NonConfigurableTypeException;

/** 
 * Class representing an enumeration with multiple or single selected element(s)
 *
 *  @author alexis
 */
@Bean(  name="SibEnumeration",
        internationalizationRef="org.siberia.rc.i18n.type.SibEnumeration",
        expert=false,
        hidden=false,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public class SibEnumeration extends SibConfigurable implements PropertyChangeListener
{   
    /** single selection */
    public static final int SINGLE_SELECTION   = 0;
    
    /** multiple selection */
    public static final int MULTIPLE_SELECTION = 1;
    
    
    /* map of content and their name */
    private Map<String, SibBoolean> content             = null;
    
    /** selection mode */
    private int                       selectionMode       = SINGLE_SELECTION;
    
    /** indicates if none selection is allowed */
    private boolean                   selectionImperative = false;
    
    /** return the number of selected item */
    private int                       selectionCount      = 0;
    
    /** true if the enumeration has the rights to rename element to guarantee unique names */
    private boolean                   manageName          = true;
    
    /** create a new instanceof SibEnum */
    public SibEnumeration()
    {   super(); }

    /** return true if the enumeration has the right to rename element
     *  @return true if the enumeration has the right to rename element
     */
    public boolean manageName()
    {   return manageName; }

    /** tell if the enumeration has the right to rename element
     *  @param manageName true if the enumeration has the right to rename element
     */
    public void shouldManageName(boolean manageName)
    {   this.manageName = manageName; }
    
    /** return the number of selected element
     *  @return the number of selected element
     */
    public int getSelectionCount()
    {   int count = 0;
        if ( this.content != null )
        {   for(Iterator<SibBoolean> it = this.content.values().iterator(); it.hasNext();)
            {   if ( it.next().isSelected() )
                    count ++;
            }
        }
        return count;
    }
    
    /** return an iterator over all the entries of theenumeration
     *  @return an iterator over String
     */
    public Iterator<String> entries()
    {   if ( this.content == null )
            return Collections.EMPTY_LIST.iterator();
        return this.content.keySet().iterator();
    }
    
    /** return the selected element or null if no selection has been found
     *  @return the selected element or null if no selection has been found
     *  @throw UnsupportedOperationException if the mode of selection is MULTIPLE
     */
    public String getSelectedElement()
    {   if ( this.getSelectionMode() == MULTIPLE_SELECTION )
            throw new UnsupportedOperationException("getSelectedElement cannot be used on an enumeration with use multiple selection");
        String[] selections = this.getSelectedElements();
        String selection = null;
        if ( selections != null )
        {   if ( selections.length >= 1 )
            {   selection = selections[0]; }
        }
        
        return selection;
    }   
    
    /** return an array with all the selected element or null if no selection has been found
     *  @return an array with all the selected element or null if no selection has been found
     */
    public String[] getSelectedElements()
    {   String[] selections = null;
        if ( this.content != null )
        {   int count = this.getSelectionCount();
            if ( count > 0 )
            {   selections = new String[count];
                int currentIndex = 0;
                Iterator<SibBoolean> it = this.content.values().iterator();
                SibBoolean current = null;
                while(it.hasNext())
                {   current = it.next();
                    if ( current != null )
                    {   selections[currentIndex++] = current.getName(); }
                }
            }
        }
        return selections;
    }

    /** return the kind of selection
     *  @return one of the static constant defined in SibEnum : <br><ul><li>SINGLE_SELECTION</li>
     *                                                                    <li>MULTIPLE_SELECTION</li></ul>
     */
    public int getSelectionMode()
    {   return this.selectionMode; }

    /** return the kind of selection
     *  @param selectionMode one of the static constant defined in SibEnum : <br><ul><li>SINGLE_SELECTION</li>
     *                                                                                 <li>MULTIPLE_SELECTION</li></ul>
     */
    public void setSelectionMode(int selectionMode) throws NonConfigurableTypeException
    {   if ( this.isConfigurable() )
        {   if ( (this.selectionMode == SINGLE_SELECTION || this.selectionMode == MULTIPLE_SELECTION ) )
            {
                if ( this.getSelectionMode() != selectionMode )
                {   this.selectionMode = selectionMode;

                    /** keep only the first selected item */
                    if ( this.content != null && this.getSelectionMode() == SINGLE_SELECTION ) // MULTIPLE 2 SINGLE
                    {   boolean selectionApplied = false;

                        SibBoolean currentItem = null;
                        for(Iterator<SibBoolean> it = this.content.values().iterator(); it.hasNext();)
                        {   currentItem = it.next();

                            if ( ! selectionApplied )
                            {   if ( currentItem.isSelected() )
                                {   selectionApplied = true;
                                    this.selectionCount = 1;
                                }
                            }
                            else
                            {   try
                                {   currentItem.unselect(); }
                                catch(PropertyVetoException ex)
                                {   ex.printStackTrace(); }
                            }
                        }
                    }
                }
            }
        }
        else
            throw new NonConfigurableTypeException("selection mode");
    }

    /** return true if a selection must always exists
     *  @return true if a selection must always exists
     */
    public boolean isSelectionImperative()
    {   return selectionImperative; }

    /** tell if a selection must always exists
     *  @param selectionImperative true if a selection must always exists
     */
    public void setSelectionImperative(boolean selectionImperative)
    {   if ( this.isConfigurable() )
        {   if ( this.selectionImperative != selectionImperative )
            {
                this.selectionImperative = selectionImperative;
                if ( this.content != null )
                {   if ( this.isSelectionImperative() && this.selectionCount < 1 && this.content.size() >= 1 )
                    {   /* select the first */
                        this.ensureValidSelection((String)this.content.keySet().toArray()[0], true);
                    }
                }
            }
        }
        else
            throw new UnsupportedOperationException("Item is not configurable");
    }
    
    /** provide a unique code base on the given String
     *  @param baseCode
     *  @return a unique name base on baseCode
     */
    private String createKey(String baseCode)
    {   String code = baseCode == null ? "x" : baseCode;
        if ( this.content != null )
        {   if ( this.content.containsKey(code) )
            {   int num = 1;
                String candidate = new String(code);
                while(true)
                {   candidate = code + String.valueOf(num);
                    if ( ! this.content.containsKey(candidate) )
                        return candidate;
                    num ++;
                }
            }
        }
        return code;
    }
    
    /** return true if the enumeration already contains an item named code
     *  @param code the code of an item
     *  @return true if the enumeration already contains an item named code
     */
    public boolean containsElement(String code)
    {   if ( code == null ) return false;
        if ( this.content == null ) return false;
        return this.content.containsKey(code);
    }
    
    /** add a new item to the enumeration if it is configurable
     *  @param elt the element to add
     *  @param isSelected true if the element has to be selected
     */
    public void addElement(String elt, boolean isSelected) throws NonConfigurableTypeException
    {   if ( this.isConfigurable() )
        {
            if ( elt != null )
            {   if ( this.content == null )
                    this.content = new HashMap<String, SibBoolean>();
                String key = elt;
                if ( this.containsElement(elt) )
                {   key = this.createKey(elt); }
                SibBoolean b = new SibBoolean();
                b.addPropertyChangeListener(this);
                b.setParent(this);
                
                try
                {   b.setName(key); }
                catch (PropertyVetoException ex)
                {   ex.printStackTrace(); }
                
                try
                {   b.setValue(Boolean.FALSE); }
                catch (PropertyVetoException ex)
                {   ex.printStackTrace(); }
                
                try
                {   b.setNameCouldChange(false); }
                catch (PropertyVetoException ex)
                {   ex.printStackTrace(); }
                
                this.content.put(key, b);

                // if selectionCount == 0 and selection imperative, isSelected would be turn into TRUE
                this.ensureValidSelection(elt, (this.selectionCount == 0 && this.isSelectionImperative() ? true : isSelected) );
            }
        }
        else
            throw new NonConfigurableTypeException("enumeration elements");
    }
    
    /** add a new non selected item to the enumeration if it is configurable
     *  @param elt the element to add
     */
    public void addElement(String elt) throws NonConfigurableTypeException
    {   this.addElement(elt, false); }
    
    /** remove anitem to the enumeration if it is configurable
     *  @param elt the element to remove
     */
    public void removeElement(String elt) throws NonConfigurableTypeException
    {   if ( this.isConfigurable() )
        {   if ( this.content != null )
            {   SibBoolean b = this.content.get(elt);

                if ( b != null )
                {   b.setParent(null);
                    this.removeChildElement(b);
                    b.removePropertyChangeListener(this);
                    this.content.remove(elt);
                    if ( b.getValue().booleanValue() )
                        this.selectionCount --;

                    // if selectionCount is 0 and selection imperative, select first element
                    if ( this.content.size() >= 1 )
                    {   if ( this.selectionCount == 0 && this.isSelectionImperative() )
                        {   // select first
                            this.ensureValidSelection((String)this.content.keySet().toArray()[0], true);
                        }
                    }
                }
            }
        }
        else
            throw new NonConfigurableTypeException("enumeration elements");
    }
    
    /** select the element code
     *  @param code a code
     */
    public void select(String code)
    {   this.ensureValidSelection(code, true); }
    
    /** select the element code
     *  @param code an array of code
     */
    public void select(String[] code)
    {   if ( code != null )
        {   if ( code.length == 1 )
            {   this.select(code[0]); }
            else if ( code.length > 1 )
            {   if ( this.getSelectionMode() == SINGLE_SELECTION )
                {   this.select(code[0]); }
                else
                {   for(int i = 0; i < code.length; i++)
                        this.select(code[i]);
                }
            }
        }
    }
    
    /** unselect the element code
     *  @param code a code
     */
    public void unselect(String code)
    {   this.ensureValidSelection(code, false); }
    
    /** select an element and ensure that the context of the enumeration is valid
     *  @param code the code of the element
     *  @param select true if the element must be selected
     */
    private void ensureValidSelection(String code, boolean select)
    {   if ( this.content != null )
        {   if ( this.content.containsKey(code) )
            {   if ( this.content.get(code).isSelected() != select )
                {   if ( this.getSelectionMode() == SINGLE_SELECTION )
                    {   if ( select )
                        {   // deselect current selection and select the element named code
                            SibBoolean current = null;
                            boolean fund = false;
                            for(Iterator<SibBoolean> it = this.content.values().iterator(); it.hasNext();)
                            {   current = it.next();

                                if ( current.isSelected() )
                                {   if ( ! current.getName().equals(code) )
                                    {   fund = true;
                                        break;
                                    }
                                }
                            }
                            
                            if ( fund && current != null)
                            {
                                try
                                {   current.unselect(); }
                                catch (PropertyVetoException ex)
                                {   ex.printStackTrace(); }
                            }
                            else
                                this.selectionCount ++;
                            
                            try
                            {   this.content.get(code).select(); }
                            catch (PropertyVetoException ex)
                            {   ex.printStackTrace(); }
                        }
                        else // sure that the element named code is selected
                        {   if ( ! this.isSelectionImperative() )
                            {
                                try
                                {   this.content.get(code).unselect(); }
                                catch (PropertyVetoException ex)
                                {   ex.printStackTrace(); }
                                
                                this.selectionCount --;
                            }
                        }
                    }
                    else if ( this.getSelectionMode() == MULTIPLE_SELECTION )
                    {   if ( select )
                        {
                            try
                            {   this.content.get(code).select(); }
                            catch (PropertyVetoException ex)
                            {   ex.printStackTrace(); }
                            
                            this.selectionCount ++;
                        }
                        else
                        {   if ( this.isSelectionImperative() )
                            {   if ( this.selectionCount >= 2 )
                                {
                                    try
                                    {   this.content.get(code).unselect(); }
                                    catch (PropertyVetoException ex)
                                    {   ex.printStackTrace(); }
                                    
                                    this.selectionCount --;
                                }
                                // else prohibit unselection on the only selected element
                            }
                            else
                            {
                                try
                                {   this.content.get(code).unselect(); }
                                catch (PropertyVetoException ex)
                                {   ex.printStackTrace(); }
                                
                                this.selectionCount --;
                            }
                        }
                    }
                }
            }
            // else do nothing
        }
    }
    
    /** return an html representation fo the object without tag html
     *  @return an html representation fo the object without tag html
     */
    public String getHtmlContent()
    {   StringBuffer buffer = new StringBuffer();
        if ( this.content != null )
        {   buffer.append("<ul>");
            SibBoolean current = null;
            for(Iterator<SibBoolean> it = this.content.values().iterator(); it.hasNext();)
            {   current = it.next();
                buffer.append("<li>" + (current.isSelected() ? "\u2588" : "\u2591") + " " + current.getName() + "</li>");
            }
            buffer.append("</ul>");
        }
        
        return buffer.toString();
    }

    /*  return a String representation of the value
     *  @return a String representation of the value
     */
    public String valueAsString()
    {   StringBuffer value = new StringBuffer();
        String[] selections = this.getSelectedElements();
        if ( selections != null )
        {   for(int i = 0; i < selections.length; i++)
            {   value.append(selections[i]);
                if ( i < selections.length - 1 )
                    value.append(", ");
            }
        }
        return value.toString();
    }
    
    public Object clone()
    {   SibEnumeration other = (SibEnumeration)super.clone();
        
        other.setSelectionImperative(this.isSelectionImperative());
        
        try
        {   other.setConfigurable(true); }
        catch (PropertyVetoException ex)
        {   ex.printStackTrace(); }
        
        other.manageName = this.manageName;
        try
        {   other.setSelectionMode(this.getSelectionMode()); }
        catch(NonConfigurableTypeException e)
        {   e.printStackTrace(); }
        
        try
        {   other.setConfigurable(this.isConfigurable()); }
        catch (PropertyVetoException ex)
        {   ex.printStackTrace(); }
        
        Iterator<String> it = this.entries();
        while(it.hasNext())
        {   try
            {   other.addElement(it.next()); }
            catch(NonConfigurableTypeException e)
            {   /* do nothing */ }
        }
        
        /* selections */
        String[] selections = this.getSelectedElements();
        if ( selections != null )
        {   for(int i = 0;i < selections.length; i++)
            {   other.select(selections[i]); }
        }
        
        return other;
    }
    
    /** return a collection of SibBoolean that are used to define this enumeration
     *  @return a collection of SibBoolean
     */
    public Collection<SibBoolean> values()
    {   if ( this.content == null )
            return Collections.EMPTY_SET;
        return this.content.values();
    }
    
    /** Add a new SibBoolean that will be used to define this enumeration
     *  @param t an SibBoolean
     */
    public void add(SibBoolean t)
    {   if ( t != null )
        {   try
            {   this.addElement(t.getName(), t.getValue().booleanValue()); }
            catch(NonConfigurableTypeException e)
            {   e.printStackTrace(); }
        }
    }
   
   /* ##########################################################################
    * ################ PropertyChangeListener implementation ###################
    * ########################################################################## */
   
    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source 
     *   	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt)
    {   
        if ( this.content != null )
        {   if ( this.content.containsValue(evt.getSource()) )
            {   if ( evt.getPropertyName().equals("name") )
                {   /* we should remove the element and replace it to prevent hashCode error */
                    SibBoolean b = this.content.get(evt.getOldValue());
                    
                    if ( b != null )
                    {   this.content.remove(evt.getOldValue());
                        this.content.put((String)evt.getNewValue(), b);
                        
                        /** PENDING(api) : if manageName... and the name of one SibBoolean changed ..???? */
                    }
                }
                else if ( evt.getPropertyName().equals("value") &&
                                        evt.getSource() instanceof SibBoolean &&
                                        evt.getNewValue() instanceof Boolean )
                {   this.ensureValidSelection( ((SibBoolean)evt.getSource()).getName(),
                                               ((Boolean)evt.getNewValue()).booleanValue());
                }
            }
        }
    }
}
