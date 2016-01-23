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
package org.blackdog.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.log4j.Logger;
import org.blackdog.kernel.MusikKernelResources;
import org.blackdog.player.Player;
import org.blackdog.player.PlayerStatus;
import org.blackdog.player.event.PlayerListener;
import org.blackdog.type.Playable;
import org.blackdog.type.TimeBasedItem;
import org.blackdog.type.base.AudioDuration;
import org.blackdog.type.base.RepeatMode;
import org.siberia.TypeInformationProvider;
import org.siberia.editor.AbstractEditor;
import org.siberia.editor.annotation.Editor;
import org.siberia.properties.PropertiesManager;
import org.siberia.type.SibType;
import org.blackdog.type.session.SinglePlayableSession;
import org.blackdog.type.session.PlayableSession;
import org.blackdog.type.AudioItem;

/**
 *
 * Player for AudioItem
 *
 * @author alexis
 */
@Editor(relatedClasses={AudioItem.class, org.blackdog.type.session.PlayableSession.class},
                  description="Audio player",
                  name="Audio player",
                  launchedInstancesMaximum=1)
public class AudioReader extends AbstractEditor implements PropertyChangeListener
{
    /** log play */
    private static final boolean DEBUG_PLAY = true;
    
    /** logger */
    private static Logger logger = Logger.getLogger(AudioReader.class.getName());
    
    /**
     * audioReaderPanel
     */
    private AudioReaderPanel                audioReaderPanel     = null;
    
    /** synchronisation object when play */
    private Object                          playLock             = new Object();
    
    /** current player */
    private Player                          player               = null;
    
    /** player listener */
    private PlayerListener                  listener             = null;
    
    /** resource bundle related to this class */
    private SoftReference<ResourceBundle>   rb                   = new SoftReference<ResourceBundle>(null);
    
    /** scrollpane including the auduio reader */
    private JScrollPane                     scroll               = null;
    
    /** executor service */
    private ExecutorService                 playerService        = null;
    
    /* current playable future */
    private Future                          playableFuture       = null;
    
    /** integer that is 0 when we are not in a property change of the progression player */
    private int                             progressionInhibiter = 0;
    
    /** default title */
    private String                          defaultTitle         = null;
    
    /** file */
    private File                            file                 = null;
    
    /** Creates a new instance of AudioPlayer */
    public AudioReader()
    {   
	this.setGainFocusWhenLaunched(false);
	this.setGainFocusWhenReEdited(false);
	
	this.defaultTitle = this.getResourceBundle().getString("player.title");
    }

    /**
     * method called when the editor is about to be closed
     */
    @Override
    public void aboutToBeClosed()
    {   
	super.aboutToBeClosed();
        
        /** stop player if it is active */
        this.stop();
    }
    
