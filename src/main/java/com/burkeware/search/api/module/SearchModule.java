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

import java.io.File;
import java.io.IOException;

import com.burkeware.search.api.dao.IndexDao;
import com.burkeware.search.api.dao.impl.IndexDaoImpl;
import com.burkeware.search.api.provider.AnalyzerProvider;
import com.burkeware.search.api.provider.DirectoryProvider;
import com.burkeware.search.api.provider.IndexReaderProvider;
import com.burkeware.search.api.provider.IndexSearcherProvider;
import com.burkeware.search.api.provider.IndexWriterProvider;
import com.burkeware.search.api.provider.SearchProvider;
import com.burkeware.search.api.service.ConfigService;
import com.burkeware.search.api.service.IndexService;
import com.burkeware.search.api.service.impl.ConfigServiceImpl;
import com.burkeware.search.api.service.impl.IndexServiceImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.burkeware.search.api.dao.SearchDao;
import com.burkeware.search.api.dao.impl.SearchDaoImpl;
import com.burkeware.search.api.service.SearchService;
import com.burkeware.search.api.service.impl.SearchServiceImpl;
import com.google.inject.name.Names;
import com.google.inject.throwingproviders.CheckedProvider;
import com.google.inject.throwingproviders.CheckedProvides;
import com.google.inject.throwingproviders.ThrowingProviderBinder;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class SearchModule extends AbstractModule {

    /**
     * Configures a {@link com.google.inject.Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        // TODO: hard coding this to tmp folder, should be read from configuration file
        bind(String.class).annotatedWith(Names.named("configuration.lucene.directory")).toInstance("/tmp/lucene");
        bind(String.class).annotatedWith(Names.named("configuration.lucene.document.key")).toInstance("name");

        bind(SearchService.class).to(SearchServiceImpl.class).in(Singleton.class);
        bind(SearchDao.class).to(SearchDaoImpl.class).in(Singleton.class);

        bind(ConfigService.class).to(ConfigServiceImpl.class).in(Singleton.class);

        bind(IndexService.class).to(IndexServiceImpl.class).in(Singleton.class);
        bind(IndexDao.class).to(IndexDaoImpl.class).in(Singleton.class);

        bind(Analyzer.class).toProvider(AnalyzerProvider.class);

        ThrowingProviderBinder.create(binder())
                .bind(SearchProvider.class, Directory.class)
                .to(DirectoryProvider.class)
                .in(Singleton.class);

        ThrowingProviderBinder.create(binder())
                .bind(SearchProvider.class, IndexReader.class)
                .to(IndexReaderProvider.class)
                .in(Singleton.class);

        ThrowingProviderBinder.create(binder())
                .bind(SearchProvider.class, IndexSearcher.class)
                .to(IndexSearcherProvider.class)
                .in(Singleton.class);

        ThrowingProviderBinder.create(binder())
                .bind(SearchProvider.class, IndexWriter.class)
                .to(IndexWriterProvider.class)
                .in(Singleton.class);
    }
}
