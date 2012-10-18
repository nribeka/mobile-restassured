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

import com.burkeware.search.api.JsonLuceneConfig;
import com.burkeware.search.api.algorithm.Algorithm;

public interface ConfigService {

    /**
     * Service method to register new algorithm to create object based on raw information retrieved from the index.
     *
     * @param clazz the expected type of the object
     * @param algorithm the algorithm to transform the raw data to the correct object
     */
    void registerAlgorithm(final Class clazz, final Algorithm algorithm);

    /**
     * Service method to register new lucene configuration into the core of the search api.
     *
     * @param config the configurations classes
     */
    void registerConfiguration(final JsonLuceneConfig... config);

}
