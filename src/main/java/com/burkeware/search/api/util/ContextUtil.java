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
package com.burkeware.search.api.util;

import com.burkeware.search.api.Context;
import com.burkeware.search.api.module.FactoryModule;
import com.burkeware.search.api.module.SearchModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

// TODO: need to look at how the best way to implement this
public class ContextUtil {

    private static Injector injector;

    public static Context createContext(final Module... module) {
        if (injector == null)
            injector = Guice.createInjector(new SearchModule(), new FactoryModule(), Modules.combine(module));
        return injector.getInstance(Context.class);
    }
}
