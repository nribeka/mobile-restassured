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

import com.burkeware.search.api.factory.DefaultAlgorithmFactory;
import com.burkeware.search.api.factory.DefaultFigureOuterFactory;
import com.burkeware.search.api.factory.DefaultResourceFactory;
import com.burkeware.search.api.factory.internal.Factory;
import com.burkeware.search.api.module.FactoryModule;
import com.burkeware.search.api.module.SearchModule;
import com.burkeware.search.api.resource.ObjectResource;
import com.burkeware.search.api.resource.internal.Resource;
import com.burkeware.search.api.resource.registry.Properties;
import com.burkeware.search.api.resource.registry.PropertiesRegistry;
import com.burkeware.search.api.sample.PatientCohortAlgorithm;
import com.burkeware.search.api.sample.PatientFigureOuter;
import com.burkeware.search.api.serialization.Algorithm;
import com.burkeware.search.api.uri.FigureOuter;
import com.burkeware.search.api.util.ResourceUtil;
import com.google.inject.Guice;
import com.google.inject.Injector;
import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import java.io.File;
import java.net.URL;

public class RestAssuredServiceTest {

    private static final Log log = LogFactory.getLog(RestAssuredServiceTest.class);

    /**
     * @verifies add any resources inside the file
     * @see RestAssuredService#addResources(java.io.File)
     */
    @Test
    public void addResources_shouldAddAnyResourcesInsideTheFile() throws Exception {

        Injector injector = Guice.createInjector(new FactoryModule(), new SearchModule());

        URL j2l = this.getClass().getResource("j2l/cohort-template.j2l");
        Properties properties = ResourceUtil.loadResourceConfig(new File(j2l.getPath()));

        String resourceName = properties.getEntryValue(Resource.RESOURCE_NAME);

        PropertiesRegistry propertiesRegistry = injector.getInstance(PropertiesRegistry.class);
        Assert.assertNotNull(propertiesRegistry);

        propertiesRegistry.putEntry(resourceName, properties);

        Factory<Resource> resourceFactory = injector.getInstance(DefaultResourceFactory.class);
        resourceFactory.registerImplementation(resourceName, ObjectResource.class);

        Factory<Algorithm> algorithmFactory = injector.getInstance(DefaultAlgorithmFactory.class);
        algorithmFactory.registerImplementation(resourceName, PatientCohortAlgorithm.class);

        Factory<FigureOuter> figureOuterFactory = injector.getInstance(DefaultFigureOuterFactory.class);
        figureOuterFactory.registerImplementation(resourceName, PatientFigureOuter.class);

        Resource resource = resourceFactory.createImplementation(properties.getEntryValue(Resource.RESOURCE_NAME));

        Assert.assertNotNull(resource);
        log.info("Created new resource with name: " + resource.getName());
    }
}
