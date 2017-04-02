package uk.co.pollett.flink.newsreader.nlp

import opennlp.tools.namefind.NameFinderME
import opennlp.tools.util.Span

import scala.collection.mutable.ListBuffer

abstract class NameFinder {
  def parse(text: List[String]): List[String]

  protected def findWithModel(text: List[String], model: NameFinderME): List[String] = {
    var out = ListBuffer[String]()

    try {
      val nameSpans: Array[Span] = model.find(text.toArray)
      model.clearAdaptiveData()

      for (name <- nameSpans) {
        val sb: StringBuilder = new StringBuilder
        for (si <- name.getStart until name.getEnd) {
          sb.append(text(si).trim).append(" ")
        }
        out += sb.toString().trim
      }
      out.toList.distinct
    } catch {
      case e: Exception =>
        println(e)
        List("Exception", e.getMessage)
    }
  }
}
