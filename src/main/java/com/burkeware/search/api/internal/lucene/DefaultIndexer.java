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
import com.burkeware.search.api.registry.Registry;
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
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DefaultIndexer implements Indexer {

    private final QueryParser parser;

    private static final String DEFAULT_FIELD_UUID = "_uuid";

    private static final String DEFAULT_FIELD_JSON = "_json";

    private static final String DEFAULT_FIELD_CLASS = "_class";

    private static final String DEFAULT_FIELD_RESOURCE = "_resource";

    private static final Integer DEFAULT_MAX_DOCUMENTS = 20;

    private IndexWriter indexWriter;

    private IndexSearcher indexSearcher;

    private final IndexWriterProvider writerProvider;

    private final IndexSearcherProvider searcherProvider;

    private final Registry<String, Resource> resourceRegistry;

    @Inject
    public DefaultIndexer(final IndexWriterProvider writerProvider, final IndexSearcherProvider searcherProvider,
                          final @Named("configuration.lucene.document.key") String defaultField,
                          final Version version, final Analyzer analyzer,
                          final Registry<String, Resource> resourceRegistry) {
        this.writerProvider = writerProvider;
        this.searcherProvider = searcherProvider;
        this.resourceRegistry = resourceRegistry;
        this.parser = new QueryParser(version, defaultField, analyzer);
    }

    private IndexWriter getIndexWriter() throws IOException {
        if (indexWriter == null)
            indexWriter = writerProvider.get();
        return indexWriter;
    }

    private IndexSearcher getIndexSearcher() {
        try {
            indexSearcher = searcherProvider.get();
        } catch (IOException e) {
            // silent on exception, just return null index searcher
        }
        return indexSearcher;
    }

    @Override
    public void commitIndex() throws IOException {
        if (indexWriter != null) {
            indexWriter.commit();
            indexWriter.close();
        }
        // remove the instance
        indexWriter = null;
        indexSearcher = null;
    }

    /**
     * Create a single term lucene query. The value for the query will be surrounded with single quote.
     *
     * @param field the field on which the query should be performed.
     * @param value the value for the field
     * @return the valid lucene query for single term.
     */
    private String createQuery(final String field, final String value) {
        return "(" + field + ":" + StringUtil.quote(value) + ")";
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
    private String createSearchableFieldQuery(final Object jsonObject, final List<SearchableField> searchableFields) {
        boolean uniqueExists = false;
        StringBuilder fullQuery = new StringBuilder();
        StringBuilder uniqueQuery = new StringBuilder();
        for (SearchableField searchableField : searchableFields) {
            String value = JsonPath.read(jsonObject, searchableField.getExpression()).toString();
            String query = createQuery(searchableField.getName(), value);

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
     * Create query fragment for a certain class. Calling this method will ensure the documents returned will of the
     * <code>clazz</code> type meaning the documents can be converted into object of type <code>clazz</code>. Converting
     * the documents need to be performed by getting the correct resource object from the document and then calling the
     * serialize method or getting the algorithm class and perform the serialization process from the algorithm object.
     * <p/>
     * Example use case: please retrieve all patients data. This should be performed by querying all object of certain
     * class type  because the caller is interested only in the type of object, irregardless of the resources from
     * which the objects are coming from.
     *
     * @param clazz the clazz for which the query is based on
     * @return the base query for a resource
     */
    private String createClassQuery(final Class clazz) {
        StringBuilder builder = new StringBuilder();
        builder.append(createQuery(DEFAULT_FIELD_CLASS, clazz.getName()));
        return builder.toString();
    }

    /**
     * Create query for a certain resource object. Calling this method will ensure the documents returned will be
     * documents that was indexed using the resource object.
     *
     * @param resource the resource for which the query is based on
     * @return the base query for a resource
     */
    private String createResourceQuery(final Resource resource) {
        StringBuilder builder = new StringBuilder();
        builder.append(createQuery(DEFAULT_FIELD_RESOURCE, resource.getName()));
        return builder.toString();
    }

    /**
     * Search the local lucene repository for documents with similar information with information inside the
     * <code>query</code>. Search can return multiple documents with similar information or empty list when no
     * document have similar information with the <code>query</code>.
     *
     * @param query the lucene query.
     * @return objects with similar information with the query.
     * @throws IOException when the search encounter error.
     */
    private List<Document> findDocuments(final Query query) throws IOException {
        List<Document> documents = new ArrayList<Document>();
        IndexSearcher searcher = getIndexSearcher();
        if (searcher != null) {
            TopDocs docs = searcher.search(query, DEFAULT_MAX_DOCUMENTS);
            ScoreDoc[] hits = docs.scoreDocs;
            for (ScoreDoc hit : hits)
                documents.add(searcher.doc(hit.doc));
        }
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
        document.add(new Field(DEFAULT_FIELD_UUID, UUID.randomUUID().toString(), Field.Store.YES,
                Field.Index.ANALYZED_NO_NORMS));
        document.add(new Field(DEFAULT_FIELD_CLASS, resource.getResourceObject().getName(), Field.Store.YES,
                Field.Index.ANALYZED_NO_NORMS));
        document.add(new Field(DEFAULT_FIELD_RESOURCE, resource.getName(), Field.Store.YES,
                Field.Index.ANALYZED_NO_NORMS));

        for (SearchableField searchableField : resource.getSearchableFields()) {
            Object value = JsonPath.read(jsonObject, searchableField.getExpression());
            document.add(new Field(searchableField.getName(), String.valueOf(value), Field.Store.YES,
                    Field.Index.ANALYZED_NO_NORMS));
        }
        writer.addDocument(document);
    }

    private void deleteObject(final Object jsonObject, final Resource resource, final IndexWriter indexWriter)
            throws ParseException, IOException {
        String queryString =
                createResourceQuery(resource) + " AND "
                        + createSearchableFieldQuery(jsonObject, resource.getSearchableFields());
        Query query = parser.parse(queryString);
        List<Document> documents = findDocuments(query);
        System.out.println("Document Size: " + documents.size());
        if (!CollectionUtil.isEmpty(documents) && documents.size() > 1)
            throw new IOException("Unable to uniquely identify an object using the json object in the repository.");
        indexWriter.deleteDocuments(query);
    }

    private void updateObject(final Object jsonObject, final Resource resource, final IndexWriter indexWriter)
            throws ParseException, IOException {
        // search for the same object, if they exists, delete them :)
        deleteObject(jsonObject, resource, indexWriter);
        // write the new object
        writeObject(jsonObject, resource, indexWriter);
    }

    @Override
    public void updateIndex(final Resource resource, final InputStream inputStream)
            throws ParseException, IOException {
        InputStreamReader reader = new InputStreamReader(inputStream);
        updateIndex(resource, reader);
    }

    @Override
    public void updateIndex(final Resource resource, final Reader reader)
            throws ParseException, IOException {
        String json = IOUtil.readAsString(reader);
        Object jsonObject = JsonPath.read(json, resource.getRootNode());
        if (jsonObject instanceof JSONArray) {
            JSONArray array = (JSONArray) jsonObject;
            for (Object element : array)
                updateObject(element, resource, getIndexWriter());
        } else if (jsonObject instanceof JSONObject) {
            updateObject(jsonObject, resource, getIndexWriter());
        }
    }

    @Override
    public Object getObject(final String key, final Class clazz) throws ParseException, IOException {
        Object object = null;

        String queryString = createClassQuery(clazz) + " AND " + StringUtil.quote(key);
        System.out.println("Query string in getObject(String, Class): " + queryString);

        Query query = parser.parse(queryString);

        List<Document> documents = findDocuments(query);
        if (!CollectionUtil.isEmpty(documents)) {
            if (documents.size() > 1)
                throw new IOException(
                        "Unable to uniquely identify an object using key: '" + key + "'in the repository.");
            for (Document document : documents) {
                String resourceName = document.get(DEFAULT_FIELD_RESOURCE);
                Resource resource = resourceRegistry.getEntryValue(resourceName);
                Algorithm algorithm = resource.getAlgorithm();
                String json = document.get(DEFAULT_FIELD_JSON);
                object = algorithm.deserialize(json);
            }
        }
        return object;
    }

    @Override
    public Object getObject(final String key, final Resource resource) throws ParseException, IOException {
        Object object = null;

        String queryString = createResourceQuery(resource) + " AND " + StringUtil.quote(key);
        System.out.println("Query string in getObject(String, Resource): " + queryString);

        Query query = parser.parse(queryString);

        List<Document> documents = findDocuments(query);
        if (!CollectionUtil.isEmpty(documents)) {
            if (documents.size() > 1)
                throw new IOException(
                        "Unable to uniquely identify an object using key: '" + key + "'in the repository.");

            Algorithm algorithm = resource.getAlgorithm();
            for (Document document : documents) {
                String json = document.get(DEFAULT_FIELD_JSON);
                object = algorithm.deserialize(json);
            }
        }
        return object;
    }

    @Override
    public List<Object> getObjects(final String searchString, final Class clazz)
            throws ParseException, IOException {
        List<Object> objects = new ArrayList<Object>();

        String queryString = createClassQuery(clazz);
        if (!StringUtil.isEmpty(searchString))
            queryString = queryString + " AND " + searchString;
        System.out.println("Query string in getObjects(String, Class): " + queryString);

        Query query = parser.parse(queryString);

        List<Document> documents = findDocuments(query);
        if (!CollectionUtil.isEmpty(documents)) {
            for (Document document : documents) {
                String resourceName = document.get(DEFAULT_FIELD_RESOURCE);
                Resource resource = resourceRegistry.getEntryValue(resourceName);
                Algorithm algorithm = resource.getAlgorithm();
                String json = document.get(DEFAULT_FIELD_JSON);
                objects.add(algorithm.deserialize(json));
            }
        }
        return objects;
    }

    @Override
    public List<Object> getObjects(final String searchString, final Resource resource)
            throws ParseException, IOException {
        List<Object> objects = new ArrayList<Object>();

        String queryString = createResourceQuery(resource);
        if (!StringUtil.isEmpty(searchString))
            queryString = queryString + " AND " + searchString;
        System.out.println("Query string in getObjects(String, Resource): " + queryString);

        Query query = parser.parse(queryString);

        List<Document> documents = findDocuments(query);
        if (!CollectionUtil.isEmpty(documents)) {
            Algorithm algorithm = resource.getAlgorithm();
            for (Document document : documents) {
                String json = document.get(DEFAULT_FIELD_JSON);
                objects.add(algorithm.deserialize(json));
            }
        }
        return objects;
    }

    @Override
    public Object createObject(final Object object, final Resource resource) throws ParseException, IOException {
        String jsonString = resource.serialize(object);
        Object jsonObject = JsonPath.read(jsonString, "$");
        writeObject(jsonObject, resource, getIndexWriter());
        return object;
    }

    @Override
    public Object deleteObject(final Object object, final Resource resource) throws ParseException, IOException {
        String jsonString = resource.serialize(object);
        Object jsonObject = JsonPath.read(jsonString, "$");
        deleteObject(jsonObject, resource, getIndexWriter());
        return object;
    }

    @Override
    public Object updateObject(final Object object, final Resource resource) throws ParseException, IOException {
        // TODO: need to look at how to incorporate Class.cast() here using resource.getResourceObject()
        String jsonString = resource.serialize(object);
        Object jsonObject = JsonPath.read(jsonString, "$");
        updateObject(jsonObject, resource, getIndexWriter());
        return object;
    }
}
