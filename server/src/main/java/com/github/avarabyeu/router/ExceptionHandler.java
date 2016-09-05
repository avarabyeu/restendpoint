package com.github.avarabyeu.router;

import java.io.IOException;

/**
 * Created by avarabyeu on 12/18/15.
 */
public interface ExceptionHandler {

    void handle(Exception e, Response response) throws IOException;
}
