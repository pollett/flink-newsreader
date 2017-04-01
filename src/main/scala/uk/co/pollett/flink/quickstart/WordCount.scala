package uk.co.pollett.flink.quickstart

import java.net.{InetAddress, InetSocketAddress}
import java.util
import java.util.Properties

import com.gravity.goose.{Configuration, Goose}
import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.elasticsearch2.{ElasticsearchSink, ElasticsearchSinkFunction, RequestIndexer}
import org.apache.lucene.analysis.en.EnglishAnalyzer
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.Requests
import uk.co.pollett.flink.quickstart.nlp._
import uk.co.pollett.flink.quickstart.rss.{Entry, Source}

import scala.collection.JavaConversions._
import scala.collection.immutable.HashMap

object WordCount {
  def main(args: Array[String]) {
    val properties = new Properties()
    properties.load(getClass.getResourceAsStream("/config.properties"))

    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "trace")
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val reutersSource = env.addSource(new Source("http://feeds.reuters.com/Reuters/UKTopNews?format=rss")).name("Reuters source")
    val bbcSource = env.addSource(new Source("http://feeds.bbci.co.uk/news/rss.xml")).name("BBC Source")

    val aggregate = reutersSource.union(bbcSource)
      .map(e => (e.link, e)).name("process entry")
      .keyBy(0)
      .flatMap(new DuplicateFilter[(String, Entry)]()).name("dedup")

    val stream = aggregate.map(e => e._2).name("reduce tuple")
      .filter {
        !_.title.isEmpty
      }.name("remove blanks")
      .map(e => enrich(e)).name("enrich").startNewChain()

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

  def enrich(e: Entry): Entry = {
    var c = new Configuration
    c.enableImageFetching = false
    val goose = new Goose(c)
    val article = goose.extractContent(e.link)

    var tokenizer = new Tokenizer
    val bodyWords = tokenizer.tokenize(article.cleanedArticleText)

    val filteredWords = bodyWords.diff(EnglishAnalyzer.getDefaultStopSet.toList)

    tokenizer.close()

    var placeFinder = new PlaceFinder
    val places = placeFinder.parse(filteredWords)
    placeFinder.close()

    var organizationFinder = new OrganizationFinder
    val orgs = organizationFinder.parse(filteredWords)
    organizationFinder.close()

    var personFinder = new PersonFinder
    val people = personFinder.parse(filteredWords)
    personFinder.close()

    e.copy(body=Some(article.cleanedArticleText), places = Some(places), people = Some(people), organizations = Some(orgs))
  }
}
