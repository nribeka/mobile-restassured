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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.burkeware.search.api.JsonLuceneConfig;
import com.burkeware.search.api.factory.SearchObjectFactory;
import com.burkeware.search.api.module.SearchModule;
import com.burkeware.search.api.provider.SearchProvider;
import com.burkeware.search.api.sample.Patient;
import com.burkeware.search.api.sample.PatientAlgorithm;
import com.burkeware.search.api.util.JsonLuceneUtil;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SearchServiceTest {
    private static final Log log = LogFactory.getLog(SearchServiceTest.class);

    @BeforeClass
    public static void prepareIndex() throws Exception {
        Injector injector = Guice.createInjector(new SearchModule());

        URL j2l = GuiceInjectionTest.class.getResource("j2l/json-template.j2l");
        JsonLuceneConfig config = JsonLuceneUtil.load(new File(j2l.getPath()));
        URL corpus = GuiceInjectionTest.class.getResource("corpus");
        File corpusDirectory = new File(corpus.getPath());

        Key<SearchProvider<IndexWriter>> indexWriterKey = Key.get(new TypeLiteral<SearchProvider<IndexWriter>>() { });
        SearchProvider<IndexWriter> indexWriterProvider = injector.getInstance(indexWriterKey);
        IndexWriter indexWriter = indexWriterProvider.get();

        IndexService indexService = injector.getInstance(IndexService.class);
        indexService.updateIndex(config, corpusDirectory);
    }

    /**
     * @verifies return object with matching key
     * @see SearchService#getObject(Class, String)
     */
    @Test
    public void getObject_shouldReturnObjectWithMatchingKey() throws Exception {
        Injector injector = Guice.createInjector(new SearchModule());
        // TODO: this should be done inside a bootstrap method to start the search service
        ConfigService configService = injector.getInstance(ConfigService.class);
        configService.registerAlgorithm(Patient.class, new PatientAlgorithm());

        SearchService searchService = injector.getInstance(SearchService.class);
        Patient patient = searchService.getObject(Patient.class, "Testarius0 Ambote Indakasi");
        Assert.assertEquals(Patient.class, patient.getClass());
        Assert.assertEquals(patient.getName(), "Testarius0 Ambote Indakasi");
        log.info("Patient: " + patient.getName() + " with UUID: " + patient.getUuid());
    }

    /**
     * @verifies return objecst with matching search term
     * @see SearchService#getObjects(Class, String)
     */
    @Test
    public void getObjects_shouldReturnObjecstWithMatchingSearchTerm() throws Exception {
        Injector injector = Guice.createInjector(new SearchModule());

        ConfigService configService = injector.getInstance(ConfigService.class);
        configService.registerAlgorithm(Patient.class, new PatientAlgorithm());

        SearchService searchService = injector.getInstance(SearchService.class);
        List<Patient> patients = searchService.getObjects(Patient.class, "Testarius1?");
        for (Patient patient : patients) {
            Assert.assertEquals(Patient.class, patient.getClass());
            Assert.assertTrue(patient.getName().startsWith("Testarius1"));
            log.info("Patient: " + patient.getName() + " with UUID: " + patient.getUuid());
        }
    }
}
