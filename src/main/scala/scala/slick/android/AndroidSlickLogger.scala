package scala.slick.android

import scala.slick.ast.{Dump, Node}
import android.util.Log

final class AndroidSlickLogger(tag: String) {
  @inline
  def debug(msg: => String, n: => Node): Unit = debug(msg+"\n"+Dump.get(n, prefix = "  "))

  @inline
  def isDebugEnabled = Log.isLoggable(tag, Log.DEBUG)

  @inline
  def error(msg: => String) { Log.e(tag, msg) }

  @inline
  def error(msg: => String, t: Throwable) { Log.e(tag, msg, t) }

  @inline
  def warn(msg: => String) { Log.w(tag, msg) }

  @inline
  def warn(msg: => String, t: Throwable) { Log.w(tag, msg, t) }

  @inline
  def info(msg: => String) { Log.i(tag, msg) }

  @inline
  def info(msg: => String, t: Throwable) { Log.i(tag, msg, t) }

  @inline
  def debug(msg: => String) { Log.d(tag, msg) }

  @inline
  def debug(msg: => String, t: Throwable) { Log.d(tag, msg, t) }

  @inline
  def trace(msg: => String) { Log.v(tag, msg) }

  @inline
  def trace(msg: => String, t: Throwable) { Log.v(tag, msg, t) }
}

trait Logging {
  protected[this] lazy val logger = new AndroidSlickLogger(getClass.getName)
}

