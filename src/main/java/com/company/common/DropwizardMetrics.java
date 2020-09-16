package com.company.common;

import com.codahale.metrics.*;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.dropwizard.DropwizardExports;
import io.prometheus.client.hotspot.DefaultExports;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DropwizardMetrics {
  private static DropwizardMetrics instance = null;
  private MetricRegistry registry;

  private DropwizardMetrics() {
    registry = new MetricRegistry();

    // native jvm metrics
    DefaultExports.initialize();

    // Register DropWizard metrics with Prometheus default registry
    CollectorRegistry.defaultRegistry.register(new DropwizardExports(registry));
  }

  public MetricRegistry getRegistry() {
    return registry;
  }

  public static DropwizardMetrics getInstance() {
    if (instance == null) {
      instance = new DropwizardMetrics();
    }
    return instance;
  }
}
