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

    /**
     * Load object described using the <code>resource</code> into local lucene repository. This method will use the URI
     * resolver to resolve the URI of the REST resources and then apply the <code>searchString</code> to limit the data
     * which will be loaded into the local lucene repository.
     * <p/>
     * Internally, this method will also add the following field:
     * <pre>
     * _class : the expected representation of the json when serialized
     * _resource : the resource configuration used to convert the json to lucene
     * _date_indexed : date and time when the json was indexed
     * </pre>
     *
     * @param searchString the string to filter object that from the REST resource.
     * @param resource     the resource object which will describe how to index the json resource to lucene.
     */
    void loadObjects(final String searchString, final Resource resource);

    /**
     * Load object described using the <code>resource</code> into local lucene repository. This method will load locally
     * saved json payload and then apply the <code>searchString</code> to limit the data which will be loaded into the
     * local lucene repository.
     *
     * @param searchString the search string to filter object returned from the file.
     * @param resource     the resource object which will describe how to index the json resource to lucene.
     * @param file         the file in the filesystem where the json resource is saved.
     * @see com.burkeware.search.api.RestAssuredService#loadObjects(String, com.burkeware.search.api.resource.Resource)
     */
    void loadObjects(final String searchString, final Resource resource, final File file);

    /**
     * Search for an object with matching <code>key</code> and <code>clazz</code> type from the local repository. This
     * method will only return single object or null if no object match the key.
     * <p/>
     * Internally, this method will go through every registered resources to find which resources can be used to convert
     * the json payload to the an instance of <code>clazz</code> object. The method then extract the unique field from
     * each resource and then perform the lucene query for that resource. If the resource doesn't specify unique
     * searchable field, all searchable fields for that resource will be used for searching.
     *
     * @param key   the key to distinguish the object
     * @param clazz the expected return type of the object
     * @param <T>   generic type of the object
     * @return object with matching key and clazz or null
     */
    <T> T getObject(final String key, final Class<T> clazz);

    /**
     * Search for an object with matching <code>key</code> and <code>clazz</code> type from the local repository. This
     * method will only return single object or null if no object match the key.
     * <p/>
     * Internally, this method will pull unique searchable fields from the resource and then create the query for that
     * fields and passing the key as the value. If the resource doesn't specify unique searchable field, all
     * searchable fields for that resource will be used for searching.
     *
     * @param key      the key to distinguish the object
     * @param resource the resource object which will describe how to index the json resource to lucene.
     * @param <T>      generic type of the object
     * @return object with matching key and clazz or null
     */
    <T> T getObject(final String key, final Resource resource);

    /**
     * Search for objects with matching <code>searchString</code> and <code>clazz</code> type from the local repository.
     * This method will return list of all matching object or empty list if no object match the search string.
     *
     * @param clazz        the expected return type of the object
     * @param searchString the search string to limit the number of returned object
     * @param <T>          generic type of the object
     * @return list of all object with matching <code>searchString</code> and <code>clazz</code> or empty list
     */
    <T> List<T> getObjects(final String searchString, final Class<T> clazz);

    /**
     * Remove an object based on the resource from the local repository. The method will determine if there's unique
     * <code>object</code> in the local repository and then remove it. This method will return null if there's no
     * object in the local repository match the object passed to this method.
     * <p/>
     * Internally, this method will serialize the object to json and then using the resource object, the method will
     * recreate unique key query to find the entry in the local lucene repository. If no unique searchable field is
     * specified in the resource configuration, this method will use all searchable index to find the entry.
     *
     * @param object   the object to be removed if the object exists.
     * @param resource the resource object which will describe how to index the json resource to lucene.
     * @param <T>      generic type of the object
     * @return removed object or null if no object was removed.
     */
    <T> T invalidate(final T object, final Resource resource);

    /**
     * Create an instance of object in the local repository.
     * <p/>
     * Internally, this method will serialize the object and using the resource configuration to create an entry in
     * the lucene local repository.
     *
     * @param object   the object to be created
     * @param resource the resource object which will describe how to index the json resource to lucene.
     * @param <T>      generic type of the object
     * @return the object that was created
     */
    <T> T createObject(final T object, final Resource resource);

    /**
     * Update an instance of object in the local repository.
     * <p/>
     * Internally, this method will perform invalidation of the object and then recreate the object in the local lucene
     * repository. If the changes are performed on the unique searchable field, this method will end up creating a new
     * entry in the lucene local repository.
     *
     * @param object   the object to be updated
     * @param resource the resource object which will describe how to index the json resource to lucene.
     * @param <T>      generic type of the object
     * @return the object that was updated
     */
    <T> T updateObject(final T object, final Resource resource);

}
