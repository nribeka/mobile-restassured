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

import com.burkeware.search.api.module.FactoryModule;
import com.burkeware.search.api.module.SearchModule;
import com.burkeware.search.api.module.UnitTestModule;
import com.burkeware.search.api.resource.Resource;
import com.burkeware.search.api.sample.algorithm.CohortAlgorithm;
import com.burkeware.search.api.sample.algorithm.CohortMemberAlgorithm;
import com.burkeware.search.api.sample.algorithm.PatientAlgorithm;
import com.burkeware.search.api.sample.domain.Cohort;
import com.burkeware.search.api.sample.domain.Patient;
import com.burkeware.search.api.sample.resolver.CohortMemberResolver;
import com.burkeware.search.api.sample.resolver.CohortResolver;
import com.burkeware.search.api.sample.resolver.PatientResolver;
import com.burkeware.search.api.util.StringUtil;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.List;

public class RestAssuredServiceTest {

    private static final Log log = LogFactory.getLog(RestAssuredServiceTest.class);

    /**
     * @verifies index data from the rest resource
     * @see RestAssuredService#loadObjects(String, com.burkeware.search.api.resource.Resource)
     */
    @Test
    public void loadObjects_shouldIndexDataFromTheRestResource() throws Exception {
        URL j2l = RestAssuredService.class.getResource("sample/j2l");
        URL object = RestAssuredService.class.getResource("sample/corpus");

        Injector injector = Guice.createInjector(new SearchModule(), new FactoryModule(), new UnitTestModule());

        Context context = injector.getInstance(Context.class);
        context.registerAlgorithm(PatientAlgorithm.class, CohortAlgorithm.class, CohortMemberAlgorithm.class);
        context.registerResolver(PatientResolver.class, CohortResolver.class, CohortMemberResolver.class);
        context.registerObject(Patient.class, Cohort.class);
        context.registerResources(new File(j2l.getPath()));

        Resource resource = context.getResource("Patient");
        Assert.assertNotNull(resource);

        RestAssuredService service = injector.getInstance(RestAssuredService.class);
        service.loadObjects("Male", resource, new File(object.getPath()));

        List<Patient> patients = service.getObjects("name:Test*", Patient.class);
        Assert.assertNotNull(patients);
        Assert.assertTrue(patients.size() > 0);
        for (Patient patient : patients) {
            Assert.assertNotNull(patient);
            Assert.assertEquals(Patient.class, patient.getClass());
        }

        Patient patient = service.getObject("name: " + StringUtil.quote("Testarius Ambote Indakasi"), Patient.class);
        Assert.assertNotNull(patient);
        Assert.assertEquals(Patient.class, patient.getClass());
    }
}
