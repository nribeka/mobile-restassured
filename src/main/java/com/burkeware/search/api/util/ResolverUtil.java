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

import org.apache.commons.codec.binary.Base64;

public class ResolverUtil {

    public static String getBasicAuth(final String username, final String password) {
        String auth = username + ":" + password;
        return "Basic " + new String(new Base64().encode(auth.getBytes()));
    }

}
