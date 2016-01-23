/*
 * InputStreamProvider.java
 *
 * Created on 19 février 2008, 19:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.blackdog.player.impl;

import java.io.InputStream;

/**
 *
 * @author alexis
 */
public interface InputStreamProvider
{
    /** return a new InputStream
     *	@return an InputStream
     */
    public InputStream createStream();
}
