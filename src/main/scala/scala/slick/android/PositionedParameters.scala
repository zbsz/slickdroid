package scala.slick.android

import java.sql._
import scala.Array


/** A wrapper for a JDBC `PreparedStatement` which allows inceremental setting of
  * parameters without having to sepcify the column index each time. */
class PositionedParameters(val ps: PreparedStatement) {

  var pos = 0

  /** Set the next parameter of the specified type, provided that a
    * `SetParameter` instance is available for it. */
  def >> [T](value: T)(implicit f: SetParameter[T]): Unit = f(value, this)

  /** Set the next parameter to SQL NULL with the specified SQL type code. */
  def setNull(sqlType: Int)            { val npos = pos + 1; ps.bindNull(npos);     pos = npos }

  /** Set the next parameter */
  def setBoolean(value: Boolean)       { val npos = pos + 1; ps.bindLong(npos, if (value) 1 else 0); pos = npos }
  /** Set the next parameter */
  def setBlob(value: Blob)             { val npos = pos + 1; ps.bindBlob      (npos, value.getBytes(0, value.length().toInt)); pos = npos }
  /** Set the next parameter */
  def setByte(value: Byte)             { val npos = pos + 1; ps.bindLong      (npos, value); pos = npos }
  /** Set the next parameter */
  def setBytes(value: Array[Byte])     { val npos = pos + 1; ps.bindBlob      (npos, value); pos = npos }
  /** Set the next parameter */
  def setClob(value: Clob)             { val npos = pos + 1; ps.bindString    (npos, value.getSubString(0, value.length().toInt)); pos = npos }
  /** Set the next parameter */
  def setDate(value: Date)             { val npos = pos + 1; ps.bindLong      (npos, value.getTime); pos = npos }
  /** Set the next parameter */
  def setDouble(value: Double)         { val npos = pos + 1; ps.bindDouble    (npos, value); pos = npos }
  /** Set the next parameter */
  def setFloat(value: Float)           { val npos = pos + 1; ps.bindDouble    (npos, value); pos = npos }
  /** Set the next parameter */
  def setInt(value: Int)               { val npos = pos + 1; ps.bindLong      (npos, value); pos = npos }
  /** Set the next parameter */
  def setLong(value: Long)             { val npos = pos + 1; ps.bindLong      (npos, value); pos = npos }
  /** Set the next parameter */
  def setShort(value: Short)           { val npos = pos + 1; ps.bindLong      (npos, value); pos = npos }
  /** Set the next parameter */
  def setString(value: String)         { val npos = pos + 1; ps.bindString    (npos, value); pos = npos }
  /** Set the next parameter */
  def setTime(value: Time)             { val npos = pos + 1; ps.bindLong      (npos, value.getTime); pos = npos }
  /** Set the next parameter */
  def setTimestamp(value: Timestamp)   { val npos = pos + 1; ps.bindLong      (npos, value.getTime); pos = npos }
  /** Set the next parameter */
  def setBigDecimal(value: BigDecimal) { val npos = pos + 1; ps.bindString    (npos, value.toString); pos = npos }
  /** Set the next parameter to an object of a driver-specific type that
    * corresponds to the specified SQL type code. */
  def setObject(value: AnyRef, sqlType: Int) { val npos = pos + 1; ??? /*ps.bindObject(npos, value, sqlType)*/; pos = npos }

