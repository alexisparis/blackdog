/*
 * blackdog sound spi : define player based on java sound spi
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
package org.blackdog.sound;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;
import javax.sound.sampled.spi.FormatConversionProvider; 
import org.apache.log4j.Logger;
import org.siberia.ResourceLoader;
import org.siberia.env.PluginResources;
import org.siberia.env.SiberExtension;
import org.siberia.exception.ResourceException;

/**
 *
 * Due to multi-classloaders architecture, the SPI system could not be completely
 *  initialize with the services declarations of mp3spi and vorbisspi.
 *  Therefore, this class provide some methods to avoid using an AudioSystem class
 *  that is incompletely initialized.
 *
 * @author alexis
 */
public class BlackdogAudioSystem
{
    /** logger */
    private static Logger                logger  = Logger.getLogger(BlackdogAudioSystem.class);
    
    public static final int NOT_SPECIFIED = AudioSystem.NOT_SPECIFIED;
    
    /** extension point for SpiAudioFileReader */
    private static final String EXTENSION_POINT_FILEREADER          = "SpiAudioFileReader";
    
    /** extension point for SpiFormatConversionProvider */
    private static final String EXTENSION_POINT_CONVERSION_PROVIDER = "SpiFormatConversionProvider";
    
    /** list of AudioFileReader */
    private static List<AudioFileReader> readers = new ArrayList<AudioFileReader>();
    
    /* map linking preferred extension with the AudioFileReader to use
     *	the extension are always expressed in lower case
     */
    private static Map<String, AudioFileReader> preferredReaders = new HashMap<String, AudioFileReader>();
    
    static
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("registering spi audio file readers");
	}
	
        /** inspect plugin to look for type extensions */
        Set<SiberExtension> extensions = PluginResources.getInstance().getExtensions(EXTENSION_POINT_FILEREADER);
        if ( extensions != null )
        {   Iterator<SiberExtension> it = extensions.iterator();
            while(it.hasNext())
            {   SiberExtension currentExtension = it.next();
                
		String classname = currentExtension.getStringParameterValue("class");
                try
                {   
		    Class typeClass = ResourceLoader.getInstance().getClass(classname);
		    
		    /** instantiate class */
		    Object o = typeClass.newInstance();
		    
		    if ( o instanceof AudioFileReader )
		    {
			readers.add( (AudioFileReader)o );
			
			/** process preferred extensions */
			String preferredExtensions = currentExtension.getStringParameterValue("preferredExtensions");
			
			if ( preferredExtensions != null && preferredExtensions.length() > 0 )
			{
			    String[] formats = preferredExtensions.split(",");
			    
			    if ( formats != null )
			    {
				for(int i = 0; i < formats.length; i++)
				{
				    String currentFormat = formats[i];
				    
				    if ( currentFormat != null )
				    {
					currentFormat = currentFormat.trim();
				    }
				    
				    if ( currentFormat != null )
				    {
					preferredReaders.put(currentFormat, (AudioFileReader)o);
					if ( logger.isDebugEnabled() )
					{
					    logger.debug("extension '" + currentFormat + "' registered with audio file reader of kind " + classname);
					}
				    }
				    else
				    {
					logger.info("unable to register preferred extension '" + currentFormat + "' for audio file reader of kind " + classname);
				    }
				}
			    }
			}
		    }
		    else
		    {
			logger.warn("trying to register " + o + " as a file reader --> failed");
		    }
                }
                catch(Exception e)
                {   
		    logger.error("error while registering class=" + classname + " as audio file reader", e);
		}           
            }
        }
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of registration of spi audio file readers");
	}
	
	
//        readers.add(new javazoom.spi.mpeg.sampled.file.MpegAudioFileReader());
//        readers.add(new javazoom.spi.vorbis.sampled.file.VorbisAudioFileReader());
//        readers.add(new org.kc7bfi.jflac.sound.spi.FlacAudioFileReader());
	
