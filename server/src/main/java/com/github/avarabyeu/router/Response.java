package com.github.avarabyeu.router;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * Server response representation
 *
 * @author Andrei Varabyeu
 */
public class Response {

    private final HttpServletResponse delegate;

    public Response(HttpServletResponse delegate) throws IOException {
        this.delegate = delegate;
    }

    public Response content(String content) throws IOException {
        return content(content.getBytes(Charsets.UTF_8));
    }

    public Response content(byte[] content) throws IOException {
        ByteSource.wrap(content).copyTo(this.delegate.getOutputStream());
        return this;
    }

    public Response content(InputStream is) throws IOException {
        ByteStreams.copy(is, this.delegate.getOutputStream());
        return this;
    }

    public Response contentType(String content) {
        this.delegate.setContentType(content);
        return this;
    }

    public HttpServletResponse raw() {
        return this.delegate;
    }

    public Response statusCode(int statusCode) {
        this.delegate.setStatus(statusCode);
        return this;
    }

}
