package uk.co.pollett.flink.newsreader.nlp

import com.gravity.goose.{Configuration, Goose}
import uk.co.pollett.flink.newsreader.rss.Entry

object Enrich {
  def enrich(e: Entry): Entry = {
    var c = new Configuration
    c.enableImageFetching = false
    val goose = new Goose(c)
    val article = goose.extractContent(e.link)

    var tokenizer = new Tokenizer
    val bodyWords = tokenizer.tokenize(article.cleanedArticleText)

    var placeFinder = new PlaceFinder
    val places = placeFinder.parse(bodyWords)

    var organizationFinder = new OrganizationFinder
    val orgs = organizationFinder.parse(bodyWords)

    var personFinder = new PersonFinder
    val people = personFinder.parse(bodyWords)

    e.copy(body = Some(article.cleanedArticleText), places = Some(places), people = Some(people), organizations = Some(orgs))
  }
}
