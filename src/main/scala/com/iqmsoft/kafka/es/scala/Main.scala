package com.iqmsoft.kafka.es.scala


import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.model.dataformat.JsonLibrary
import org.apache.camel.{Exchange, Processor}
import java.util.concurrent.CountDownLatch
import org.apache.log4j.{ConsoleAppender, LogManager, PatternLayout}

object Main extends App {
    
  LogManager.getRootLogger.addAppender(new ConsoleAppender(new PatternLayout()))
  val ctx = new DefaultCamelContext()
  ctx.addRoutes(new RouteBuilder {
    override def configure() {
      from("kafka:localhost:9092?topic=city&groupId=kafkaelasticsearch")
      .multicast().parallelProcessing()
        .to("stream:out",
          "elasticsearch://elasticsearch?transportAddresses=localhost:9200&operation=INDEX&indexName=countries&indexType=country")
    }
  })
  ctx.start()
  println("Please press enter to stop")
  new CountDownLatch(100).await()
  ctx.shutdown()
}