package com.github.avarabyeu.router;

import com.google.common.base.Strings;
import com.google.common.io.Resources;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

/**
 * Created by avarabyeu on 11/28/15.
 */
public class ResourceHandler implements RequestHandler {

    private final String basePath;

    public ResourceHandler(String basePath) {
        this.basePath = basePath;

    }

    @Override
    public void handle(Request request, Response response) throws IOException {
        String filename = request.getRequestUri().substring(1, request.getRequestUri().length());
        URL resource =
                Thread.currentThread().getContextClassLoader()
                        .getResource(Strings.isNullOrEmpty(basePath) ? filename : basePath + "/" + filename);
        if (null != resource) {
            String contentType = Utils.resolveMimeType(filename);
            response.contentType(contentType);
            Resources.copy(resource, response.raw().getOutputStream());
        } else {
            response.statusCode(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Only GET method is supported
     *
     * @param route Route to be checked
     * @return TRUE if GET method is supported
     */
    @Override
    public boolean supports(Route route) {
        return !route.getMethod().isPresent() || Request.Method.GET.equals(route.getMethod().get());
    }
}
