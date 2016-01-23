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
 *  $Revision: 3266 $
 **/

package org.jajuk.services.bookmark;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jajuk.base.Collection;
import org.jajuk.base.FileManager;
import org.jajuk.services.events.Event;
import org.jajuk.services.events.ObservationManager;
import org.jajuk.services.events.Observer;
import org.jajuk.util.ConfigurationManager;
import org.jajuk.util.EventSubject;
import org.jajuk.util.ITechnicalStrings;
import org.jajuk.util.Messages;
import org.jajuk.util.Util;
import org.jajuk.util.error.JajukException;
import org.jajuk.util.log.Log;
import org.xml.sax.Attributes;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Stores all files user read
 */
public class History extends DefaultHandler implements ITechnicalStrings, ErrorHandler, Observer {
  /** Self instance */
  private static History history;

  /** History repository, last play first */
  private static Vector<HistoryItem> vHistory = new Vector<HistoryItem>(100);

  /** History begin date */
  private static long lDateStart;

  /** Cached date formatter */
  private SimpleDateFormat formatter;

  /** Instance getter */
  public static synchronized History getInstance() {
    if (history == null) {
      history = new History();
    }
    return history;
  }

  /** Hidden constructor */
  private History() {
    ObservationManager.register(this);
    // check if something has already started
    if (ObservationManager.getDetailLastOccurence(EventSubject.EVENT_FILE_LAUNCHED,
        DETAIL_CURRENT_FILE_ID) != null
        && ObservationManager.getDetailLastOccurence(EventSubject.EVENT_FILE_LAUNCHED,
            DETAIL_CURRENT_DATE) != null) {
      update(new Event(EventSubject.EVENT_FILE_LAUNCHED, ObservationManager
          .getDetailsLastOccurence(EventSubject.EVENT_FILE_LAUNCHED)));
    }
    // Fill date formater
    formatter = new SimpleDateFormat(Messages.getString("HistoryItem.0"));
  }

  public Set<EventSubject> getRegistrationKeys() {
    HashSet<EventSubject> eventSubjectSet = new HashSet<EventSubject>();
    eventSubjectSet.add(EventSubject.EVENT_FILE_LAUNCHED);
    eventSubjectSet.add(EventSubject.EVENT_DEVICE_REFRESH);
    eventSubjectSet.add(EventSubject.EVENT_CLEAR_HISTORY);
    eventSubjectSet.add(EventSubject.EVENT_FILE_NAME_CHANGED);
    eventSubjectSet.add(EventSubject.EVENT_LANGUAGE_CHANGED);
    eventSubjectSet.add(EventSubject.EVENT_WEBRADIO_LAUNCHED);
    return eventSubjectSet;
  }

  /**
   * 
   * @return the history
   */
  public synchronized Vector<HistoryItem> getHistory() {
    return vHistory;
  }

  /** Add an history item */
  public synchronized void addItem(String sFileId, long lDate) {
    if (ConfigurationManager.getProperty(CONF_HISTORY).equals("0")) { // no
      // history
      // 
      return;
    }
    // check the ID maps an existing file
    if (FileManager.getInstance().getFileByID(sFileId) == null) {
      return;
    }
    // OK, begin to add the new history item
    HistoryItem hi = new HistoryItem(sFileId, lDate);
    // check if previous history item is not the same,
    // otherwise, keep last one
    if (vHistory.size() > 0) {
      HistoryItem hiPrevious = vHistory.get(0);
      if (hiPrevious.getFileId().equals(hi.getFileId())) {
        vHistory.remove(0);
      }
      vHistory.add(0, hi); // keep only most recent date
      // test maximum history size, if >, remove oldest item
      if (vHistory.size() > MAX_HISTORY_SIZE) {
        vHistory.remove(vHistory.size() - 1);
      }
    } else { // first element in history
      vHistory.add(0, hi);
    }
  }

