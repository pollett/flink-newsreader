package uk.co.pollett.flink.quickstart.rss

import java.text.SimpleDateFormat
import java.util.Date

import scala.collection.immutable.HashMap

object Entry {
  val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
}

case class Entry(title: String, desc: String, link: String, date: Date, source: String) extends Serializable {
  def getMap: HashMap[String, String] = {
    HashMap(
      "date" -> Entry.dateFormat.format(this.date),
      "title" -> this.title,
      "desc" -> this.desc,
      "link" -> this.link,
      "source" -> this.source
    )
  }
}