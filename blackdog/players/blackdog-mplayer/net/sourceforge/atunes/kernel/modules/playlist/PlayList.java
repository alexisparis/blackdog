/*
 * aTunes 1.8.2
 * Copyright (C) 2006-2008 Alex Aranda, Sylvain Gaudard, Thomas Beckers and contributors
 *
 * See http://www.atunes.org/?page_id=7 for information about contributors
 *
 * http://www.atunes.org
 * http://sourceforge.net/projects/atunes
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
 */

package net.sourceforge.atunes.kernel.modules.playlist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.sourceforge.atunes.kernel.handlers.PlayerHandler;
import net.sourceforge.atunes.kernel.modules.podcast.PodcastFeedEntry;
import net.sourceforge.atunes.kernel.modules.radio.Radio;
import net.sourceforge.atunes.model.AudioObject;
import net.sourceforge.atunes.utils.StringUtils;

/**
 * This class represents a play list
 * 
 * @author fleax
 * 
 */
public class PlayList extends ArrayList<AudioObject> {

	private static final long serialVersionUID = 1793886828081842328L;

	private String name;

	/**
	 * Random generator for shuffle mode
	 */
	private Random randomGenerator;

	/**
	 * Counter for random mode, counts number of songs played in shuffle mode
	 */
	private transient int randomCounter = 1;

	/**
	 * A pointer to the next object to play
	 */
	private int nextAudioObject;

	/**
	 * A list containing previous played elements
	 */
	private ArrayList<AudioObject> previousPlayedList;

	/**
	 * Index of previous played elements.
	 */
	private int previousPlayedIndex = 0;

	/**
	 * Default Constructor
	 */
	public PlayList() {
		this(new ArrayList<AudioObject>());
	}

	/**
	 * Constructor
	 * 
	 * @param files
	 */
	public PlayList(List<AudioObject> files) {
		super(files);
		// Initialize random generator
		randomGenerator = new Random(System.currentTimeMillis());
		previousPlayedList = new ArrayList<AudioObject>();
	}

	public PlayList(PlayList anotherPlayList) {
		super(anotherPlayList);
		randomGenerator = anotherPlayList.randomGenerator;
		randomCounter = anotherPlayList.randomCounter;
		nextAudioObject = anotherPlayList.nextAudioObject;
		name = anotherPlayList.name;
		previousPlayedIndex = anotherPlayList.previousPlayedIndex;
		previousPlayedList = anotherPlayList.previousPlayedList;
	}

	/**
	 * Clear play list
	 */
	@Override
	public void clear() {
		previousPlayedList.clear();
		previousPlayedIndex = 0;
		super.clear();
	}

	/**
	 * Return current audio object
	 * 
	 * @return
	 */
	public AudioObject getCurrentAudioObject() {
		return size() > 0 && size() > nextAudioObject ? get(nextAudioObject) : null;
	}