  /** Clear history */
  public synchronized void clear() {
    vHistory.clear();
  }

  /**
   * Cleanup history of dead items (removed files after a refresh)
   * 
   */
  public synchronized void cleanup() {
    Iterator it = vHistory.iterator();
    while (it.hasNext()) {
      HistoryItem hi = (HistoryItem) it.next();
      if (hi.toString() == null) {
        it.remove();
      }
    }
  }

  /**
   * Change ID for a file
   * 
   * @param sIDOld
   * @param sIDNew
   */
  public synchronized void changeID(String sIDOld, String sIDNew) {
    for (int i = 0; i < vHistory.size(); i++) {
      HistoryItem hi = vHistory.get(i);
      if (hi.getFileId().equals(sIDOld)) {
        vHistory.remove(i);
        vHistory.add(i, new HistoryItem(sIDNew, hi.getDate()));
      }
    }
  }

  /** Clear history for all history items before iDays days */
  public synchronized void clear(int iDays) {
    // Begins by clearing deleted files
    Iterator it = vHistory.iterator();
    while (it.hasNext()) {
      HistoryItem hi = (HistoryItem) it.next();
      if (hi.toString() == null) {
        it.remove();
      }
    }
    // Follow day limits
    if (iDays == -1) { // infinite history
      return;
    }
    it = vHistory.iterator();
    while (it.hasNext()) {
      HistoryItem hi = (HistoryItem) it.next();
      if (hi.getDate() < (System.currentTimeMillis() - (iDays * ITechnicalStrings.MILLISECONDS_IN_A_DAY))) {
        it.remove();
      }
    }

  }

