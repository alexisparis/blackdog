/*
 * BasicPlayerListener.java
 *
 * Created on 17 mai 2007, 13:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.blackdog.player.impl;

/**
 *
 * @author alexis
 */
public interface JSPlayerListener
{
  public void updateCursor(int cursor, int total);

  public void updateMediaData(byte[] data);

  public void updateMediaState(String state);

  public void updateTime(long nbmilliseconds);
}

