package me.enkode.todo.server.common

import org.slf4j.Logger

trait Logging {
  def logger: Logger
  
  type KeyValues = Seq[(String, Any)]

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

  def debug(message: ⇒ String, kvs: ⇒ KeyValues = Nil): Unit = {
    if (logger.isDebugEnabled) {
      if (kvs.isEmpty) {
        logger.debug(message)
      } else {
        logger.debug(s"$message: ${stringify(kvs)}")
      }
    }
  }

  def info(message: ⇒ String, kvs: ⇒ KeyValues = Nil): Unit = {
    if (logger.isInfoEnabled) {
      if (kvs.isEmpty) {
        logger.info(message)
      } else {
        logger.info(s"$message: ${stringify(kvs)}")
      }
    }
  }

  def warn(message: ⇒ String, kvs: ⇒ KeyValues = Nil): Unit = {
    if (logger.isWarnEnabled) {
      if (kvs.isEmpty) {
        logger.warn(message)
      } else {
        logger.warn(s"$message: ${stringify(kvs)}")
      }
    }
  }

  def warn(message: ⇒ String, t: Throwable, kvs: ⇒ KeyValues): Unit = {
    if (logger.isInfoEnabled) {
      if (kvs.isEmpty) {
        logger.warn(message, t)
      } else {
        logger.warn(s"$message: ${stringify(kvs)}", t)
      }
    }
  }

  def error(message: ⇒ String, kvs: ⇒ KeyValues = Nil): Unit = {
    if (logger.isWarnEnabled) {
      if (kvs.isEmpty) {
        logger.error(message)
      } else {
        logger.error(s"$message: ${stringify(kvs)}")
      }
    }
  }

  def error(message: ⇒ String, t: Throwable, kvs: ⇒ KeyValues): Unit = {
    if (logger.isErrorEnabled) {
      if (kvs.isEmpty) {
        logger.error(message, t)
      } else {
        logger.error(s"$message: ${stringify(kvs)}", t)
      }
    }
  }
}
