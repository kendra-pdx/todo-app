package me.enkode.todo.server.common

import me.enkode.logging.LoggingOps
import org.slf4j.{MDC, Logger}

trait Logging extends LoggingOps { self ⇒
  def logger: Logger

  implicit val loggingOps = self

  override def withMdc[R](mdc: KeyValues)(eval: ⇒ R) = {
    val original = MDC.getCopyOfContextMap
    mdc.foreach { case (k, v) ⇒
      if (k != null && v != null){
        MDC.put(k, v.toString)
      }
    }
    val result = eval
    if (original != null) {
      MDC.setContextMap(original)
    } else {
      MDC.setContextMap(java.util.Collections.emptyMap())
    }
    result
  }

  protected def stringify(kvs: KeyValues): String = {
    def quote(s: String): String = {
      val separators = Set(" ", "\t", ",")
      if (separators.exists(s contains _)) {
        s"""'${s.replaceAllLiterally("'", "\\'")}'"""
      } else {
        s
      }
    }

    (kvs map { case (k, v) ⇒ s"$k=${quote(v.toString)}"}).mkString(", ")
  }

  override def debug(message: ⇒ String, kvs: ⇒ KeyValues = Nil): Unit = {
    if (logger.isDebugEnabled) {
      if (kvs.isEmpty) {
        logger.debug(message)
      } else {
        logger.debug(s"$message: ${stringify(kvs)}")
      }
    }
  }

  override def info(message: ⇒ String, kvs: ⇒ KeyValues = Nil): Unit = {
    if (logger.isInfoEnabled) {
      if (kvs.isEmpty) {
        logger.info(message)
      } else {
        logger.info(s"$message: ${stringify(kvs)}")
      }
    }
  }

  override def warn(message: ⇒ String, kvs: ⇒ KeyValues = Nil): Unit = {
    if (logger.isWarnEnabled) {
      if (kvs.isEmpty) {
        logger.warn(message)
      } else {
        logger.warn(s"$message: ${stringify(kvs)}")
      }
    }
  }

  override def warn(message: ⇒ String, t: Throwable, kvs: ⇒ KeyValues): Unit = {
    if (logger.isInfoEnabled) {
      if (kvs.isEmpty) {
        logger.warn(message, t)
      } else {
        logger.warn(s"$message: ${stringify(kvs)}", t)
      }
    }
  }

  override def error(message: ⇒ String, kvs: ⇒ KeyValues = Nil): Unit = {
    if (logger.isWarnEnabled) {
      if (kvs.isEmpty) {
        logger.error(message)
      } else {
        logger.error(s"$message: ${stringify(kvs)}")
      }
    }
  }

  override def error(message: ⇒ String, t: Throwable, kvs: ⇒ KeyValues): Unit = {
    if (logger.isErrorEnabled) {
      if (kvs.isEmpty) {
        logger.error(message, t)
      } else {
        logger.error(s"$message: ${stringify(kvs)}", t)
      }
    }
  }
}