	/**
	 * Returns play list length in string format
	 * 
	 * @return
	 */
	public String getLength() {
		long seconds = 0;
		for (AudioObject song : this) {
			seconds += song.getDuration();
		}
		return StringUtils.fromSecondsToHoursAndDays(seconds);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the index of the next file
	 * 
	 * @return
	 */
	public int getNextAudioObject() {
		return nextAudioObject;
	}

	/**
	 * Returns the next file to play
	 * 
	 * @return
	 */
	public AudioObject getNextAudioObjectToPlay() {
		// If previousPlayedIndex == 0 means we have to calculate the next audio object to play
		if (previousPlayedIndex >= -1) {
			previousPlayedIndex = 0;
			if (previousPlayedList == null)
				previousPlayedList = new ArrayList<AudioObject>();
			if (PlayerHandler.getInstance().isShuffle()) {
				if (PlayerHandler.getInstance().isRepeat()) {
					randomCounter = 1;
					int nextInt = randomGenerator.nextInt();
					int next = Math.abs(nextInt != Integer.MIN_VALUE ? nextInt : 1) % size();
					nextAudioObject = next;
					AudioObject nextAO = get(nextAudioObject);
					previousPlayedList.add(nextAO);
					return nextAO;
				} else if (randomCounter < size()) {
					randomCounter++;
					// randomCounter gets reset to 0 upon song deletion, so if a user deletes down to one track, return a valid value.
					int next = 0;
					if (size() <= 1) {
						next = 0;
					} else {
						// If next track is the same than previous played, select previous or next track
						next = randomGenerator.nextInt(size());
						if (next == nextAudioObject) {
							if (next < size() - 1) {
								next++;
							} else {
								next--;
							}
						}
					}
					nextAudioObject = next;
					AudioObject nextAO = get(nextAudioObject);
					previousPlayedList.add(nextAO);
					return nextAO;
				} else {
					return null;
				}
			} else if (nextAudioObject < size() - 1) {
				nextAudioObject++;
				AudioObject nextAO = get(nextAudioObject);
				previousPlayedList.add(nextAO);
				return nextAO;
			} else {
				if (PlayerHandler.getInstance().isRepeat()) {
					nextAudioObject = 0;
					AudioObject nextAO = get(nextAudioObject);
					previousPlayedList.add(nextAO);
					return nextAO;
				}
				return null;
			}
		} else {
			// previousPlayedIndex != 0 (< 0) means user have pressed previous buttons a few times, so next audio object has to be
			// an already audio object
			previousPlayedIndex++;
			nextAudioObject = indexOf(previousPlayedList.get(previousPlayedList.size() + previousPlayedIndex));
			return get(nextAudioObject);
		}

	}

	/**
	 * Returns number of podcast feed entries in play list
	 */
	public int getNumberOfPodcastFeedEntries() {
		int result = 0;
		for (AudioObject file : this) {
			if (file instanceof PodcastFeedEntry)
				result++;
		}
		return result;
	}

	/**
	 * Returns number of radios in play list
	 */
	public int getNumberOfRadios() {
		int result = 0;
		for (AudioObject file : this) {
			if (file instanceof Radio)
				result++;
		}
		return result;
	}

	/**
	 * Return the previous file played
	 * 
	 * @return
	 */
	public AudioObject getPreviousAudioObjectToPlay() {
		// There is no previous played audio object to play again...
		if (previousPlayedList == null || previousPlayedList.isEmpty() || previousPlayedList.size() + previousPlayedIndex <= 0) {
			if (previousPlayedList == null)
				previousPlayedList = new ArrayList<AudioObject>();

			if (PlayerHandler.getInstance().isShuffle()) {
				if (randomCounter < size() || PlayerHandler.getInstance().isRepeat()) {
					randomCounter++;
					if (randomCounter == size())
						randomCounter = 0;
					// NOTE: Random int should not be Integer.MIN_VALUE because Math.abs(Integer.MIN_VALUE) == Integer.MIN_VALUE!
					int r = randomGenerator.nextInt();
					while (r == Integer.MIN_VALUE) {
						r = randomGenerator.nextInt();
					}
					int prevFile = Math.abs(r % size());
					nextAudioObject = prevFile;
					AudioObject prevAO = get(prevFile);
					previousPlayedList.add(0, prevAO);
					previousPlayedIndex--;
					return prevAO;
				}
				return null;
			} else if (nextAudioObject > 0) {
				nextAudioObject--;
				AudioObject prevAO = get(nextAudioObject);
				previousPlayedList.add(0, prevAO);
				previousPlayedIndex--;
				return prevAO;
			} else {
				if (PlayerHandler.getInstance().isRepeat()) {
					nextAudioObject = size() - 1;
					if (nextAudioObject == -1)
						return null;
					else {
						AudioObject prevAO = get(nextAudioObject);
						previousPlayedList.add(0, prevAO);
						previousPlayedIndex--;
						return prevAO;
					}
				}
				return null;
			}
		} else {
			previousPlayedIndex--;
			// when pressing previous when previousPlayedIndex == 0, last audio object in previousPlayedList is current audio object
			// so we must add one more -1
			if (previousPlayedIndex == -1)
				previousPlayedIndex--;
			nextAudioObject = indexOf(previousPlayedList.get(previousPlayedList.size() + previousPlayedIndex));
			return get(nextAudioObject);
		}
	}

	/**
	 * Returns a random position in playlist
	 */
	public int getRandomPosition() {
		return randomGenerator.nextInt(size());
	}

	/**
	 * Removes a song
	 */
	@Override
	public AudioObject remove(int index) {
		randomCounter = 0;
		AudioObject ao = get(index);
		ArrayList<AudioObject> list = new ArrayList<AudioObject>();
		list.add(ao);
		previousPlayedList.removeAll(list);
		if (previousPlayedList.isEmpty())
			previousPlayedIndex = 0;
		return super.remove(index);
	}

	/**
	 * Removes a list of songs
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		randomCounter = 0;
		previousPlayedList.removeAll(c);
		if (previousPlayedList.isEmpty())
			previousPlayedIndex = 0;
		return super.removeAll(c);
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the index of the next file
	 * 
	 * @param nextFile
	 */
	public void setNextAudioObject(int nextFile) {
		this.nextAudioObject = nextFile;
		if (!isEmpty()) {
			AudioObject nextAO = get(nextAudioObject);
			previousPlayedList.add(nextAO);
		}
	}

	/**
	 * Sets a list of songs to this playlist
	 * 
	 * @param songs
	 */
	public void setSongs(List<AudioObject> songs) {
		clear();
		addAll(songs);
	}
}
