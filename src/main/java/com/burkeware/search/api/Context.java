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
package com.burkeware.search.api;

import com.burkeware.search.api.exception.ParseException;
import com.burkeware.search.api.internal.factory.Factory;
import com.burkeware.search.api.logger.Logger;
import com.burkeware.search.api.registry.Registry;
import com.burkeware.search.api.resolver.Resolver;
import com.burkeware.search.api.resource.ObjectResource;
import com.burkeware.search.api.resource.Resource;
import com.burkeware.search.api.resource.ResourceConstants;
import com.burkeware.search.api.serialization.Algorithm;
import com.burkeware.search.api.util.ResourceFileFilter;
import com.burkeware.search.api.util.ResourceUtil;
import com.burkeware.search.api.util.StringUtil;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Singleton
public class Context {

    @Inject
    private Logger logger;

    @Inject
    private Registry<String, Class> classRegistry;

    @Inject
    private Registry<String, Resource> resourceRegistry;

    @Inject
    private Registry<String, Registry<String, String>> propertiesRegistry;

    @Inject
    private Factory<Resolver> resolverFactory;

    @Inject
    private Factory<Algorithm> algorithmFactory;

    /**
     * Register a new resource object for future use.
     *
     * @param resource the resource to be registered.
     */
    public void registerResource(final Resource resource) {
        if (resource != null)
            resourceRegistry.putEntry(resource.getName(), resource);
    }

    /**
     * Read the input file and then convert each file into resource object and register them.
     *
     * @param file the file (could be a directory too).
     * @throws ParseException when the parser fail to parse the configuration file
     * @throws IOException    when the parser fail to read the configuration file
     */
    public void registerResources(final File file) throws ParseException, IOException {
        FileFilter fileFilter = new ResourceFileFilter();
        if (!file.isDirectory() && fileFilter.accept(file)) {
            registerResource(createResource(file));
        } else {
            File[] files = file.listFiles(fileFilter);
            if (files != null) {
                for (File resourceFile : files)
                    registerResources(resourceFile);
            }
        }
    }

    /**
     * Internal method to convert the actual resource file into the resource object.
     *
     * @param file the file
     * @return the resource object
     * @throws ParseException when the parser fail to parse the configuration file
     * @throws IOException    when the parser fail to read the configuration file
     */
    private Resource createResource(final File file) throws ParseException, IOException {

        Registry<String, String> properties = ResourceUtil.readConfiguration(file);
        String resourceName = properties.getEntryValue(ResourceConstants.RESOURCE_NAME);

        String rootNode = properties.getEntryValue(ResourceConstants.RESOURCE_ROOT_NODE);

        String objectClassKey = properties.getEntryValue(ResourceConstants.RESOURCE_CLASS);
        Class objectClass = classRegistry.getEntryValue(objectClassKey);

        String algorithmKey = properties.getEntryValue(ResourceConstants.RESOURCE_ALGORITHM_CLASS);
        Algorithm algorithm = algorithmFactory.createImplementation(algorithmKey);

        String resolverKey = properties.getEntryValue(ResourceConstants.RESOURCE_URI_CLASS);
        Resolver resolver = resolverFactory.createImplementation(resolverKey);

        Resource resource = new ObjectResource(resourceName, rootNode, objectClass, algorithm, resolver);

        Object uniqueField = properties.getEntryValue(ResourceConstants.RESOURCE_UNIQUE_FIELD);
        List<String> uniqueFields = new ArrayList<String>();
        if (uniqueField != null)
            uniqueFields = Arrays.asList(StringUtil.split(uniqueField.toString(), ","));

        List<String> ignoredField = ResourceConstants.NON_SEARCHABLE_FIELDS;
        Map<String, String> entries = properties.getEntries();
        for (String fieldName : entries.keySet()) {
            if (!ignoredField.contains(fieldName)) {
                Boolean unique = Boolean.FALSE;
                if (uniqueFields.contains(fieldName))
                    unique = Boolean.TRUE;
                resource.addFieldDefinition(fieldName, entries.get(fieldName), unique);
            }
        }

        return resource;
    }

    /**
     * Get all registered resources from the resource registry.
     *
     * @return all registered resources.
     */
    public Collection<Resource> getResources() {
        return this.resourceRegistry.getEntries().values();
    }

    /**
     * Get resource with the name denoted by the parameter.
     *
     * @param name the name of the resource
     * @return the matching resource object or null if no resource match have the matching name.
     */
    public Resource getResource(final String name) {
        return this.resourceRegistry.getEntryValue(name);
    }

    /**
     * Register all domain object classes.
     *
     * @param classes the domain object classes
     */
    public void registerObject(final Class<?>... classes) {
        for (Class<?> clazz : classes)
            classRegistry.putEntry(clazz.getName(), clazz);
    }

    /**
     * Register all algorithm classes.
     *
     * @param algorithms the algorithm classes
     */
    public void registerAlgorithm(final Class<? extends Algorithm>... algorithms) {
        for (Class<? extends Algorithm> algorithm : algorithms)
            algorithmFactory.registerImplementation(algorithm.getName(), algorithm);
    }

    /**
     * Register all resolver classes
     *
     * @param resolvers the resolver classes
     */
    public void registerResolver(final Class<? extends Resolver>... resolvers) {
        for (Class<? extends Resolver> resolver : resolvers)
            resolverFactory.registerImplementation(resolver.getName(), resolver);
    }
}
