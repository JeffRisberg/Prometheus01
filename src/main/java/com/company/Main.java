package com.company;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.company.common.DropwizardMetrics;
import io.prometheus.client.exporter.MetricsServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Main {
  static DropwizardMetrics dm;

  static class HelloServlet extends HttpServlet {
    // Create a Dropwizard counter.
    private static final Counter helloRequests = dm.getRegistry().counter("hello_worlds_total");

    /*
    private static final Counter helloRequests = Counter.build()
            .name("hello_worlds_total")
            .help("Hello Worlds Requested.")
            .register();
    */

    static final Histogram helloRequestLatency =
        dm.getRegistry().histogram("hello_requests_latency_seconds");

    /*
    private static final Histogram helloRequestLatency = Histogram.build()
            .name("hello_requests_latency_seconds")
            .help("Hello Request latency in seconds.")
            .register();
     */

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
        throws ServletException, IOException {

      long startTime = System.currentTimeMillis();

      // Increment the number of requests.
      helloRequests.inc();

      System.out.println(req.getRequestURI());
      System.out.println(req.getMethod());
      System.out.println("count " + helloRequests.getCount());

      try {
        resp.getWriter().println("Hello World!");
      } catch (Exception e) {
      }

      long deltaTime = System.currentTimeMillis() - startTime;
      System.out.println(deltaTime);
      helloRequestLatency.update(deltaTime);
    }
  }

  static class GoodbyeServlet extends HttpServlet {
    // Create a Dropwizard counter.
    private static final Counter goodbyeRequests = dm.getRegistry().counter("goodbye_worlds_total");

    /*
    private static final Counter goodbyeRequests = Counter.build()
            .name("goodbye_worlds_total")
            .help("Goodbye Worlds Requested.").register();
    */

    static final Histogram goodbyeRequestLatency =
        dm.getRegistry().histogram("goodbye_requests_latency_seconds");

    /*
    private static final Histogram goodbyeRequestLatency = Histogram.build()
            .name("goodbye_requests_latency_seconds")
            .help("Goodbye Request latency in seconds.")
            .register();
     */

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
        throws ServletException, IOException {

      long startTime = System.currentTimeMillis();

      // Increment the number of requests.
      goodbyeRequests.inc();

      System.out.println(req.getRequestURI());
      System.out.println(req.getMethod());
      System.out.println("count " + goodbyeRequests.getCount());

      try {
        resp.getWriter().println("Goodbye World!");
      } catch (Exception e) {
      }

      long deltaTime = System.currentTimeMillis() - startTime;
      System.out.println(deltaTime);
      goodbyeRequestLatency.update(deltaTime);
    }
  }

  public static void main(String[] args) throws Exception {
    Server server = new Server(8000);
    ServletContextHandler context = new ServletContextHandler();
    context.setContextPath("/");
    server.setHandler(context);

    // Add metrics about CPU, JVM memory etc.
    dm = DropwizardMetrics.getInstance();

    // Expose our example servlets.
    context.addServlet(new ServletHolder(new HelloServlet()), "/hello");
    context.addServlet(new ServletHolder(new GoodbyeServlet()), "/goodbye");

    // Expose Prometheus metrics.
    context.addServlet(new ServletHolder(new MetricsServlet()), "/metrics");

    // Start the webserver.
    server.start();
    server.join();
  }
}
