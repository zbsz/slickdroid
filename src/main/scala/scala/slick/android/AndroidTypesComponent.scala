package scala.slick.android

import java.sql.{Blob, Clob, Date, Time, Timestamp}
import java.util.UUID
import scala.slick.SlickException
import scala.slick.ast._
import scala.slick.profile.RelationalTypesComponent
import scala.reflect.ClassTag
import android.database.Cursor
import android.database.sqlite.SQLiteStatement
import javax.sql.rowset.serial.{SerialClob, SerialBlob}

trait AndroidTypesComponent extends RelationalTypesComponent { driver: AndroidDriver =>

  abstract class MappedAndroidType[T, U](implicit tmd: AndroidType[U], val classTag: ClassTag[T]) extends AndroidType[T] {
    def map(t: T): U
    def comap(u: U): T

    def newSqlType: Option[Int] = None
    def newSqlTypeName: Option[String] = None
    def newValueToSQLLiteral(value: T): Option[String] = None
    def newHasLiteralForm: Option[Boolean] = None

    def sqlType = newSqlType.getOrElse(tmd.sqlType)
    def sqlTypeName = newSqlTypeName.getOrElse(tmd.sqlTypeName)
    def setValue(v: T, p: SQLiteStatement, idx: Int) = tmd.setValue(map(v), p, idx)
    def setNull(p: SQLiteStatement, idx: Int): Unit = tmd.setNull(p, idx)
    def getValue(r: Cursor, idx: Int) = {
      if (tmd.isNull(r, idx)) null.asInstanceOf[T]
      else {
        val v = tmd.getValue(r, idx)
        if (v.asInstanceOf[AnyRef] eq null) null.asInstanceOf[T] else comap(v)
      }
    }
    def isNull(r: Cursor, idx: Int) = tmd.isNull(r, idx)
    def valueToSQLLiteral(value: T) = newValueToSQLLiteral(value).getOrElse(tmd.valueToSQLLiteral(map(value)))
    def hasLiteralForm = newHasLiteralForm.getOrElse(tmd.hasLiteralForm)
    def scalaType = ScalaBaseType[T]
  }

  object MappedAndroidType extends MappedColumnTypeFactory {
    def base[T : ClassTag, U : BaseColumnType](tmap: T => U, tcomap: U => T): BaseColumnType[T] = {
      assertNonNullType(implicitly[BaseColumnType[U]])
      new MappedAndroidType[T, U] with BaseTypedType[T] {
        def map(t: T) = tmap(t)
        def comap(u: U) = tcomap(u)
      }
    }
  }

  object AndroidType {
    def unapply(t: Type) = Some((androidTypeFor(t), t.isInstanceOf[OptionType]))
  }

  def androidTypeFor(t: Type): AndroidType[Any] = ((t match {
    case tmd: AndroidType[_] => tmd
    case ScalaBaseType.booleanType => columnTypes.booleanJdbcType
    case ScalaBaseType.bigDecimalType => columnTypes.bigDecimalJdbcType
    case ScalaBaseType.byteType => columnTypes.byteJdbcType
    case ScalaBaseType.charType => columnTypes.charJdbcType
    case ScalaBaseType.doubleType => columnTypes.doubleJdbcType
    case ScalaBaseType.floatType => columnTypes.floatJdbcType
    case ScalaBaseType.intType => columnTypes.intJdbcType
    case ScalaBaseType.longType => columnTypes.longJdbcType
    case ScalaBaseType.nullType => columnTypes.nullJdbcType
    case ScalaBaseType.shortType => columnTypes.shortJdbcType
    case ScalaBaseType.stringType => columnTypes.stringJdbcType
    case t: OptionType => androidTypeFor(t.elementType)
    case t => throw new SlickException("AndroidProfile has no AndroidType for type "+t)
  }): AndroidType[_]).asInstanceOf[AndroidType[Any]]

