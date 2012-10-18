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

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.google.inject.throwingproviders.CheckedProvider;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

public class DirectoryProvider implements SearchProvider<Directory> {

    private final String directory;

    // TODO: create a factory to customize the type of directory returned by this provider
    @Inject
    public DirectoryProvider(final @Named("configuration.lucene.directory") String directory) {
        this.directory = directory;
    }

    @Override
    public Directory get() throws IOException {
        return NIOFSDirectory.open(new File(directory));
    }
}