  /**
   * Write history on disk
   * 
   * @exception IOException
   */
  public static void commit() throws IOException {
    if (lDateStart == 0) {
      lDateStart = System.currentTimeMillis();
    }
    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Util
        .getConfFileByPath(FILE_HISTORY)), "UTF-8"));
    bw.write("<?xml version='1.0' encoding='UTF-8'?>\n");
    bw.write("<history jajuk_version='" + JAJUK_VERSION + "' begin_date='"
        + Long.toString(lDateStart) + "'>\n");
    Iterator it = vHistory.iterator();
    while (it.hasNext()) {
      HistoryItem hi = (HistoryItem) it.next();
      bw.write("\t<play file='" + hi.getFileId() + "' date='" + hi.getDate() + "'/>\n");
    }
    bw.write("</history>");
    bw.flush();
    bw.close();
  }

  /**
   * Read history from disk
   * 
   * @exception JajukException
   */
  public static void load() {
    try {
      SAXParserFactory spf = SAXParserFactory.newInstance();
      spf.setValidating(false);
      SAXParser saxParser = spf.newSAXParser();
      File frt = Util.getConfFileByPath(FILE_HISTORY);
      saxParser.parse(frt.toURL().toString(), getInstance());
      getInstance().clear(Integer.parseInt(ConfigurationManager.getProperty(CONF_HISTORY))); // delete
      // old
      // history items
    } catch (Exception e) {
      Log.error(new JajukException(119));
      try {
        commit(); // this history looks corrupted, write a void one
      } catch (Exception e2) {
        Log.error(e2);
      }
    }
  }

  /**
   * 
   * @return id of last played registered track or null if history is empty
   */
  public synchronized String getLastFile() {
    HistoryItem hiLast = null;
    if (vHistory.size() == 0) {
      return null;
    }
    hiLast = vHistory.get(0);
    if (hiLast == null) {
      return null;
    }
    return hiLast.getFileId();
  }

  /**
   * Return the history item by index
   * 
   * @param index
   * @return
   */
  public synchronized HistoryItem getHistoryItem(int index) {
    return (index >= 0 ? (HistoryItem) vHistory.get(index) : null);
  }

  /**
   * parsing warning
   * 
   * @param spe
   * @exception SAXException
   */
  public void warning(SAXParseException spe) throws SAXException {
    throw new SAXException(Messages.getErrorMessage(119) + " / " + spe.getSystemId() + "/"
        + spe.getLineNumber() + "/" + spe.getColumnNumber() + " : " + spe.getMessage());
  }

  /**
   * parsing error
   * 
   * @param spe
   * @exception SAXException
   */
  public void error(SAXParseException spe) throws SAXException {
    throw new SAXException(Messages.getErrorMessage(119) + " / " + spe.getSystemId() + "/"
        + spe.getLineNumber() + "/" + spe.getColumnNumber() + " : " + spe.getMessage());
  }

  /**
   * parsing fatal error
   * 
   * @param spe
   * @exception SAXException
   */
  public void fatalError(SAXParseException spe) throws SAXException {
    throw new SAXException(Messages.getErrorMessage(119) + " / " + spe.getSystemId() + "/"
        + spe.getLineNumber() + "/" + spe.getColumnNumber() + " : " + spe.getMessage());
  }

  /**
   * Called at parsing start
   */
  public void startDocument() {
    Log.debug("Starting history file parsing...");
  }

  /**
   * Called at parsing end
   */
  public void endDocument() {
    Log.debug("History file parsing done");
  }

  /**
   * Called when we start an element
   * 
   */
  public void startElement(String sUri, String sName, String sQName, Attributes attributes)
      throws SAXException {
    if (sQName.equals("history")) {
      History.lDateStart = Long.parseLong(attributes.getValue(attributes.getIndex("begin_date")));
    } else if (sQName.equals("play")) {
      String sID = attributes.getValue(attributes.getIndex("file"));
      // check id has not been changed
      HashMap<String, String> hm = Collection.getInstance().getHmWrongRightFileID();
      if (hm.size() > 0 && hm.containsKey(sID)) {
        sID = hm.get(sID);
        Log.debug("upload:" + sID);
      }
      // test if this file is still kwown int the collection
      if (FileManager.getInstance().getFileByID(sID) != null) {
        HistoryItem hi = new HistoryItem(sID, Long.parseLong(attributes.getValue(attributes
            .getIndex("date"))));
        vHistory.add(hi);
      }
    }
  }

  /**
   * Called when we reach the end of an element
   */
  public void endElement(String sUri, String sName, String sQName) throws SAXException {

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.jajuk.ui.Observer#update(java.lang.String)
   */
  public void update(Event event) {
    EventSubject subject = event.getSubject();
    try {
      if (EventSubject.EVENT_FILE_LAUNCHED.equals(subject)) {
        String sFileID = (String) ObservationManager.getDetail(event, DETAIL_CURRENT_FILE_ID);
        long lDate = ((Long) ObservationManager.getDetail(event, DETAIL_CURRENT_DATE)).longValue();
        addItem(sFileID, lDate);
      } else if (EventSubject.EVENT_DEVICE_REFRESH.equals(subject)) {
        cleanup();
      } else if (EventSubject.EVENT_CLEAR_HISTORY.equals(subject)) {
        clear();
      } else if (EventSubject.EVENT_LANGUAGE_CHANGED.equals(subject)) {
        // reset formatter
        formatter = new SimpleDateFormat(Messages.getString("HistoryItem.0"));
      } else if (EventSubject.EVENT_FILE_NAME_CHANGED.equals(subject)) {
        Properties properties = event.getDetails();
        org.jajuk.base.File fileOld = (org.jajuk.base.File) properties.get(DETAIL_OLD);
        org.jajuk.base.File fNew = (org.jajuk.base.File) properties.get(DETAIL_NEW);
        // change id in history
        changeID(fileOld.getID(), fNew.getID());
      }
    } catch (Exception e) {
      Log.error(e);
      return;
    }
  }

  /**
   * 
   * @return Cached date formater
   */
  public SimpleDateFormat getDateFormatter() {
    return this.formatter;
  }

}
