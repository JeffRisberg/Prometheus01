package com.company;

import io.prometheus.client.Counter;
import io.prometheus.client.exporter.MetricsServlet;
import io.prometheus.client.hotspot.DefaultExports;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Main {
  static class HelloServlet extends HttpServlet {

    static final Counter helloRequests = Counter.build()
      .name("hello_worlds_total")
      .help("Hello Worlds Requested.").register();

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException {

      // Increment the number of requests.
      helloRequests.inc();

      System.out.println(req.getRequestURI());
      System.out.println(req.getMethod());
      System.out.println("count " + helloRequests.get());
      resp.getWriter().println("Hello World!");
    }
  }

  static class GoodbyServlet extends HttpServlet {

    static final Counter goodbyRequests = Counter.build()
      .name("hello_worlds_total")
      .help("Hello Worlds Requested.").register();

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException {

      // Increment the number of requests.
      goodbyRequests.inc();

      System.out.println(req.getRequestURI());
      System.out.println(req.getMethod());
      System.out.println("count " + goodbyRequests.get());
      resp.getWriter().println("Goodby World!");
    }
  }

  public static void main(String[] args) throws Exception {
    Server server = new Server(8000);
    ServletContextHandler context = new ServletContextHandler();
    context.setContextPath("/");
    server.setHandler(context);

    // Expose our example servlets.
    context.addServlet(new ServletHolder(new HelloServlet()), "/hello");
    context.addServlet(new ServletHolder(new GoodbyServlet()), "/goodby");

    // Expose Prometheus metrics.
    context.addServlet(new ServletHolder(new MetricsServlet()), "/metrics");

    // Add metrics about CPU, JVM memory etc.
    DefaultExports.initialize();

    // Start the webserver.
    server.start();
    server.join();
  }
}
