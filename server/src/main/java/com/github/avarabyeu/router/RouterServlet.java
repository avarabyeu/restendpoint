package com.github.avarabyeu.router;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

/**
 * Dispatcher Servlet
 *
 * @author Andrei Varabyeu
 */
public class RouterServlet extends HttpServlet {

    private final Router router;

    public RouterServlet(Router router) {
        this.router = router;
    }

    @Override
    protected void service(HttpServletRequest rq, HttpServletResponse rs) throws ServletException, IOException {
        /* wrap request and response */
        Request request = new Request(rq);
        Response response = new Response(rs);

        /* looking for request handler */
        try {
            /* if handler has found than handle, if not - throw the error */
            router.getHandler(request)
                    .orElseThrow(HandlerNotFoundException::new)
                    .handle(request, response);
        } catch (Exception t) {
            t.printStackTrace();
            /* look for exception handler... */
            Optional<ExceptionHandler> exceptionHandler = this.router.getHandler(t.getClass());
            if (exceptionHandler.isPresent()) {

                /* exception handler is also may cause an error. So wrap exception handler as well */
                try {
                    exceptionHandler.get().handle(t, response);
                } catch (Exception e2) {
                    rs.setStatus(SC_INTERNAL_SERVER_ERROR);
                }
            } else {
                rs.setStatus(SC_INTERNAL_SERVER_ERROR);
            }
        }
    }
}
