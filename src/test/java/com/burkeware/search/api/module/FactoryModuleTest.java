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

import com.burkeware.search.api.internal.provider.SearchProvider;
import com.burkeware.search.api.registry.Registry;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import junit.framework.Assert;
import org.apache.lucene.index.IndexWriter;
import org.junit.Test;

public class FactoryModuleTest {

    /**
     * @verifies bind instances of factory and string
     * @see FactoryModule#configure()
     */
    @Test
    public void configure_shouldBindInstancesOfFactoryAndString() throws Exception {
        Injector injector = Guice.createInjector(new FactoryModule(), new SearchModule());

        Registry<String, String> realRegistry = injector.getInstance(
                Key.get(new TypeLiteral<Registry<String, String>>() {}));
        Assert.assertNotNull(realRegistry);

        SearchProvider<IndexWriter> writerProvider = injector.getInstance(
                Key.get(new TypeLiteral<SearchProvider<IndexWriter>>() {}));
        Assert.assertNotNull(writerProvider);
    }
}
