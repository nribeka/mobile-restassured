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

import com.burkeware.search.api.resource.internal.Resource;

import java.io.File;
import java.util.List;

public interface RestAssuredService {

    /**
     * Add new resource for into the registry of all known resources.
     *
     * @param resource the new resource to be added into the list of all known resources
     * @param <T>      parameterizable denoting the resource is applicable for the object T
     */
    <T> void addResource(final Resource<T> resource);

    /**
     * @param file
     * @should add any resources inside the file
     */
    void addResources(final File file);

    /* Get known resources for T */
    <T> List<Resource<T>> getResources(final T t);

    List<Resource> getResources();

    /* Load object(s) into local database from remote URL */
    <T> void loadObjects(final Resource<T> resource, final String searchString);

    /* Load object(s) into local database from local file or directory */
    <T> void loadObjects(final Resource<T> resource, final String searchString, final File file);

    /* Get object */
    <T> T getObject(final Resource<T> resource, final String key);

    /* Search for object(s) */
    <T> List<T> getObjects(final Resource<T> resource, final String searchString);

    /* Remove object from local database  */
    <T> T invalidate(final Resource<T> resource, final Object object);

    /* Create a new object or changes to an object */
    <T> T createObject(final T object);

    /* Update an existing object */
    <T> T updateObject(final T object);

}