    /** method called to close the editor */
    @Override
    public void close()
    {   
	super.close();
	
	if ( this.player != null )
	{
	    if ( this.audioReaderPanel != null )
	    {
		/* store volume gain */
		Number volume = audioReaderPanel.getVolumeControlPanel().getVolumeGain() * 1000;
		
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("updating volume in properties to " + volume);
		}
		
		try
		{   
		    boolean volumeSet = PropertiesManager.setGeneralProperty("player.volumeGain", volume.intValue());
		    if ( logger.isDebugEnabled() )
		    {
			logger.debug("volume set to " + volume);
		    }
		}
		catch (Exception ex)
		{
		    logger.error("could not set property '" + "player.volumeGain" + "' to " + volume, ex);
		}
	    }
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("stopping and disposing current player");
	    }
	    this.player.stop();
	    this.player.dispose();
	}
    }
    
    /** return the resourceBundle linked to this class
     *  @return a ResourceBundle
     */
    private synchronized ResourceBundle getResourceBundle()
    {
        ResourceBundle r = this.rb.get();
        
        if ( r == null )
        {
            r = ResourceBundle.getBundle(this.getClass().getName());
            
            this.rb = new SoftReference<ResourceBundle>(r);
        }
        
        return r;
    }
    
    /** called to play the current associated item */
    private void play()
    {   
	if ( this.playerService == null )
	{
	    this.playerService = Executors.newSingleThreadExecutor();
	}
	
	if ( this.playableFuture != null && ! this.playableFuture.isDone() )
	{
	    this.playableFuture.cancel(true);
	}
	
	this.playableFuture = this.playerService.submit(new Runnable()
	{
	    public void run()
	    {
		synchronized(playLock)
		{
		    if ( getInstance() != null )
		    {
			final Playable playable = getInstance().getCurrentPlayable();

			if ( logger.isDebugEnabled() && DEBUG_PLAY )
			{
			    try
			    {
				if ( file == null )
				{
				    file = new File( System.getProperty("user.home") + System.getProperty("file.separator") + "blackdog-read.txt");

				    if ( ! file.exists() )
				    {
					file.createNewFile();
				    }
				}

				FileWriter writer = new FileWriter(file, true);

				writer.write(DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()) + " : playing " + playable + "\n");

				writer.close();
			    }
			    catch(IOException e)
			    {
				e.printStackTrace();
			    }
			}

			logger.debug("play item of kind " + (playable == null ? null : playable.getClass()));

			if ( playable != null )
			{
			    if ( player != null && listener != null )
			    {
				player.removePlayerListener(listener);
				player.removePropertyChangeListener(AudioReader.this);
			    }

			    player = MusikKernelResources.getInstance().getPlayerFor(playable);

			    if ( player != null )
			    {
				if ( listener == null )
				{
				    createPlayerListener();
				}

				player.addPropertyChangeListener(AudioReader.this);

				player.addPlayerListener(listener);
			    }

			    logger.debug("player is " + player);

			    if ( player == null )
			    {   
				ResourceBundle rb = getResourceBundle();

				if ( rb != null )
				{   JOptionPane.showMessageDialog(audioReaderPanel, rb.getString("noPlayerFoundErrorMessage"),
								  rb.getString("noPlayerFoundErrorTitle"), JOptionPane.ERROR_MESSAGE);

				    Runnable runnable = new Runnable()
				    {
					public void run()
					{
					    audioReaderPanel.getPlayButton().setSelected(false);
					}
				    };

				    if ( SwingUtilities.isEventDispatchThread() )
				    {
					runnable.run();
				    }
				    else
				    {
					SwingUtilities.invokeLater(runnable);
				    }
				}
			    }
			    else
			    {   
				player.setItem(playable);
				player.play();

				if ( logger.isDebugEnabled() )
				{
				    logger.debug("setting volume gain : " + audioReaderPanel.getVolumeControlPanel().getVolumeGain());
				}
				player.setVolumeGain(audioReaderPanel.getVolumeControlPanel().getVolumeGain());

				if ( logger.isDebugEnabled() )
				{
				    logger.debug("volume gain set to : " + player.getVolumeGain());
				}
				
				Runnable runnable = new Runnable()
				{
				    public void run()
				    {
					audioReaderPanel.getPlayButton().setSelected(true);
					audioReaderPanel.getPauseButton().setSelected(false);
				    }
				};

				if ( SwingUtilities.isEventDispatchThread() )
				{
				    runnable.run();
				}
				else
				{
				    SwingUtilities.invokeLater(runnable);
				}
			    }
			}

			MusikKernelResources.getInstance().setCurrentItemPlayed(playable);
		    }
		}
	    }
	});
    }
    
    /** mark the given playable as played
     *	@param playable a Playable
     */
    private void markAsPlayed(final Playable playable)
    {
	if ( playable != null )
	{
	    Runnable runnable = new Runnable()
	    {
		public void run()
		{
		    try
		    {
			playable.setCountPlayed(playable.getCountPlayed() + 1);
			playable.setLastTimePlayed(Calendar.getInstance().getTime());
		    }
		    catch(PropertyVetoException e)
		    {
			e.printStackTrace();
		    }
		}
	    };

	    new Thread(runnable).run();
	}
    }
    
    /** method that create the player listener */
    private void createPlayerListener()
    {
        this.listener = new PlayerListener()
        {
            public void errorReceived(Player player, Exception exception)
            {
		logger.error("error received cause=" + (exception == null ? null : exception.getCause()), exception);
		
                if ( exception instanceof LineUnavailableException ||
		     (exception != null && exception.getCause() instanceof LineUnavailableException) )
                {   
		    Runnable runnable = new Runnable()
		    {
			public void run()
			{
			    ResourceBundle rb = getResourceBundle();

			    JOptionPane.showMessageDialog(getComponent(), rb.getString("lineUnavailableErrorMessage"),
							  rb.getString("lineUnavailableErrorTitle"), JOptionPane.ERROR_MESSAGE);
			    
			    audioReaderPanel.getPauseButton().setSelected(false);
			    audioReaderPanel.getPlayButton().setSelected(false);
			}
		    };

		    if ( SwingUtilities.isEventDispatchThread() )
		    {
			runnable.run();
		    }
		    else
		    {
			SwingUtilities.invokeLater(runnable);
		    }
                }
            }
            
	    /** called when the current SongItem has been fully read
	     *  @param player the player that played the SongItem
	     *  @param playable the Playable objct that has been fully read
	     *	@param milliSecondsEstimation the estimation of the stream in milli-seconds
	     */
	    public void itemFullyRead(Player player, final Playable playable, final long milliSecondsEstimation)
            {
		markAsPlayed(playable);
		
		if ( playable instanceof org.blackdog.type.TimeBasedItem )
		{
		    Runnable runnable = new Runnable()
		    {
			public void run()
			{
			    org.blackdog.type.TimeBasedItem timeBasedItem = (org.blackdog.type.TimeBasedItem)playable;

			    if ( milliSecondsEstimation > 0 )
			    {
				boolean assignDuration = false;

				if ( ! timeBasedItem.isDurationVerified() )
				{
				    assignDuration = true;
				}
				else if ( timeBasedItem.getDuration() == null )
				{
				    assignDuration = true;
				}
				else if ( timeBasedItem.getDuration().getTimeInMilli() <= 0 )
				{
				    assignDuration = true;
				}
				else if ( Math.abs(timeBasedItem.getDuration().getTimeInMilli() - milliSecondsEstimation) > 500 ) // if difference > 500ms
				{
				    assignDuration = true;
				}

				if ( assignDuration )
				{
				    try
				    {
					timeBasedItem.setDuration(new org.blackdog.type.base.AudioDuration(milliSecondsEstimation));
					timeBasedItem.setDurationVerified(true);
				    }
				    catch(PropertyVetoException e)
				    {
					logger.error("unable to set the duration of " + playable.getName(), e);
				    }
				}
			    }
			}
		    };
		    
		    Thread thread = new Thread(runnable);
		    thread.setPriority(Thread.MIN_PRIORITY);
		    
		    thread.start();
		}
		
		Runnable runnable = new Runnable()
		{
		    public void run()
		    {
			audioReaderPanel.getPlayButton().setSelected(false);
			audioReaderPanel.getPauseButton().setSelected(false);
		    }
		};

		if ( SwingUtilities.isEventDispatchThread() )
		{
		    runnable.run();
		}
		else
		{
		    SwingUtilities.invokeLater(runnable);
		}
                
		if( getInstance() != null )
		{
		    if ( RepeatMode.CURRENT.equals(audioReaderPanel.getSelectedRepeatMode()) )
		    {
			getInstance().setCurrentPlayable(null);
			getInstance().setCurrentPlayable(playable);
		    }
		    else
		    {
			getInstance().setCurrentPlayable(null);
			getInstance().goToNextPlayable(playable, audioReaderPanel.getShuffleCheckBox().isSelected(),
								 audioReaderPanel.getSelectedRepeatMode());
		    }
		}
            }
        };
    }
    
    /** update displayed elements of the player according to the current playable */
    private void updateDisplayedItems()
    {
	final Playable playable = (this.getInstance() == null ? null : this.getInstance().getCurrentPlayable());
	
	Runnable runnable = null;
	
	if ( playable == null )
	{
	    runnable = new Runnable()
	    {
		public void run()
		{
		    setIcon(null);
		    setTitle(defaultTitle);

		    if( audioReaderPanel != null )
		    {
			if ( audioReaderPanel.getSongLabel() != null )
			{
			    audioReaderPanel.getSongLabel().setText("");
			}
			if ( audioReaderPanel.getTimeLabel() != null )
			{
			    audioReaderPanel.getTimeLabel().setText("");
			}
		    }
		}
	    };
	}
	else
	{
	    runnable = new Runnable()
	    {
		public void run()
		{
		    setIcon( org.siberia.ui.IconCache.getInstance().get(playable) );
		    setTitle(playable.getName());

		    if( audioReaderPanel != null )
		    {
			if ( audioReaderPanel.getSongLabel() != null )
			{
			    audioReaderPanel.getSongLabel().setText(playable.getName());
			}

			if ( audioReaderPanel.getTimeLabel() != null )
			{
			    String timeLabel = null;

			    if ( playable instanceof TimeBasedItem )
			    {
				AudioDuration duration = ((TimeBasedItem)playable).getDuration();

				if ( duration != null )
				{
				    timeLabel = duration.getStringRepresentation();
				}
			    }

	//		    if ( timeLabel == null )
	//		    {
	//			timeLabel = "";
	//		    }

			    audioReaderPanel.getTimeLabel().setText(timeLabel);
			}
		    }
		}
	    };
	}
	
	if ( runnable != null )
	{
	    if ( SwingUtilities.isEventDispatchThread() )
	    {
		runnable.run();
	    }
	    else
	    {
		SwingUtilities.invokeLater(runnable);
	    }
	}
	
    }
    
    /** called to stop the current player */
    private void stop()
    {   Player currentPlayer = player;
        if ( currentPlayer != null )
        {   currentPlayer.stop(); }
    }
    
    /** called to pause the current player */
    private void pause()
    {   Player currentPlayer = player;
        if ( currentPlayer != null )
        {   currentPlayer.pause(); }
    }
    
    /** called to resume the current player */
    private void resume()
    {   Player currentPlayer = player;
        if ( currentPlayer != null )
        {   currentPlayer.resume(); }
    }
    
    @Override
    public PlayableSession getInstance()
    {
	return (PlayableSession)super.getInstance();
    }
    
    /** set the SibType instance associated with the editor
     *  @param instance instance of SibType
     */
    public synchronized void setInstance(AudioItem instance)
    {   
	PlayableSession session = null;
	
	if ( instance != null )
	{
	    session = new SinglePlayableSession(instance);
	}
	
	this.setInstance( session );
    }
    
    /** set the SibType instance associated with the editor
     *  @param instance instance of SibType
     **/
    @Override
    public synchronized void setInstance(SibType instance)
    {   
	SibType _instance = instance;
	
	if ( _instance instanceof Playable )
	{
	    _instance = new SinglePlayableSession( (Playable)_instance );
	}
	
	PlayableSession oldSession = this.getInstance();
	if ( oldSession != null )
	{
	    oldSession.removePropertyChangeListener(PlayableSession.PROPERTY_CURRENT_PLAYABLE, this);
	}
	
	super.setInstance(_instance);
	
	this.updateDisplayedItems();
	
	PlayableSession session = this.getInstance();
	if ( session != null )
	{
	    session.addPropertyChangeListener(PlayableSession.PROPERTY_CURRENT_PLAYABLE, this);
	}
	
	/* if it is the same session, mark the current item as played */
	if ( session == oldSession && session != null )
	{
	    markAsPlayed(session.getCurrentPlayable());
	}
	
	if ( player != null )
	{   player.stop(); }

	this.play();
    }
    
    /** update the volume on the control panel according to the value registered on the properties */
    private void updateVolumeGainFromProperties()
    {
	this.updatePlayerVolumeGain(true);
    }
    
    /** update the volume on the control panel according to the value registered on the properties
     *	@param getValueFromProperties true to get the value from the properties
     */
    private void updatePlayerVolumeGain(boolean getValueFromProperties)
    {
	double gain = -1;
	
	if ( getValueFromProperties )
	{
	    Object o = PropertiesManager.getGeneralProperty("player.volumeGain");

	    int volumeGainFor1000 = -1;

	    if ( o instanceof Number )
	    {
		volumeGainFor1000 = ((Number)o).intValue();
	    }

	    if ( volumeGainFor1000 < 0 || volumeGainFor1000 > 1000 )
	    {
		volumeGainFor1000 = 500;
	    }

	    this.audioReaderPanel.getVolumeControlPanel().setVolumeLevel(volumeGainFor1000);
	    
	    gain = ((double)volumeGainFor1000) / 1000;
	}
	else
	{
	    gain = this.audioReaderPanel.getVolumeControlPanel().getVolumeGain();
	}

	if ( this.player != null )
	{
	    this.player.setVolumeGain(gain);
	}
    }
    
    /**
     * return the component that render the editor
     * 
     * @return a Component
     */
    public Component getComponent()
    {   
	if ( this.scroll == null )
	{
	    this.audioReaderPanel   = new AudioReaderPanel();
	    
	    this.updateDisplayedItems();
	    
	    this.audioReaderPanel.getVolumeControlPanel().addPropertyChangeListener(VolumeControlPanel.PROPERTY_VOLUME_LEVEL, this);
	    
	    this.updateVolumeGainFromProperties();

	    ActionListener listener = new ActionListener()
	    {
		public void actionPerformed(ActionEvent e)
		{
		    if ( e.getSource() instanceof JToggleButton )
		    {
			boolean selected = ((JToggleButton)e.getSource()).isSelected();

			if ( selected )
			{   if ( e.getSource() == audioReaderPanel.getPlayButton() )
			    {   
				PlayerStatus status = null;

				if ( player != null )
				{
				    status = player.getPlayerStatus();
				}

				if ( status != null && PlayerStatus.PAUSED.equals(status) )
				{
				    resume();
				}
				else
				{
				    play();
				}
			    }
			    else if ( e.getSource() == audioReaderPanel.getPauseButton() )
			    {   
				pause();
			    }
			}
			else
			{
			    if ( e.getSource() == audioReaderPanel.getPlayButton() )
			    {
				stop();

				Runnable runnable = new Runnable()
				{
				    public void run()
				    {
					try
					{
					    progressionInhibiter++;
					    audioReaderPanel.getSongSlider().setValue(audioReaderPanel.getSongSlider().getMinimum());
					}
					finally
					{
					    progressionInhibiter --;
					}
				    }
				};

				if ( SwingUtilities.isEventDispatchThread() )
				{
				    runnable.run();
				}
				else
				{
				    SwingUtilities.invokeLater(runnable);
				}
			    }
			    else if ( e.getSource() == audioReaderPanel.getPauseButton() )
			    {
				PlayerStatus status = player == null ? null : player.getPlayerStatus();
				
				boolean wasPaused = true;
				
				if ( status != null )
				{
				    if ( PlayerStatus.STOPPED.equals(status) )
				    {
					wasPaused = false;
				    }
				}
				
				/* resume */
				if ( wasPaused )
				{
				    resume();
				}
				else
				{
				    play();
				}
			    }
			}
		    }
		    else if ( e.getSource() == audioReaderPanel.getStopButton() )
		    {   stop();

			Runnable runnable = new Runnable()
			{
			    public void run()
			    {
				try
				{
				    progressionInhibiter++;
				    audioReaderPanel.getSongSlider().setValue(audioReaderPanel.getSongSlider().getMinimum());
				}
				finally
				{
				    progressionInhibiter --;
				}
			    }
			};

			if ( SwingUtilities.isEventDispatchThread() )
			{
			    runnable.run();
			}
			else
			{
			    SwingUtilities.invokeLater(runnable);
			}
		    }
		    else if ( e.getSource() == audioReaderPanel.getPreviousButton() )
		    {
			if ( getInstance() != null )
			{
			    Playable playable = getInstance().getCurrentPlayable();
			    getInstance().setCurrentPlayable(null);
			    getInstance().goToPreviousPlayable(playable, audioReaderPanel.getShuffleCheckBox().isSelected(),
												   audioReaderPanel.getSelectedRepeatMode());
			}
		    }
		    else if ( e.getSource() == audioReaderPanel.getNextButton() )
		    {
			if ( getInstance() != null )
			{
			    Playable playable = getInstance().getCurrentPlayable();
			    getInstance().setCurrentPlayable(null);
			    getInstance().goToNextPlayable(playable, audioReaderPanel.getShuffleCheckBox().isSelected(),
											       audioReaderPanel.getSelectedRepeatMode());
			}
		    }
		}
	    };
	    this.audioReaderPanel.getPlayButton().addActionListener(listener);
	    this.audioReaderPanel.getPauseButton().addActionListener(listener);
	    this.audioReaderPanel.getStopButton().addActionListener(listener);
	    this.audioReaderPanel.getPreviousButton().addActionListener(listener);
	    this.audioReaderPanel.getNextButton().addActionListener(listener);

	    this.audioReaderPanel.getSongSlider().addChangeListener(new ChangeListener()
	    {
		public void stateChanged(ChangeEvent e)
		{
		    if ( progressionInhibiter == 0 )
		    {
			if ( ! audioReaderPanel.getSongSlider().getValueIsAdjusting() )
			{
			    int value = audioReaderPanel.getSongSlider().getValue();

			    PlayerStatus status = (player == null ? null : player.getPlayerStatus());

    //			if ( ! PlayerStatus.STOPPED.equals(status) )
			    {
				if ( player != null )
				{
				    if ( PlayerStatus.PAUSED.equals(status) )
				    {
					player.setPlayerStatus(PlayerStatus.PLAYING);
					
					Runnable run = new Runnable()
					{
					    public void run()
					    {
						if ( audioReaderPanel != null )
						{
						    JToggleButton playB = audioReaderPanel.getPlayButton();
						    if ( playB != null )
						    {
							playB.setSelected(true);
						    }
						    JToggleButton pauseB = audioReaderPanel.getPauseButton();
						    if ( pauseB != null )
						    {
							pauseB.setSelected(false);
						    }
						}
					    }
					};
					
					if ( SwingUtilities.isEventDispatchThread() )
					{
					    run.run();
					}
					else
					{
					    SwingUtilities.invokeLater(run);
					}
				    }
				    
				    player.playAt( ((double)value) / audioReaderPanel.getSongSlider().getMaximum() );
				}
			    }
			}
		    }
		}
	    });
	    
	    
	    this.scroll = new JScrollPane(this.audioReaderPanel);
//	    this.scroll.setPreferredSize(this.audioReaderPanel.getPreferredSize());
	}
	
	return this.scroll;
    }
    
    public void propertyChange(PropertyChangeEvent evt)
    {
	if ( evt.getSource() == this.getInstance() )
	{
	    if ( evt.getPropertyName().equals(PlayableSession.PROPERTY_CURRENT_PLAYABLE) )
	    {
		if ( logger.isDebugEnabled() )
		{
		    logger.debug("current playable changed " + (getInstance().getCurrentPlayable() == null ? null : getInstance().getCurrentPlayable().getName()));
		}
		
		if ( this.player != null )
		{
		    if ( ! PlayerStatus.STOPPED.equals(this.player.getPlayerStatus()) )
		    {
			this.stop();
			
			this.audioReaderPanel.getPlayButton().setSelected(false);
		    }
		}
		
		/* launch play on the current playable */
		this.play();
		
		this.updateDisplayedItems();
	    }
	}
	else if ( this.player == evt.getSource() )
	{
	    if ( Player.PROPERTY_PERCENTAGE_PLAYED.equals(evt.getPropertyName()) )
	    {
		PlayerStatus status = (player == null ? null : player.getPlayerStatus());
		
		if ( status != null && ! PlayerStatus.STOPPED.equals(status) && ! PlayerStatus.PAUSED.equals(status) )
		{
		    if ( evt.getNewValue() instanceof Number )
		    {
			if ( ! audioReaderPanel.getSongSlider().getValueIsAdjusting() )
			{
			    final int value = ((Number)evt.getNewValue()).intValue();

			    Runnable runnable = new Runnable()
			    {
				public void run()
				{
				    try
				    {
					progressionInhibiter ++;
					
					int finalValue = value;
					if ( player != null && PlayerStatus.STOPPED.equals(player.getPlayerStatus()) )
					{
					    finalValue = 0;
					}
					
					audioReaderPanel.getSongSlider().setValue(finalValue);
				    }
				    finally
				    {
					progressionInhibiter --;
				    }
				}
			    };

			    if ( SwingUtilities.isEventDispatchThread() )
			    {
				runnable.run();
			    }
			    else
			    {
				SwingUtilities.invokeLater(runnable);
			    }
			}
		    }
		}
	    }
	}
	else if ( evt.getSource() == this.audioReaderPanel.getVolumeControlPanel() )
	{
	    if ( evt.getPropertyName().equals(VolumeControlPanel.PROPERTY_VOLUME_LEVEL) )
	    {
		/** apply new volume on the player */
		if ( this.player != null )
		{
		    this.player.setVolumeGain(this.audioReaderPanel.getVolumeControlPanel().getVolumeGain());
		    
		    /** if the player is paused, then resume */
		    PlayerStatus status = this.player.getPlayerStatus();
		    
		    if ( PlayerStatus.PAUSED.equals(status) )
		    {
			resume();

			Runnable run = new Runnable()
			{
			    public void run()
			    {
				if ( audioReaderPanel != null )
				{
				    JToggleButton playB = audioReaderPanel.getPlayButton();
				    if ( playB != null )
				    {
					playB.setSelected(true);
				    }
				    JToggleButton pauseB = audioReaderPanel.getPauseButton();
				    if ( pauseB != null )
				    {
					pauseB.setSelected(false);
				    }
				}
			    }
			};

			if ( SwingUtilities.isEventDispatchThread() )
			{
			    run.run();
			}
			else
			{
			    SwingUtilities.invokeLater(run);
			}
		    }
		}
	    }
	}
    }
}
