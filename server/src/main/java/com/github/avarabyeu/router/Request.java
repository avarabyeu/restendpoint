package com.github.avarabyeu.router;

import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import com.google.common.net.MediaType;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Andrei Varabyeu
 */
public class Request {

    private final HttpServletRequest delegate;

    /**
     * HTTP Methods Representation
     *
     * @author Andrei Varabyeu
     */
    public enum Method {
        GET(false),
        POST(true),
        PUT(true),
        PATCH(true),
        DELETE(false);

        private final boolean hasBody;

        /**
         * @param hasBody Whether method contains body
         */
        Method(boolean hasBody) {
            this.hasBody = hasBody;
        }

        /**
         * @return TRUE if method contains body
         */
        boolean hasBody() {
            return hasBody;
        }
    }

    private Map<String, String> pathVariables;

    public Request(HttpServletRequest delegate) {
        this.delegate = delegate;
    }

    public Map<String, String> getHeaders() {
        return Utils.enumerationAsStream(delegate.getHeaderNames())
                .collect(Collectors.toMap(name -> name, delegate::getHeader));
    }

    public Optional<MediaType> getContentType() {
        String contentType = delegate.getHeader("Content-Type");
        return Strings.isNullOrEmpty(contentType) ? Optional.empty() : Optional.of(MediaType.parse(contentType));
    }

    public Map<String, String> getPathVariables() {
        return pathVariables;
    }

    void setPathVariables(Map<String, String> pathVariables) {
        this.pathVariables = pathVariables;
    }

    public HttpServletRequest raw() {
        return this.delegate;
    }

    public Method getMethod() {
        return Method.valueOf(this.delegate.getMethod().toUpperCase());
    }

    public String getRequestUri() {
        return delegate.getRequestURI();
    }

    public String getBody() throws IOException {
        return getBodyAs(is -> CharStreams.toString(this.delegate.getReader()));
    }

    public <T> T getBodyAs(Converter<T> converter) throws IOException {
        return converter.convert(this.delegate.getInputStream());
    }

    @FunctionalInterface
    interface Converter<T> {
        T convert(InputStream is) throws IOException;
    }

}
