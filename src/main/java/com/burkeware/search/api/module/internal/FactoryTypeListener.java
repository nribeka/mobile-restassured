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
package com.burkeware.search.api.module.internal;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class FactoryTypeListener implements TypeListener {

    /**
     * Invoked when Guice encounters a new type eligible for constructor or members injection.
     * Called during injector creation (or afterwords if Guice encounters a type at run time and
     * creates a JIT binding).
     *
     * @param type      encountered by Guice
     * @param encounter context of this encounter, enables reporting errors, registering injection
     *                  listeners and binding method interceptors for {@code type}.
     * @param <I>       the injectable type
     */
    @Override
    public <I> void hear(final TypeLiteral<I> type, final TypeEncounter<I> encounter) {
    }
}
