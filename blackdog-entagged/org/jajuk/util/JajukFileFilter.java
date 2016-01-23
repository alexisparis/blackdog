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

package org.jajuk.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Advanced file filter
 * 
 * @see https://trac.jajuk.info/wiki/JajukDevGuide#Filesfilters for direction to
 *      use
 *      <p>
 *      Exemple: new
 *      JajukFilter(false,JajukFileFilter.DirectoryFilter.getInstance(),
 *      JajukFileFilter.AudioFilter.getInstance());
 *      </p>
 *      This class can be use by file choosers (probably a JajukFileChooser) and
 *      for engine file selection that uses the raw accept method. In the second
 *      case, it can be useful to use grouping fiters like music or report (but
 *      it is not intended to be used by file choosers that require only one
 *      extension by filter) and or/and argument given as an argument
 * 
 */
public class JajukFileFilter extends FileFilter implements java.io.FileFilter, ITechnicalStrings {

  /** Filters */
  private JajukFileFilter[] filters = {};

  /** Show directories (useful to allow user to navigate) */
  protected boolean bShowDirectories = false;

  /** List of Extensions for the current filter */
  protected String[] extensions = {};
  protected String extensionsString = "";

  /** And or OR applied to multi filters ? */
  private boolean bAND = true;

  /**
   * Filter constructor
   * 
   * @param filters
   *          undefined list of jajuk filter to be applied (logical AND applied
   *          between filters)
   * @param bAND:
   *          should be applied an AND or an OR between filters ?
   *          <p>
   *          Example: audio files or directories: new
   *          JajukFilter(false,JajukFileFilter.DirectoryFilter.getInstance(),JajukFileFilter.AudioFilter.getInstance());
   *          </p>
   */
  public JajukFileFilter(final boolean bAND, final JajukFileFilter... filters) {
    this.bAND = bAND;
    this.filters = filters;
  }

  /**
   * Filter constructor
   * 
   * @param filters
   *          undefined list of jajuk filter to be applied (logical AND applied
   *          between filters)
   *          <p>
   *          Example: only audio files new
   *          JajukFilter(JajukFileFilter.AudioFilter.getInstance());
   *          </p>
   */
  public JajukFileFilter(final JajukFileFilter... filters) {
    this(true, filters);
  }

  /**
   * Filter constructor, used mostly by subclasses to define type-safe
   * JajukFileFilters defining their own extensions lists, and occasionally
   * overriding some methods to influence the filtering process.
   * 
   * @param extensions
   *          an array of extension strings
   */
  public JajukFileFilter(final String[] extensions) {
    this.extensions = (extensions != null) ? extensions : new String[] {};
    final int size = this.extensions.length;
    for (int i = 0; i < size; i++) {
      this.extensions[i] = this.extensions[i].toLowerCase();
      extensionsString += this.extensions[i] + ',';
    }
    // Drop last coma
    if (!Util.isVoid(extensionsString)) {
      extensionsString = extensionsString.substring(0, extensionsString.length() - 1);
    }
  }

  /**
   * Returns the filtering boolean status, after having combined all filters
   * with either an AND or OR logical rule.
   * 
   * @param f
   *          file to test
   */
  @Override
  public boolean accept(final File f) {
    boolean acceptance = false;

    if (filters.length != 0) {
      boolean test = false;
      if (bAND) {
        test = true;
        for (final JajukFileFilter element : filters) {
          test &= element.accept(f);
        }
      } else {
        for (final JajukFileFilter element : filters) {
          test |= element.accept(f);
        }
      }
      acceptance = test;
    } else {
      acceptance = show(f);
    }
    return (acceptance);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.filechooser.FileFilter#getDescription()
   */
  @Override
  public String getDescription() {
    return (extensionsString);
  }

  /**
   * Returns an array of strings containing the extension patterns defined by
   * the current JajukFileFilter object instance.
   * 
   * @return array of extension strings
   */
  public String[] getExtensions() {
    return (extensions);
  }

  public JajukFileFilter[] getFilters() {
    return (filters);
  }

  /**
   * Checks if the given file's extension matches the ones expected by the
   * filter's registered extension. Beware that this method may be overriden.
   * 
   * @param file
   *          the file to be filtered
   * @return known-extension flag
   */
  protected boolean isKnownExtension(final File file) {
    if (file != null) {
      final String extension = Util.getExtension(file).toLowerCase();

      for (final String ext : extensions) {
        if (extension.equals(ext)) {
          return (true);
        }
      }
    }
    return (false);
  }

  /**
   * Force the filter to accept or reject directories
   * 
   * @param b
   *          directory acceptance flag
   */
  public void setAcceptDirectories(final boolean b) {
    bShowDirectories = b;
  }

  /**
   * Checks if the given file should be shown, according if it is a directory
   * and that directory display is activated, or a file with a known extension.
   * Beware that this method or the isKnownExtension may be overriden.
   * 
   * @param file
   *          the file to be filtered
   * @return display status flag (fallback is false)
   */
  protected boolean show(final File file) {
    return ((file.isDirectory()) ? bShowDirectories : (isKnownExtension(file)));
  }

}
