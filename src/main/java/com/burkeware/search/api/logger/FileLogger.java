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

import com.burkeware.search.api.exception.LoggerException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileLogger extends BaseLogger {

    private final OutputStream outputStream;

    private final DateFormat dateFormat;

    /**
     * Create the file logger to log messages from the REST Assured framework.
     *
     * @param file the log file
     */
    public FileLogger(final File file) {
        try {
            this.dateFormat = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss.SSS");
            this.outputStream = new BufferedOutputStream(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            throw new LoggerException("Unable to initialize the logger with the file: " + file.getName(), e);
        }
    }

    /**
     * Actual implementation of the logger should implement the actual process of writing this message to whatever
     * the logger wants to write. Stone is definitely not an option :)
     *
     * @param logLevel  the log level
     * @param source    the source of the log
     * @param message   the message inside the log
     * @param throwable the throwable object
     */
    @Override
    protected void doLog(final LogLevel logLevel, final String source,
                         final String message, final Throwable throwable) {
        StringBuilder logBuilder = new StringBuilder();
        String lineSeparator = System.getProperty("line.separator", "\n");

        logBuilder.append("[").append(dateFormat.format(new Date())).append("]");
        logBuilder.append("[").append(logLevel.getName()).append("]");
        logBuilder.append("[").append(source).append("]");
        logBuilder.append(message);

        if (throwable != null)
            logBuilder.append(lineSeparator).append(throwable).append(lineSeparator);

        String log = logBuilder.toString();
        try {
            outputStream.write(log.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            throw new LoggerException("Failed to write log message [" + log + "]", e);
        }
    }
}
