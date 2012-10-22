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

import com.burkeware.search.api.module.SearchModule;
import com.burkeware.search.api.provider.SearchProvider;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.junit.Test;

public class GuiceInjectionTest {

    private static final Log log = LogFactory.getLog(GuiceInjectionTest.class);

    @Test
    public void registerPatientAlgorithm() throws Exception {

        Injector injector = Guice.createInjector(new SearchModule());

        Analyzer analyzer = injector.getInstance(Analyzer.class);
        Assert.assertEquals(StandardAnalyzer.class, analyzer.getClass());
        log.info("Injected Analyzer class: " + analyzer.getClass().getName());

        Key<SearchProvider<Directory>> directoryKey = Key.get(new TypeLiteral<SearchProvider<Directory>>() {
        });
        SearchProvider<Directory> directoryProvider = injector.getInstance(directoryKey);
        Directory directory = directoryProvider.get();
        Assert.assertEquals(NIOFSDirectory.class, directory.getClass());
        log.info("Injected Directory class: " + directory.getClass().getName());

        Key<SearchProvider<IndexReader>> indexReaderKey = Key.get(new TypeLiteral<SearchProvider<IndexReader>>() {
        });
        SearchProvider<IndexReader> indexReaderProvider = injector.getInstance(indexReaderKey);
        IndexReader indexReader = indexReaderProvider.get();
        Assert.assertTrue(IndexReader.class.isAssignableFrom(indexReader.getClass()));
        log.info("Injected IndexReader class: " + indexReader.getClass().getName());

        Key<SearchProvider<IndexSearcher>> indexSearcherKey = Key.get(new TypeLiteral<SearchProvider<IndexSearcher>>() {
        });
        SearchProvider<IndexSearcher> indexSearcherProvider = injector.getInstance(indexSearcherKey);
        IndexSearcher indexSearcher = indexSearcherProvider.get();
        Assert.assertTrue(IndexSearcher.class.isAssignableFrom(indexSearcher.getClass()));
        log.info("Injected IndexSearcher class: " + indexSearcher.getClass().getName());

        Key<SearchProvider<IndexWriter>> indexWriterKey = Key.get(new TypeLiteral<SearchProvider<IndexWriter>>() {
        });
        SearchProvider<IndexWriter> indexWriterProvider = injector.getInstance(indexWriterKey);
        IndexWriter indexWriter = indexWriterProvider.get();
        Assert.assertTrue(IndexWriter.class.isAssignableFrom(indexWriter.getClass()));
        log.info("Injected IndexWriter class: " + indexWriter.getClass().getName());
    }

}
