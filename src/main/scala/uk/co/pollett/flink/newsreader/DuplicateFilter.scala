package uk.co.pollett.flink.newsreader

import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.configuration.Configuration
import org.apache.flink.util.Collector

object DuplicateFilter {
  val descriptor: ValueStateDescriptor[Boolean] = new ValueStateDescriptor[Boolean]("seen", classOf[Boolean])
}

class DuplicateFilter[T] extends RichFlatMapFunction[T, T] {
  var operatorState: ValueState[Boolean] = _

  override def open(parameters: Configuration): Unit = {
    operatorState = this.getRuntimeContext.getState(DuplicateFilter.descriptor)
  }

  override def flatMap(value: T, out: Collector[T]): Unit = {
    if (!operatorState.value()) {
      out.collect(value)
      operatorState.update(true)
    }
  }
}
