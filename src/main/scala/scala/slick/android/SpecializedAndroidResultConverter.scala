package scala.slick.android

import scala.language.existentials
import scala.slick.relational._
import scala.slick.SlickException
import scala.slick.ast.{Dump, ScalaBaseType}
import android.database.Cursor
import android.database.sqlite.SQLiteStatement

/** Factory methods for JdbcResultConverters which are manually specialized on
  * the underlying AndroidType. A generic implementation of this factory still
  * provides allocation free call paths but performs almost 100% slower in the
  * fast path benchmark. */
object SpecializedAndroidResultConverter {
  /** Create a new type-specialized `BaseResultConverter` for the given type-specialized `AndroidType` */
  def base[T](ti: AndroidType[T], name: String, idx: Int) = (ti.scalaType match {
    case ScalaBaseType.byteType => new BaseResultConverter[Byte](ti.asInstanceOf[AndroidType[Byte]], name, idx)
    case ScalaBaseType.shortType => new BaseResultConverter[Short](ti.asInstanceOf[AndroidType[Short]], name, idx)
    case ScalaBaseType.intType => new BaseResultConverter[Int](ti.asInstanceOf[AndroidType[Int]], name, idx)
    case ScalaBaseType.longType => new BaseResultConverter[Long](ti.asInstanceOf[AndroidType[Long]], name, idx)
    case ScalaBaseType.charType => new BaseResultConverter[Char](ti.asInstanceOf[AndroidType[Char]], name, idx)
    case ScalaBaseType.floatType => new BaseResultConverter[Float](ti.asInstanceOf[AndroidType[Float]], name, idx)
    case ScalaBaseType.doubleType => new BaseResultConverter[Double](ti.asInstanceOf[AndroidType[Double]], name, idx)
    case ScalaBaseType.booleanType => new BaseResultConverter[Boolean](ti.asInstanceOf[AndroidType[Boolean]], name, idx)
    case _ => new BaseResultConverter[T](ti.asInstanceOf[AndroidType[T]], name, idx) {
      override def read(pr: Cursor) = {
        val v = ti.getValue(pr, idx - 1)
        if(v.asInstanceOf[AnyRef] eq null) throw new SlickException("Read NULL value ("+v+") for Cursor column "+name)
        v
      }
    }
  }).asInstanceOf[ResultConverter[AndroidResultConverterDomain, T]]

  /** Create a new type-specialized `OptionResultConverter` for the given type-specialized `AndroidType` */
  def option[T](ti: AndroidType[T], idx: Int) = (ti.scalaType match {
    case ScalaBaseType.byteType => new OptionResultConverter[Byte](ti.asInstanceOf[AndroidType[Byte]], idx)
    case ScalaBaseType.shortType => new OptionResultConverter[Short](ti.asInstanceOf[AndroidType[Short]], idx)
    case ScalaBaseType.intType => new OptionResultConverter[Int](ti.asInstanceOf[AndroidType[Int]], idx)
    case ScalaBaseType.longType => new OptionResultConverter[Long](ti.asInstanceOf[AndroidType[Long]], idx)
    case ScalaBaseType.charType => new OptionResultConverter[Char](ti.asInstanceOf[AndroidType[Char]], idx)
    case ScalaBaseType.floatType => new OptionResultConverter[Float](ti.asInstanceOf[AndroidType[Float]], idx)
    case ScalaBaseType.doubleType => new OptionResultConverter[Double](ti.asInstanceOf[AndroidType[Double]], idx)
    case ScalaBaseType.booleanType => new OptionResultConverter[Boolean](ti.asInstanceOf[AndroidType[Boolean]], idx)
    case _ => new OptionResultConverter[T](ti.asInstanceOf[AndroidType[T]], idx) {
      override def read(pr: Cursor) = {
        val v = ti.getValue(pr, idx - 1)
        if(v.asInstanceOf[AnyRef] eq null) None else Some(v)
      }
    }
  }).asInstanceOf[ResultConverter[AndroidResultConverterDomain, Option[T]]]
}

