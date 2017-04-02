package uk.co.pollett.flink.newsreader.nlp

import opennlp.tools.util.Span
import opennlp.tools.namefind.NameFinderME

import scala.collection.mutable.ListBuffer

abstract class NameFinder {
  protected def findWithModel(text: List[String], model: NameFinderME): List[String] = {
    var out = ListBuffer[String]()

    try {
      val nameSpans: Array[Span] = model.find(text.toArray)
      model.clearAdaptiveData()

      for (name <- nameSpans) {
        val sb: StringBuilder = new StringBuilder
        for (si <- name.getStart to name.getEnd) {
          sb.append(text(si)).append(" ")
        }
        out += sb.toString().trim
      }
      out.toList
    } catch {
      case e: Exception =>
        println(e)
        List("Exception", e.getMessage)
    }
  }

  def parse(text: List[String]): List[String]
}
