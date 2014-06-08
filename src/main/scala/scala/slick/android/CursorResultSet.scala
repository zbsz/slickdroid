package scala.slick.android

import android.database.Cursor
import java.sql._
import java.io.{Reader, InputStream}
import java.util.Calendar
import scala.Array
import java.{sql, util}
import java.math.BigDecimal
import java.net.URL
import language.implicitConversions

/**
  */
class CursorResultSet(cursor: Cursor) extends java.sql.ResultSet {

  cursor.moveToFirst()
  cursor.moveToPrevious()

  private implicit def columnIndex(name: String): Int = cursor.getColumnIndex(name)
  private var _wasNull = false
  
  private def get[T](idx: Int, f: Int => T): T = {
    if (cursor.isNull(idx)) {
      _wasNull = true
      null.asInstanceOf[T]
    } else {
      _wasNull = false
      f(idx)
    }
  }
  
  override def next(): Boolean = cursor.moveToNext()

  override def getType: Int = ResultSet.TYPE_SCROLL_SENSITIVE

  override def isBeforeFirst: Boolean = cursor.isBeforeFirst

  override def updateString(p1: Int, p2: String): Unit = ???

  override def updateString(p1: String, p2: String): Unit = ???

  override def getTimestamp(p1: Int): Timestamp = get(p1 - 1, idx => new Timestamp(cursor.getLong(idx)))

  override def getTimestamp(p1: String): Timestamp = get(p1, idx => new Timestamp(cursor.getLong(idx)))

  override def getTimestamp(p1: Int, p2: Calendar): Timestamp = ???

  override def getTimestamp(p1: String, p2: Calendar): Timestamp = ???

  override def updateNString(p1: Int, p2: String): Unit = ???

  override def updateNString(p1: String, p2: String): Unit = ???

  override def clearWarnings(): Unit = ???

  override def updateTimestamp(p1: Int, p2: Timestamp): Unit = ???

  override def updateTimestamp(p1: String, p2: Timestamp): Unit = ???

  override def updateByte(p1: Int, p2: Byte): Unit = ???

  override def updateByte(p1: String, p2: Byte): Unit = ???

  override def updateBigDecimal(p1: Int, p2: BigDecimal): Unit = ???

  override def updateBigDecimal(p1: String, p2: BigDecimal): Unit = ???

  override def updateDouble(p1: Int, p2: Double): Unit = ???

  override def updateDouble(p1: String, p2: Double): Unit = ???

  override def updateDate(p1: Int, p2: Date): Unit = ???

  override def updateDate(p1: String, p2: Date): Unit = ???

  override def isAfterLast: Boolean = cursor.isAfterLast

  override def updateBoolean(p1: Int, p2: Boolean): Unit = ???

  override def updateBoolean(p1: String, p2: Boolean): Unit = ???

  override def getBinaryStream(p1: Int): InputStream = ???

  override def getBinaryStream(p1: String): InputStream = ???

  override def beforeFirst(): Unit = {
    cursor.moveToPosition(-1)
  }

  override def updateNCharacterStream(p1: Int, p2: Reader, p3: Long): Unit = ???

  override def updateNCharacterStream(p1: String, p2: Reader, p3: Long): Unit = ???

  override def updateNCharacterStream(p1: Int, p2: Reader): Unit = ???

  override def updateNCharacterStream(p1: String, p2: Reader): Unit = ???

  override def updateNClob(p1: Int, p2: NClob): Unit = ???

  override def updateNClob(p1: String, p2: NClob): Unit = ???

  override def updateNClob(p1: Int, p2: Reader, p3: Long): Unit = ???

  override def updateNClob(p1: String, p2: Reader, p3: Long): Unit = ???

  override def updateNClob(p1: Int, p2: Reader): Unit = ???

  override def updateNClob(p1: String, p2: Reader): Unit = ???

  override def last(): Boolean = cursor.moveToLast()

  override def isLast: Boolean = cursor.isLast

  override def getNClob(p1: Int): NClob = ???

  override def getNClob(p1: String): NClob = ???

  override def getCharacterStream(p1: Int): Reader = ???

  override def getCharacterStream(p1: String): Reader = ???

  override def updateArray(p1: Int, p2: sql.Array): Unit = ???

  override def updateArray(p1: String, p2: sql.Array): Unit = ???

  override def updateBlob(p1: Int, p2: Blob): Unit = ???

  override def updateBlob(p1: String, p2: Blob): Unit = ???

  override def updateBlob(p1: Int, p2: InputStream, p3: Long): Unit = ???

  override def updateBlob(p1: String, p2: InputStream, p3: Long): Unit = ???

  override def updateBlob(p1: Int, p2: InputStream): Unit = ???

  override def updateBlob(p1: String, p2: InputStream): Unit = ???

  override def getDouble(p1: Int): Double = get(p1 - 1, cursor.getDouble)

  override def getDouble(p1: String): Double = get(p1, cursor.getDouble)

