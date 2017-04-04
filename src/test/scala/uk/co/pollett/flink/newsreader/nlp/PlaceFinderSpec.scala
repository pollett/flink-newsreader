package uk.co.pollett.flink.newsreader.nlp

import org.scalatest.FlatSpec
import uk.co.pollett.flink.newsreader.nlp.classify.Sentiment
import uk.co.pollett.flink.newsreader.nlp.features.PlaceFinder

class PlaceFinderSpec extends FlatSpec {

  val placeFinder = new PlaceFinder
  val sentimentFinder = new Sentiment
  val listWithPlace = List("this","is","text","about","New","Orleans")
  val listWithSentimentPos = List("this","is","awesome")
  val listWithSentimentNeg = List("this","is","rubbish")

  "A list with a place" should "return the place" in {
    assert(placeFinder.parse(listWithPlace) === List("New Orleans"))
  }

  "A list with a meaning" should "return it's sentiment" in {
    assert(sentimentFinder.categorize(listWithSentimentPos) === "positive")
    assert(sentimentFinder.categorize(listWithSentimentNeg) === "negative")
  }
}