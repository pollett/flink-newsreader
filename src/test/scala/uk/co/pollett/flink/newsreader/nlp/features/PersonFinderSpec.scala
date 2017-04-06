package uk.co.pollett.flink.newsreader.nlp.features

import org.scalatest.FlatSpec

class PersonFinderSpec extends FlatSpec {

  val personFinder = new PersonFinder
  val listWithPerson = List("this","is","text","about","John","Smith","and","his","house")

  "A list with a person" should "return the name" in {
    assert(personFinder.parse(listWithPerson) === List("John Smith"))
  }
}
