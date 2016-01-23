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
package org.blackdog.ui.filter;

import java.util.StringTokenizer;
import javax.swing.table.TableModel;
import org.apache.log4j.Logger;
import org.siberia.base.LangUtilities;
import org.siberia.ui.swing.table.filter.AbstractFilter2;

/**
 *
 * Filter that use the string critera enter by user and
 *  use the artist and album given
 *
 * @author alexis
 */
public class RadioListEditorFilter extends AbstractFilter2
{   
    /** logger */
    private static Logger logger = Logger.getLogger(RadioListEditorFilter.class.getName());
    
    /** name of the property that is related to the input */
    public static final String    PROPERTY_INPUT   = "input";
    
    /** the current input used to filter data */
    private String                input            = null;
    
    /** the input element */
    private String[]              processedInput   = null;
    
    /**
     * Creates a new instance of PlayListEditorFilter */
    public RadioListEditorFilter(TableModel model)
    {   
	super(model, 4000);
    }
    
    /** return the input
     *  @return a String
     */
    public String getInput()
    {
        return this.input;
    }
    
    /** set the filtered String
     *  @param input the input used to filter the table
     */
    public void setInput(String input)
    {   
        if ( ! LangUtilities.equals(this.getInput(), input) )
        {
            String oldInput = this.getInput();
            
            this.input = input;

            if ( this.input == null || this.input.trim().length() == 0 )
            {   this.processedInput = null; }
            else
            {
                StringTokenizer tokenizer = new StringTokenizer(this.input.trim(), " ");
                this.processedInput = new String[tokenizer.countTokens()];

                for(int i = 0; i < this.processedInput.length; i++)
                {   String token = tokenizer.nextToken();
                    if ( token == null || token.trim().length() == 0 )
                    {   this.processedInput[i] = null; }
                    else
                    {   this.processedInput[i] = token.trim().toLowerCase(); }
                }
            }
            
            this.firePropertyChange(PROPERTY_INPUT, oldInput, this.getInput());
        }
    }

    @Override
    protected void filter()
    {	
	/** initialization */
        if ( logger.isDebugEnabled() )
        {
            logger.debug("calling filter");
            logger.debug("filter criterions : ");
            if ( this.processedInput == null )
            {
                logger.debug("\t" + null);
            }
            else
            {
                for(int i = 0; i < this.processedInput.length; i++)
                {
                    logger.debug("\t" + this.processedInput[i] + "#");
                }
            }
        }
        
        int index = -1;

        TableModel model = this.getTableModel();
	
	String[] entries = new String[model.getColumnCount()];
	
//	this.getRedirectionMap().clear();
	
	int inputSize = getInputSize();
	int current = 0;
	for (int i = 0; i < inputSize; i++)
	{
	    if ( test(i, model, entries) )
	    {
		toPrevious.add(new Integer(i));
		// generate inverse map entry while we are here
		fromPrevious[i] = current++;
	    }
	}
    }

    /**
     * Tests whether the given row (in this filter's coordinates) should
     * be added. <p>
     * 
     * PENDING JW: why is this public? called from a protected method? 
     * @param row the row to test
     * @return true if the row should be added, false if not.
     */
    public boolean test(int row, TableModel model, String[] entries)
    {
        // ask the adapter if the column should be includes
        if ( ! adapter.isTestable(getColumnIndex()) )
	{
            return false; 
        }
	
	boolean itemConveyToCritera = true;

	if ( (this.processedInput != null && this.processedInput.length > 0) )
	{
	    itemConveyToCritera = false;

	    for(int j = 0; j < model.getColumnCount(); j++)
	    {   Object current = model.getValueAt(row, j);
		String value = String.valueOf(current);
		if ( value == null || value.length() == 0 )
		{   entries[j] = null; }
		else
		{   entries[j] = value.trim().toLowerCase(); }
	    }

	    for(int k = 0; k < this.processedInput.length; k++)
	    {
		String currentToken = this.processedInput[k];

		boolean found = false;

		for(int h = 0; h < entries.length && ! itemConveyToCritera; h++)
		{
		    String currentEntry = entries[h];

		    if ( currentEntry != null && currentEntry.indexOf(currentToken) != -1 )
		    {
			found = true;
			break;
		    }
		}

		if ( found )
		{
		    if ( k >= this.processedInput.length - 1 )
		    {
			itemConveyToCritera = true;
		    }
		}
		else
		{
		    break;
		}
	    }
	}

	return itemConveyToCritera;
    }
    
}