//        readers.add(new org.tritonus.sampled.file.jorbis.JorbisAudioFileReader());
	
//        readers.add(new com.sun.media.sound.WaveFileReader());
//        readers.add(new com.sun.media.sound.AiffFileReader());
//        readers.add(new com.sun.media.sound.AuFileReader());
    }
    
    /** list de FormatConversionProvider */
    private static List<FormatConversionProvider> codecs = new ArrayList<FormatConversionProvider>();
    
    /* map linking preferred extension with the FormatConversionProvider to use
     *	the extension are always expressed in lower case
     */
    private static Map<String, FormatConversionProvider> preferredConverters = new HashMap<String, FormatConversionProvider>();
    
    static
    {
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("registering spi audio conversion providers");
	}
	
        /** inspect plugin to look for type extensions */
        Set<SiberExtension> extensions = PluginResources.getInstance().getExtensions(EXTENSION_POINT_CONVERSION_PROVIDER);
        if ( extensions != null )
        {   Iterator<SiberExtension> it = extensions.iterator();
            while(it.hasNext())
            {   SiberExtension currentExtension = it.next();
                
		String classname = currentExtension.getStringParameterValue("class");
                try
                {   
		    Class typeClass = ResourceLoader.getInstance().getClass(classname);
		    
		    /** instantiate class */
		    Object o = typeClass.newInstance();
		    
		    if ( o instanceof FormatConversionProvider )
		    {
			codecs.add( (FormatConversionProvider)o );
			
			/** process preferred extensions */
			String preferredExtensions = currentExtension.getStringParameterValue("preferredExtensions");
			
			if ( preferredExtensions != null && preferredExtensions.length() > 0 )
			{
			    String[] formats = preferredExtensions.split(",");
			    
			    if ( formats != null )
			    {
				for(int i = 0; i < formats.length; i++)
				{
				    String currentFormat = formats[i];
				    
				    if ( currentFormat != null )
				    {
					currentFormat = currentFormat.trim();
				    }
				    
				    if ( currentFormat != null )
				    {
					preferredConverters.put(currentFormat, (FormatConversionProvider)o);
					if ( logger.isDebugEnabled() )
					{
					    logger.debug("extension '" + currentFormat + "' registered with audio conversion provider of kind " + classname);
					}
				    }
				    else
				    {
					logger.info("unable to register preferred extension '" + currentFormat + "' for audio conversion provider of kind " + classname);
				    }
				}
			    }
			}
		    }
		    else
		    {
			logger.warn("trying to register " + o + " as a conversion provider --> failed");
		    }
                }
                catch(Exception e)
                {   
		    logger.error("error while registering class=" + classname + " as audio conversion provider", e);
		}           
            }
        }
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("end of registration of spi audio conversion providers");
	}
//        codecs.add(new javazoom.spi.mpeg.sampled.convert.MpegFormatConversionProvider());
//        codecs.add(new javazoom.spi.vorbis.sampled.convert.VorbisFormatConversionProvider());
//        codecs.add(new org.kc7bfi.jflac.sound.spi.FlacFormatConvertionProvider());
	
//        codecs.add(new org.tritonus.sampled.convert.jorbis.JorbisFormatConversionProvider());
	
