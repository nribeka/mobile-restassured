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

import com.burkeware.search.api.exception.FactoryException;
import com.burkeware.search.api.factory.internal.BaseFactory;

import java.lang.reflect.Constructor;
import java.util.List;

public class DefaultFactory<T> extends BaseFactory<T> {

    protected final String implementationName;

    /**
     * The implementation of the base factory.
     */
    protected DefaultFactory(final String implementationName) {
        this.implementationName = implementationName;
    }

    /**
     * Generic method to create an implementation based on the registered implementation classes.
     *
     * @param key the key under which the implementation class is registered
     * @return the created instance
     */
    @Override
    public T createImplementation(final String key) {
        if (!getMappings().containsKey(key)) {
            String message = "Cannot create " + implementationName + ". There's no registered "
                    + implementationName + " for the parameters " + "(" + key + "). ";

            List<String> hints = getKeys();
            if (hints.isEmpty()) {
                message = message + "Actually there are no valid keys registered for this "
                        + implementationName + ". Maybe you've made a mistake spelling it?";
            } else {
                message = message + "Valid keys for the " + implementationName + " are: ";
                for (String hint : hints) {
                    message = message + "\n  - " + hint;
                }
            }

            throw new FactoryException(message);
        }

        Class<? extends T> implementationClass = getMappings().get(key);

        T implementation;
        try {
            Constructor<? extends T> constructor = implementationClass.getConstructor();
            implementation = constructor.newInstance();
        } catch (Exception e) {
            throw new FactoryException("Failed to create " + implementationName + " with implementation "
                    + implementationClass + " for the parameters (" + key + ").", e);
        }

        return implementation;
    }
}
