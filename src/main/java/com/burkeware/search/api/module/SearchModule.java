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
import com.burkeware.search.api.internal.lucene.DefaultIndexer;
import com.burkeware.search.api.internal.lucene.Indexer;
import com.burkeware.search.api.internal.provider.AnalyzerProvider;
import com.burkeware.search.api.internal.provider.DirectoryProvider;
import com.burkeware.search.api.internal.provider.IndexReaderProvider;
import com.burkeware.search.api.internal.provider.IndexSearcherProvider;
import com.burkeware.search.api.internal.provider.IndexWriterProvider;
import com.burkeware.search.api.internal.provider.SearchProvider;
import com.burkeware.search.api.service.RestAssuredServiceImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.throwingproviders.ThrowingProviderBinder;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

public class SearchModule extends AbstractModule {

    /**
     * Configures a {@link com.google.inject.Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        bind(RestAssuredService.class).to(RestAssuredServiceImpl.class).in(Singleton.class);
        bind(Indexer.class).to(DefaultIndexer.class).in(Singleton.class);

        bind(Version.class).toInstance(Version.LUCENE_36);
        bind(Analyzer.class).toProvider(AnalyzerProvider.class);
        ThrowingProviderBinder.create(binder())
                .bind(SearchProvider.class, Directory.class)
                .to(DirectoryProvider.class)
                .in(Singleton.class);
        ThrowingProviderBinder.create(binder())
                .bind(SearchProvider.class, IndexReader.class)
                .to(IndexReaderProvider.class);
        ThrowingProviderBinder.create(binder())
                .bind(SearchProvider.class, IndexSearcher.class)
                .to(IndexSearcherProvider.class);
        ThrowingProviderBinder.create(binder())
                .bind(SearchProvider.class, IndexWriter.class)
                .to(IndexWriterProvider.class);
    }
}
