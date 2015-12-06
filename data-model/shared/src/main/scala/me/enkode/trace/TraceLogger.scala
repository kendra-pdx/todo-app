package me.enkode.trace

import me.enkode.logging.LoggingOps

class TraceLogger(trace: Trace, baseLogger: LoggingOps) extends LoggingOps {

  def withTraceMdc[R](f: ⇒ R) = withMdc(trace.asMap.toSeq)(f)

  override def withMdc[R](mdc: KeyValues)(f: ⇒ R) = baseLogger.withMdc(mdc)(f)

  override def debug(message: ⇒ String, kvs: ⇒ KeyValues) =
    withTraceMdc { baseLogger.debug(message, kvs) }

  override def info(message: ⇒ String, kvs: ⇒ KeyValues) =
    withTraceMdc { baseLogger.info(message, kvs) }

  override def warn(message: ⇒ String, kvs: ⇒ KeyValues) =
    withTraceMdc { baseLogger.warn(message, kvs) }

  override def error(message: ⇒ String, kvs: ⇒ KeyValues) =
    withTraceMdc { baseLogger.error(message, kvs) }

  override def warn(message: ⇒ String, t: Throwable, kvs: ⇒ KeyValues) =
    withTraceMdc { baseLogger.warn(message, t, kvs) }

  override def error(message: ⇒ String, t: Throwable, kvs: ⇒ KeyValues) =
    withTraceMdc { baseLogger.error(message, t, kvs) }
}
