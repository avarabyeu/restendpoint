package com.github.avarabyeu.router;

/**
 * Created by avarabyeu on 12/18/15.
 */
public class HandlerNotFoundException extends RuntimeException {

    /**
     * Default implementation for exception
     */
    static ExceptionHandler HANDLER = (e, response) -> response.statusCode(404);
}
