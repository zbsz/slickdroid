package scala.slick.android

import scala.slick.ast.BaseTypedType
import android.database.sqlite.SQLiteStatement
import android.database.Cursor

/** A JdbcType object represents a Scala type that can be
  * used as a column type in the database. Implicit JdbcTypes
  * for the standard types of a profile are provided by the drivers. */
trait AndroidType[@specialized(Byte, Short, Int, Long, Char, Float, Double, Boolean) T] extends BaseTypedType[T] { self =>
  /** The constant from java.sql.Types that is used for setting parameters
    * of the type to NULL. */
  def sqlType: Int
  /** The default name for the SQL type that is used for column declarations. */
  def sqlTypeName: String
  /** Set a parameter of the type. */
  def setValue(v: T, p: SQLiteStatement, idx: Int): Unit
  /** Set a parameter of the type to NULL. */
  def setNull(p: SQLiteStatement, idx: Int): Unit
  /** Set an Option parameter of the type. */
  final def setOption(v: Option[T], p: SQLiteStatement, idx: Int): Unit = v match {
    case Some(v) => setValue(v, p, idx)
    case None => setNull(p, idx)
  }

  /** Get a result column of the type. For reference types, SQL NULL values
    * are returned as `null`, for primitive types a default value is returned. */
  def getValue(r: Cursor, idx: Int): T
  /** Check if the value in cursor is NULL. */
  def isNull(r: Cursor, idx: Int): Boolean

  /** Convert a value to a SQL literal.
    * This should throw a `SlickException` if `hasLiteralForm` is false. */
  def valueToSQLLiteral(value: T): String

  /** Indicates whether values of this type have a literal representation in
    * SQL statements.
    * This must return false if `valueToSQLLiteral` throws a SlickException.
    * QueryBuilder (and driver-specific subclasses thereof) uses this method
    * to treat LiteralNodes as volatile (i.e. using bind variables) as needed. */
  def hasLiteralForm: Boolean

  override def toString = {
    def cln = getClass.getName
    val pos = cln.lastIndexOf("$AndroidTypes$")
    val s = if(pos >= 0) cln.substring(pos+11) else cln
    val s2 = if(s.endsWith("AndroidType")) s.substring(0, s.length-8) else s
    s2 + "/" + sqlTypeName
  }
}
