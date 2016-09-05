package com.github.avarabyeu.router;

import java.io.IOException;

/**
 * Handles Request
 *
 * @author Andrei Varabyeu
 */
@FunctionalInterface
public interface RequestHandler {

    /**
     * Handles request and fills response
     *
     * @param request  Request to Server
     * @param response Response from Server
     */
    void handle(Request request, Response response) throws IOException;

    /**
     * By default handler supports all routes except special cases like {@link FileSystemHandler}
     *
     * @param route Route to be checked
     * @return TRUE if handler supports route
     */
    default boolean supports(Route route) {
        return true;
    }

}
