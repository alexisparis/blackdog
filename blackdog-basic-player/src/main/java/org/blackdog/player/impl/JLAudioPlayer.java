//package org.blackdog.player.impl;
//
//import java.io.IOException;
//import javazoom.jl.decoder.Bitstream;
//import javazoom.jl.decoder.BitstreamException;
//import javazoom.jl.decoder.Decoder;
//import javazoom.jl.decoder.Header;
//import javazoom.jl.decoder.JavaLayerException;
//import javazoom.jl.decoder.SampleBuffer;
//import javazoom.jl.player.AudioDevice;
//import javazoom.jl.player.FactoryRegistry;
//import org.blackdog.player.AbstractAudioPlayer;
//import org.blackdog.player.PlayerStatus;
//import org.blackdog.player.annotation.PlayerCaracteristics;
//import org.blackdog.type.AudioItem;
//
///**
// *
// * Audio player based on javazoom mp3 library
// *
// * @author alexis
// */
//
//@PlayerCaracteristics(extensions={"mp3"})
//
//public class JLAudioPlayer extends AbstractAudioPlayer
//{
//    
//    /**
//     * The current frame number.
//     */
//    private int frame = 0;
//    
//    /**
//     * The MPEG audio bitstream.
//     */
//    // javac blank final bug.
//    /*final*/
//    private Bitstream		bitstream;
//    
//    /**
//     * The MPEG audio decoder.
//     */
//    /*final*/
//    private Decoder		decoder;
//    
//    /**
//     * The AudioDevice the audio samples are written to.
//     */
//    private AudioDevice	audio;
//    
//    /**
//     * Has the player been closed?
//     */
//    private boolean		closed = false;
//    
//    /**
//     * Has the player played back all frames from the stream?
//     */
//    private boolean		complete = false;
//    
//    private int			lastPosition = 0;
//    
//    /** Creates a new instance of JLAudioPlayer */
//    public JLAudioPlayer()
//    {   }
//
//    public void setItem(AudioItem item)
//    {
//        this.frame = 0;
//        
//        super.setItem(item);
//        
//        if ( item != null )
//        {   
//            try
//            {   this.bitstream = new Bitstream(item.createInputStream()); }
//            catch (IOException ex)
//            {   ex.printStackTrace(); }
//            
//            this.decoder = new Decoder();
//            FactoryRegistry r;
//            
//            try
//            {   r = FactoryRegistry.systemRegistry();
//                this.audio = r.createAudioDevice();
//                this.audio.open(this.decoder);
//            }
//            catch(Exception e)
//            {   e.printStackTrace(); }
//        }
//    }
//
//    /**
//     * stop this player. Any audio currently playing is stopped
//     * immediately.
//     * Do not care about the status of the player
//     * 
//     * Another call to play will restart the same player
//     */
//    public void stopImpl()
//    {
//        AudioDevice out = audio;
//        if ( out != null )
//        {   this.audio = null;
//            
//            // this may fail, so ensure object state is set up before
//            // calling this method.
//            out.close();
//            lastPosition = out.getPosition();
//            try
//            {   bitstream.close(); }
//            catch (BitstreamException ex)
//            {   ex.printStackTrace(); }
//        }
//    }
//
//    /**
//     * Plays an audio sample from the beginning. do not care about the status of the player
//     */
//    public void playImpl()
//    {   playAt(0d); }
//
//    /**
//     * Stop temporarly or start at the current position the current audio sample. do not care about the status of the player
//     */
//    public void pauseImpl()
//    {
//    }
//
//    /**
//     * Plays an audio sample at a given percentage position. do not care about the status of the player
//     * 
//     * @param position a position in percentage of the current audio sample length.<br/>
//     *      if position is 0, then the player start the audio sample at the beginning. if position is 98, then the player<br/>
//     *      will only process the end of the audio sample.
//     */
//    public void playAtImpl(double position)
//    {
//        
//        Runnable runnable = new Runnable()
//        {
//            public void run()
//            {
//                int frames = Integer.MAX_VALUE;
//                while ( frames-- > 0 && ! getPlayerStatus().equals(PlayerStatus.STOPPED) )
//                {   if ( getPlayerStatus().equals(PlayerStatus.PLAYING) )
//                    {   
//                        try
//                        {   boolean nomore = decodeFrame();
//                            System.out.println("nomore : " + nomore);
//                            if ( nomore )
//                            {   break; }
//                        }
//                        catch (JavaLayerException ex)
//                        {   ex.printStackTrace(); }
//                    }
//                }
//
//                // last frame, ensure all data flushed to the audio device.
//                AudioDevice out = audio;
//                if ( out != null )
//                {
//                    out.flush();
//                    synchronized (this)
//                    {   stop(); }
//                }
//            }
//        };
//        
//        new Thread(runnable).start();
//    }
//	
//    /**
//     * Decodes a single frame.
//     *
//     * @return true if there are no more frames to decode, false otherwise.
//     */
//    protected boolean decodeFrame() throws JavaLayerException
//    {
//        try
//        {   AudioDevice out = audio;
//            if ( out == null )
//            {   return false; }
//            
//            Header h = bitstream.readFrame();
//            
//            if ( h == null )
//            {   return false; }
//            
//            // sample buffer set when decoder constructed
//            SampleBuffer output = (SampleBuffer)decoder.decodeFrame(h, bitstream);
//            
//            synchronized (this)
//            {
//                out = audio;
//                if ( out != null )
//                {   out.write(output.getBuffer(), 0, output.getBufferLength()); }
//            }
//            
//            bitstream.closeFrame();
//        }
//        catch (RuntimeException ex)
//        {
//            throw new JavaLayerException("Exception decoding audio frame", ex);
//        }
//        
//        return true;
//    }
//				
//    /**
//     * Retrieves the position in milliseconds of the current audio
//     * sample being played. This method delegates to the <code>
//     * AudioDevice</code> that is used by this player to sound
//     * the decoded audio samples. 
//     */
////    public int getPosition()
////    {   int position = lastPosition;
////
////        AudioDevice out = audio;		
////        if ( out != null )
////        {   position = out.getPosition(); }
////        
////        return position;
////    }	
//    
//}
