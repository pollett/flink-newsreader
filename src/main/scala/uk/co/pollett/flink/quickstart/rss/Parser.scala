package uk.co.pollett.flink.quickstart.rss

import java.net.URL
import java.util

import com.sun.syndication.feed.synd.SyndEntryImpl
import com.sun.syndication.io.{SyndFeedInput, XmlReader}


class Parser(url: String) {
  var i: util.Iterator[_] = _

  def one(): Entry = {
    if (i == null || !i.hasNext) {
      reset()
    }
    val e = i.next().asInstanceOf[SyndEntryImpl]
    Entry(e.getTitle, e.getDescription.getValue, e.getLink, e.getPublishedDate)
  }

  def reset(): Unit = {
    val urlu = new URL(url)
    println(urlu)
    val xmlreader = new XmlReader(urlu)

    val feed = new SyndFeedInput().build(xmlreader)

    i = feed.getEntries.iterator()
  }
}
