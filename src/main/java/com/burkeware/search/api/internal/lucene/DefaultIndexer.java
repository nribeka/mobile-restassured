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
package com.burkeware.search.api.internal.lucene;

import com.burkeware.search.api.internal.provider.IndexSearcherProvider;
import com.burkeware.search.api.internal.provider.IndexWriterProvider;
import com.burkeware.search.api.resource.Resource;
import com.burkeware.search.api.resource.SearchableField;
import com.burkeware.search.api.serialization.Algorithm;
import com.burkeware.search.api.util.CollectionUtil;
import com.burkeware.search.api.util.IOUtil;
import com.burkeware.search.api.util.StringUtil;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class DefaultIndexer implements Indexer {

    private final QueryParser parser;

    private final IndexWriterProvider writerProvider;

    private final IndexSearcherProvider searcherProvider;

    private static final String DEFAULT_FIELD_JSON = "_json";

    private static final String DEFAULT_FIELD_CLASS = "_class";

    private static final String DEFAULT_FIELD_RESOURCE = "_resource";

    private static final Integer DEFAULT_MAX_DOCUMENTS = 20;

    @Inject
    public DefaultIndexer(final IndexWriterProvider writerProvider, final IndexSearcherProvider searcherProvider,
                          final @Named("configuration.lucene.document.key") String defaultField,
                          final Version version, final Analyzer analyzer) {
        this.writerProvider = writerProvider;
        this.searcherProvider = searcherProvider;
        this.parser = new QueryParser(version, defaultField, analyzer);
    }

    /**
     * Create a single term lucene query. The value for the query will be surrounded with single quote.
     *
     * @param field the field on which the query should be performed.
     * @param value the value for the field
     * @return the valid lucene query for single term.
     */
    private String createQuery(final String field, final String value) {
        return field + ":" + StringUtil.quote(value);
    }

    /**
     * Create lucene query string based on the searchable field name and value. The values for the searchable field
     * will be retrieved from the <code>jsonObject</code>. This method will try to create a unique query in the case
     * where a searchable field is marked as unique. Otherwise the method will create a query string using all
     * available searchable fields.
     *
     * @param jsonObject       the json object from which the value for each field can be retrieved from.
     * @param searchableFields the searchable fields definition
     * @return query string which could be either a unique or full searchable field based query.
     */
    private String createJsonObjectQuery(final Object jsonObject, final List<SearchableField> searchableFields) {
        boolean uniqueExists = false;
        StringBuilder fullQuery = new StringBuilder();
        StringBuilder uniqueQuery = new StringBuilder();
        for (SearchableField searchableField : searchableFields) {
            String value = JsonPath.read(jsonObject, searchableField.getExpression());
            String query = createQuery(searchableField.getName(), StringUtil.quote(value));

            if (searchableField.isUnique()) {
                uniqueExists = true;
                if (!StringUtil.isBlank(uniqueQuery.toString()))
                    uniqueQuery.append(" AND ");
                uniqueQuery.append(query);
            }

            // only create the full query if we haven't found any unique key in the searchable fields.
            if (!uniqueExists) {
                if (!StringUtil.isBlank(fullQuery.toString()))
                    fullQuery.append(" AND ");
                fullQuery.append(query);
            }
        }

        if (uniqueExists)
            return uniqueQuery.toString();
        else
            return fullQuery.toString();
    }

    /**
     * Create query for a resource and matching the jsonObject passed to this method. Calling this method will ensure
     * our query will restrict the returned to object to the correct resource id.
     * <p/>
     * Example use case: please retrieve all patients data. This should be performed through query by object type
     * because the caller is interested only in the type of object, irregardless of the resources from which the objects
     * are coming from. To limit them by resource, the query should include the resource information (obviously) :)
     *
     * @param resource the resource for which the query is based on
     * @return the base query for a resource
     */
    private String createJsonObjectQuery(final Object jsonObject, final Resource resource) {
        StringBuilder builder = new StringBuilder();
        builder.append(createQuery(DEFAULT_FIELD_CLASS, resource.getResourceObject().getName()));
        builder.append(" AND ");
        builder.append(createQuery(DEFAULT_FIELD_RESOURCE, resource.getName()));
        builder.append(" AND ");
        builder.append(createJsonObjectQuery(jsonObject, resource.getSearchableFields()));
        return builder.toString();
    }

    /**
     * Search the local lucene repository for documents with similar information with information inside the
     * <code>query</code>. Search can return multiple documents with similar information or empty list when no
     * document have similar information with the <code>query</code>.
     *
     * @param query    the lucene query.
     * @param searcher the searcher object which will perform the search.
     * @return objects with similar information with the query.
     * @throws IOException when the search encounter error.
     */
    private List<Document> findDocuments(final Query query, final IndexSearcher searcher) throws IOException {
        TopDocs docs = searcher.search(query, DEFAULT_MAX_DOCUMENTS);
        ScoreDoc[] hits = docs.scoreDocs;

        List<Document> documents = new ArrayList<Document>();
        for (ScoreDoc hit : hits)
            documents.add(searcher.doc(hit.doc));
        return documents;
    }

    /**
     * Write json representation of a single object as a single document entry inside Lucene index.
     *
     * @param jsonObject the json object to be written to the index
     * @param resource   the configuration to transform json to lucene document
     * @param writer     the lucene index writer
     * @throws java.io.IOException when writing document failed
     */
    private void writeObject(final Object jsonObject, final Resource resource, final IndexWriter writer)
            throws IOException {

        Document document = new Document();
        document.add(new Field(DEFAULT_FIELD_JSON, jsonObject.toString(), Field.Store.YES, Field.Index.NO));
        document.add(new Field(DEFAULT_FIELD_CLASS, resource.getResourceObject().getName(), Field.Store.YES,
                Field.Index.NOT_ANALYZED_NO_NORMS));
        document.add(new Field(DEFAULT_FIELD_RESOURCE, resource.getName(), Field.Store.YES,
                Field.Index.NOT_ANALYZED_NO_NORMS));

        for (SearchableField searchableField : resource.getSearchableFields()) {
            Object value = JsonPath.read(jsonObject, searchableField.getExpression());
            document.add(new Field(searchableField.getName(), String.valueOf(value), Field.Store.YES,
                    Field.Index.ANALYZED_NO_NORMS));
        }
        writer.addDocument(document);
    }

    private void updateIndexInternal(final Object jsonObject, final Resource resource,
                                     final IndexSearcher indexSearcher, final IndexWriter indexWriter)
            throws ParseException, IOException {
        Query query = parser.parse(createJsonObjectQuery(jsonObject, resource));
        List<Document> documents = findDocuments(query, indexSearcher);
        // only write new object, prevent duplicates
        if (CollectionUtil.isEmpty(documents))
            writeObject(jsonObject, resource, indexWriter);
    }

    @Override
    public void updateIndex(final Resource resource, final InputStream inputStream)
            throws ParseException, IOException {
        IndexSearcher indexSearcher = null;
        IndexWriter indexWriter = null;
        try {
            indexWriter = writerProvider.get();
            indexSearcher = searcherProvider.get();
            String json = IOUtil.readAsString(inputStream);
            Object jsonObject = JsonPath.read(json, resource.getRootNode());
            if (jsonObject instanceof JSONArray) {
                JSONArray array = (JSONArray) jsonObject;
                for (Object element : array)
                    updateIndexInternal(element, resource, indexSearcher, indexWriter);
            } else if (jsonObject instanceof JSONObject) {
                updateIndexInternal(jsonObject, resource, indexSearcher, indexWriter);
            }
        } finally {
            if (indexWriter != null)
                indexWriter.close();
            if (indexSearcher != null)
                indexSearcher.close();
        }
    }

    @Override
    public void updateIndex(final Resource resource, final Reader reader)
            throws ParseException, IOException {
        IndexSearcher indexSearcher = null;
        IndexWriter indexWriter = null;
        try {
            indexWriter = writerProvider.get();
            indexSearcher = searcherProvider.get();
            String json = IOUtil.readAsString(reader);
            Object jsonObject = JsonPath.read(json, resource.getRootNode());
            if (jsonObject instanceof JSONArray) {
                JSONArray array = (JSONArray) jsonObject;
                for (Object element : array)
                    updateIndexInternal(element, resource, indexSearcher, indexWriter);
            } else if (jsonObject instanceof JSONObject) {
                updateIndexInternal(jsonObject, resource, indexSearcher, indexWriter);
            }
        } finally {
            if (indexWriter != null)
                indexWriter.close();
            if (indexSearcher != null)
                indexSearcher.close();
        }
    }

    private String createKeyQuery(final String key, final Resource resource) {
        StringBuilder builder = new StringBuilder();
        builder.append(createQuery(DEFAULT_FIELD_CLASS, resource.getResourceObject().getName()));
        builder.append(" AND ");
        builder.append(createQuery(DEFAULT_FIELD_RESOURCE, resource.getName()));
        builder.append(" AND ");
        builder.append(key);
        return builder.toString();
    }

    @Override
    public Object getObject(final String key, final Resource resource) throws ParseException, IOException {
        Object object = null;
        IndexSearcher indexSearcher = null;
        try {
            indexSearcher = searcherProvider.get();
            Query query = parser.parse(createKeyQuery(key, resource));
            List<Document> documents = findDocuments(query, indexSearcher);
            if (!CollectionUtil.isEmpty(documents)) {
                if (documents.size() > 1)
                    throw new IOException("Unable to uniquely identify an object in the repository.");

                for (Document document : documents) {
                    String json = document.get("_json");
                    Algorithm algorithm = resource.getAlgorithm();
                    object = algorithm.serialize(json);
                }

            }
        } finally {
            if (indexSearcher != null)
                indexSearcher.close();
        }
        return object;
    }
}