  override def getArray(p1: Int): sql.Array = ???

  override def getArray(p1: String): sql.Array = ???

  override def isFirst: Boolean = cursor.isFirst

  override def getURL(p1: Int): URL = ???

  override def getURL(p1: String): URL = ???

  override def updateRow(): Unit = ???

  override def insertRow(): Unit = ???

  override def getMetaData: ResultSetMetaData = ???

  override def updateBinaryStream(p1: Int, p2: InputStream, p3: Int): Unit = ???

  override def updateBinaryStream(p1: String, p2: InputStream, p3: Int): Unit = ???

  override def updateBinaryStream(p1: Int, p2: InputStream, p3: Long): Unit = ???

  override def updateBinaryStream(p1: String, p2: InputStream, p3: Long): Unit = ???

  override def updateBinaryStream(p1: Int, p2: InputStream): Unit = ???

  override def updateBinaryStream(p1: String, p2: InputStream): Unit = ???

  override def absolute(p1: Int): Boolean = cursor.moveToPosition(p1)

  override def updateRowId(p1: Int, p2: RowId): Unit = ???

  override def updateRowId(p1: String, p2: RowId): Unit = ???

  override def getRowId(p1: Int): RowId = ???

  override def getRowId(p1: String): RowId = ???

  override def moveToInsertRow(): Unit = ???

  override def rowInserted(): Boolean = ???

  override def getFloat(p1: Int): Float = get(p1 - 1, cursor.getFloat)

  override def getFloat(p1: String): Float = get(p1, cursor.getFloat)

  override def getBigDecimal(p1: Int, p2: Int): BigDecimal = ???

  override def getBigDecimal(p1: String, p2: Int): BigDecimal = ???

  override def getBigDecimal(p1: Int): BigDecimal = get(p1 - 1, idx => new BigDecimal(cursor.getString(idx)))

  override def getBigDecimal(p1: String): BigDecimal = get(p1, idx => new BigDecimal(cursor.getString(idx)))

  override def getClob(p1: Int): Clob = ???

  override def getClob(p1: String): Clob = ???

  override def getRow: Int = cursor.getPosition

  override def getLong(p1: Int): Long = get(p1 - 1, cursor.getLong)

  override def getLong(p1: String): Long = get(p1, cursor.getLong)

  override def getHoldability: Int = ???

  override def updateFloat(p1: Int, p2: Float): Unit = ???

  override def updateFloat(p1: String, p2: Float): Unit = ???

  override def afterLast(): Unit = {
    cursor.moveToLast()
    cursor.moveToNext()
  }

  override def refreshRow(): Unit = ???

  override def getNString(p1: Int): String = ???

  override def getNString(p1: String): String = ???

  override def deleteRow(): Unit = ???

  override def getConcurrency: Int = ResultSet.CONCUR_READ_ONLY

  override def updateObject(p1: Int, p2: scala.Any, p3: Int): Unit = ???

  override def updateObject(p1: Int, p2: scala.Any): Unit = ???

  override def updateObject(p1: String, p2: scala.Any, p3: Int): Unit = ???

  override def updateObject(p1: String, p2: scala.Any): Unit = ???

  override def getFetchSize: Int = ???

  override def getTime(p1: Int): Time = get(p1 - 1, idx => new Time(cursor.getLong(idx)))

  override def getTime(p1: String): Time = get(p1, idx => new Time(cursor.getLong(idx)))

  override def getTime(p1: Int, p2: Calendar): Time = ???

  override def getTime(p1: String, p2: Calendar): Time = ???

  override def updateCharacterStream(p1: Int, p2: Reader, p3: Int): Unit = ???

  override def updateCharacterStream(p1: String, p2: Reader, p3: Int): Unit = ???

  override def updateCharacterStream(p1: Int, p2: Reader, p3: Long): Unit = ???

  override def updateCharacterStream(p1: String, p2: Reader, p3: Long): Unit = ???

  override def updateCharacterStream(p1: Int, p2: Reader): Unit = ???

  override def updateCharacterStream(p1: String, p2: Reader): Unit = ???

  override def getByte(p1: Int): Byte = get(p1 - 1, cursor.getInt(_).toByte)

  override def getByte(p1: String): Byte = get(p1, cursor.getInt(_).toByte)

  override def getBoolean(p1: Int): Boolean = get(p1 - 1, cursor.getInt(_) == 1)

  override def getBoolean(p1: String): Boolean = get(p1, cursor.getInt(_) == 1)

  override def setFetchDirection(p1: Int): Unit = ???

  override def getFetchDirection: Int = ???

  override def updateRef(p1: Int, p2: Ref): Unit = ???

  override def updateRef(p1: String, p2: Ref): Unit = ???

  override def getAsciiStream(p1: Int): InputStream = ???

  override def getAsciiStream(p1: String): InputStream = ???

  override def getShort(p1: Int): Short = get(p1 - 1, cursor.getShort)