//        codecs.add(new com.sun.media.sound.PCMtoPCMCodec());
//        codecs.add(new com.sun.media.sound.AlawCodec());
//        codecs.add(new com.sun.media.sound.UlawCodec());
	
    }
    
    /** Creates a new instance of AudioFileReaderRegistry */
    private BlackdogAudioSystem()
    {   }
    
    public static Line getLine(Line.Info info) throws LineUnavailableException {
	return AudioSystem.getLine(info);
    }
    /**
     * Obtains an array of mixer info objects that represents
     * the set of audio mixers that are currently installed on the system.
     * @return an array of info objects for the currently installed mixers.  If no mixers
     * are available on the system, an array of length 0 is returned.
     * @see #getMixer
     */
    public static Mixer.Info[] getMixerInfo()
    {
	return AudioSystem.getMixerInfo();
    }
    
    public static Mixer getMixer(Mixer.Info info)
    {
	return AudioSystem.getMixer(info);
    }
    
    public static AudioInputStream getAudioInputStream(URL url) throws UnsupportedAudioFileException, IOException
    {
	return getAudioInputStream(url.openStream());
    }
    
    public static AudioInputStream getAudioInputStream(File file) throws UnsupportedAudioFileException, IOException
    {
	AudioInputStream stream = null;
	
	if ( file != null )
	{
	    stream = getAudioInputStream(new FileInputStream(file));
	}
	
	return stream;
    }
    
    public static AudioInputStream getAudioInputStream(InputStream stream) throws UnsupportedAudioFileException, IOException
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getaudioInputStream with " + stream);
	}
	
	AudioInputStream audioStream = null;

	for(int i = 0; i < readers.size(); i++ )
        {   AudioFileReader reader = (AudioFileReader)readers.get(i);
	    	
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("getAudioInputStream try to use reader " + reader);
	    }
	    
	    List<String> toto = new ArrayList<String>();
	    
	    try
            {   audioStream = reader.getAudioInputStream( stream ); // throws IOException
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("getAudioInputStream use reader " + reader + " with stream " + audioStream);
		}
		
		break;
	    }
            catch (UnsupportedAudioFileException e)
            {   continue; }
            catch (Exception e)
            {   continue; }
	}
	
	if ( audioStream == null )
	{
	    audioStream = AudioSystem.getAudioInputStream(stream);
	}

	if( audioStream==null )
        {   throw new UnsupportedAudioFileException("could not get audio input stream from input stream"); }
        else
        {   return audioStream; }
    }
    
    /** create an AudioInputStream with the given InputStreamProvider
     *	@param provider a InputStreamProvider
     *	@return a AudioInputStream
     *
     *	@exception UnsupportedAudioFileException if no AudioFileReader were found
     *	@exception IOException
     */
    public static AudioInputStream getAudioInputStream(InputStreamProvider provider) throws UnsupportedAudioFileException, IOException
    {
	return getAudioInputStream(provider, null);
    }
    
    /** create an AudioInputStream with the given InputStreamProvider
     *	@param provider a InputStreamProvider
     *	@param extension the extension of the file if we know it<br>
     *		this helps optimization to choose the best AudioFileReader to use
     *	@return a AudioInputStream
     *
     *	@exception UnsupportedAudioFileException if no AudioFileReader were found
     *	@exception IOException
     */
    public static AudioInputStream getAudioInputStream(InputStreamProvider provider, String extension) throws UnsupportedAudioFileException, IOException
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getAudioInputStream with " + provider + " and extension " + extension);
	}
	
	AudioInputStream audioStream = null;

	if ( provider != null )
	{
	    /** search with the extension */
	    if ( extension != null )
	    {
		AudioFileReader preferredReader = preferredReaders.get(extension);
		
		if ( preferredReader != null )
		{   
		    InputStream stream = provider.createStream();

		    try
		    {   audioStream = preferredReader.getAudioInputStream( stream ); // throws IOException

			if ( logger.isDebugEnabled() )
			{
			    logger.debug("getAudioInputStream use reader " + preferredReader + " with stream " + audioStream);
			}
		    }
		    catch (Exception e)
		    {   
			if ( stream != null )
			{
			    try
			    {
				stream.close();
			    }
			    catch(IOException ex)
			    {
				ex.printStackTrace();
			    }
			}
		    }
		}
	    }
	    
	    if ( audioStream == null )
	    {
		for(int i = 0; i < readers.size(); i++ )
		{   
		    AudioFileReader reader = (AudioFileReader)readers.get(i);

		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("getAudioInputStream try to use reader " + reader);
		    }

		    InputStream stream = provider.createStream();

		    try
		    {   audioStream = reader.getAudioInputStream( stream ); // throws IOException

			if ( logger.isDebugEnabled() )
			{
			    logger.debug("getAudioInputStream use reader " + reader + " with stream " + audioStream);
			}

			break;
		    }
		    catch (Exception e)
		    {   
			if ( stream != null )
			{
			    try
			    {
				stream.close();
			    }
			    catch(IOException ex)
			    {
				ex.printStackTrace();
			    }
			}

			continue;
		    }
		}
	    }

	    if ( audioStream == null )
	    {
		InputStream stream = provider.createStream();
		
		try
		{
		    audioStream = AudioSystem.getAudioInputStream(stream);
		}
		catch(Exception e)
		{
		    if ( stream != null )
		    {
			try
			{
			    stream.close();
			}
			catch(IOException ex)
			{
			    ex.printStackTrace();
			}
		    }
		    
		    if ( e instanceof UnsupportedAudioFileException )
		    {
			throw (UnsupportedAudioFileException)e;
		    }
		    else if ( e instanceof IOException )
		    {
			throw (IOException)e;
		    }
		    else
		    {
			logger.error("get exception while calling getAudioInputStream", e);
		    }
		}
	    }
	}

	if( audioStream==null )
        {   throw new UnsupportedAudioFileException("could not get audio input stream from input stream"); }
        else
        {   return audioStream; }
    }
    
    public static AudioFileFormat getAudioFileFormat(File file) throws UnsupportedAudioFileException, IOException
    {
	return getAudioFileFormat(new FileInputStream(file));
    }
    
    public static AudioFileFormat getAudioFileFormat(URL url) throws UnsupportedAudioFileException, IOException
    {
	return getAudioFileFormat(url.openStream());
    }
    
    public static AudioFileFormat getAudioFileFormat(InputStream stream) throws UnsupportedAudioFileException, IOException
    {   
        AudioFileFormat format = null;

	for(int i = 0; i < readers.size(); i++ )
        {   AudioFileReader reader = (AudioFileReader)readers.get(i);
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("getAudioInputStream try to use reader " + reader);
	    }
	    
	    try
            {   format = reader.getAudioFileFormat( stream ); // throws IOException
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("getAudioFileFormat use reader " + reader);
		}
		break;
	    }
            catch (UnsupportedAudioFileException e)
            {   continue; }
	}
	
	if ( format == null )
	{
	    format = AudioSystem.getAudioFileFormat(stream);
	}

	if( format==null )
        {   throw new UnsupportedAudioFileException("file is not a supported file type"); }
        else
        {   return format; }
    }
    
    /** create an AudioFileFormat with the given InputStreamProvider
     *	@param provider a InputStreamProvider
     *	@return a AudioFileFormat
     *
     *	@exception UnsupportedAudioFileException if no AudioFileReader were found
     *	@exception IOException
     */
    public static AudioFileFormat getAudioFileFormat(InputStreamProvider provider) throws UnsupportedAudioFileException, IOException
    {
	return getAudioFileFormat(provider, null);
    }
    
    /** create an AudioFileFormat with the given InputStreamProvider
     *	@param provider a InputStreamProvider
     *	@param extension the extension of the file if we know it<br>
     *		this helps optimization to choose the best AudioFileFormat to use
     *	@return a AudioFileFormat
     *
     *	@exception UnsupportedAudioFileException if no AudioFileReader were found
     *	@exception IOException
     */
    public static AudioFileFormat getAudioFileFormat(InputStreamProvider provider, String extension) throws UnsupportedAudioFileException, IOException
    {   
        AudioFileFormat format = null;

	if ( provider != null )
	{
	    /** search with the extension */
	    if ( extension != null )
	    {
		AudioFileReader preferredReader = preferredReaders.get(extension);
		
		if ( preferredReader != null )
		{   
		    InputStream stream = provider.createStream();

		    try
		    {   format = preferredReader.getAudioFileFormat( stream ); // throws IOException

			if ( logger.isDebugEnabled() )
			{
			    logger.debug("getAudioFileFormat use reader " + preferredReader);
			}
		    }
		    catch (Exception e)
		    {   
			if ( stream != null )
			{
			    try
			    {
				stream.close();
			    }
			    catch(IOException ex)
			    {
				ex.printStackTrace();
			    }
			}
		    }
		}
	    }
	    
	    if ( format == null )
	    {
		for(int i = 0; i < readers.size(); i++ )
		{   AudioFileReader reader = (AudioFileReader)readers.get(i);

		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("getAudioInputStream try to use reader " + reader);
		    }

		    InputStream stream = provider.createStream();
		    try
		    {   
			format = reader.getAudioFileFormat( stream ); // throws IOException
			if ( logger.isDebugEnabled() )
			{
			    logger.debug("getAudioFileFormat use reader " + reader);
			}
			break;
		    }
		    catch (Exception e)
		    {   
			if ( stream != null )
			{
			    try
			    {
				stream.close();
			    }
			    catch(IOException ec)
			    {
				ec.printStackTrace();
			    }
			}

			continue;
		    }
		}
	    }

	    if ( format == null )
	    {
		InputStream stream = provider.createStream();
		
		try
		{
		    format = AudioSystem.getAudioFileFormat(stream);
		}
		catch(Exception e)
		{
		    if ( stream != null )
		    {
			try
			{
			    stream.close();
			}
			catch(IOException ex)
			{
			    ex.printStackTrace();
			}
		    }
		    
		    if ( e instanceof UnsupportedAudioFileException )
		    {
			throw (UnsupportedAudioFileException)e;
		    }
		    else if ( e instanceof IOException )
		    {
			throw (IOException)e;
		    }
		    else
		    {
			logger.error("get exception while calling getAudioFileFormat", e);
		    }
		}
	    }
	}

	if( format == null )
        {   throw new UnsupportedAudioFileException("file is not a supported file type"); }
        else
        {   return format; }
    }
    
    /**
     * Obtains an audio input stream of the indicated format, by converting the
     * provided audio input stream.
     * @param targetFormat the desired audio format after conversion
     * @param sourceStream the stream to be converted
     * @return an audio input stream of the indicated format
     * @throws IllegalArgumentException if the conversion is not supported
     * #see #getTargetEncodings(AudioFormat)
     * @see #getTargetFormats(AudioFormat.Encoding, AudioFormat)
     * @see #isConversionSupported(AudioFormat, AudioFormat)
     * @see #getAudioInputStream(AudioFormat.Encoding, AudioInputStream)
     */
    public static AudioInputStream getAudioInputStream(AudioFormat targetFormat, AudioInputStream sourceStream)
    {   
	AudioInputStream result = null;
	
	if (sourceStream.getFormat().matches(targetFormat))
        {   result = sourceStream; }

	if( result == null )
	{
	    for(int i = 0; i < codecs.size(); i++)
	    {   FormatConversionProvider codec = (FormatConversionProvider) codecs.get(i);

		if ( logger.isDebugEnabled() )
		{
		    logger.debug("getAudioInputStream try to use codec " + codec);
		}

		if(codec.isConversionSupported(targetFormat,sourceStream.getFormat()) )
		{   
		    result = codec.getAudioInputStream(targetFormat, sourceStream);
		    
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("getAudioInputStream use codec " + codec);
		    }
		    
		    break;
		}
		else
		{
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("getAudioInputStream could not use codec " + codec + " cause conversion is not supported");
		    }
		}
	    }
	}
	
	if ( result == null )
	{
	    result = AudioSystem.getAudioInputStream(targetFormat, sourceStream);
	}

	if ( result == null )
	{
	    // we ran out of options...
	    throw new IllegalArgumentException("Unsupported conversion: " + targetFormat + " from " + sourceStream.getFormat());
	}
	
	return result;
    }
    
}
