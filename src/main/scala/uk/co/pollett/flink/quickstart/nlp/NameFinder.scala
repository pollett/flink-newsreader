package uk.co.pollett.flink.quickstart.nlp

import opennlp.tools.util.Span
import opennlp.tools.namefind.NameFinderME
import opennlp.tools.namefind.TokenNameFinderModel
import opennlp.tools.tokenize.TokenizerME
import opennlp.tools.tokenize.TokenizerModel

import scala.collection.mutable.ListBuffer

class NameFinder {
  private val tokenModelIn = getClass.getResourceAsStream("/en-token.bin")
  private val tokenModel = new TokenizerModel(tokenModelIn)
  private val tokenizer = new TokenizerME(tokenModel)

  private val modelLocationIn = getClass.getResourceAsStream("/en-ner-location.bin")
  private val modelLocation = new TokenNameFinderModel(modelLocationIn)
  private val nameFinderLocation = new NameFinderME(modelLocation)

  private val modelPersonIn = getClass.getResourceAsStream("/en-ner-person.bin")
  private val modelPerson = new TokenNameFinderModel(modelPersonIn)
  private val nameFinderPerson = new NameFinderME(modelPerson)

  private val modelOrganizationIn = getClass.getResourceAsStream("/en-ner-organization.bin")
  private val modelOrganization = new TokenNameFinderModel(modelOrganizationIn)
  private val nameFinderOrganization = new NameFinderME(modelOrganization)

  def findLocation(text: List[String]): List[String] = {
    findWithModel(text, nameFinderLocation)
  }

  def findPerson(text: List[String]): List[String] = {
    findWithModel(text, nameFinderPerson)
  }

  def findOrganization(text: List[String]): List[String] = {
    findWithModel(text, nameFinderOrganization)
  }

  private def findWithModel(text: List[String], model: NameFinderME): List[String] = {
    var out = ListBuffer[String]()
    model.clearAdaptiveData()

    try {
      val nameSpans: Array[Span] = model.find(text.toArray)

      for (name <- nameSpans) {
        val sb: StringBuilder = new StringBuilder
        for (si <- name.getStart to name.getEnd) {
          sb.append(text(si)).append(" ")
        }
        out += sb.toString().trim
      }
      return out.toList
    } catch {
      case e: Exception =>
        println(e)
    }
    List("None found")
  }

  def tokenize(text: String): List[String] = {
    val tokens = tokenizer.tokenize(text)

    tokens.toList
  }
}
