package uk.co.pollett.flink.newsreader.rss

import org.apache.flink.streaming.util.serialization.SerializationSchema

class Serializer extends SerializationSchema[(String, Entry, Int)] {
  override def serialize(element: (String, Entry, Int)): Array[Byte] = {
    (element._1 + " - " + element._3 + "\n").getBytes
  }
}
