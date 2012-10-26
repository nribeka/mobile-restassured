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

import com.burkeware.search.api.RestAssuredService;
import com.burkeware.search.api.factory.internal.Factory;
import com.burkeware.search.api.resource.internal.Registry;
import com.burkeware.search.api.resource.internal.Resource;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.io.File;
import java.util.List;

public class RestAssuredServiceImpl implements RestAssuredService {

    private final Factory<Resource> resourceFactory;

    private final Registry<String, Resource> resourceRegistry;

    @Inject
    public RestAssuredServiceImpl(final @Named("ResourceFactory") Factory<Resource> resourceFactory,
                                  final @Named("ResourceRegistry") Registry<String, Resource> resourceRegistry) {
        this.resourceFactory = resourceFactory;
        this.resourceRegistry = resourceRegistry;
    }

    /**
     * Add new resource for into the registry of all known resources.
     *
     * @param resource the new resource to be added into the list of all known resources
     * @param <T>      parameterizable denoting the resource is applicable for the object T
     */
    @Override
    public <T> void addResource(final Resource<T> resource) {
    }

    @Override
    public void addResources(final File file) {
    }

    @Override
    public <T> List<Resource<T>> getResources(final T t) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Resource> getResources() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T> void loadObjects(final Resource<T> resource, final String searchString) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T> void loadObjects(final Resource<T> resource, final String searchString, final File file) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T> T getObject(final Resource<T> resource, final String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T> List<T> getObjects(final Resource<T> resource, final String searchString) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T> T invalidate(final Resource<T> resource, final Object object) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T> T createObject(final T object) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T> T updateObject(final T object) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
