/* 
 * Siberia image searcher : siberia plugin defining image searchers
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
package org.siberia.image.searcher.impl;

import org.siberia.image.searcher.AbstractImageSearcher;
import org.siberia.image.searcher.ImageSize;
import org.siberia.image.searcher.event.ImageFoundEvent;
import org.siberia.image.searcher.event.ImageSearcherEvent;
import org.siberia.image.searcher.event.ImageSearcherListener;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.EventListenerList;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

/**
 *
 * Object that is able to do search about images on google search engine
 *
 * @author Alexis
 */
public class GoogleImageSearcher extends AbstractImageSearcher
{   
    /** logger */
    private transient Logger logger = Logger.getLogger(GoogleImageSearcher.class);
    
    /** url du site de recherche d'images de google */
    private static final String GOOGLE_URL           = "http://images.google.com/images";
    
    /** nom du param�tre html pour la taille de l'image */
    private static final String PARAMETER_IMAGE_SIZE = "imgsz";
    
    /** nom du param�tre html pour la la locale */
    private static final String PARAMETER_LOCALE     = "hl";
    
    /** nom du param�tre html pour les param�tres de recherche */
    private static final String PARAMETER_TOKENS     = "q";
    
    /** list de liens trouv�s */
    private List<URL>         linkFound        = null;
    
    /** executor service */
    private ExecutorService   service          = Executors.newFixedThreadPool(5);
    
    /** list of futures */
    
    /** Creates a new instance of ImageResearch
     *  @param criterions une liste de crit�res de recherches
     */
    public GoogleImageSearcher()
    {	}
    
    /** convert an ImageSize into a String that google understand
     *	@param size an Imagesize
     *	@return a String
     */
    private String convertImageSizeCriterion(ImageSize size)
    {
	String converted = null;
	
	if ( size != null )
	{
	    if ( size.equals(ImageSize.ICON) )
	    {
		converted = "icon";
	    }
	    if ( size.equals(ImageSize.MEDIUM) )
	    {
		converted = "small|medium|large|xlarge";
	    }
	    if ( size.equals(ImageSize.LARGE) )
	    {
		converted = "xxlarge";
	    }
	    if ( size.equals(ImageSize.HUGE) )
	    {
		converted = "huge";
	    }
	}
	 
	if ( converted == null )
	{
	    converted = "";
	}
	
	return converted;
    }
    
    /** cancel search */
    public void cancel()
    {
	if ( this.service != null )
	{
	    this.service.shutdownNow();
	    
	    // bof ... a revoir
	    this.service = Executors.newFixedThreadPool(5);
	}
    }
    
