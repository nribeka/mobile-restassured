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

package com.burkeware.search.api.sample;

import com.burkeware.search.api.algorithm.Algorithm;
import com.jayway.jsonpath.JsonPath;

public class PatientAlgorithm implements Algorithm<Patient> {
    /**
     * Implementation of this method will define how the patient will be serialized from the JSON representation.
     *
     *
     * @param json the json representation
     * @return the concrete patient object
     */
    @Override
    public Patient serialize(final String json) {
        Patient patient = new Patient();

        // get the full json object representation and then pass this around to the next JsonPath.read()
        // this should minimize the time for the subsequent read() call
        Object jsonObject = JsonPath.read(json, "$");

        String uuid = JsonPath.read(jsonObject, "$.uuid");
        patient.setUuid(uuid);

        String name = JsonPath.read(jsonObject, "$.person.display");
        patient.setName(name);

        String identifier = JsonPath.read(jsonObject, "$.identifiers[0].identifier");
        patient.setIdentifier(identifier);

        String gender = JsonPath.read(jsonObject, "$.person.gender");
        patient.setGender(gender);

        patient.setJson(json);

        return patient;
    }

    /**
     * Implementation of this method will define how the patient will be deserialized into the JSON representation.
     *
     *
     * @param patient the patient
     * @return the json representation
     */
    @Override
    public String deserialize(final Patient patient) {
        return patient.getJson();
    }
}
