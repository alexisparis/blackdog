/*
 *  Jajuk
 *  Copyright (C) 2007 The Jajuk Team
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
 *  $$Revision: 3216 $$
 */
package org.jajuk.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.jajuk.util.log.Log;

/**
 * Convenient class to compute MD5 hascodes and return them using predefined
 * base
 */

public class MD5Processor {

  /**
   * MD5 hashcoding, return a hashcode
   * 
   * @param sIn
   *          input String
   * @return hashed output
   */
  public static final String hash(String sIn) {
    java.security.MessageDigest msgDigest;
    try {
      msgDigest = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      Log.error(e);
      return null;
    }
    msgDigest.update(sIn.getBytes());
    byte[] digest = msgDigest.digest();
    return new BigInteger(digest).abs().toString(36).intern();
  }
}
