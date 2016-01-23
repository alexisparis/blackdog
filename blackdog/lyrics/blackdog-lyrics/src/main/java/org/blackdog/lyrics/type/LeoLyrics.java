/*
 * blackdog lyrics : define editor and systems to get lyrics for a song
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
package org.blackdog.lyrics.type;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import org.siberia.type.annotation.bean.Bean;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

/**
 *
 * Default representation of lyrics by using Leo's lyrics web site
 *
 *
 *
 * input « zero smashing » (artist name, song title) --> http://www.leoslyrics.com/search.php?search=zero+smashing&sartist=1&ssongtitle=1
 *
 * input « zero smashing » (song title)              --> http://www.leoslyrics.com/search.php?search=zero+smashing&ssongtitle=1
 *
 * input « zero smashing » (artist name)             --> http://www.leoslyrics.com/search.php?search=zero+smashing&sartist=1
 *
 *  result is : 
 *
 * <table border=0 width="100%">
 *  <tr>
 *    <td><font face="Arial, Verdana" size=3><B>ARTIST</B></font></td>
 *   <td><font face="Arial, Verdana" size=3><B>SONG</B></font></td>
 *  </tr>
 * 
 *   <tr>
 *     <td>
 *       <font size=3 face="Arial, Verdana">
 *       <a href="/artists/451/">The Smashing Pumpkins</a>
 *       </font>
 *     </td>
 *     <td>
 *       <font size=3 face="Arial, Verdana">
 *       <a href="/listlyrics.php?hid=j5d0nvN8ncA%3D"><b>Zero</b></a>
 *       
 *       </font>
 *     </td>
 *
 *
 *  we find '/listlyrics.php?hid=j5d0nvN8ncA%3D'
 *
 *  let's try http://www.leoslyrics.com/listlyrics.php?hid=j5d0nvN8ncA%3D
 *
 *  we get : 
 *
 *  
 *                                   <p>
 *                                       This song is on the following albums:
 *                                         <li>
 *                                         <a href="/albums/96/">Mellon Collie And The Infinite Sadness</a>
 *                                   </p>
 *                                   <p>
 *                                     <font face="Trebuchet MS, Verdana, Arial" size=-1>
 *                                   <br /> 
 *                                   <br />
 *                                   <br />
 *                                   <br />my reflection  dirty mirror 
 *                                   <br />there&#39;s no connection to myself 
 *                                   <br />i&#39;m your lover  i&#39;m your zero 
 *                                   <br />i&#39;m the face in your dreams of glass 
 *                                   <br />so save your prayers 
 *                                   <br />for when you&#39;re really gonna need &#39;em 
 *                                   <br />throw out your cares and fly 
 *                                   <br />wanna go for a ride? 
 *                                   <br />
 *                                   <br />she&#39;s the one for me 
 *                                   <br />she&#39;s all i really need 
 *                                   <br />cause she&#39;s the one for me 
 *                                   <br />emptiness is loneliness  and loneliness is cleanliness 
 *                                   <br />and cleanliness is godliness, and god is empty just like me 
 *                                   <br />intoxicated with the madness, i&#39;m in love with my sadness 
 *                                   <br />bullshit fakers, enchanted kingdoms 
 *                                   <br />the fashion victims chew their charcoal teeth 
 *                                   <br />i never let on, that i was on a sinking ship 
 *                                   <br />i never let on that i was down 
 *                                   <br />you blame yourself, for what you can&#39;t ignore 
 *                                   <br />you blame yourself for wanting more 
 *                                   <br />she&#39;s the one for me 
 *                                   <br />she&#39;s all i really need 
 *                                   <br />she&#39;s the one for me 
 *                                   <br />she&#39;s my one and only
 *                                   <br />
 *                                   <br />
 *                                     </font>
 *                                   </p>
 *
 * @author alexis
 */
@Bean(  name="lyrics",
        internationalizationRef="org.blackdog.rc.i18n.type.LeoLyrics",
        expert=false,
        hidden=true,
        preferred=true,
        propertiesClassLimit=Object.class,
        methodsClassLimit=Object.class
      )
public class LeoLyrics extends AbstractThreadedLyrics
{   
    /** url of the leo lyrics site */
    private static final String LEO_URL            = "http://www.leoslyrics.com";
    
