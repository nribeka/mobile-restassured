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

package com.burkeware.search.api.resource.registry;

import com.burkeware.search.api.resource.internal.Registry;
import com.burkeware.search.api.resource.internal.Resource;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class ResourceRegistry implements Registry<String, Resource> {

    private Map<String, Resource> entries;

    public ResourceRegistry() {
        entries = new HashMap<String, Resource>();
    }

    /**
     * Check whether the key is already registered or not
     *
     * @param key the key
     * @return true if the key is already registered, false otherwise
     */
    @Override
    public boolean hasEntry(final String key) {
        return entries.containsKey(key);
    }

    /**
     * Generic method to add a new entry into the registry
     *
     * @param key   the key to the element in the registry
     * @param value the value to be registered
     */
    @Override
    public void putEntry(final String key, final Resource value) {
        getEntries().put(key, value);
    }

    /**
     * @param key the key to value we would like to return
     * @return the registry's value
     */
    @Override
    public Resource getEntryValue(final String key) {
        return getEntries().get(key);
    }

    /**
     * @return the list of all entries in the registry set
     */
    @Override
    public Map<String, Resource> getEntries() {
        return this.entries;
    }
}
