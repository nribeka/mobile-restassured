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
package com.burkeware.search.api.logger;

import com.burkeware.search.api.util.StringUtil;

public class LogLevel {

    public static final LogLevel INFO = new LogLevel("INFO", 1);

    public static final LogLevel WARN = new LogLevel("WARN", 2);

    public static final LogLevel DEBUG = new LogLevel("DEBUG", 3);

    private String name;

    private Integer level;

    private LogLevel(final String name, final Integer level) {
        this.name = name;
        this.level = level;
    }

    public Integer getLevel() {
        return this.level;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public int hashCode() {
        return level.hashCode() + name.hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        LogLevel logLevel = (LogLevel) object;
        return (StringUtil.equals(this.name, logLevel.name) && level.equals(logLevel.level));
    }
}
