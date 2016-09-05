package com.github.avarabyeu.router;

import com.google.common.base.MoreObjects;

import java.util.Optional;

/**
 * Route representation
 * 'Route' means HTTP Method + request path pair
 *
 * @author Andrei Varabyeu
 */
public abstract class Route {

    //absent means ANY
    protected Optional<Request.Method> httpMethod;

    public Route(Optional<Request.Method> httpMethod) {
        this.httpMethod = httpMethod;
    }

    public Optional<Request.Method> getMethod() {
        return httpMethod;
    }

    /**
     * Check whether giver request is matches to Route
     *
     * @param request Request
     * @return TRUE if route matches
     */
    protected boolean matches(Request request) {
        return anyMethod() || httpMethod.get().equals(request.getMethod());
    }

    /**
     * Just convenience method to bring more source code understanding
     *
     * @return TRUE if ALL methods are supported
     */
    private boolean anyMethod() {
        return !httpMethod.isPresent();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("httpMethod", httpMethod)
                .toString();
    }
}
