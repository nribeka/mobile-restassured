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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import com.burkeware.search.api.JsonLuceneConfig;

public class JsonLuceneUtil {

    public static JsonLuceneConfig load(File file){
        Map<String, String> templateMap = new TreeMap<String, String>();

        try {
            String line;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                String[] templateFragment = line.split("=");
                templateMap.put(templateFragment[0].trim(), templateFragment[1].trim());
            }
            reader.close();
        } catch (IOException e) {
            // Ignoring the exception again :)
        }

        return new JsonLuceneConfig(templateMap);
    }

}
