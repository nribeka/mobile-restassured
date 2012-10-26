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

package com.burkeware.search.api.factory;

import com.burkeware.search.api.uri.FigureOuter;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public final class DefaultFigureOuterFactory extends DefaultFactory<FigureOuter> {

    /**
     * The implementation of the base factory.
     */
    @Inject
    protected DefaultFigureOuterFactory(final @Named("FigureOuterFactory.name") String implementationName) {
        super(implementationName);
    }
}
