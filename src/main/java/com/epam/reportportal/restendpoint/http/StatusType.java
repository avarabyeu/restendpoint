/*
 * Copyright (C) 2014 Andrei Varabyeu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.reportportal.restendpoint.http;

/**
 * HTTP Response Status Type
 *
 * @author Andrei Varabyeu
 */
public enum StatusType {

    /**
     * Informational Response
     */
    INFORMATIONAL(1),
    /**
     * Successful response
     */
    SUCCESSFUL(2),
    /**
     * Redirection response
     */
    REDIRECTION(3),
    /**
     * Client Error Response
     */
    CLIENT_ERROR(4),
    /**
     * Server Error Response
     */
    SERVER_ERROR(5);

    /**
     * First Symbol of HTTP response code
     */
    private final int value;

    StatusType(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    /**
     * Obrains {@link StatusType} from HTTP status code. If there are no status
     * defined throws {@link java.lang.IllegalArgumentException}
     *
     * @param status HTTP status code
     * @return HTTP Response Type
     */
    public static StatusType valueOf(int status) {
        int seriesCode = status / 100;
        for (StatusType series : values()) {
            if (series.value == seriesCode) {
                return series;
            }
        }
        throw new IllegalArgumentException("No matching constant for [" + status + "]");
    }

}
