/*
 *  Jajuk
 *  Copyright (C) 2003 The Jajuk Team
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  $Revision: 3454 $
 */

package org.jajuk.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.jajuk.services.events.Event;
import org.jajuk.services.events.ObservationManager;
import org.jajuk.services.events.Observer;
import org.jajuk.services.players.FIFO;
import org.jajuk.services.tags.Tag;
import org.jajuk.util.ConfigurationManager;
import org.jajuk.util.EventSubject;
import org.jajuk.util.MD5Processor;
import org.jajuk.util.Messages;
import org.jajuk.util.error.JajukException;
import org.jajuk.util.error.NoneAccessibleFileException;

/**
 * Convenient class to manage Tracks
 * 
 * @TODO Refactor this error detection system (isChangePbm)
 */
public class TrackManager extends ItemManager implements Observer {
  /** Self instance */
  private static TrackManager singleton;

  /** Unmounted tracks flag */
  boolean bChangePbm = false;

  /** Max rate */
  private long lMaxRate = 0l;

  /**
   * No constructor available, only static access
   */
  private TrackManager() {
    super();
    // ---register properties---
    // ID
    registerProperty(new PropertyMetaInformation(XML_ID, false, true, false, false, false,
        String.class, null));
    // Name
    registerProperty(new PropertyMetaInformation(XML_NAME, false, true, true, true, false,
        String.class, null));
    // Album
    registerProperty(new PropertyMetaInformation(XML_ALBUM, false, true, true, true, true,
        String.class, null));
    // Style
    registerProperty(new PropertyMetaInformation(XML_STYLE, false, true, true, true, true,
        String.class, null));
    // Author
    registerProperty(new PropertyMetaInformation(XML_AUTHOR, false, true, true, true, true,
        String.class, null));
    // Length
    registerProperty(new PropertyMetaInformation(XML_TRACK_LENGTH, false, true, true, false, false,
        Long.class, null));
    // Type
    registerProperty(new PropertyMetaInformation(XML_TRACK_TYPE, false, true, true, false, false,
        Long.class, null));
    // Year
    registerProperty(new PropertyMetaInformation(XML_YEAR, false, true, true, true, true,
        Long.class, 0));
    // Rate
    registerProperty(new PropertyMetaInformation(XML_TRACK_RATE, false, false, true, true, true,
        Long.class, 0));
    // Files
    registerProperty(new PropertyMetaInformation(XML_FILES, false, false, true, false, false,
        String.class, null));
    // Hits
    registerProperty(new PropertyMetaInformation(XML_TRACK_HITS, false, false, true, false, false,
        Long.class, 0));
    // Addition date
    registerProperty(new PropertyMetaInformation(XML_TRACK_ADDED, false, false, true, false, false,
        Date.class, null));
    // Comment
    registerProperty(new PropertyMetaInformation(XML_TRACK_COMMENT, false, false, true, true, true,
        String.class, null));
    // Track order
    registerProperty(new PropertyMetaInformation(XML_TRACK_ORDER, false, true, true, true, false,
        Long.class, null));
    // ---subscriptions---
    ObservationManager.register(this);
  }

  public Set<EventSubject> getRegistrationKeys() {
    return Collections.singleton(EventSubject.EVENT_FILE_NAME_CHANGED);
  }

  /**
   * @return singleton
   */
  public static TrackManager getInstance() {
    if (singleton == null) {
      singleton = new TrackManager();
    }
    return singleton;
  }

  /**
   * Register an Track
   */
  public synchronized Track registerTrack(String sName, Album album, Style style, Author author,
      long length, Year year, long lOrder, Type type) {
    String sId = createID(sName, album, style, author, length, year, lOrder, type);
    return registerTrack(sId, sName, album, style, author, length, year, lOrder, type);
  }

  /**
   * Return hashcode for a track
   * 
   * @param track
   * @return
   */
  protected static String createID(String sName, Album album, Style style, Author author,
      long length, Year year, long lOrder, Type type) {
    StringBuilder sb = new StringBuilder(100);
    sb.append(style.getID()).append(author.getID()).append(album.getID()).append(sName).append(
        year.getValue()).append(length).append(lOrder).append(type.getID());
    // distinguish tracks by type because we can't find best file
    // on different quality levels by format
    return MD5Processor.hash(sb.toString());
  }

