package uk.co.pollett.flink.newsreader.nlp.classify

import org.scalatest.FlatSpec

class SentimentSpec extends FlatSpec{

  val sentimentFinder = new Sentiment
  val listWithSentimentPos = List("this","is","awesome")
  val listWithSentimentNeg = List("this","is","rubbish")

  "A list with a meaning" should "return it's sentiment" in {
    assert(sentimentFinder.categorize(listWithSentimentPos) === "positive")
    assert(sentimentFinder.categorize(listWithSentimentNeg) === "negative")
  }
}
