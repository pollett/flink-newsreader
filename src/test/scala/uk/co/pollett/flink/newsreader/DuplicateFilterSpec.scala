package uk.co.pollett.flink.newsreader

import org.apache.flink.api.common.state.ValueState
import org.apache.flink.util.Collector

import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec

class DuplicateFilterSpec extends FlatSpec with MockFactory {
  val data: String = "value"

  val duplicateFilter: DuplicateFilter[String] = new DuplicateFilter[String]()
  val collector: Collector[String] = mock[Collector[String]]

  val dummyOperatorState: ValueState[Boolean] = new ValueState[Boolean] {
    var state: Boolean = false
    override def update(value: Boolean): Unit = { state = value }

    override def value(): Boolean = state

    override def clear(): Unit = { state = false }
  }
  duplicateFilter.operatorState = dummyOperatorState

  "A first send" should "be emitted" in {
    (collector.collect _).expects(data) // output data
    duplicateFilter.flatMap(data, collector)
  }

  "A second send" should "not emit" in {
    (collector.collect _).expects(*).never // shouldn't emit
    duplicateFilter.flatMap(data, collector)
  }
}
