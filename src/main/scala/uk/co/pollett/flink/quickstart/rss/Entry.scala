package uk.co.pollett.flink.quickstart.rss

import java.util.Date

case class Entry(title: String, desc: String, link: String, date: Date) extends Serializable