  def defaultSqlTypeName(tmd: AndroidType[_]): String = tmd.sqlType match {
    case java.sql.Types.VARCHAR => "VARCHAR(254)"
    case java.sql.Types.DECIMAL => "DECIMAL(21,2)"
    case t => JdbcTypesComponent.typeNames.getOrElse(t,
      throw new SlickException("No SQL type name found in java.sql.Types for code "+t))
  }

  abstract class DriverAndroidType[@specialized T](implicit val classTag: ClassTag[T]) extends AndroidType[T] {
    def scalaType = ScalaBaseType[T]
    def sqlTypeName: String = driver.defaultSqlTypeName(this)
    def valueToSQLLiteral(value: T) =
      if(hasLiteralForm) value.toString
      else throw new SlickException(sqlTypeName + " does not have a literal representation")
    def hasLiteralForm = true
    def isNull(r: Cursor, idx: Int) = r.isNull(idx)
    def setNull(p: SQLiteStatement, idx: Int): Unit = p.bindNull(idx)
  }

  class JdbcTypes {
    val booleanJdbcType = new BooleanAndroidType
    val blobJdbcType = new BlobAndroidType
    val byteJdbcType = new ByteAndroidType
    val byteArrayJdbcType = new ByteArrayAndroidType
    val charJdbcType = new CharAndroidType
    val clobJdbcType = new ClobAndroidType
    val dateJdbcType = new DateAndroidType
    val doubleJdbcType = new DoubleAndroidType
    val floatJdbcType = new FloatAndroidType
    val intJdbcType = new IntAndroidType
    val longJdbcType = new LongAndroidType
    val shortJdbcType = new ShortAndroidType
    val stringJdbcType = new StringAndroidType
    val timeJdbcType = new TimeAndroidType
    val timestampJdbcType = new TimestampAndroidType
    val uuidJdbcType = new UUIDSQLiteType
    val bigDecimalJdbcType = new BigDecimalAndroidType
    val nullJdbcType = new NullAndroidType

    class BooleanAndroidType extends DriverAndroidType[Boolean] {
      def sqlType = java.sql.Types.BOOLEAN
      def setValue(v: Boolean, p: SQLiteStatement, idx: Int) = p.bindLong(idx, if (v) 1 else 0)
      def getValue(r: Cursor, idx: Int) = r.getInt(idx) == 1
      override def sqlTypeName = "INTEGER"
      override def valueToSQLLiteral(value: Boolean) = if(value) "1" else "0"
    }

    class BlobAndroidType extends DriverAndroidType[Blob] {
      def sqlType = java.sql.Types.BLOB
      def setValue(v: Blob, p: SQLiteStatement, idx: Int) = p.bindBlob(idx, v.getBytes(0, v.length().toInt))
      def getValue(r: Cursor, idx: Int) = new SerialBlob(r.getBlob(idx))
      override def hasLiteralForm = false
    }

    class ByteAndroidType extends DriverAndroidType[Byte] with NumericTypedType {
      def sqlType = java.sql.Types.TINYINT
      def setValue(v: Byte, p: SQLiteStatement, idx: Int) = p.bindLong(idx, v)
      def getValue(r: Cursor, idx: Int) = r.getInt(idx).toByte
    }

    class ByteArrayAndroidType extends DriverAndroidType[Array[Byte]] {
      def sqlType = java.sql.Types.BLOB
      def setValue(v: Array[Byte], p: SQLiteStatement, idx: Int) = p.bindBlob(idx, v)
      def getValue(r: Cursor, idx: Int) = r.getBlob(idx)
      override def hasLiteralForm = false
    }

    class ClobAndroidType extends DriverAndroidType[Clob] {
      def sqlType = java.sql.Types.CLOB
      def setValue(v: Clob, p: SQLiteStatement, idx: Int) = p.bindString(idx, v.getSubString(0, v.length().toInt))
      def getValue(r: Cursor, idx: Int) = new SerialClob(r.getString(idx).toCharArray)
      override def hasLiteralForm = false
    }