  /**
   * Register an Track with a known id
   * 
   * @param sName
   */
  public Track registerTrack(String sId, String sName, Album album, Style style, Author author,
      long length, Year year, long lOrder, Type type) {
    synchronized (TrackManager.getInstance().getLock()) {
      Track track = (Track) hmItems.get(sId);
      if (track != null) {
        return track;
      }
      track = new Track(sId, sName, album, style, author, length, year, lOrder, type);
      hmItems.put(sId, track);
      // For performances, add the track to the album cache
      album.tracks.add(track);
      return track;
    }
  }

  /**
   * Change a track album
   * 
   * @param old
   *          track
   * @param new
   *          album name
   * @param filter
   *          files we want to deal with
   * @return new track
   */
  public Track changeTrackAlbum(Track track, String sNewAlbum, HashSet<File> filter)
      throws JajukException {
    synchronized (TrackManager.getInstance().getLock()) {
      // check there is actually a change
      if (track.getAlbum().getName2().equals(sNewAlbum)) {
        return track;
      }
      ArrayList<File> alReady = null;
      // check if files are accessible
      alReady = track.getReadyFiles(filter);
      if (alReady.size() == 0) {
        throw new NoneAccessibleFileException(10);
      }
      // change tag in files
      for (File file : alReady) {
        Tag tag = new Tag(file.getIO());
        tag.setAlbumName(sNewAlbum);
        tag.commit();
      }
      // Remove the track from the old album
      track.getAlbum().tracks.remove(track);
      // if current track album name is changed, notify it
      if (FIFO.getInstance().getCurrentFile() != null
          && FIFO.getInstance().getCurrentFile().getTrack().getAlbum().equals(track.getAlbum())) {
        ObservationManager.notify(new Event(EventSubject.EVENT_ALBUM_CHANGED));
      }
      // register the new album
      Album newAlbum = AlbumManager.getInstance().registerAlbum(sNewAlbum);
      Track newTrack = registerTrack(track.getName(), newAlbum, track.getStyle(),
          track.getAuthor(), track.getDuration(), track.getYear(), track.getOrder(), track
              .getType());
      postChange(track, newTrack, filter);
      // remove this album if no more references
      AlbumManager.getInstance().cleanup(track.getAlbum());
      return newTrack;
    }
  }

  /**
   * Change a track author
   * 
   * @param old
   *          track
   * @param new
   *          author name
   * @param filter
   *          files we want to deal with
   * @return new track
   */
  public Track changeTrackAuthor(Track track, String sNewAuthor, HashSet<File> filter)
      throws JajukException {
    synchronized (TrackManager.getInstance().getLock()) {
      // check there is actually a change
      if (track.getAuthor().getName2().equals(sNewAuthor)) {
        return track;
      }
      ArrayList<File> alReady = null;
      // check if files are accessible
      alReady = track.getReadyFiles(filter);
      if (alReady.size() == 0) {
        throw new NoneAccessibleFileException(10);
      }
      // change tag in files
      for (final File file : alReady) {
        final Tag tag = new Tag(file.getIO());

        tag.setAuthorName(sNewAuthor);
        tag.commit();
      }
      // if current track author name is changed, notify it
      if (FIFO.getInstance().getCurrentFile() != null
          && FIFO.getInstance().getCurrentFile().getTrack().getAuthor().equals(track.getAuthor())) {
        ObservationManager.notify(new Event(EventSubject.EVENT_AUTHOR_CHANGED));
      }
      // register the new item
      Author newAuthor = AuthorManager.getInstance().registerAuthor(sNewAuthor);
      Track newTrack = registerTrack(track.getName(), track.getAlbum(), track.getStyle(),
          newAuthor, track.getDuration(), track.getYear(), track.getOrder(), track.getType());
      postChange(track, newTrack, filter);
      // remove this item if no more references
      AuthorManager.getInstance().cleanup(track.getAuthor());
      return newTrack;
    }
  }