/** Specialized JDBC ResultConverter for non-`Option` values. */
class BaseResultConverter[@specialized(Byte, Short, Int, Long, Char, Float, Double, Boolean) T](val ti: AndroidType[T], val name: String, val idx: Int) extends ResultConverter[AndroidResultConverterDomain, T] {
  def read(pr: Cursor) = {
    if(ti.isNull(pr, idx - 1)) throw new SlickException("Read NULL value for Cursor column "+name)
    ti.getValue(pr, idx - 1)
  }
  def update(value: T, pr: Updater) = throw new SlickException("update on cursor not supported")
  def set(value: T, pp: SQLiteStatement) = if (value == null) ti.setNull(pp, idx) else ti.setValue(value, pp, idx)
  override def info = super.info + "(" + Dump + ti + Dump + s", idx=$idx, name=$name)"
  def width = 1
}

/** Specialized JDBC ResultConverter for handling values of type `Option[T]`.
  * Boxing is avoided when the result is `None`. */
class OptionResultConverter[@specialized(Byte, Short, Int, Long, Char, Float, Double, Boolean) T](val ti: AndroidType[T], val idx: Int) extends ResultConverter[AndroidResultConverterDomain, Option[T]] {
  def read(pr: Cursor) = {
    if(ti.isNull(pr, idx - 1)) None else Some(ti.getValue(pr, idx - 1))
  }
  def update(value: Option[T], pr: Updater) = throw new SlickException("update on cursor not supported")
  def set(value: Option[T], pp: SQLiteStatement) = value match {
    case Some(v) => ti.setValue(v, pp, idx)
    case _ => ti.setNull(pp, idx)
  }
  override def info = super.info + "(" + Dump + ti + Dump + s", idx=$idx)"
  def width = 1
  def getOrElse(default: () => T): DefaultingResultConverter[T] =
    if(ti.scalaType.isPrimitive) new DefaultingResultConverter[T](ti, default, idx)
    else new DefaultingResultConverter[T](ti, default, idx) {
      override def read(pr: Cursor) = {
        val v = ti.getValue(pr, idx - 1)
        if(v.asInstanceOf[AnyRef] eq null) default() else v
      }
    }
}

/** Specialized JDBC ResultConverter for handling non-`Option`values with a default.
  * A (possibly specialized) function for the default value is used to translate SQL `NULL` values. */
class DefaultingResultConverter[@specialized(Byte, Short, Int, Long, Char, Float, Double, Boolean) T](val ti: AndroidType[T], val default: () => T, val idx: Int) extends ResultConverter[AndroidResultConverterDomain, T] {
  def read(pr: Cursor) = {
    if(ti.isNull(pr, idx - 1)) default() else ti.getValue(pr, idx - 1)
  }
  def update(value: T, pr: Updater) = throw new SlickException("update on cursor not supported")
  def set(value: T, pp: SQLiteStatement) = if (value == null) ti.setNull(pp, idx) else ti.setValue(value, pp, idx)
  override def info =
    super.info + "(" + Dump + ti + Dump + ", idx=" + idx + ", default=" +
      { try default() catch { case e: Throwable => "["+e.getClass.getName+"]" } } + ")"
  def width = 1
}

/** A `ResultConverter` that simplifies the implementation of fast path
  * converters for `JdbcProfile`. It always wraps a `TypeMappingResultConverter`
  * on top of a `ProductResultConverter`, allowing direct access to the product
  * elements. */
abstract class AndroidFastPath[T](protected[this] val rc: TypeMappingResultConverter[AndroidResultConverterDomain, T, _]) extends ResultConverter[AndroidResultConverterDomain, T] {
  private[this] val ch = rc.child.asInstanceOf[ProductResultConverter[AndroidResultConverterDomain, _]].elementConverters
  private[this] var idx = -1

  /** Return the next specialized child `ResultConverter` for the specified type. */
  protected[this] def next[C] = {
    idx += 1
    ch(idx).asInstanceOf[ResultConverter[AndroidResultConverterDomain, C]]
  }

  def read(pr: Reader) = rc.read(pr)
  def update(value: T, pr: Updater) = rc.update(value, pr)
  def set(value: T, pp: Writer) = rc.set(value, pp)

  override def children = Iterator(rc)
  override def info = super.info + Dump + " [FastPath]" + Dump
  def width = rc.width
}