    class CharAndroidType extends DriverAndroidType[Char] {
      def sqlType = java.sql.Types.CHAR
      override def sqlTypeName = "CHAR(1)"
      def setValue(v: Char, p: SQLiteStatement, idx: Int) = stringJdbcType.setValue(String.valueOf(v), p, idx)
      def getValue(r: Cursor, idx: Int) = {
        val s = stringJdbcType.getValue(r, idx)
        if(s == null || s.isEmpty) ' ' else s.charAt(0)
      }
      override def valueToSQLLiteral(v: Char) = stringJdbcType.valueToSQLLiteral(String.valueOf(v))
    }

    class DateAndroidType extends DriverAndroidType[Date] {
      def sqlType = java.sql.Types.DATE
      def setValue(v: Date, p: SQLiteStatement, idx: Int) = p.bindLong(idx, v.getTime)
      def getValue(r: Cursor, idx: Int) = new Date(r.getLong(idx))
      override def valueToSQLLiteral(value: Date) = value.getTime.toString
    }

    class DoubleAndroidType extends DriverAndroidType[Double] with NumericTypedType {
      def sqlType = java.sql.Types.DOUBLE
      def setValue(v: Double, p: SQLiteStatement, idx: Int) = p.bindDouble(idx, v)
      def getValue(r: Cursor, idx: Int) = r.getDouble(idx)
    }

    class FloatAndroidType extends DriverAndroidType[Float] with NumericTypedType {
      def sqlType = java.sql.Types.FLOAT
      def setValue(v: Float, p: SQLiteStatement, idx: Int) = p.bindDouble(idx, v)
      def getValue(r: Cursor, idx: Int) = r.getFloat(idx)
    }

    class IntAndroidType extends DriverAndroidType[Int] with NumericTypedType {
      def sqlType = java.sql.Types.INTEGER
      def setValue(v: Int, p: SQLiteStatement, idx: Int) = p.bindLong(idx, v)
      def getValue(r: Cursor, idx: Int) = r.getInt(idx)
    }

    class LongAndroidType extends DriverAndroidType[Long] with NumericTypedType {
      def sqlType = java.sql.Types.BIGINT
      def setValue(v: Long, p: SQLiteStatement, idx: Int) = p.bindLong(idx, v)
      def getValue(r: Cursor, idx: Int) = r.getLong(idx)
    }

    class ShortAndroidType extends DriverAndroidType[Short] with NumericTypedType {
      def sqlType = java.sql.Types.SMALLINT
      def setValue(v: Short, p: SQLiteStatement, idx: Int) = p.bindLong(idx, v)
      def getValue(r: Cursor, idx: Int) = r.getShort(idx)
    }

    class StringAndroidType extends DriverAndroidType[String] {
      def sqlType = java.sql.Types.VARCHAR
      def setValue(v: String, p: SQLiteStatement, idx: Int) = p.bindString(idx, v)
      def getValue(r: Cursor, idx: Int) = r.getString(idx)
      override def valueToSQLLiteral(value: String) = if(value eq null) "NULL" else {
        val sb = new StringBuilder
        sb append '\''
        for(c <- value) c match {
          case '\'' => sb append "''"
          case _ => sb append c
        }
        sb append '\''
        sb.toString
      }
    }

    class TimeAndroidType extends DriverAndroidType[Time] {
      def sqlType = java.sql.Types.TIME
      def setValue(v: Time, p: SQLiteStatement, idx: Int) = p.bindLong(idx, v.getTime)
      def getValue(r: Cursor, idx: Int) = new Time(r.getLong(idx))
      override def valueToSQLLiteral(value: Time) = value.getTime.toString
    }

