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

package com.burkeware.search.api.internal.factory;

import com.burkeware.search.api.serialization.Algorithm;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import java.lang.reflect.Constructor;

@Singleton
public class DefaultAlgorithmFactory extends BaseFactory<Algorithm> {

    /**
     * The implementation of the base factory.
     */
    @Inject
    protected DefaultAlgorithmFactory(final @Named("AlgorithmFactory.name") String implementationName) {
        super(implementationName);
    }

    /**
     * Create a constructor.
     *
     * @param algorithmClass registered class for which to create the constructor
     * @param key            the key to this implementation class
     * @return the constructor to use for creating an instance
     * @throws NoSuchMethodException in case of error
     */
    @Override
    protected Constructor<? extends Algorithm> getConstructor(final Class<? extends Algorithm> algorithmClass,
                                                              final String key)
            throws NoSuchMethodException {
        return algorithmClass.getConstructor();
    }

    /**
     * Create an implementation class instance.
     *
     * @param constructor the constructor to use for creating the instance
     * @param key         the key to differentiate this implementation class
     * @return the created instance
     * @throws Exception in case of error
     */
    @Override
    protected Algorithm createInstance(final Constructor<? extends Algorithm> constructor,
                                       final String key)
            throws Exception {
        return constructor.newInstance();
    }
}