    /** pattern list */
    private static final String SEARCH_SUFFIX      = "/search.php?search=";
    
    /** pattern list */
    private static final String ID_LYRIC_PAGE_PREF = "/listlyrics.php?hid=";
    
    /** logger */
    private Logger logger = Logger.getLogger(LeoLyrics.class);
    
    /**
     * Creates a new instance of LeoLyrics
     */
    public LeoLyrics()
    {	}
    
    /** retrieve the content according to the current song item caracteristics */
    protected void _retrieve()
    {
	this.setRetrieveStatus(LyricsRetrievedStatus.CURRENTLY_IN_PROCESS);
	
	try
	{
	    String content = this.getLyrics(true, this.getArtistCriteriums(), true, this.getSongNameCriteriums());
	    
	    this.setHtmlContent(content);
	    
	    if ( content != null && content.length() > 0 )
	    {
		this.setRetrieveStatus(LyricsRetrievedStatus.RETRIEVED);
	    }
	    else
	    {
		this.setRetrieveStatus(LyricsRetrievedStatus.UNRETRIEVED);
	    }
	}
	catch(Exception e)
	{
	    logger.error("unable to get lyrics from leo's lyrics", e);
	    this.setRetrieveStatus(LyricsRetrievedStatus.UNRETRIEVED);
	}
	
    }
    /** return the lyrics found for given parameters
     *  @param searchArtist true to search for artist
     *  @param artistTokens a list of String representing the critera for artist
     *  @param searchSong true to search for song
     *  @param songTokens a list of String representing the critera for the song
     *  @return the lyrics or null if not found
     */
    public String getLyrics(boolean searchArtist, List<String> artistTokens, boolean searchSong, List<String> songTokens) throws HttpException, IOException
    {
	if ( logger.isDebugEnabled() )
	{
	    StringBuffer buffer = new StringBuffer();
	    
	    if ( artistTokens != null )
	    {
		buffer.append("artist tokens=[");
		for(int i = 0; i < artistTokens.size(); i++)
		{
		    if ( i > 0 )
		    {
			buffer.append(",");
		    }
		    
		    buffer.append(artistTokens.get(i));
		}
		buffer.append("] ");
	    }
	    if ( songTokens != null )
	    {
		buffer.append("song tokens=[");
		for(int i = 0; i < songTokens.size(); i++)
		{
		    if ( i > 0 )
		    {
			buffer.append(",");
		    }
		    
		    buffer.append(songTokens.get(i));
		}
		buffer.append("] ");
	    }
	    
	    logger.debug("getLyrics (searchArtist=" + searchArtist + ", searchSong=" + searchSong + " with criteriums : " + buffer.toString());
	}
	
        String page = this.getLyricsFirstCandidateURI(searchArtist, artistTokens, searchSong, songTokens);
        
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("first candidate URI : " + page);
	}
	
