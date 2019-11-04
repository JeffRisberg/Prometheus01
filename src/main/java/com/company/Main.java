package com.company;

import com.company.service.CountService;
import com.company.service.DataService;
import com.company.service.DeleteService;
import com.company.service.IngestService;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Arrays;

public class Main {
  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    String clusterName = "elasticsearch";
    String indexName = "products";

    Settings settings = Settings.builder()
      .put("cluster.name", clusterName).build();

    Client client = new PreBuiltTransportClient(settings)
      .addTransportAddress(new TransportAddress(new InetSocketAddress("127.0.0.1", 9300)));

    CountService countService = new CountService(client, indexName);
    DataService dataService = new DataService(client, indexName);
    IngestService ingestService = new IngestService(client, indexName);
    DeleteService deleteService = new DeleteService(client, indexName);

    // Count
    System.out.println("getMatchAllQueryCount " + countService.getMatchAllQueryCount());
    System.out.println("getBoolQueryCount " + countService.getBoolQueryCount());
    System.out.println("getPhraseQueryCount " + countService.getPhraseQueryCount());

    // Data
    System.out.println("getMatchAllQueryData");
    dataService.getMatchAllQueryData().forEach(item -> System.out.println(item));

    System.out.println("getBoolQueryData");
    dataService.getBoolQueryData().forEach(item -> System.out.println(item));

    System.out.println("getPhraseQueryData");
    dataService.getPhraseQueryData().forEach(item -> System.out.println(item));

    // Ingest single record
    System.out.println("\nIngestService response::: " + ingestService.ingest("default", json1));

    // Ingest batch of records
    System.out.println("\nIngestService response::: " + ingestService.ingest("default", Arrays.asList(json2, json3, json4)));

    // Count records
    System.out.println("getMatchAllQueryCount " + countService.getMatchAllQueryCount());

    // Delete
    System.out.println("delete by query " + deleteService.deleteByQuery("furby"));

    System.out.println("delete by query " + deleteService.deleteByQuery("blizzard"));

    // Count records
    System.out.println("getMatchAllQueryCount " + countService.getMatchAllQueryCount());

    client.close();
  }
}
