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

package com.github.avarabyeu.restendpoint.http.mock;

import com.github.avarabyeu.restendpoint.async.Will;
import com.github.avarabyeu.restendpoint.http.HttpMethod;
import com.github.avarabyeu.restendpoint.http.annotation.Body;
import com.github.avarabyeu.restendpoint.http.annotation.Path;
import com.github.avarabyeu.restendpoint.http.annotation.Query;
import com.github.avarabyeu.restendpoint.http.annotation.Request;

import java.util.Map;

/**
 * @author Andrey Vorobyov
 */
public interface RestInterface {

    @Request(method = HttpMethod.GET, url = "/")
    String get();

    @Request(method = HttpMethod.GET, url = "/{path}")
    String getWithPath(@Path("path") String path);

    @Request(method = HttpMethod.GET, url = "/")
    String getWithQuery(@Query Map<String, String> queryParams);

    @Request(method = HttpMethod.POST, url = "/")
    String post(@Body String body);

    @Request(method = HttpMethod.PUT, url = "/")
    String put(@Body String body);

    @Request(method = HttpMethod.DELETE, url = "/")
    String delete();

    @Request(method = HttpMethod.GET, url = "/")
    Will<String> getAsync();

    @Request(method = HttpMethod.POST, url = "/")
    Will<String> postAsync(@Body String body);

    @Request(method = HttpMethod.PUT, url = "/")
    Will<String> putAsync(@Body String body);

    @Request(method = HttpMethod.DELETE, url = "/")
    Will<String> deleteAsync();
}
