package uk.co.pollett.flink.newsreader.nlp

import java.util.Date

import org.scalatest.FlatSpec
import org.scalamock.scalatest.MockFactory
import uk.co.pollett.flink.newsreader.rss.Entry

class EnrichSpec extends FlatSpec with MockFactory {
  def e = Entry("", "","https://blog.pollett.co.uk/chat/aws-s3-at-speed/", new Date(), "", None, None, None, None, None)

  "An entry" should "return more content" in {
    def enriched = Enrich.enrich(e)

    assert(enriched.body.isDefined)
  }
}