    class TimestampAndroidType extends DriverAndroidType[Timestamp] {
      def sqlType = java.sql.Types.TIMESTAMP
      def setValue(v: Timestamp, p: SQLiteStatement, idx: Int) = p.bindLong(idx, v.getTime)
      def getValue(r: Cursor, idx: Int) = new Timestamp(r.getLong(idx))
      override def valueToSQLLiteral(value: Timestamp) = value.getTime.toString
    }

    class UUIDSQLiteType extends DriverAndroidType[UUID] {
      def sqlType = java.sql.Types.BLOB
      def setValue(v: UUID, p: SQLiteStatement, idx: Int) = p.bindBlob(idx, toBytes(v))
      def getValue(r: Cursor, idx: Int) = fromBytes(r.getBlob(idx))
      override def hasLiteralForm = false
      def toBytes(uuid: UUID) = if(uuid eq null) null else {
        val msb = uuid.getMostSignificantBits
        val lsb = uuid.getLeastSignificantBits
        val buff = new Array[Byte](16)
        var i = 0
        while(i < 8) {
          buff(i) = ((msb >> (8 * (7 - i))) & 255).toByte;
          buff(8 + i) = ((lsb >> (8 * (7 - i))) & 255).toByte;
          i += 1
        }
        buff
      }
      def fromBytes(data: Array[Byte]) = if(data eq null) null else {
        var msb = 0L
        var lsb = 0L
        var i = 0
        while(i < 8) {
          msb = (msb << 8) | (data(i) & 0xff)
          i += 1
        }
        while(i < 16) {
          lsb = (lsb << 8) | (data(i) & 0xff)
          i += 1
        }
        new UUID(msb, lsb)
      }
    }

    class BigDecimalAndroidType extends DriverAndroidType[BigDecimal] with NumericTypedType {
      def sqlType = java.sql.Types.DECIMAL
      def setValue(v: BigDecimal, p: SQLiteStatement, idx: Int) = p.bindString(idx, v.bigDecimal.toPlainString)
      def getValue(r: Cursor, idx: Int) = {
        val v = r.getString(idx)
        if(v eq null) null else BigDecimal(v)
      }
    }

    class NullAndroidType extends DriverAndroidType[Null] {
      def sqlType = java.sql.Types.NULL
      def setValue(v: Null, p: SQLiteStatement, idx: Int) = p.bindNull(idx)
      override def setNull(p: SQLiteStatement, idx: Int) = p.bindNull(idx)
      def getValue(r: Cursor, idx: Int) = null
      override def valueToSQLLiteral(value: Null) = "NULL"
    }
  }

  trait ImplicitColumnTypes extends super.ImplicitColumnTypes {
    implicit def booleanColumnType = columnTypes.booleanJdbcType
    implicit def blobColumnType = columnTypes.blobJdbcType
    implicit def byteColumnType = columnTypes.byteJdbcType
    implicit def byteArrayColumnType = columnTypes.byteArrayJdbcType
    implicit def charColumnType = columnTypes.charJdbcType
    implicit def clobColumnType = columnTypes.clobJdbcType
    implicit def dateColumnType = columnTypes.dateJdbcType
    implicit def doubleColumnType = columnTypes.doubleJdbcType
    implicit def floatColumnType = columnTypes.floatJdbcType
    implicit def intColumnType = columnTypes.intJdbcType
    implicit def longColumnType = columnTypes.longJdbcType
    implicit def shortColumnType = columnTypes.shortJdbcType
    implicit def stringColumnType = columnTypes.stringJdbcType
    implicit def timeColumnType = columnTypes.timeJdbcType
    implicit def timestampColumnType = columnTypes.timestampJdbcType
    implicit def uuidColumnType = columnTypes.uuidJdbcType
    implicit def bigDecimalColumnType = columnTypes.bigDecimalJdbcType
  }
}

object JdbcTypesComponent {
  private[android] lazy val typeNames = Map() ++ (for(f <- classOf[java.sql.Types].getFields) yield f.get(null).asInstanceOf[Int] -> f.getName)
}
