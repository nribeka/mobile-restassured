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

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.burkeware.search.api.JsonLuceneConfig;
import com.burkeware.search.api.module.SearchModule;
import com.burkeware.search.api.util.JsonLuceneUtil;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.junit.Assert;
import org.junit.Test;

public class IndexServiceTest {

    /**
     * @verifies update the index using json data from the input stream
     * @see IndexService#updateIndex(com.burkeware.search.api.JsonLuceneConfig, java.io.InputStream)
     */
    @Test
    public void updateIndex_shouldUpdateTheIndexUsingJsonDataFromTheInputStream() throws Exception {
        URL url = new URL("http://localhost:8080/openmrs/ws/rest/v1/cohort/19aaa4f4-ce95-4b91-a305-250818101f98/member?v=full");
        URLConnection connection = url.openConnection();
        String auth = "admin:test";
        String basicAuth = "Basic " + new String(new Base64().encode(auth.getBytes()));
        connection.setRequestProperty("Authorization", basicAuth);

        Injector injector = Guice.createInjector(new SearchModule());

        URL j2l = GuiceInjectionTest.class.getResource("j2l/cohort-template.j2l");
        JsonLuceneConfig config = JsonLuceneUtil.load(new File(j2l.getPath()));

        IndexService indexService = injector.getInstance(IndexService.class);
        indexService.updateIndex(config, connection.getInputStream());
    }
}
