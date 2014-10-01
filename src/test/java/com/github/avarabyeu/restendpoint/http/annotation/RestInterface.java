package com.github.avarabyeu.restendpoint.http.annotation;

import com.github.avarabyeu.restendpoint.http.HttpMethod;
import com.github.avarabyeu.restendpoint.http.annotation.Path;
import com.github.avarabyeu.restendpoint.http.annotation.Rest;

/**
 * @author Andrey Vorobyov
 */
public interface RestInterface {

    @Rest(method = HttpMethod.GET, url = "/{api}/json?tree=jobs[name,url,color]")
    String getSomething(@Path("api") String api);
}
