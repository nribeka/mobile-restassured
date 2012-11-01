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

import com.burkeware.search.api.internal.factory.DefaultAlgorithmFactory;
import com.burkeware.search.api.internal.factory.DefaultResolverFactory;
import com.burkeware.search.api.internal.factory.Factory;
import com.burkeware.search.api.registry.DefaultRegistry;
import com.burkeware.search.api.registry.Registry;
import com.burkeware.search.api.resolver.Resolver;
import com.burkeware.search.api.resource.Resource;
import com.burkeware.search.api.serialization.Algorithm;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
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
                .annotatedWith(Names.named("ResolverFactory.name"))
                .toInstance("Resolver");

        bind(new TypeLiteral<Registry<String, Resource>>() {})
                .toInstance(new DefaultRegistry<String, Resource>());

        bind(new TypeLiteral<Registry<String, Registry<String, String>>>() {})
                .toInstance(new DefaultRegistry<String, Registry<String, String>>());

        bind(new TypeLiteral<Registry<String, Class>>() {})
                .toInstance(new DefaultRegistry<String, Class>());

        bind(DefaultRegistry.class).in(Singleton.class);

        bind(new TypeLiteral<Factory<Algorithm>>() {})
                .to(DefaultAlgorithmFactory.class);

        bind(new TypeLiteral<Factory<Resolver>>() {})
                .to(DefaultResolverFactory.class);

        bind(DefaultAlgorithmFactory.class).in(Singleton.class);
        bind(DefaultResolverFactory.class).in(Singleton.class);

    }
}
