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

package com.burkeware.search.api.algorithm;

/**
 * Base interface to serialize and deserialize JSON into the correct object representation.
 *
 * @param <T> the object representation
 */
public interface Algorithm<T> {

    /**
     * Implementation of this method will define how the object will be serialized from the JSON representation.
     *
     * @param json the json representation
     * @return the concrete object
     */
    T serialize(final Object json);

    /**
     * Implementation of this method will define how the object will be deserialized into the JSON representation.
     * @param t the object
     * @return the json representation
     */
    Object deserialize(final T t);

}
