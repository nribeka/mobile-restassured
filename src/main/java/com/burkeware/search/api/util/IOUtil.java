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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created after reading the IOUtils from Apache's commons-io
 */
public class IOUtil {

    /**
     * The default buffer size to use.
     */
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public static String readAsString(final Reader input) throws IOException {

        char[] buffer = new char[DEFAULT_BUFFER_SIZE];

        StringWriter writer = new StringWriter();
        BufferedReader reader = new BufferedReader(input);

        int count;
        while ((count = reader.read(buffer)) != -1)
            writer.write(buffer, 0, count);
        return writer.toString();
    }

    public static String readAsString(final InputStream inputStream) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        return readAsString(streamReader);
    }

    public static String readAsString(final InputStream inputStream, final String encoding) throws IOException {
        if (encoding == null) {
            return readAsString(inputStream);
        } else {
            InputStreamReader streamReader = new InputStreamReader(inputStream, encoding);
            return readAsString(streamReader);
        }
    }

    public static List<String> readAsList(final Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        List<String> strings = new ArrayList<String>();

        String line = bufferedReader.readLine();
        while (line != null) {
            strings.add(line);
            line = bufferedReader.readLine();
        }
        return strings;
    }

    public static List<String> readAsList(final InputStream inputStream) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        return readAsList(streamReader);
    }

    public static List<String> readAsList(final InputStream inputStream, final String encoding) throws IOException {
        if (encoding == null) {
            return readAsList(inputStream);
        } else {
            InputStreamReader streamReader = new InputStreamReader(inputStream, encoding);
            return readAsList(streamReader);
        }
    }
}
