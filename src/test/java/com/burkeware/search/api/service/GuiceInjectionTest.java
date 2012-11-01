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

package com.burkeware.search.api.service;

import com.burkeware.search.api.module.FactoryModule;
import com.burkeware.search.api.registry.Registry;
import com.burkeware.search.api.resource.Resource;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class GuiceInjectionTest {

    private static final Log log = LogFactory.getLog(GuiceInjectionTest.class);

    @Test
    public void registerPatientAlgorithm() throws Exception {

        Injector injector = Guice.createInjector(new FactoryModule());

        String name = injector.getInstance(Key.get(String.class, Names.named("AlgorithmFactory.name")));
        log.info("Value for AlgorithmFactory name is: " + name);

        Registry registry = injector.getInstance(Key.get(new TypeLiteral<Registry<String, Resource>>() {},
                Names.named("ResourceRegistry")));
        log.info("Registry class" + registry);
        registry.putEntry("Example", "Data");
        log.info("Registry class" + registry.getEntryValue("Example"));

        registry = injector.getInstance(Key.get(new TypeLiteral<Registry<String, Resource>>() {},
                Names.named("ResourceRegistry")));
        log.info("Registry class" + registry.getEntryValue("Example"));
    }

}
