package uk.co.pollett.flink.quickstart.nlp

import opennlp.tools.tokenize.{TokenizerME, TokenizerModel}

class Tokenizer {
  private val tokenModelIn = getClass.getResourceAsStream("/en-token.bin")
  private val tokenModel = new TokenizerModel(tokenModelIn)
  private val tokenizer = new TokenizerME(tokenModel)

  def tokenize(text: String): List[String] = {
    val tokens = tokenizer.tokenize(text)

    tokens.toList
  }

  def close(): Unit = {
    tokenModelIn.close()
  }
}