package com.github.avarabyeu.router;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Router is a mapping between routes and their handlers
 *
 * @author Andrei Varabyeu
 */
public class Router {

    public static final String ANY_PATH = ".*";

    private final Map<Route, RequestHandler> routes;

    private final InstanceOfMap<Exception, ExceptionHandler> exceptionHandlers;

    private Router(Map<Route, RequestHandler> routes,
            Map<Class<? extends Exception>, ExceptionHandler> exceptionHandlers) {
        this.routes = routes;
        this.exceptionHandlers = InstanceOfMap.<Exception, ExceptionHandler>builder().fromMap(exceptionHandlers);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Optional<RequestHandler> getHandler(Request request) {
        return routes.entrySet().stream().filter(e -> e.getKey().matches(request)).findFirst().map(Map.Entry::getValue);
    }

    public Optional<ExceptionHandler> getHandler(Class<? extends Exception> clazz) {
        return Optional.ofNullable(exceptionHandlers.getInstanceOf(clazz));
    }

    public static class Builder {

        private ImmutableMap.Builder<Route, RequestHandler> routes = ImmutableMap
                .builder();

        /**
         * Using JDK map here because Guava's one doesn't allow replacement of value with some key
         */
        private Map<Class<? extends Exception>, ExceptionHandler> exceptionHandlers = new HashMap<Class<? extends Exception>, ExceptionHandler>() {
            {
                put(HandlerNotFoundException.class, HandlerNotFoundException.HANDLER);
            }
        };

        public Builder path(Request.Method method, String route, RequestHandler handler) {
            routes.put(new RegexpRoute(method, route), handler);
            return this;
        }

        public Builder path(String route, RequestHandler handler) {
            routes.put(new RegexpRoute(Optional.empty(), route), handler);
            return this;
        }

        public Builder post(String route, RequestHandler handler) {
            routes.put(new RegexpRoute(Request.Method.POST, route), handler);
            return this;
        }

        public Builder filesystem(String route, Path path) {
            routes.put(new RegexpRoute(Request.Method.GET, route), new FileSystemHandler(path));
            return this;
        }

        public Builder resources(String route, String basePath) {
            routes.put(new RegexpRoute(Request.Method.GET, route), new ResourceHandler(basePath));
            return this;
        }

        public Builder onException(Class<? extends Exception> type, ExceptionHandler handler) {
            exceptionHandlers.put(type, handler);
            return this;
        }

        public Router build() {
            ImmutableMap<Route, RequestHandler> routesMap = routes.build();
            routesMap.forEach(
                    (r, h) -> Preconditions
                            .checkArgument(h.supports(r), "Route %s is not supported by assigned handler", r));

            return new Router(routesMap, Collections.unmodifiableMap(exceptionHandlers));
        }

    }
}
