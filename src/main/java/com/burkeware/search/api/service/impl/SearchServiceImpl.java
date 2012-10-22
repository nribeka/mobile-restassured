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

package com.burkeware.search.api.service.impl;

import java.util.List;

import com.google.inject.Inject;
import com.burkeware.search.api.dao.SearchDao;
import com.burkeware.search.api.service.SearchService;

public class SearchServiceImpl implements SearchService {

    private SearchDao dao;

    @Inject
    public SearchServiceImpl(final SearchDao dao) {
        this.dao = dao;
    }

    /**
     * Service method to get an object of certain type based on the key.
     *
     * @param tClass the expected type of the object
     * @param key    the key to uniquely identify the object
     * @param <T>    the object type
     * @return the object uniquely identified by the key
     */
    @Override
    public <T> T getObject(final Class<T> tClass, final String key) {
        return dao.getObject(tClass, key);
    }

    /**
     * Service method to search objects of certain type based on certain query term.
     *
     * @param tClass       the expected type of the object
     * @param searchString the query term
     * @param <T>          the type of the object
     * @return list of all matching objects
     */
    @Override
    public <T> List<T> getObjects(final Class<T> tClass, final String searchString) {
        return dao.getObjects(tClass, searchString);
    }

    /**
     * Invalidate entry in the index.
     *
     * @param tClass the expected type of the object
     * @param key    the key to uniquely identify the object
     * @param <T>    the type of the object
     * @return the object that was invalidated by the method call
     */
    @Override
    public <T> T invalidate(final Class<T> tClass, final String key) {
        return dao.invalidate(tClass, key);
    }
}