  /**
   * Change a track style
   * 
   * @param old
   *          item
   * @param new
   *          item name
   * @param filter
   *          files we want to deal with
   * @return new track
   */
  public Track changeTrackStyle(Track track, String sNewStyle, HashSet<File> filter)
      throws JajukException {
    synchronized (TrackManager.getInstance().getLock()) {
      // check there is actually a change

      if (track.getStyle().getName2().equals(sNewStyle)) {
        return track;
      }
      ArrayList<File> alReady = null;
      // check if files are accessible
      alReady = track.getReadyFiles(filter);
      if (alReady.size() == 0) {
        throw new NoneAccessibleFileException(10);
      }
      // change tag in files
      for (final File file : alReady) {
        final Tag tag = new Tag(file.getIO());

        tag.setStyleName(sNewStyle);
        tag.commit();
      }
      // register the new item
      Style newStyle = StyleManager.getInstance().registerStyle(sNewStyle);
      Track newTrack = registerTrack(track.getName(), track.getAlbum(), newStyle,
          track.getAuthor(), track.getDuration(), track.getYear(), track.getOrder(), track
              .getType());
      postChange(track, newTrack, filter);
      // remove this item if no more references
      StyleManager.getInstance().cleanup(track.getStyle());
      return newTrack;
    }
  }

  /**
   * Change a track year
   * 
   * @param old
   *          item
   * @param new
   *          item name
   * @param filter
   *          files we want to deal with
   * @return new track or null if wrong format
   */
  public Track changeTrackYear(Track track, String newItem, HashSet<File> filter)
      throws JajukException {
    synchronized (TrackManager.getInstance().getLock()) {
      // check there is actually a change
      if (track.getYear().getName().equals(newItem)) {
        return track;
      }
      long lNewItem = Long.parseLong(newItem);
      if (lNewItem < 0 || lNewItem > 10000) {
        Messages.showErrorMessage(137);
        throw new JajukException(137);
      }
      ArrayList<File> alReady = null;
      // check if files are accessible
      alReady = track.getReadyFiles(filter);
      if (alReady.size() == 0) {
        throw new NoneAccessibleFileException(10);
      }
      // change tag in files
      for (final File file : alReady) {
        final Tag tag = new Tag(file.getIO());

        tag.setYear(newItem);
        tag.commit();
      }
      // Register new item
      Year newYear = YearManager.getInstance().registerYear(newItem);
      Track newTrack = registerTrack(track.getName(), track.getAlbum(), track.getStyle(), track
          .getAuthor(), track.getDuration(), newYear, track.getOrder(), track.getType());
      postChange(track, newTrack, filter);
      return newTrack;
    }
  }

  /**
   * Change a track comment
   * 
   * @param old
   *          item
   * @param new
   *          item name
   * @param filter
   *          files we want to deal with
   * @return new track or null if wronf format
   */
  public Track changeTrackComment(Track track, String sNewItem, HashSet<File> filter)
      throws JajukException {
    synchronized (TrackManager.getInstance().getLock()) {
      // check there is actually a change
      if (track.getComment().equals(sNewItem)) {
        return track;
      }
      ArrayList<File> alReady = null;
      // check if files are accessible
      alReady = track.getReadyFiles(filter);
      if (alReady.size() == 0) {
        throw new NoneAccessibleFileException(10);
      }
      // change tag in files
      for (File file : alReady) {
        Tag tag = new Tag(file.getIO());
        tag.setComment(sNewItem);
        tag.commit();
      }
      track.setComment(sNewItem);
      return track;
    }
  }

  /**
   * Change a track rate
   * 
   * @param old
   *          item
   * @param new
   *          item name
   * @return new track or null if wrong format
   */
  public Track changeTrackRate(Track track, long lNew) throws JajukException {
    synchronized (TrackManager.getInstance().getLock()) {
      // check there is actually a change
      if (track.getRate() == lNew) {
        return track;
      }
      // check format
      if (lNew < 0) {
        Messages.showErrorMessage(137);
        throw new JajukException(137);
      }
      track.setRate(lNew);
      return track;
    }
  }

