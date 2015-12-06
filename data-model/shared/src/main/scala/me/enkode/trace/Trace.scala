package me.enkode.trace

import me.enkode.logging.LoggingOps

import scala.util.Random

case class Trace(
  traceId: String,
  spanName: String,
  parentSpanName: Option[String] = None) {

  def complete(): Trace = {
    this
  }

  def span(newSpanName: String): Trace = {
    val originalParentName: Option[String] = this.parentSpanName
    val originalSpanName: String = this.spanName
    new Trace(this.traceId, newSpanName, Some(this.spanName)) {
      override def complete(): Trace = Trace(this.traceId, originalSpanName, originalParentName)
    }
  }
}

object Trace {
  object Headers {
    val traceId = "Trace-Id"
    val spanName = "Span-Name"
    val parentSpanName = "Parent-Span-Id"
  }

  val httpNameMappings = Seq(
    "traceId" → Headers.traceId,
    "spanName" → Headers.spanName,
    "parentSpanName" → Headers.parentSpanName)

  val httpNameMappingsByName = httpNameMappings.groupBy(_._1).map(x ⇒ x._1 → x._2.head._2)
  val httpNameMappingsByHeader = httpNameMappings.groupBy(_._2).map(x ⇒ x._1 → x._2.head._2)

  def apply(spanName: String): Trace = Trace(Random.alphanumeric.take(32).mkString, spanName, None)

  def fromMap(map: Map[String, String]): Trace = {
    require(map contains "traceId")
    require(map contains "spanName")
    Trace(map("traceId"), map("spanName"), map.get("parentSpanName"))
  }

  def fromHttpHeaders(headers: Map[String, String]): Trace  = {
    fromMap(headers.map(h ⇒ httpNameMappingsByHeader(h._1) → h._2))
  }

  implicit class TraceExt(val trace: Trace) extends AnyVal {
    def asMap: Map[String, String] = {
      Map(
        "traceId" → trace.traceId,
        "spanName" → trace.spanName
      ) ++ trace.parentSpanName.toList.map("parentSpanName" → _)
    }

    def toHttpHeaders: Map[String, String] = {
      asMap.map(x ⇒ httpNameMappingsByName(x._1) → x._2)
    }

    def log(implicit baseLogging: LoggingOps) = new TraceLogger(trace, baseLogging)
  }
}