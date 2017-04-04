package uk.co.pollett.flink.newsreader.rss

import java.text.SimpleDateFormat
import java.util.Date

import scala.collection.immutable.HashMap

object Entry {
  val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
}

case class Entry(
                  title: String,
                  desc: String,
                  link: String,
                  date: Date,
                  source: String,
                  body: Option[String],
                  places: Option[List[String]],
                  people: Option[List[String]],
                  organizations: Option[List[String]],
                  sentiment: Option[String]
                ) extends Serializable {
  def getMap: HashMap[String, String] = {
    HashMap(
      "date" -> Entry.dateFormat.format(date),
      "title" -> title,
      "desc" -> desc,
      "link" -> link,
      "source" -> source,
      "body" -> (if (body.isDefined) body.get else ""),
      "places" -> (if (places.isDefined) places.get.mkString(", ") else ""),
      "people" -> (if (people.isDefined) people.get.mkString(", ") else ""),
      "organizations" -> (if (organizations.isDefined) organizations.get.mkString(", ") else ""),
      "sentiment" -> (if (sentiment.isDefined) sentiment.get else "")
    )
  }
}