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

package com.burkeware.search.api.factory;

import java.util.HashMap;
import java.util.Map;

import com.burkeware.search.api.algorithm.Algorithm;

// TODO: use guice to make this class generic and can be injected
public final class SearchObjectFactory {

    private Map<Class, Algorithm> algorithms = new HashMap<Class, Algorithm>();

    private static volatile SearchObjectFactory instance = null;

    /**
     * The class should not be instantiated outside the class
     */
    private SearchObjectFactory() {
    }

    /**
     * Get the search object factory instance.
     *
     * @return the search object instance
     */
    public static SearchObjectFactory getInstance() {
        if (instance == null) {
            synchronized (SearchObjectFactory.class) {
                if (instance == null) {
                    instance = new SearchObjectFactory();
                }
            }
        }
        return instance;
    }

    /**
     * Remove algorithm that handle the clazz conversion.
     *
     * @param clazz the expected return type from the algorithm conversion process
     */
    public final void removeAlgorithm(final Class clazz) {
        algorithms.remove(clazz);
    }

    /**
     * Add a new algorithm to convert json to the object of type denoted by the param clazz.
     *
     * @param clazz the expected return type from the conversion process performed by the algorithm
     * @param algorithm the algorithm
     */
    public final void addAlgorithm(final Class clazz, final Algorithm algorithm) {
        algorithms.put(clazz, algorithm);
    }

    public Algorithm getAlgorithm(final Class clazz) {
        return algorithms.get(clazz);
    }
}
