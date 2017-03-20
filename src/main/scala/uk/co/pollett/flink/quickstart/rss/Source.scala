package uk.co.pollett.flink.quickstart.rss

import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext

class Source(url: String) extends RichParallelSourceFunction[Entry] {
  var running = true

  override def cancel(): Unit = {
    running = false
  }

  override def run(sourceContext: SourceContext[Entry]): Unit = {
    val feed = new Parser(url)

    while (running) {
      sourceContext.getCheckpointLock.synchronized {
        sourceContext.collect(feed.one())
      }
      Thread.sleep(5000L)
    }
  }
}
