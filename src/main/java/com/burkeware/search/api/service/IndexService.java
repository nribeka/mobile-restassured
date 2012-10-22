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

package com.burkeware.search.api.service;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import com.burkeware.search.api.JsonLuceneConfig;

public interface IndexService {

    /**
     * Service method to update the index with entries from the input stream.
     *
     * @param config      the j2l configuration file to be used to map the json payload to lucene document
     * @param inputStream input stream where json entries will be read
     * @should update the index using json data from the input stream
     */
    void updateIndex(final JsonLuceneConfig config, final InputStream inputStream);

    /**
     * Service method to update the index with entries from the file.
     *
     * @param config    the j2l configuration file to be used to map the json payload to lucene document
     * @param directory directory information where json entries will be read
     * @should update the index using json data from file inside the directory
     */
    void updateIndex(final JsonLuceneConfig config, final File directory);
}
