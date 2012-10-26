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

import com.burkeware.search.api.dao.SearchDao;
import com.burkeware.search.api.provider.SearchProvider;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

import java.util.ArrayList;
import java.util.List;

public class SearchDaoImpl implements SearchDao {

    private static final Integer MAX_OBJECT = 20;

    // TODO: we need to reuse the IndexReader as much as possible
    // Currently it's not being reused
    // TODO: we need to check if the IndexReader inside the SearchProvider is up to date.
    // The check is done to ensure that we're reading from an up-to-date index data.
    // method call: indexReader.isCurrent()
    private final SearchProvider<IndexWriter> writerProvider;

    private final SearchProvider<IndexSearcher> searcherProvider;

    private final QueryParser parser;

    @Inject
    public SearchDaoImpl(final Version version, final Analyzer analyzer,
                         final SearchProvider<IndexWriter> writerProvider,
                         final SearchProvider<IndexSearcher> searcherProvider,
                         final @Named("configuration.lucene.document.key") String key) {
        this.writerProvider = writerProvider;
        this.searcherProvider = searcherProvider;
        this.parser = new QueryParser(version, key, analyzer);
    }

    /**
     * Append class name filter in the query to make sure we're returning the correct object type.
     *
     * @param clazz        the expected return type of the object
     * @param searchString the queryString to the object
     * @return the new query with class clause in the query
     */
    private Query prepareQuery(final Class clazz, final String searchString) throws ParseException {
        String keyWords = searchString + " AND _class: " + clazz.getName();
        return parser.parse(keyWords);
    }

    /**
     * Internal implementation of the search into lucene index. The implementation will search based on the search
     * string and return n numbers of Document with highest score.
     *
     * @param clazz        the expected type of object in the document
     * @param searchString the search string query
     * @return list of top n documents that match the query
     */
    private List<Document> searchDocuments(final Class clazz, final String searchString) {
        List<Document> documents = new ArrayList<Document>();
        return documents;
    }

    /**
     * DAO method to get an object of certain type based on the key.
     *
     * @param tClass the expected type of the object
     * @param key    the key to uniquely identify the object
     * @param <T>    the object type
     * @return the object uniquely identified by the key
     */
    @Override
    public <T> T getObject(final Class<T> tClass, final String key) {
        T t = null;

        List<Document> documents = searchDocuments(tClass, key);

        return t;
    }

    /**
     * DAO method to search objects of certain type based on certain query term.
     *
     * @param tClass       the expected type of the object
     * @param searchString the query term
     * @param <T>          the type of the object
     * @return list of all matching objects
     */
    @Override
    public <T> List<T> getObjects(final Class<T> tClass, final String searchString) {
        List<T> tList = new ArrayList<T>();
        return tList;
    }

    /**
     * DAO method to invalidate entry in the index.
     *
     * @param tClass the expected type of the object
     * @param key    the key to uniquely identify the object
     * @param <T>    the type of the object
     * @return the object that was invalidated by the method call
     */
    @Override
    public <T> T invalidate(final Class<T> tClass, final String key) {
        T t = null;
        return t;
    }
}