  /** Set the next parameter to the specified value or a properly typed SQL NULL */
  def setBooleanOption(value: Option[Boolean]) {
    val npos = pos + 1
    if(value eq None) ps.bindNull(npos) else ps.bindLong(npos, if (value.get) 1 else 0)
    pos = npos
  }
  /** Set the next parameter to the specified value or a properly typed SQL NULL */
  def setBlobOption(value: Option[Blob]) {
    val npos = pos + 1
    if(value eq None) ps.bindNull(npos) else ps.bindBlob(npos, value.get.getBytes(0, value.get.length().toInt))
    pos = npos
  }
  /** Set the next parameter to the specified value or a properly typed SQL NULL */
  def setByteOption(value: Option[Byte]) {
    val npos = pos + 1
    if(value eq None) ps.bindNull(npos) else ps.bindLong(npos, value.get)
    pos = npos
  }
  /** Set the next parameter to the specified value or a properly typed SQL NULL */
  def setBytesOption(value: Option[Array[Byte]]) {
    val npos = pos + 1
    if(value eq None) ps.bindNull(npos) else ps.bindBlob(npos, value.get)
    pos = npos
  }
  /** Set the next parameter to the specified value or a properly typed SQL NULL */
  def setClobOption(value: Option[Clob]) {
    val npos = pos + 1
    if(value eq None) ps.bindNull(npos) else ps.bindString(npos, value.get.getSubString(0, value.get.length().toInt))
    pos = npos
  }
  /** Set the next parameter to the specified value or a properly typed SQL NULL */
  def setDateOption(value: Option[Date]) {
    val npos = pos + 1
    if(value eq None) ps.bindNull(npos) else ps.bindLong(npos, value.get.getTime)
    pos = npos
  }
  /** Set the next parameter to the specified value or a properly typed SQL NULL */
  def setDoubleOption(value: Option[Double]) {
    val npos = pos + 1
    if(value eq None) ps.bindNull(npos) else ps.bindDouble(npos, value.get)
    pos = npos
  }
  /** Set the next parameter to the specified value or a properly typed SQL NULL */
  def setFloatOption(value: Option[Float]) {
    val npos = pos + 1
    if(value eq None) ps.bindNull(npos) else ps.bindDouble(npos, value.get)
    pos = npos
  }
  /** Set the next parameter to the specified value or a properly typed SQL NULL */
  def setIntOption(value: Option[Int]) {
    val npos = pos + 1
    if(value eq None) ps.bindNull(npos) else ps.bindLong(npos, value.get)
    pos = npos
  }
  /** Set the next parameter to the specified value or a properly typed SQL NULL */
  def setLongOption(value: Option[Long]) {
    val npos = pos + 1
    if(value eq None) ps.bindNull(npos) else ps.bindLong(npos, value.get)
    pos = npos
  }
  /** Set the next parameter to the specified value or a properly typed SQL NULL */
  def setShortOption(value: Option[Short]) {
    val npos = pos + 1
    if(value eq None) ps.bindNull(npos) else ps.bindLong(npos, value.get)
    pos = npos
  }
  /** Set the next parameter to the specified value or a properly typed SQL NULL */
  def setStringOption(value: Option[String]) {
    val npos = pos + 1
    if(value eq None) ps.bindNull(npos) else ps.bindString(npos, value.get)
    pos = npos
  }
  /** Set the next parameter to the specified value or a properly typed SQL NULL */
  def setTimeOption(value: Option[Time]) {
    val npos = pos + 1
    if(value eq None) ps.bindNull(npos) else ps.bindLong(npos, value.get.getTime)
    pos = npos
  }
  /** Set the next parameter to the specified value or a properly typed SQL NULL */
  def setTimestampOption(value: Option[Timestamp]) {
    val npos = pos + 1
    if(value eq None) ps.bindNull(npos) else ps.bindLong(npos, value.get.getTime)
    pos = npos
  }
  /** Set the next parameter to the specified value or a properly typed SQL NULL */
  def setBigDecimalOption(value: Option[BigDecimal]) {
    val npos = pos + 1
    if(value eq None) ps.bindNull(npos) else ps.bindString(npos, value.get.toString())
    pos = npos
  }
  /** Set the next parameter to the specified value or a properly typed SQL NULL */
  def setObjectOption(value: Option[AnyRef], sqlType: Int) {
    val npos = pos + 1
    if(value eq None) ps.bindNull(npos) else ??? //ps.bindObject(npos, value.get, sqlType)
    pos = npos
  }
}

