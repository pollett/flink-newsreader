package uk.co.pollett.flink.quickstart.nlp

import opennlp.tools.tokenize.{TokenizerME, TokenizerModel}

object Tokenizer {
  val stopWords: List[String] = List[String]("a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", "is", "it", "no", "not", "of", "on", "or", "such", "that", "the", "their", "then", "there", "these", "they", "this", "to", "was", "will", "with")
}

class Tokenizer {
  private val modelIn = getClass.getResourceAsStream("/en-token.bin")
  private val model = new TokenizerModel(modelIn)
  private val tokenizer = new TokenizerME(model)
  modelIn.close()

  def tokenize(text: String): List[String] = {
    val tokens = tokenizer.tokenize(text).map{
      _.trim()
    }
      .filterNot(_.isEmpty)
      .filterNot((e) => Tokenizer.stopWords.contains(e.toLowerCase()))
      .filterNot(_.forall(!_.isLetter))

    tokens.toList
  }
}