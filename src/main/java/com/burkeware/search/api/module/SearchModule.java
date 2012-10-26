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

import com.burkeware.search.api.RestAssuredService;
import com.burkeware.search.api.factory.DefaultAlgorithmFactory;
import com.burkeware.search.api.factory.DefaultFigureOuterFactory;
import com.burkeware.search.api.factory.DefaultResourceFactory;
import com.burkeware.search.api.factory.internal.Factory;
import com.burkeware.search.api.resource.internal.Registry;
import com.burkeware.search.api.resource.internal.Resource;
import com.burkeware.search.api.resource.registry.Properties;
import com.burkeware.search.api.resource.registry.PropertiesRegistry;
import com.burkeware.search.api.resource.registry.ResourceRegistry;
import com.burkeware.search.api.serialization.Algorithm;
import com.burkeware.search.api.service.RestAssuredServiceImpl;
import com.burkeware.search.api.uri.FigureOuter;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

public class SearchModule extends AbstractModule {

    /**
     * Configures a {@link com.google.inject.Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        bind(RestAssuredService.class).to(RestAssuredServiceImpl.class).in(Singleton.class);

        bind(new TypeLiteral<Registry<String, Resource>>() {

        })
                .annotatedWith(Names.named("ResourceRegistry"))
                .to(ResourceRegistry.class);

        bind(new TypeLiteral<Registry<String, Properties>>() {

        })
                .annotatedWith(Names.named("PropertiesRegistry"))
                .to(PropertiesRegistry.class);

        bind(new TypeLiteral<Factory<Resource>>() {

        })
                .annotatedWith(Names.named("ResourceFactory"))
                .to(DefaultResourceFactory.class);

        bind(new TypeLiteral<Factory<Algorithm>>() {

        })
                .annotatedWith(Names.named("AlgorithmFactory"))
                .to(DefaultAlgorithmFactory.class);

        bind(new TypeLiteral<Factory<FigureOuter>>() {

        })
                .annotatedWith(Names.named("FigureOuterFactory"))
                .to(DefaultFigureOuterFactory.class);
    }
}
