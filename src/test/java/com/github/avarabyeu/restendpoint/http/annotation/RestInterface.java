package com.github.avarabyeu.restendpoint.http.annotation;

import com.github.avarabyeu.restendpoint.async.Will;
import com.github.avarabyeu.restendpoint.http.HttpMethod;

import java.util.Map;

/**
 * @author Andrey Vorobyov
 */
public interface RestInterface {

    @Rest(method = HttpMethod.GET, url = "/")
    String get();

    @Rest(method = HttpMethod.GET, url = "/{path}")
    String getWithPath(@Path("path") String path);

    @Rest(method = HttpMethod.GET, url = "/")
    String getWithQuery(@Query Map<String, String> queryParams);

    @Rest(method = HttpMethod.POST, url = "/")
    String post(@Body String body);

    @Rest(method = HttpMethod.PUT, url = "/")
    String put(@Body String body);

    @Rest(method = HttpMethod.DELETE, url = "/")
    String delete();

    @Rest(method = HttpMethod.GET, url = "/")
    Will<String> getAsync();

    @Rest(method = HttpMethod.POST, url = "/")
    Will<String> postAsync(@Body String body);

    @Rest(method = HttpMethod.PUT, url = "/")
    Will<String> putAsync(@Body String body);

    @Rest(method = HttpMethod.DELETE, url = "/")
    Will<String> deleteAsync();
}
