package uk.co.pollett.flink.newsreader.nlp

import org.scalatest.FlatSpec

class PlaceFinderSpec extends FlatSpec {

  val placeFinder = new PlaceFinder
  val listWithPlace = List("this","is","text","about","New","Orleans")

  "A list with a place" should "return the place" in {
    assert(placeFinder.parse(listWithPlace) === List("New Orleans"))
  }
}