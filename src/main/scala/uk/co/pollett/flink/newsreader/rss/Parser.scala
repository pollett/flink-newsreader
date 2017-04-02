package uk.co.pollett.flink.newsreader.rss

import java.net.URL
import java.util

import com.sun.syndication.feed.synd.{SyndEntryImpl, SyndFeed}
import com.sun.syndication.io.{SyndFeedInput, XmlReader}


class Parser(url: String) {
  var feed: SyndFeed = _
  var i: util.Iterator[_] = _

  def one(): Entry = {
    if (i == null || !i.hasNext) {
      reset()
    }
    val e = i.next().asInstanceOf[SyndEntryImpl]
    Entry(
      title = e.getTitle,
      desc = e.getDescription.getValue,
      link = e.getLink,
      date = e.getPublishedDate,
      source = feed.getTitle,
      None,
      None,
      None,
      None
    )
  }

  def reset(): Unit = {
    val urlu = new URL(url)
    println(urlu)
    val xmlreader = new XmlReader(urlu)

    feed = new SyndFeedInput().build(xmlreader)

    i = feed.getEntries.iterator()
  }
}