  override def getShort(p1: String): Short = get(p1, cursor.getShort)

  override def getObject(p1: Int): AnyRef = get(p1 - 1, cursor.getString)

  override def getObject(p1: String): AnyRef = get(p1, cursor.getString)

  override def getObject(p1: Int, p2: util.Map[String, Class[_]]): AnyRef = ???

  override def getObject(p1: String, p2: util.Map[String, Class[_]]): AnyRef = ???

  override def getObject[T](p1: Int, p2: Class[T]): T = ???

  override def getObject[T](p1: String, p2: Class[T]): T = ???

  override def updateShort(p1: Int, p2: Short): Unit = ???

  override def updateShort(p1: String, p2: Short): Unit = ???

  override def getNCharacterStream(p1: Int): Reader = ???

  override def getNCharacterStream(p1: String): Reader = ???

  override def close(): Unit = cursor.close()

  override def relative(p1: Int): Boolean = cursor.move(p1)

  override def updateInt(p1: Int, p2: Int): Unit = ???

  override def updateInt(p1: String, p2: Int): Unit = ???

  override def wasNull(): Boolean = _wasNull

  override def rowUpdated(): Boolean = ???

  override def getRef(p1: Int): Ref = ???

  override def getRef(p1: String): Ref = ???

  override def updateLong(p1: Int, p2: Long): Unit = ???

  override def updateLong(p1: String, p2: Long): Unit = ???

  override def moveToCurrentRow(): Unit = ???

  override def isClosed: Boolean = cursor.isClosed

  override def updateClob(p1: Int, p2: Clob): Unit = ???

  override def updateClob(p1: String, p2: Clob): Unit = ???

  override def updateClob(p1: Int, p2: Reader, p3: Long): Unit = ???

  override def updateClob(p1: String, p2: Reader, p3: Long): Unit = ???

  override def updateClob(p1: Int, p2: Reader): Unit = ???

  override def updateClob(p1: String, p2: Reader): Unit = ???

  override def findColumn(p1: String): Int = cursor.getColumnIndex(p1) + 1

  override def getWarnings: SQLWarning = ???

  override def getDate(p1: Int): Date = get(p1 - 1, idx => new Date(cursor.getLong(idx)))

  override def getDate(p1: String): Date = get(p1, idx => new Date(cursor.getLong(idx)))

  override def getDate(p1: Int, p2: Calendar): Date = ???

  override def getDate(p1: String, p2: Calendar): Date = ???

  override def getCursorName: String = ???

  override def updateNull(p1: Int): Unit = ???

  override def updateNull(p1: String): Unit = ???

  override def getStatement: Statement = ???

  override def cancelRowUpdates(): Unit = ???

  override def getSQLXML(p1: Int): SQLXML = ???

  override def getSQLXML(p1: String): SQLXML = ???

  override def getUnicodeStream(p1: Int): InputStream = ???

  override def getUnicodeStream(p1: String): InputStream = ???

  override def getInt(p1: Int): Int = get(p1 - 1, cursor.getInt)

  override def getInt(p1: String): Int = get(p1, cursor.getInt)

  override def updateTime(p1: Int, p2: Time): Unit = ???

  override def updateTime(p1: String, p2: Time): Unit = ???

  override def setFetchSize(p1: Int): Unit = ???

  override def previous(): Boolean = cursor.moveToPrevious()

  override def updateAsciiStream(p1: Int, p2: InputStream, p3: Int): Unit = ???

  override def updateAsciiStream(p1: String, p2: InputStream, p3: Int): Unit = ???

  override def updateAsciiStream(p1: Int, p2: InputStream, p3: Long): Unit = ???

  override def updateAsciiStream(p1: String, p2: InputStream, p3: Long): Unit = ???

  override def updateAsciiStream(p1: Int, p2: InputStream): Unit = ???

  override def updateAsciiStream(p1: String, p2: InputStream): Unit = ???

  override def rowDeleted(): Boolean = ???

  override def getBlob(p1: Int): Blob = ???

  override def getBlob(p1: String): Blob = ???

  override def first(): Boolean = cursor.moveToFirst()

  override def getBytes(p1: Int): Array[Byte] = get(p1 - 1, cursor.getBlob)

  override def getBytes(p1: String): Array[Byte] = get(p1, cursor.getBlob)

  override def updateBytes(p1: Int, p2: Array[Byte]): Unit = ???

  override def updateBytes(p1: String, p2: Array[Byte]): Unit = ???

  override def updateSQLXML(p1: Int, p2: SQLXML): Unit = ???

  override def updateSQLXML(p1: String, p2: SQLXML): Unit = ???

  override def getString(p1: Int): String = get(p1 - 1, cursor.getString)

  override def getString(p1: String): String = get(p1, cursor.getString)

  override def unwrap[T](p1: Class[T]): T = ???

  override def isWrapperFor(p1: Class[_]): Boolean = ???
}
