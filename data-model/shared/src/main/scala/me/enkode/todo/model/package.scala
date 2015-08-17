package me.enkode.todo

import java.util.UUID

import upickle._
import upickle.default._

package object model {
  implicit val uuidReader = Reader[UUID] {
    case Js.Str(uuid) ⇒ UUID.fromString(uuid)
  }

  implicit val uuidWriter = Writer[UUID] {
    case uuid: UUID ⇒ Js.Str(uuid.toString)
  }
}