  /**
   * Change a track order
   * 
   * @param old
   *          item
   * @param new
   *          item order
   * @param filter
   *          files we want to deal with
   * @return new track or null if wronf format
   */
  public Track changeTrackOrder(Track track, long lNewOrder, HashSet<File> filter)
      throws JajukException {
    synchronized (TrackManager.getInstance().getLock()) {
      // check there is actually a change
      if (track.getOrder() == lNewOrder) {
        return track;
      }
      // check format
      if (lNewOrder < 0) {
        Messages.showErrorMessage(137);
        return null;
      }
      ArrayList<File> alReady = null;
      // check if files are accessible
      alReady = track.getReadyFiles(filter);
      if (alReady.size() == 0) {
        throw new NoneAccessibleFileException(10);
      }
      // change tag in files
      for (File file : alReady) {
        Tag tag = new Tag(file.getIO());
        tag.setOrder(lNewOrder);
        tag.commit();
      }
      Track newTrack = registerTrack(track.getName(), track.getAlbum(), track.getStyle(), track
          .getAuthor(), track.getDuration(), track.getYear(), lNewOrder, track.getType());
      postChange(track, newTrack, filter);
      return newTrack;
    }
  }

  /**
   * Change a track name
   * 
   * @param old
   *          item
   * @param new
   *          item name
   * @param filter
   *          files we want to deal with
   * @return new track
   */
  public Track changeTrackName(Track track, String sNewItem, HashSet<File> filter)
      throws JajukException {
    synchronized (TrackManager.getInstance().getLock()) {
      // check there is actually a change
      if (track.getName().equals(sNewItem)) {
        return track;
      }
      ArrayList<File> alReady = null;
      // check if files are accessible
      alReady = track.getReadyFiles(filter);
      if (alReady.size() == 0) {
        throw new NoneAccessibleFileException(10);
      }
      // change tag in files
      for (File file : alReady) {
        Tag tag = new Tag(file.getIO());
        tag.setTrackName(sNewItem);
        tag.commit();
      }
      Track newTrack = registerTrack(sNewItem, track.getAlbum(), track.getStyle(), track
          .getAuthor(), track.getDuration(), track.getYear(), track.getOrder(), track.getType());
      postChange(track, newTrack, filter);
      // if current track name is changed, notify it
      if (FIFO.getInstance().getCurrentFile() != null
          && FIFO.getInstance().getCurrentFile().getTrack().equals(track)) {
        ObservationManager.notify(new Event(EventSubject.EVENT_TRACK_CHANGED));
      }
      return newTrack;
    }
  }

  private void updateFilesReferences(Track oldTrack, Track newTrack, HashSet<File> filter) {
    synchronized (TrackManager.getInstance().getLock()) {
      // Reset files property before adding new files
      for (File file : oldTrack.getReadyFiles(filter)) {
        file.setTrack(newTrack);// set new track for the changed file
        newTrack.addFile(file); // add changed file
        oldTrack.removeFile(file); // remove file from old track
      }
    }
  }

  private void postChange(Track track, Track newTrack, HashSet<File> filter) {
    synchronized (TrackManager.getInstance().getLock()) {
      // re apply old properties from old item
      newTrack.cloneProperties(track);
      // update files references
      updateFilesReferences(track, newTrack, filter);
      if (track.getFiles().size() == 0) { // normal case: old track has no
        // more associated
        // tracks, remove it
        removeItem(track.getID());// remove old track
        bChangePbm = false;
      } else { // some files have not been changed because located on
        // unmounted devices
        bChangePbm = true;
      }
    }
  }

