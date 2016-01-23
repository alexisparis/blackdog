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

package net.sourceforge.atunes.kernel.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

/**
 * <p>
 * Utility methods for encrypting and decrypting
 * </p>
 * <p>
 * <b> NOTE: This class does not not produce really secret encryptions (the
 * secret key is public)!</b>
 * </p>
 */
public class CryptoUtils {

	/* Length: 8 Bytes for DES */
	private static final String SECRET_KEY = "5Gjd9T0b";

	/**
	 * Decrypts a byte array
	 * 
	 * @param bytes
	 *            The byte array to decrypt
	 * @return The decrypted byte array
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public static byte[] decrypt(byte[] bytes) throws GeneralSecurityException, IOException {
		if (bytes == null || bytes.length == 0) {
			return new byte[0];
		}

		Cipher c = Cipher.getInstance("DES");
		Key k = new SecretKeySpec(SECRET_KEY.getBytes(), "DES");
		c.init(Cipher.DECRYPT_MODE, k);

		ByteArrayInputStream input = new ByteArrayInputStream(bytes);
		CipherInputStream cis = new CipherInputStream(input, c);
		BufferedReader reader = new BufferedReader(new InputStreamReader(cis));
		String string = reader.readLine();
		reader.close();
		return string.getBytes();
	}

	/**
	 * Encrypts a byte array
	 * 
	 * @param bytes
	 *            The byte array to encrypt
	 * @return The encrypted byte array
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public static byte[] encrypt(byte[] bytes) throws GeneralSecurityException, IOException {
		if (bytes == null || bytes.length == 0) {
			return new byte[0];
		}

		Cipher c = Cipher.getInstance("DES");
		Key k = new SecretKeySpec(SECRET_KEY.getBytes(), "DES");
		c.init(Cipher.ENCRYPT_MODE, k);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		CipherOutputStream cos = new CipherOutputStream(output, c);
		cos.write(bytes);
		cos.close();
		return output.toByteArray();
	}

	/* For testing */
	public static void main(String[] args) throws Exception {
		System.out.println(new String(decrypt(encrypt("aTunes".getBytes()))));
		System.out.println(new String(decrypt(encrypt("abcdefgh".getBytes()))));
		System.out.println(new String(decrypt(encrypt("".getBytes()))));
		System.out.println(new String(decrypt(encrypt(null))));
		System.out.println(new String(decrypt(encrypt("*1bkk49fkjküpp)U=(Zhfh98".getBytes()))));
	}
}
