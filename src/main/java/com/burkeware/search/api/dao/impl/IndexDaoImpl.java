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
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.UUID;

import com.burkeware.search.api.JsonLuceneConfig;
import com.burkeware.search.api.dao.IndexDao;
import com.burkeware.search.api.provider.SearchProvider;
import com.google.inject.Inject;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

public class IndexDaoImpl implements IndexDao {

    private final SearchProvider<IndexWriter> writerProvider;

    @Inject
    public IndexDaoImpl(final SearchProvider<IndexWriter> writerProvider) {
        this.writerProvider = writerProvider;
    }

    /**
     * Write a single json representation as a single document entry inside Lucene index.
     *
     * @param config     the configuration to transform json to lucene document
     * @param writer     the lucene's index writer
     * @param jsonObject the json object to be written to the index
     * @throws IOException when writing document failed
     */
    private void updateIndexInternal(final JsonLuceneConfig config, final IndexWriter writer, final Object jsonObject) throws IOException {
        Document document = new Document();
        document.add(new Field("_json", jsonObject.toString(), Field.Store.YES, Field.Index.NO));
        document.add(new Field("_uuid", UUID.randomUUID().toString(), Field.Store.YES, Field.Index.NO));

        Map<String, String> mappings = config.getMappings();
        for (Map.Entry<String, String> entry : mappings.entrySet()) {
            Object value = JsonPath.read(jsonObject, entry.getValue());
            document.add(new Field(entry.getKey(), String.valueOf(value), Field.Store.YES, Field.Index.ANALYZED));
        }
        writer.addDocument(document);
    }

    /**
     * Read json payload from the input stream.
     *
     * @param inputStream the input stream where the json payload will be read.
     * @return the json payload
     * @throws IOException when reading payload failed.
     */
    private String readJsonPayload(final InputStream inputStream) throws IOException {
        String line;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = reader.readLine()) != null)
                builder.append(line);
        } finally {
            if (reader != null)
                reader.close();
        }
        return builder.toString();
    }

    /**
     * Dao method to update the index with entries from the input stream.
     *
     * @param config      the j2l configuration file to be used to map the json payload to lucene document
     * @param inputStream input stream where json entries will be read
     */
    @Override
    public void updateIndex(final JsonLuceneConfig config, final InputStream inputStream) throws IOException {
        IndexWriter writer = null;
        try {
            writer = writerProvider.get();
            String json = readJsonPayload(inputStream);
            Object jsonObject = JsonPath.read(json, config.getRepresentation());
            if (jsonObject instanceof JSONArray) {
                JSONArray array = (JSONArray) jsonObject;
                for (Object element : array)
                    updateIndexInternal(config, writer, element);
            } else if (jsonObject instanceof JSONObject)
                updateIndexInternal(config, writer, jsonObject);
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    /**
     * Dao method to update the index with entries from the file.
     *
     * @param config    the j2l configuration file to be used to map the json payload to lucene document
     * @param directory directory information where json entries will be read
     */
    @Override
    public void updateIndex(final JsonLuceneConfig config, final File directory) throws IOException {
        IndexWriter writer = null;
        try {
            writer = writerProvider.get();
            for (File corpusFile : directory.listFiles()) {
                String json = readJsonPayload(new FileInputStream(corpusFile));
                Object jsonObject = JsonPath.read(json, config.getRepresentation());
                if (jsonObject instanceof JSONArray) {
                    JSONArray array = (JSONArray) jsonObject;
                    for (Object element : array)
                        updateIndexInternal(config, writer, element);
                } else if (jsonObject instanceof JSONObject)
                    updateIndexInternal(config, writer, jsonObject);
            }
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}