    /** search */
    public void search()
    {
	
//        http://images.google.fr/images?imgsz=xxlarge&gbv=2&hl=fr&q=b+e&btnG=Recherche+d%27images
//        http://images.google.fr/images?imgsz=xxlarge&hl=fr&q=b+e
        
	Runnable run = new Runnable()
	{
	    public void run()
	    {
		fireSearchHasBegan(new ImageSearcherEvent(GoogleImageSearcher.this));
		
		StringBuffer buffer = new StringBuffer(50);

		if ( getCriterions() != null )
		{
		    boolean oneTokenAlreadyApplied = false;

		    for(int i = 0; i < getCriterions().length; i++)
		    {
			String current = getCriterions()[i];

			if ( current != null )
			{
			    if ( oneTokenAlreadyApplied )
			    {
				buffer.append("+");
			    }

			    buffer.append(current);

			    oneTokenAlreadyApplied = true;
			}
		    }
		}

		Locale locale = getLocale();
		if ( locale == null )
		{
		    locale = Locale.getDefault();
		}

		if ( logger.isDebugEnabled() )
		{
		    logger.debug("uri : " + buffer.toString());
		}

		HttpClient client = new HttpClient();

		HttpMethod method = new GetMethod(GOOGLE_URL);

		NameValuePair[] pairs = new NameValuePair[3];
		pairs[0] = new NameValuePair("imgsz", convertImageSizeCriterion(getImageSize()));
		pairs[1] = new NameValuePair("hl", locale.getCountry().toLowerCase());
		pairs[2] = new NameValuePair("q", buffer.toString());
		method.setQueryString(pairs);

		method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));

		InputStream stream = null;

		try
		{
		    // Execute the method.
		    int statusCode = client.executeMethod(method);

		    if (statusCode == HttpStatus.SC_OK)
		    {
			/** on recherche � partir des motifs suivants
			 *  la premi�re occurrence de http:// � partir de l�, on prend jusqu'au prochaine espace ou '>' :
			 *
			 *  exemple :
			 *      <img src=http://tbn0.google.com/images?q=tbn:GIJo-j_dSy4FiM:http://www.discogs.com/image/R-378796-1136999170.jpeg width=135 height=135>
			 *
			 *  on trouve le motif, puis, on prend � partir de http://www.discogs jusqu'au prochain espace...
			 *
			 *  --> http://www.discogs.com/image/R-378796-1136999170.jpeg
			 */
			String searchMotif = "<img src=http://tbn0.google.com/images?q";
			String urlMotif    = "http://";

			int      indexInSearchMotif = -1;
			int      indexInUrlMotif    = -1;
			boolean  motifFound         = false;
			boolean  foundUrl           = false;

			StringBuffer urlBuffer = new StringBuffer(50);

			// Read the response body.
			byte[] bytes = new byte[1024 * 8];
			stream = method.getResponseBodyAsStream();

			if ( stream != null )
			{
			    int read = -1;

			    int linksRetrieved = 0;
			    
			    while( (read = stream.read(bytes)) != -1 )
			    {
				for(int i = 0; i < read; i++)
				{
				    byte currentByte = bytes[i];

				    if ( motifFound )
				    {
					if ( foundUrl )
					{
					    if ( currentByte == ' ' || currentByte == '>' )
					    {
						/* add current url to list of result */
						try
						{
						    URL url = new URL(urlBuffer.toString());

						    fireImageFound(new ImageFoundEvent(GoogleImageSearcher.this, url, linksRetrieved));
						    linksRetrieved ++;

						    if ( linksRetrieved >= getMaximumLinksRetrieved() )
						    {
							break;
						    }
						}
						catch(Exception e)
						{
						    e.printStackTrace();
						}
						finally
						{
						    urlBuffer.delete(0, urlBuffer.length());

						    foundUrl = false;
						    motifFound = false;
						}
					    }
					    else
					    {
						/* add current byte to url buffer */
						urlBuffer.append((char)currentByte);
					    }
					}
					else
					{
					    if ( indexInUrlMotif == urlMotif.length() - 1 )
					    {
						urlBuffer.append(urlMotif);
						urlBuffer.append((char)currentByte);
						foundUrl = true;
						indexInUrlMotif = -1;
					    }

					    /* if the current byte is the same as that attempted on the url motif let's continue */
					    if ( ((char)currentByte) == urlMotif.charAt(indexInUrlMotif + 1) )
					    {
						indexInUrlMotif ++;
					    }
					    else
					    {
						indexInUrlMotif = -1;
					    }
					}
				    }
				    else
				    {
					if ( indexInSearchMotif == searchMotif.length() - 1 )
					{
					    motifFound = true;
					    indexInSearchMotif = -1;
					}

					if ( ((char)currentByte) == searchMotif.charAt(indexInSearchMotif + 1) )
					{
					    indexInSearchMotif ++;
					}
					else
					{
					    indexInSearchMotif = -1;
					}
				    }
				}
				if ( linksRetrieved >= getMaximumLinksRetrieved() )
				{
				    break;
				}
			    }
			}
		    }
		    else
		    {
			System.err.println("Method failed: " + method.getStatusLine());
		    }
		}
		catch (HttpException e)
		{   System.err.println("Fatal protocol violation: " + e.getMessage());
		    e.printStackTrace();
		}
		catch (IOException e)
		{
		    if ( stream != null )
		    {
			try
			{
			    stream.close();
			}
			catch(IOException ex)
			{
			    System.err.println("Fatal transport error: " + ex.getMessage());
			}
		    }
		    System.err.println("Fatal transport error: " + e.getMessage());
		    e.printStackTrace();
		}
		finally
		{   // Release the connection.
		    method.releaseConnection();
		    
		    fireSearchFinished(new ImageSearcherEvent(GoogleImageSearcher.this));
		}
	    }
	};
	
	this.service.submit(run);
    }
}
