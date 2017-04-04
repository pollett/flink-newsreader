package uk.co.pollett.flink.newsreader.nlp.classify

import java.io.FileInputStream

import opennlp.tools.doccat.{DoccatModel, DocumentCategorizerME, DocumentSampleStream}
import opennlp.tools.util.PlainTextByLineStream

class Sentiment {
  def train(): Unit = {
    val dataIn = getClass.getResourceAsStream("/sentimentdatatext")
    val lineStream = new PlainTextByLineStream(dataIn, "UTF-8")
    val sampleStream = new DocumentSampleStream(lineStream)
    // Specifies the minimum number of times a feature must be seen
    val cutoff = 2
    val trainingIterations = 30

    def model = DocumentCategorizerME.train("en", sampleStream)

    import java.io.BufferedOutputStream
    import java.io.FileOutputStream
    def modelOut = new BufferedOutputStream(new FileOutputStream("/tmp/output.bin"))
    model.serialize(modelOut)
    println("model out")
    Thread.sleep(30000)
  }

  def categorize(text: List[String]): String = {
    def modelIn = getClass.getResourceAsStream("/en-sentiment.bin")
    def model = new DoccatModel(modelIn)
    def categorizer = new DocumentCategorizerME(model)

    def outcomes = categorizer.categorize(text.toArray)
    def category = categorizer.getBestCategory(outcomes)

    category
  }
}
