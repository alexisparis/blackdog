/* 
 * Siberia basic components : siberia plugin defining components supporting siberia types
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
package org.siberia.ui.awl;

import java.awt.Image;
import org.awl.DefaultWizard;
import org.awl.ImagePanel;
import org.awl.SummarizedWizard;
import org.awl.Wizard;
import org.awl.header.WizardHeader;

/**
 *
 * @author alexis
 */
public class WizardPreparator
{
    /** summary visible */
    private static boolean      summaryVisible = true;
    
    /** header */
    private static WizardHeader header         = null;
    
    /** background image of the summary */
    private static Image        summaryImage   = null;
    
    /** Creates a new instance of WizardPreparator */
    private WizardPreparator()
    {	}
    
    /** ask to prepare the given wizard
     *	@param wizard a wizard
     */
    public static void prepareWizard(Wizard wizard)
    {
	if ( wizard instanceof DefaultWizard )
	{
	    ((DefaultWizard)wizard).setHeader(getHeader());
	    ((DefaultWizard)wizard).setSummaryVisible(isSummaryVisible());
	    
	    ImagePanel summary = ((DefaultWizard)wizard).getSummaryPanel();
	    if ( summary != null )
	    {
		summary.setBackgroundImage(getSummaryImage());
	    }
	}
    }

    /** return true if the summary must be visible if the wizard supports summary
     *	@return a boolean
     */
    public static boolean isSummaryVisible()
    {
	return summaryVisible;
    }

    /** indicate if the summary must be visible if the wizard supports summary
     *	@param aSummaryVisible a boolean
     */
    public static void setSummaryVisible(boolean aSummaryVisible)
    {
	summaryVisible = aSummaryVisible;
    }

    /** return the WizardHeader to use if the wizard supports header
     *	@return a WizardHeader
     */
    public static WizardHeader getHeader()
    {
	return header;
    }

    /** initialize the WizardHeader to use if the wizard supports header
     *	@param aHeader a WizardHeader
     */
    public static void setHeader(WizardHeader aHeader)
    {
	header = aHeader;
    }

    /** return the image to use as background of the summary if the wizard supports it
     *	@return an Image
     */
    public static Image getSummaryImage()
    {
	return summaryImage;
    }

    /** initailize the image to use as background of the summary if the wizard supports it
     *	@param aSummaryImage an Image
     */
    public static void setSummaryImage(Image aSummaryImage)
    {
	summaryImage = aSummaryImage;
    }
    
}
