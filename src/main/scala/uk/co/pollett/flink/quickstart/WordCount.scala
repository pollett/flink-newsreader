package uk.co.pollett.flink.quickstart

import java.net.{InetAddress, InetSocketAddress}
import java.text.SimpleDateFormat

import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.elasticsearch2.{ElasticsearchSink, ElasticsearchSinkFunction, RequestIndexer}
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.Requests
import uk.co.pollett.flink.quickstart.rss.{Entry, Source}

object WordCount {
  def main(args: Array[String]) {
    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace")
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val uniqueStream = env.addSource(new Source("http://feeds.bbci.co.uk/news/rss.xml"))
      .map(e => (e.link, e)).name("create tuple")
      .keyBy(0)
      .flatMap(new DuplicateFilter[(String, Entry)]()).name("dedupe")

    val stream = uniqueStream.map(e => e._2).name("reduce tuple")
      .filter {
        !_.title.isEmpty
      }.name("remove blanks")

    val config = new java.util.HashMap[String, String]
    config.put("cluster.name", "elasticsearch")
    // This instructs the sink to emit after every element, otherwise they would be buffered
    config.put(ElasticsearchSink.CONFIG_KEY_BULK_FLUSH_MAX_ACTIONS, "1")

    val transportAddresses = new java.util.ArrayList[InetSocketAddress]
    transportAddresses.add(new InetSocketAddress(InetAddress.getByName("hostname"), 10026))
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

    stream.addSink(new ElasticsearchSink(config, transportAddresses, new ElasticsearchSinkFunction[Entry] {
      def createIndexRequest(element: Entry): IndexRequest = {
        val json = new java.util.HashMap[String, String]
        json.put("title", element.title)
        json.put("link", element.link)
        json.put("desc", element.desc)
        json.put("date", dateFormat.format(element.date))

        Requests.indexRequest().index("feeds").`type`("item").source(json)
      }

      override def process(t: Entry, runtimeContext: RuntimeContext, requestIndexer: RequestIndexer): Unit = {
        requestIndexer.add(createIndexRequest(t))
      }
    })).name("elastic output").setParallelism(4)

    env.execute("Read feed")
  }
}
