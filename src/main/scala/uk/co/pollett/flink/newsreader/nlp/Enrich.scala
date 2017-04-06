package uk.co.pollett.flink.newsreader.nlp

import com.gravity.goose.{Configuration, Goose}
import uk.co.pollett.flink.newsreader.nlp.classify.Sentiment
import uk.co.pollett.flink.newsreader.nlp.features.{OrganizationFinder, PersonFinder, PlaceFinder}
import uk.co.pollett.flink.newsreader.rss.Entry

object Enrich {
  def enrich(e: Entry): Entry = {
    var c = new Configuration
    c.enableImageFetching = false
    def goose = new Goose(c)

    val article = goose.extractContent(e.link)

    var tokenizer = new Tokenizer
    val bodyWords = tokenizer.tokenize(article.cleanedArticleText)

    var placeF = new PlaceFinder
    val places = placeF.parse(bodyWords)

    var organizationF = new OrganizationFinder
    val orgs = organizationF.parse(bodyWords)

    var personF = new PersonFinder
    val people = personF.parse(bodyWords)

    var sentimentFinder = new Sentiment
    val sentiment = sentimentFinder.categorize(bodyWords)

    e.copy(body = Some(article.cleanedArticleText), places = Some(places), people = Some(people), organizations = Some(orgs), sentiment = Some(sentiment))
  }
}
