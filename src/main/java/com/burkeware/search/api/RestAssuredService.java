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

import com.burkeware.search.api.resource.Resource;

import java.io.File;
import java.util.List;

public interface RestAssuredService {

    /* Load object(s) into local database from remote URL */
    void loadObjects(final Resource resource, final String searchString);

    /* Load object(s) into local database from local file or directory */
    void loadObjects(final Resource resource, final String searchString, final File file);

    /* Get object */
    <T> T getObject(final Class<T> clazz, final String key);

    /* Search for object(s) */
    <T> List<T> getObjects(final Class<T> clazz, final String searchString);

    /* Remove object from local database  */
    <T> T invalidate(final Resource<T> resource, final Object object);

    /* Create a new object or changes to an object */
    <T> T createObject(final T object);

    /* Update an existing object */
    <T> T updateObject(final T object);

}
