package me.enkode.todo.server.common

import upickle._
import upickle.default._

trait μPickleMarshallingSupport {

  import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
  import akka.http.scaladsl.model.MediaTypes.`application/json`
  import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
  import akka.stream.Materializer

  import scala.language.implicitConversions

  implicit def μPickleUnmarshallerConverter[T](reader: Reader[T])(implicit mat: Materializer): FromEntityUnmarshaller[T] =
    μPickleUnmarshaller(reader, mat)

  implicit def μPickleUnmarshaller[T](implicit reader: Reader[T], mat: Materializer): FromEntityUnmarshaller[T] =
    μPickleValueUnmarshaller.map(reader.read)

  implicit def μPickleValueUnmarshaller(implicit mat: Materializer): FromEntityUnmarshaller[Js.Value] =
    Unmarshaller.byteStringUnmarshaller.forContentTypes(`application/json`).mapWithCharset { (data, charset) ⇒
      val i = data.decodeString(charset.nioCharset.name)
      val jsValue: Js.Value = json.read(i)
      jsValue
    }

  implicit def μPickleMarshallerConverter[T](writer: Writer[T]): ToEntityMarshaller[T] =
    μPickleMarshaller[T](writer)

  implicit def μPickleMarshaller[T](implicit writer: Writer[T]): ToEntityMarshaller[T] =
    μPickleValueMarshaller compose writer.write

  implicit def μPickleValueMarshaller(): ToEntityMarshaller[Js.Value] =
    Marshaller.StringMarshaller.wrap(`application/json`)(json.write(_))
}
