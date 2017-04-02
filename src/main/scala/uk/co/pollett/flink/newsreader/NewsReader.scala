package uk.co.pollett.flink.newsreader

import java.net.{InetAddress, InetSocketAddress}
import java.util
import java.util.Properties

import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.elasticsearch2.{ElasticsearchSink, ElasticsearchSinkFunction, RequestIndexer}
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.Requests
import uk.co.pollett.flink.newsreader.nlp._
import uk.co.pollett.flink.newsreader.rss.{Entry, Source}

import scala.collection.JavaConversions._
import scala.collection.immutable.HashMap

object NewsReader {
  def main(args: Array[String]) {
    val properties = new Properties()
    properties.load(getClass.getResourceAsStream("/config.properties"))

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val feeds = properties.getProperty("feeds")
    var sources: DataStream[Entry] = null

    feeds.split(";").foreach((f) => {
      var feedInfo = f.split(",").toList
      var source = env.addSource(new Source(feedInfo.get(1))).name(feedInfo.get(0) + " Source")

      if (sources == null) {
        sources = source
      } else {
        sources = sources.union(source)
      }
    })

    val aggregate = sources
      .map(e => (e.link, e)).name("process entry")
      .keyBy(0)
      .flatMap(new DuplicateFilter[(String, Entry)]()).name("dedup")

    val stream = aggregate.map(e => e._2).name("reduce tuple")
      .filter {
        !_.title.isEmpty
      }.name("remove blanks")
      .map(e => Enrich.enrich(e)).name("enrich").startNewChain()

    val config = HashMap(
      "cluster.name" -> "elasticsearch",
      ElasticsearchSink.CONFIG_KEY_BULK_FLUSH_MAX_ACTIONS -> "1"
    )

    val transportAddresses = new util.ArrayList[InetSocketAddress]
    transportAddresses.add(new InetSocketAddress(InetAddress.getByName(properties.getProperty("elasticHostname")), properties.getProperty("elasticPort").toInt))

    stream.addSink(new ElasticsearchSink(new util.HashMap[String, String](config), transportAddresses, new ElasticsearchSinkFunction[Entry] {
      def createIndexRequest(element: Entry): IndexRequest = {
        Requests.indexRequest()
          .index("feeds")
          .`type`("item")
          .source(new util.HashMap[String, String](element.getMap))
      }

      override def process(t: Entry, runtimeContext: RuntimeContext, requestIndexer: RequestIndexer): Unit = {
        requestIndexer.add(createIndexRequest(t))
      }
    })).name("elastic output").setParallelism(1)

    env.execute("Read feed")
  }
}
