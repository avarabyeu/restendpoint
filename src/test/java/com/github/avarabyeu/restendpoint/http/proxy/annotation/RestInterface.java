package com.github.avarabyeu.restendpoint.http.proxy.annotation;

import com.github.avarabyeu.restendpoint.http.HttpMethod;

/**
 * @author Andrey Vorobyov
 */
public interface RestInterface {

    @Rest(method = HttpMethod.GET, url = "/{api}/json?tree=jobs[name,url,color]")
    String getSomething(@Path("api") String api);
}
