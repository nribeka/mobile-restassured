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

package com.burkeware.search.api.provider;

import java.io.IOException;

import com.google.inject.Inject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

public class IndexWriterProvider implements SearchProvider<IndexWriter> {

    private SearchProvider<Directory> directoryProvider;

    private Analyzer analyzer;

    @Inject
    public IndexWriterProvider(final SearchProvider<Directory> directoryProvider, final Analyzer analyzer) {
        this.directoryProvider = directoryProvider;
        this.analyzer = analyzer;
    }

    @Override
    public IndexWriter get() throws IOException {
        Directory directory = directoryProvider.get();
        IndexWriterConfig  config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
        return new IndexWriter(directory, config);
    }
}
