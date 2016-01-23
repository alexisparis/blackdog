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
 *  $$Revision: 3156 $$
 */
package org.jajuk.util.error;

public class CannotRenameException extends JajukException {

  private static final long serialVersionUID = 1L;

  /**
   * constructor.
   * 
   * @param pCode
   *          Code of the current error.
   */
  public CannotRenameException(int code) {
    super(code);
  }

  /**
   * JajukException constructor.
   * 
   * @param code
   *          Code of the current error.
   * @param pCause
   *          Original exception of the error.
   */
  public CannotRenameException(int code, Throwable pCause) {
    super(code, pCause);
  }

}