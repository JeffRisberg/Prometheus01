package com.company;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
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
    private static final Counter helloRequests =
        Counter.build().name("hello_worlds_total").help("Hello Worlds Requested.").register();

    private static final Histogram helloRequestLatency =
        Histogram.build()
            .name("hello_requests_latency_seconds")
            .help("Hello Request latency in seconds.")
            .register();

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
        throws ServletException, IOException {

      helloRequestLatency.time(
          () -> {
            // Increment the number of requests.
            helloRequests.inc();

            System.out.println(req.getRequestURI());
            System.out.println(req.getMethod());
            System.out.println("count " + helloRequests.get());

            try {
              resp.getWriter().println("Hello World!");
            } catch (Exception e) {
            }
          });
    }
  }

  static class GoodbyeServlet extends HttpServlet {
    private static final Counter goodbyeRequests =
        Counter.build().name("goodbye_worlds_total").help("Goodbye Worlds Requested.").register();

    private static final Histogram goodbyeRequestLatency =
        Histogram.build()
            .name("goodbye_requests_latency_seconds")
            .help("Goodbye Request latency in seconds.")
            .register();

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
        throws ServletException, IOException {

      goodbyeRequestLatency.time(
          () -> {
            // Increment the number of requests.
            goodbyeRequests.inc();

            System.out.println(req.getRequestURI());
            System.out.println(req.getMethod());
            System.out.println("count " + goodbyeRequests.get());

            try {
              resp.getWriter().println("Goodbye World!");
            } catch (Exception e) {
            }
          });
    }
  }

  public static void main(String[] args) throws Exception {
    Server server = new Server(8000);
    ServletContextHandler context = new ServletContextHandler();
    context.setContextPath("/");
    server.setHandler(context);

    // Expose our example servlets.
    context.addServlet(new ServletHolder(new HelloServlet()), "/hello");
    context.addServlet(new ServletHolder(new GoodbyeServlet()), "/goodbye");

    // Expose Prometheus metrics.
    context.addServlet(new ServletHolder(new MetricsServlet()), "/metrics");

    // Add metrics about CPU, JVM memory etc.
    DefaultExports.initialize();

    // Start the webserver.
    server.start();
    server.join();
  }
}
