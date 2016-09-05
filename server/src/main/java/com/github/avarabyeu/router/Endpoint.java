package com.github.avarabyeu.router;

import com.google.common.util.concurrent.AbstractIdleService;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Created by avarabyeu on 12/18/15.
 */
public class Endpoint extends AbstractIdleService {

    private final Router router;

    private final int port;

    private Server server;

    public Endpoint(Router router, Integer port) {
        this.router = router;
        this.port = null != port ? port : 8282;
    }

    public Endpoint(Router router) {
        this(router, null);
    }

    @Override
    protected void startUp() throws Exception {
        Server server = new Server(port);

        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(new ServletHolder(new RouterServlet(router)), "/*");

        server.setHandler(handler);

        server.start();
        server.join();
    }

    @Override
    protected void shutDown() throws Exception {
        server.stop();
    }
}
