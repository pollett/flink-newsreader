package uk.co.pollett.flink.quickstart.nlp

import opennlp.tools.namefind.{NameFinderME, TokenNameFinderModel}

class PersonFinder extends NameFinder {
  private val modelIn = getClass.getResourceAsStream("/en-ner-person.bin")
  private val model = new TokenNameFinderModel(modelIn)
  private val nameFinder = new NameFinderME(model)
  modelIn.close()

  override def parse(text: List[String]): List[String] = {
    findWithModel(text, nameFinder)
  }
}