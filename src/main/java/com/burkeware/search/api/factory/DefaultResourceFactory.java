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
import com.burkeware.search.api.factory.internal.Factory;
import com.burkeware.search.api.resource.internal.Registry;
import com.burkeware.search.api.resource.internal.Resource;
import com.burkeware.search.api.resource.registry.Properties;
import com.burkeware.search.api.serialization.Algorithm;
import com.burkeware.search.api.uri.FigureOuter;
import com.burkeware.search.api.util.StringUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Singleton
public final class DefaultResourceFactory extends BaseFactory<Resource> {

    private final String implementationName;

    private final Factory<Algorithm> algorithmFactory;

    private final Factory<FigureOuter> figureOuterFactory;

    private final Registry<String, Properties> propertiesRegistry;

    @Inject
    public DefaultResourceFactory(final @Named("AlgorithmFactory") Factory<Algorithm> algorithmFactory,
                                  final @Named("FigureOuterFactory") Factory<FigureOuter> figureOuterFactory,
                                  final @Named("PropertiesRegistry") Registry<String, Properties> propertiesRegistry,
                                  final @Named("ResourceFactory.name") String implementationName) {
        this.algorithmFactory = algorithmFactory;
        this.figureOuterFactory = figureOuterFactory;
        this.implementationName = implementationName;
        this.propertiesRegistry = propertiesRegistry;
    }

    /**
     * Generic method to create an implementation based on the registered implementation classes.
     *
     * @param key the key under which the implementation class is registered
     * @return the created instance
     */
    @Override
    public Resource createImplementation(final String key) {
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

        Class<? extends Resource> implementationClass = getMappings().get(key);

        Resource implementation;
        try {
            Constructor<? extends Resource> constructor =
                    implementationClass.getConstructor(
                            String.class, String.class, Class.class, Algorithm.class, FigureOuter.class);

            Properties properties = propertiesRegistry.getEntryValue(key);

            String resourceName = properties.getEntryValue(Resource.RESOURCE_NAME);

            String rootNode = properties.getEntryValue(Resource.RESOURCE_ROOT_NODE);

            String objectClass = properties.getEntryValue(Resource.RESOURCE_CLASS);
            Class clazz = Class.forName(objectClass);

            String algorithmKey = properties.getEntryValue(Resource.RESOURCE_ALGORITHM_CLASS);
            Algorithm algorithm = algorithmFactory.createImplementation(resourceName);

            String figureOuterKey = properties.getEntryValue(Resource.RESOURCE_URI_CLASS);
            FigureOuter figureOuter = figureOuterFactory.createImplementation(resourceName);

            implementation = constructor.newInstance(resourceName, rootNode, clazz, algorithm, figureOuter);

            Object uniqueField = properties.getEntryValue(Resource.RESOURCE_UNIQUE_FIELD);
            List<String> uniqueFields = new ArrayList<String>();
            if (uniqueField != null)
                uniqueFields = Arrays.asList(StringUtil.split(uniqueField.toString(), ","));

            List<String> ignoredField = Resource.NON_SEARCHABLE_FIELDS;
            Map<String, String> entries = properties.getEntries();
            for (String fieldName : entries.keySet()) {
                if (!ignoredField.contains(fieldName)) {
                    Boolean unique = Boolean.FALSE;
                    if (uniqueFields.contains(fieldName))
                        unique = Boolean.TRUE;
                    implementation.addFieldDefinition(fieldName, entries.get(fieldName), unique);
                }
            }
        } catch (Exception e) {
            throw new FactoryException("Failed to create " + implementationName
                    + " with implementation " + implementationClass + " for the parameters ("
                    + key + ").", e);
        }

        return implementation;
    }
}
