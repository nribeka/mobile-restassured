/*
 * ========================================================================
 *
 * Codehaus CARGO, copyright 2004-2011 Vincent Massol.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ========================================================================
 */
package com.burkeware.search.api.factory.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shamelessly copied with modification from AbstractGenericHintFactory from Cargo project.
 */
public abstract class BaseFactory<T> implements Factory<T> {

    private Map<String, Class<? extends T>> mappings;

    /**
     * The implementation of the base factory.
     */
    protected BaseFactory() {
        this.mappings = new HashMap<String, Class<? extends T>>();
    }

    /**
     * @param key the key associated with the implementation class
     * @return true if the mapping is already registered or false otherwise
     */
    @Override
    public boolean hasMapping(final String key) {
        return getMappings().containsKey(key);
    }

    /**
     * @param key the key associated with the implementation class to return
     * @return the implementation class
     */
    @Override
    public Class<? extends T> getMapping(final String key) {
        return getMappings().get(key);
    }

    /**
     * @return the mappings indexed using the key
     */
    @Override
    public Map<String, Class<? extends T>> getMappings() {
        return this.mappings;
    }

    /**
     * Register an implementation class for a given key.
     *
     * @param key                 the key under which to register the implementation class
     * @param implementationClass the implementation class to register
     */
    @Override
    public void registerImplementation(final String key, final Class<? extends T> implementationClass) {
        getMappings().put(key, implementationClass);
    }

    /**
     * @return the keys that have been registered for this factory
     */
    @Override
    public List<String> getKeys() {
        List<String> hints = new ArrayList<String>();
        for (Map.Entry<String, Class<? extends T>> mapping : getMappings().entrySet()) {
            String key = mapping.getKey();
            hints.add(key);
        }
        return hints;
    }
}
