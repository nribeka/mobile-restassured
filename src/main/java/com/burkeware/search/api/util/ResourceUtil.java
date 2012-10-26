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

import com.burkeware.search.api.exception.ParseException;
import com.burkeware.search.api.resource.internal.Resource;
import com.burkeware.search.api.resource.registry.Properties;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ResourceUtil {

    public static Properties loadResourceConfig(final File file) throws ParseException, IOException {

        java.util.Properties properties = new java.util.Properties();
        properties.load(new FileReader(file));

        for (String mandatoryField : Resource.MANDATORY_FIELDS)
            if (!properties.containsKey(mandatoryField))
                throw new ParseException("Unable to read " + mandatoryField + " (required) property from j2l file.");

        Properties resourceProperties = new Properties();
        for (Object objectKey : properties.keySet()) {
            String propertyName = (String) objectKey;
            resourceProperties.putEntry(propertyName, properties.getProperty(propertyName));
        }
        return resourceProperties;
    }

}
