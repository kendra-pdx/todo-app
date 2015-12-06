package me.enkode.logging

trait LoggingOps {
  type KeyValues = Seq[(String, Any)]

  def withMdc[R](mdc: KeyValues)(f: ⇒ R): R

  def debug(message: ⇒ String, kvs: ⇒ KeyValues = Nil): Unit
  def info(message: ⇒ String, kvs: ⇒ KeyValues = Nil): Unit
  def warn(message: ⇒ String, kvs: ⇒ KeyValues = Nil): Unit
  def error(message: ⇒ String, kvs: ⇒ KeyValues = Nil): Unit
  def warn(message: ⇒ String, t: Throwable, kvs: ⇒ KeyValues): Unit
  def error(message: ⇒ String, t: Throwable, kvs: ⇒ KeyValues): Unit
  def warn(message: ⇒ String, t: Throwable): Unit = warn(message, t, Nil)
  def error(message: ⇒ String, t: Throwable): Unit = error(message, t, Nil)
}
