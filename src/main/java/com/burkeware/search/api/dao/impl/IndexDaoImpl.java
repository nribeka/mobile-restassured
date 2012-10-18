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

package com.burkeware.search.api.dao.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

import com.burkeware.search.api.JsonLuceneConfig;
import com.burkeware.search.api.dao.IndexDao;
import com.burkeware.search.api.provider.SearchProvider;
import com.google.inject.Inject;
import com.jayway.jsonpath.JsonPath;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;

public class IndexDaoImpl implements IndexDao {

    private final SearchProvider<IndexWriter> writerProvider;

    @Inject
    public IndexDaoImpl(final SearchProvider<IndexWriter> writerProvider) {
        this.writerProvider = writerProvider;
    }

    /**
     * Dao method to update the index with entries from the input stream.
     *
     * @param config      the j2l configuration file to be used to map the json payload to lucene document
     * @param inputStream input stream where json entries will be read
     */
    @Override
    public void updateIndex(final JsonLuceneConfig config, final InputStream inputStream) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Dao method to update the index with entries from the file.
     *
     * @param config    the j2l configuration file to be used to map the json payload to lucene document
     * @param directory directory information where json entries will be read
     */
    @Override
    public void updateIndex(final JsonLuceneConfig config, final File directory) {
        try {
            File[] files = directory.listFiles();
            if (files != null) {
                IndexWriter writer = writerProvider.get();
                for (File corpusFile : files) {
                    BufferedReader reader = new BufferedReader(new FileReader(corpusFile));

                    String line;
                    StringBuilder builder = new StringBuilder();
                    while ((line = reader.readLine()) != null)
                        builder.append(line);
                    reader.close();

                    Document document = new Document();
                    String json = builder.toString();
                    document.add(new Field("_json", json, Field.Store.YES, Field.Index.NO));
                    document.add(new Field("_uuid", UUID.randomUUID().toString(), Field.Store.YES, Field.Index.NO));

                    Map<String, String> mappings = config.getMappings();
                    for (Map.Entry<String, String> entry : mappings.entrySet()) {
                        Object value = JsonPath.read(json, entry.getValue());
                        document.add(new Field(entry.getKey(), String.valueOf(value), Field.Store.YES, Field.Index.ANALYZED));
                    }
                    writer.addDocument(document);
                }
                writer.close();
            }
        } catch (IOException e) {
            // Ignoring again :)
        }
    }

    /**
     * Dao method to update the index with entries from the url.
     *
     * @param config the j2l configuration file to be used to map the json payload to lucene document
     * @param url    the url where json entries will be read
     */
    @Override
    public void updateIndex(final JsonLuceneConfig config, final URL url) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
