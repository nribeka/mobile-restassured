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

package com.burkeware.search.api.module;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class FactoryModule extends AbstractModule {

    /**
     * Configures a {@link com.google.inject.Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        bind(String.class)
                .annotatedWith(Names.named("AlgorithmFactory.name"))
                .toInstance("Algorithm");
        bind(String.class)
                .annotatedWith(Names.named("FigureOuterFactory.name"))
                .toInstance("FigureOuter");
        bind(String.class)
                .annotatedWith(Names.named("ResourceFactory.name"))
                .toInstance("Resource");

    }
}
