package uk.co.pollett.flink.newsreader.nlp.features

import opennlp.tools.namefind.{NameFinderME, TokenNameFinderModel}

class OrganizationFinder extends NameFinder {
  private val modelIn = getClass.getResourceAsStream("/en-ner-organization.bin")
  private val model = new TokenNameFinderModel(modelIn)
  private val nameFinder = new NameFinderME(model)
  modelIn.close()

  def parse(text: List[String]): List[String] = {
    findWithModel(text, nameFinder)
  }
}