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
package com.burkeware.search.api.resource;

import java.util.Arrays;
import java.util.List;

public class ResourceConstants {

    public static final String RESOURCE_NAME = "resourceName";

    public static final String RESOURCE_CLASS = "resourceClass";

    public static final String RESOURCE_OBJECT = "resourceObject";

    public static final String RESOURCE_ROOT_NODE = "rootNode";

    public static final String RESOURCE_UNIQUE_FIELD = "uniqueField";

    public static final String RESOURCE_ALGORITHM_CLASS = "algorithm.class";

    public static final String RESOURCE_URI_CLASS = "uriFigureOuter.class";

    public static final List<String> NON_SEARCHABLE_FIELDS = Arrays.asList(RESOURCE_NAME, RESOURCE_OBJECT,
            RESOURCE_ROOT_NODE, RESOURCE_UNIQUE_FIELD, RESOURCE_ALGORITHM_CLASS, RESOURCE_URI_CLASS);

    public static final List<String> MANDATORY_FIELDS = Arrays.asList(RESOURCE_NAME, RESOURCE_OBJECT,
            RESOURCE_ROOT_NODE, RESOURCE_ALGORITHM_CLASS, RESOURCE_URI_CLASS);
}
