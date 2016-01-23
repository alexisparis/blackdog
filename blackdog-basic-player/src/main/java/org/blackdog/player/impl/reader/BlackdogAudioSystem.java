package org.blackdog.player.impl.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import org.blackdog.player.impl.InputStreamProvider;

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
    public static final int NOT_SPECIFIED = AudioSystem.NOT_SPECIFIED;
    
    /** list of AudioFileReader */
    private static List<AudioFileReader> readers = new ArrayList<AudioFileReader>();
    
    /** logger */
    private static Logger                logger  = Logger.getLogger(BlackdogAudioSystem.class);
    
    static
    {
        readers.add(new javazoom.spi.mpeg.sampled.file.MpegAudioFileReader());
        readers.add(new javazoom.spi.vorbis.sampled.file.VorbisAudioFileReader());
        readers.add(new org.kc7bfi.jflac.sound.spi.FlacAudioFileReader());
	
//        readers.add(new org.tritonus.sampled.file.jorbis.JorbisAudioFileReader());
	
//        readers.add(new com.sun.media.sound.WaveFileReader());
//        readers.add(new com.sun.media.sound.AiffFileReader());
//        readers.add(new com.sun.media.sound.AuFileReader());
    }
    
    /** list de FormatConversionProvider */
    private static List<FormatConversionProvider> codecs = new ArrayList<FormatConversionProvider>();
    
    static
    {
        codecs.add(new javazoom.spi.mpeg.sampled.convert.MpegFormatConversionProvider());
        codecs.add(new javazoom.spi.vorbis.sampled.convert.VorbisFormatConversionProvider());
        codecs.add(new org.kc7bfi.jflac.sound.spi.FlacFormatConvertionProvider());
	
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
    
    public static AudioInputStream getAudioInputStream(InputStreamProvider provider) throws UnsupportedAudioFileException, IOException
    {   
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("calling getaudioInputStream with " + provider);
	}
	
	AudioInputStream audioStream = null;

	if ( provider != null )
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
		catch (UnsupportedAudioFileException e)
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
    
    public static AudioFileFormat getAudioFileFormat(InputStreamProvider provider) throws UnsupportedAudioFileException, IOException
    {   
        AudioFileFormat format = null;

	if ( provider != null )
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
		catch (UnsupportedAudioFileException e)
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

	if( format==null )
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
