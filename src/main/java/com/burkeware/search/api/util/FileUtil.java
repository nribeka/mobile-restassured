/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package com.burkeware.search.api.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtil {

    static final byte[] HEX_CHAR_TABLE = {
            (byte) '0', (byte) '1', (byte) '2', (byte) '3',
            (byte) '4', (byte) '5', (byte) '6', (byte) '7',
            (byte) '8', (byte) '9', (byte) 'a', (byte) 'b',
            (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f'
    };

    public static String getExtension(final File file) {
        String extension = null;
        String fileName = file.getName();
        int i = fileName.lastIndexOf('.');

        if (i > 0 && i < fileName.length() - 1) {
            extension = fileName.substring(i + 1).toLowerCase();
        }
        return extension;
    }

    public static String getSHA1Checksum(final String filename) throws NoSuchAlgorithmException, IOException {
        return getSHA1Checksum(new File(filename));
    }

    public static String getSHA1Checksum(final File file) throws NoSuchAlgorithmException, IOException {
        return getHexString(createChecksum(file));
    }

    private static byte[] createChecksum(final File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest complete = MessageDigest.getInstance("SHA1");

        InputStream fis = null;
        try {
            int numRead;
            byte[] buffer = new byte[1024];
            fis = new FileInputStream(file);
            while ((numRead = fis.read(buffer)) != -1)
                complete.update(buffer, 0, numRead);
        } finally {
            if (fis != null)
                fis.close();
        }

        return complete.digest();
    }

    private static String getHexString(final byte[] raw) throws UnsupportedEncodingException {
        byte[] hex = new byte[2 * raw.length];
        int index = 0;

        for (byte b : raw) {
            int v = b & 0xFF;
            hex[index++] = HEX_CHAR_TABLE[v >>> 4];
            hex[index++] = HEX_CHAR_TABLE[v & 0xF];
        }
        return new String(hex, "ASCII");
    }
}
