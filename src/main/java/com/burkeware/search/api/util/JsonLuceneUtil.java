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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import com.burkeware.search.api.JsonLuceneConfig;

public class JsonLuceneUtil {

    public static JsonLuceneConfig load(final File file) throws IOException {
        return load(new FileInputStream(file));
    }

    public static JsonLuceneConfig load(final InputStream inputStream) throws IOException {

        JsonLuceneConfig config = new JsonLuceneConfig();

        Properties properties = new Properties();
        properties.load(inputStream);

        if (!properties.containsKey(JsonLuceneConfig.OBJECT_TYPE)
                || !properties.containsKey(JsonLuceneConfig.OBJECT_REPRESENTATION)
                || !properties.containsKey(JsonLuceneConfig.OBJECT_REPRESENTATION_TYPE))
            throw new IOException("Unable to read one or more required property from j2l file.");

        Object objectType = properties.remove(JsonLuceneConfig.OBJECT_TYPE);
        if (objectType == null)
            throw new IOException("Object type value must not be null.");
        config.setObjectType(String.valueOf(objectType));

        Object representation = properties.remove(JsonLuceneConfig.OBJECT_REPRESENTATION);
        if (representation== null)
            throw new IOException("Representation value must not be null.");
        config.setRepresentation(String.valueOf(representation));

        Object representationType = properties.remove(JsonLuceneConfig.OBJECT_REPRESENTATION_TYPE);
        if (representationType== null)
            throw new IOException("Representation type value must not be null.");
        config.setRepresentationType(String.valueOf(representationType));

        Map<String, String> mappings = new TreeMap<String, String>();
        for (Object object : properties.keySet())
            mappings.put(String.valueOf(object), String.valueOf(properties.get(object)));
        config.setMappings(mappings);

        return config;
    }

}