  /**
   * Perform a track cleanup : delete useless items
   */
  @SuppressWarnings("unchecked")
  public void cleanup() {
    synchronized (TrackManager.getInstance().getLock()) {
      Iterator<Track> itTracks = hmItems.values().iterator();
      while (itTracks.hasNext()) {
        Track track = itTracks.next();
        if (track.getFiles().size() == 0) { // no associated file
          itTracks.remove();
          continue;
        }
        Iterator itFiles = track.getFiles().iterator();
        while (itFiles.hasNext()) {
          org.jajuk.base.File file = (org.jajuk.base.File) itFiles.next();
          if (FileManager.getInstance().getFileByID(file.getID()) == null) {
            itFiles.remove();// no? remove it from the track
          }
        }
        if (track.getFiles().size() == 0) { // the track don't map
          // anymore to any
          // physical
          // item, just remove it
          itTracks.remove();
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.base.ItemManager#getIdentifier()
   */
  public String getLabel() {
    return XML_TRACKS;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.base.Observer#update(org.jajuk.base.Event)
   */
  public void update(Event event) {
    EventSubject subject = event.getSubject();
    if (EventSubject.EVENT_FILE_NAME_CHANGED.equals(subject)) {
      Properties properties = event.getDetails();
      File fNew = (File) properties.get(DETAIL_NEW);
      File fileOld = (File) properties.get(DETAIL_OLD);
      Track track = fileOld.getTrack();
      track.removeFile(fileOld);
      track.addFile(fNew);
    }
  }

  /**
   * Get ordered tracks associated with this item
   * 
   * @param item
   * @return
   */
  public Set<Track> getAssociatedTracks(Item item) {
    synchronized (TrackManager.getInstance().getLock()) {
      if (item instanceof Album) {
        // check the album cache
        Set<Track> tracks = ((Album) item).tracks;
        if (tracks.size() > 0) {
          return tracks;
        }
      }
      Set<Track> out = new TreeSet<Track>(new TrackComparator(TrackComparator.ALBUM));
      // If the item is itself a track, simply return it
      if (item instanceof Track) {
        out.add((Track) item);
        return out;
      } else if (item instanceof File) {
        out.add(((File) item).getTrack());
        return out;
      } else if (item instanceof Directory) {
        Directory dir = (Directory) item;
        for (File file : dir.getFiles()) {
          out.add(file.getTrack());
        }
      }
      for (Object item2 : hmItems.values()) {
        Track track = (Track) item2;
        if ((item instanceof Album && track.getAlbum().equals(item))
            || (item instanceof Author && track.getAuthor().equals(item))
            || (item instanceof Year && track.getYear().equals(item))
            || (item instanceof Style && track.getStyle().equals(item))) {
          out.add(track);
        }
      }
      return out;
    }
  }

  public boolean isChangePbm() {
    synchronized (TrackManager.getInstance().getLock()) {
      return bChangePbm;
    }
  }

  public TrackComparator getComparator() {
    return new TrackComparator(ConfigurationManager.getInt(CONF_LOGICAL_TREE_SORT_ORDER));
  }

  /**
   * @return maximum rating between all tracks
   */
  public long getMaxRate() {
    return lMaxRate;
  }

  /**
   * Set max rate
   */
  public void setMaxRate(long lRate) {
    this.lMaxRate = lRate;
  }

  /**
   * @param sID
   *          Item ID
   * @return item
   */
  public Track getTrackByID(String sID) {
    synchronized (getLock()) {
      return (Track) hmItems.get(sID);
    }
  }

  /**
   * 
   * @return ordered tracks list
   */
  public Set<Track> getTracks() {
    Set<Track> tracks = new LinkedHashSet<Track>();
    synchronized (getLock()) {
      for (Item item : getItems()) {
        tracks.add((Track) item);
      }
    }
    return tracks;
  }

  /**
   * 
   * @return unsorted tracks list
   */
  public ArrayList<Track> getTracksAsList() {
    ArrayList<Track> tracks = new ArrayList<Track>(getItems().size());
    synchronized (getLock()) {
      for (Item item : getItems()) {
        tracks.add((Track) item);
      }
    }
    return tracks;
  }

  /**
   * Perform a search in all files names with given criteria
   * 
   * @param sCriteria
   * @return a tree set of available files
   */
  public TreeSet<SearchResult> search(String criteria) {
    TreeSet<SearchResult> tsResu = new TreeSet<SearchResult>();
    for (Object item : hmItems.values()) {
      Track track = (Track) item;
      File playable = track.getPlayeableFile(ConfigurationManager
          .getBoolean(CONF_OPTIONS_HIDE_UNMOUNTED));
      if (playable != null) {
        String sResu = track.getAny();
        if (sResu.toLowerCase().indexOf(criteria.toLowerCase()) != -1) {
          tsResu.add(new SearchResult(playable, playable.toStringSearch()));
        }
      }
    }
    return tsResu;
  }
}
