package me.enkode.todo.server.common

import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.{Directives ⇒ AkkaDirectives}
import me.enkode.trace.Trace

import scala.collection.immutable._

trait Directives extends AkkaDirectives {

  def tracedComplete[T: ToResponseMarshaller](m: ⇒ T)(implicit trace: Trace) = {
    val headers: Seq[HttpHeader] = List(
      RawHeader(Trace.Headers.traceId, trace.traceId), RawHeader(Trace.Headers.spanName, trace.spanName )
    ) ++ (trace.parentSpanName.toList map { parentSpanName ⇒ RawHeader(Trace.Headers.parentSpanName, parentSpanName) })

    respondWithHeaders(headers) {
      complete(m)
    }
  }

  def toTrace(spanName: String): (Option[String], Option[String], Option[String]) ⇒ Trace = {
    case (Some(traceId), Some(spanId), parentId) ⇒ Trace(traceId, spanId, parentId).span(spanName)
    case _ ⇒ Trace(spanName)
  }

  def trace(spanName: String) = {
    (optionalHeaderValueByName(Trace.Headers.traceId) & optionalHeaderValueByName(Trace.Headers.spanName) & optionalHeaderValueByName(Trace.Headers.parentSpanName))
      .as(toTrace(spanName))
  }
}