	if ( page == null )
	{
	    return null;
	}
	else
	{
	    return this.getLyricsContent(page);
	}
    }
    
    /** return the page containing the result of the search
     *  @param searchArtist true to search for artist
     *  @param artistTokens a list of String representing the critera for artist
     *  @param searchSong true to search for song
     *  @param songTokens a list of String representing the critera for the song
     *  @return the id of the page containing the lyrics of the first candidate or null if not found
     */
    private String getResultPage(boolean searchArtist, List<String> artistTokens, boolean searchSong, List<String> songTokens) throws HttpException, IOException
    {   
	String id = null;
        
        if ( ( searchArtist || searchSong ) &&
             ( (artistTokens == null ? false : artistTokens.size() > 0) || (songTokens == null ? false : songTokens.size() > 0)) )
        {
            HttpClient client = new HttpClient();

            StringBuffer uri = new StringBuffer();
            
            // create uri : http://www.leoslyrics.com/search.php?search=zero+smashing&sartist=1&ssongtitle=1
            
            uri.append(LEO_URL + SEARCH_SUFFIX);
            
            boolean tokenAddedForSong = false;
            
            if ( searchSong && songTokens != null )
            {   
		for(int i = 0; i < songTokens.size(); i++)
		{
		    String currentToken = songTokens.get(i);
		    
		    if ( currentToken != null )
		    {
			currentToken = currentToken.trim();
			
			if ( currentToken.length() > 0 )
			{   
			    if ( tokenAddedForSong )
			    {
				uri.append("+");
			    }
			    
			    uri.append(currentToken);
			    
			    tokenAddedForSong = true;
			}
		    }
		}
            }
            if ( searchSong && artistTokens != null )
            {   
		for(int i = 0; i < artistTokens.size(); i++)
		{
		    String currentToken = artistTokens.get(i);
		    
		    if ( currentToken != null )
		    {
			currentToken = currentToken.trim();
			
			if ( currentToken.length() > 0 )
			{   
			    if ( tokenAddedForSong )
			    {
				uri.append("+");
			    }
			    
			    uri.append(currentToken);
			    
			    tokenAddedForSong = true;
			}
		    }
		}
            }
            
            if ( searchArtist )
            {   uri.append("&");
                uri.append("sartist=1");
            }
            
            if ( searchSong )
            {   uri.append("&");
                uri.append("ssongtitle=1");
            }
            
            logger.info("leo lyrics request : " + uri);

            GetMethod method = new GetMethod(uri.toString());

            method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(5, false));

            try
            {
                int statusCode = client.executeMethod(method);
		
                if ( statusCode == HttpStatus.SC_OK )
                {   
		    InputStream stream = method.getResponseBodyAsStream();

		    if ( stream != null )
		    {   /* search first occurrence of '?hid=' */
			String hidString = "?hid=";
			StringBuffer bufferId  = new StringBuffer();
			StringBuffer bufferTmp = new StringBuffer(hidString.length());
			boolean      idFound   = false;

			byte[] buffer = new byte[128];

			try
			{   int byteRead = stream.read(buffer);

			    while(byteRead != -1)
			    {
				if ( ! idFound )
				{   
				    for(int i = 0; i < byteRead; i++)
				    {   
					byte b = buffer[i];

					if ( ! idFound )
					{
					    if ( bufferTmp.length() >= hidString.length() )
					    {   if ( b != '"' )
						{   bufferId.append((char)b); }
						else
						{   idFound = true; }
					    }
					    else
					    {
						if ( b == hidString.charAt(bufferTmp.length()) )
						{   bufferTmp.append((char)b); }
						else if ( bufferTmp.length() > 0 )
						{   bufferTmp.delete(0, bufferTmp.length() - 1); }
					    }
					}
				    }
				}

				byteRead = stream.read(buffer);
			    }
			}
			finally
			{
			    if ( stream != null )
			    {
				try
				{
				    stream.close();
				}
				catch(IOException e)
				{
				    logger.warn("unable to close the input stream from request " + uri, e);
				}
			    }
			}

			if ( bufferId.length() > 0 )
			{   id = bufferId.toString(); }
		    }
		}
		else
		{
		    logger.error("getting error while trying to connect to : " + uri + " (error code=" + statusCode + ")");
		}
            }
            finally
            {   method.releaseConnection(); }
        }
        
        return id;
    }
    
    /** method that return the uri of the page containing the lyrics of the current AudioItem
     *  @param searchArtist true to search for artist
     *  @param artistTokens a List representing the criteruims for artist
     *  @param searchSong true to search for song
     *  @param songTokens a List representing the criteruims for the song
     *  @return  the uri to use to get the lyrics
     */
    public String getLyricsFirstCandidateURI(boolean searchArtist, List<String> artistTokens, boolean searchSong, List<String> songTokens) throws HttpException, IOException
    {   String result = null;
        
        String id = this.getResultPage(searchArtist, artistTokens, searchSong, songTokens);
        
        if ( id != null )
        {   
            // http://www.leoslyrics.com/listlyrics.php?hid=j5d0nvN8ncA%3D
            result = LEO_URL + ID_LYRIC_PAGE_PREF + id;
        }
        
        return result;
    }
    
    /** return an html String representing the lyrics content of the page with the given uri
     *  @param uri the uri of the page containing the song lyrics
     *  @return a String or null if failed
     */
    private String getLyricsContent(String uri) throws HttpException, IOException
    {   String content = null;
        
        if ( uri != null )
        {
            HttpClient client = new HttpClient();

            GetMethod method = new GetMethod(uri);
//	    method.addRequestHeader(Header.)

            method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                    new DefaultHttpMethodRetryHandler(5, false));

            try
            {
                int statusCode = client.executeMethod(method);
                if (  statusCode == HttpStatus.SC_OK )
		{
		    String response = method.getResponseBodyAsString();

		    if ( response != null )
		    {
			/* search first occurrence of 'This song is on the following albums'
			 *
			 *	if not found, search <font face="Trebuchet MS, Verdana, Arial" size=-1>
			 *	take to next </font>
			 */
			String       headerString          = "This song is on the following albums";
			String       headerString2         = "<font face=\"Trebuchet MS, Verdana, Arial\" size=-1>";
			String       paragraphString       = "<p>";
			String       paragraphEndString    = "</p>";
			StringBuffer bufferContent         = new StringBuffer(800);
			StringBuffer bufferHeaderTmp       = new StringBuffer(headerString.length());
			StringBuffer bufferParagraphTmp    = new StringBuffer(paragraphString.length());
			StringBuffer bufferParagraphEndTmp = new StringBuffer(paragraphEndString.length());
			boolean      headerFound           = false;
			boolean      paragraphFound        = false;
			boolean      contentFound          = false;
    //		    
			for(int i = 0; i < response.length() && ! contentFound; i++)
			{
			    char b = response.charAt(i);

			    /** header found */
			    if ( headerFound )
			    {
				if ( paragraphFound )
				{   
				    bufferContent.append( (char)b );

				    if ( b == paragraphEndString.charAt(bufferParagraphEndTmp.length()) )
				    {   bufferParagraphEndTmp.append((char)b); }
				    else if ( bufferParagraphEndTmp.length() > 0 )
				    {   bufferParagraphEndTmp.delete(0, bufferParagraphEndTmp.length() - 1); }

				    /* did we find the end of the paragraph ? */
				    if ( bufferParagraphEndTmp.length() >= paragraphEndString.length() )
				    {   contentFound = true; }
				}
				else
				{   
    //                                                System.out.println("  <<");
				    if ( b == paragraphString.charAt(bufferParagraphTmp.length()) )
				    {   bufferParagraphTmp.append((char)b); }
				    else if ( bufferParagraphTmp.length() > 0 )
				    {   bufferParagraphTmp.delete(0, bufferParagraphTmp.length() - 1); }

				    if ( bufferParagraphTmp.length() >= paragraphString.length() )
				    {   paragraphFound = true;
					bufferHeaderTmp.append(headerString);
				    }
				}
			    }
			    else
			    {   
    //                                            System.out.println("  <<");
				if ( b == headerString.charAt(bufferHeaderTmp.length()) )
				{   bufferHeaderTmp.append((char)b); }
				else if ( bufferHeaderTmp.length() > 0 )
				{   bufferHeaderTmp.delete(0, bufferHeaderTmp.length() - 1); }

				if ( bufferHeaderTmp.length() >= headerString.length() )
				{   headerFound = true; }
			    }
			}

			if ( ! contentFound )
			{
			    int headerString2Index = response.indexOf(headerString2);

			    if ( headerString2Index > -1 )
			    {
				String truncatedResponse = response.substring(headerString2Index);

				int lastEndfont = truncatedResponse.indexOf("</font>");

				if ( lastEndfont > -1 )
				{
				    bufferContent.append(truncatedResponse.substring(0, lastEndfont));
				    contentFound = true;
				}
			    }
			}

			if ( bufferContent.length() > 0 )
			{   
			    bufferContent.insert(0, "<html>");
			    bufferContent.append("</html>");

			    try
			    {
				content = new String(bufferContent.toString().getBytes(), method.getResponseCharSet()); // "UTF-8"
			    }
			    catch(UnsupportedEncodingException e)
			    {
				logger.warn("unable to encode lyrics content in UTF-8");
				content = bufferContent.toString();
			    }
			}
		    }
		}
		else
		{
		    logger.error("getting error while trying to connect to : " + uri + " (error code=" + statusCode + ")");
		}
            }
            finally
            {   method.releaseConnection(); }
        }
        
        return content;
    }
    
}
