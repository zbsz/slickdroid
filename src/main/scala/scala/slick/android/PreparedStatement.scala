package scala.slick.android

import android.database.sqlite.SQLiteDatabase
import android.database.Cursor
import java.sql._
import java.io.{InputStream, Reader}
import scala.Array
import java.util.Calendar
import java.math.BigDecimal
import java.net.URL

case class PreparedStatement(sql: String, generatedKeyName: String = "")(implicit db: SQLiteDatabase) extends java.sql.PreparedStatement {

  var maxRows = 0
  var result: ResultSet = null
  var rowId = 0L

  val stmt = db.compileStatement(sql)
  var bindings = Map[Int, String]()

  def close(): Unit = stmt.close()

  def bindString(index: Int, value: String): Unit = {
    bindings += (index -> value)
    if (value == null) stmt.bindNull(index) else stmt.bindString(index, value)
  }

  def bindLong(index: Int, value: Long): Unit = {
    bindings += (index -> value.toString)
    stmt.bindLong(index, value)
  }

  def bindDouble(index: Int, value: Double): Unit = {
    bindings += (index -> value.toString)
    stmt.bindDouble(index, value)
  }

  def bindBlob(index: Int, value: Array[Byte]): Unit = {
    bindings += (index -> null.asInstanceOf[String])
    if (value == null) stmt.bindNull(index) else stmt.bindBlob(index, value)
  }

  def bindNull(index: Int): Unit = {
    bindings += (index -> null.asInstanceOf[String])
    stmt.bindNull(index)
  }

  def clearBindings(): Unit = {
    bindings = Map()
    stmt.clearBindings()
  }

  def executeInsert(): Long = stmt.executeInsert()

  def executeUpdateDelete(): Int = stmt.executeUpdateDelete()

//  def executeQuery(): Cursor = db.rawQuery(sql, bindings.toArray.sortBy(_._1).map(_._2))

  def simpleQueryForLong(): Long = stmt.simpleQueryForLong()

  override def execute(): Boolean = {
    val key = sql.trim.toLowerCase

    if (key.startsWith("select")) {
      result = executeQuery()
      true
    } else if (key.startsWith("insert")) {
      rowId = executeInsert()
      false
    } else {
      rowId = executeUpdateDelete()
      false
    }
  }

  override def executeQuery(): ResultSet = new CursorResultSet(db.rawQuery(sql, bindings.toArray.sortBy(_._1).map(_._2)))

  override def executeUpdate(): Int = executeUpdateDelete()

  override def setByte(p1: Int, p2: Byte): Unit = bindLong(p1, p2)

  override def getParameterMetaData: ParameterMetaData = ???

  override def setRef(p1: Int, p2: Ref): Unit = ???

  override def clearParameters(): Unit = clearBindings()

  override def setBytes(p1: Int, p2: Array[Byte]): Unit = bindBlob(p1, p2)

  override def setBinaryStream(p1: Int, p2: InputStream, p3: Int): Unit = ???

  override def setBinaryStream(p1: Int, p2: InputStream, p3: Long): Unit = ???

  override def setBinaryStream(p1: Int, p2: InputStream): Unit = ???

  override def setAsciiStream(p1: Int, p2: InputStream, p3: Int): Unit = ???

  override def setAsciiStream(p1: Int, p2: InputStream, p3: Long): Unit = ???

  override def setAsciiStream(p1: Int, p2: InputStream): Unit = ???

  override def setObject(p1: Int, p2: scala.Any, p3: Int): Unit = ???

  override def setObject(p1: Int, p2: scala.Any): Unit = ???

  override def setObject(p1: Int, p2: scala.Any, p3: Int, p4: Int): Unit = ???

  override def setDate(p1: Int, p2: Date): Unit = bindLong(p1, p2.getTime)

  override def setDate(p1: Int, p2: Date, p3: Calendar): Unit = ???

  override def setTimestamp(p1: Int, p2: Timestamp): Unit = bindLong(p1, p2.getTime)

  override def setTimestamp(p1: Int, p2: Timestamp, p3: Calendar): Unit = ???

  override def setUnicodeStream(p1: Int, p2: InputStream, p3: Int): Unit = ???

  override def getMetaData: ResultSetMetaData = ???

  override def setBlob(p1: Int, p2: Blob): Unit = ???

  override def setBlob(p1: Int, p2: InputStream, p3: Long): Unit = ???

  override def setBlob(p1: Int, p2: InputStream): Unit = ???

  override def addBatch(): Unit = ???

  override def setNClob(p1: Int, p2: NClob): Unit = ???

  override def setNClob(p1: Int, p2: Reader, p3: Long): Unit = ???

  override def setNClob(p1: Int, p2: Reader): Unit = ???

  override def setArray(p1: Int, p2: java.sql.Array): Unit = ???

  override def setNCharacterStream(p1: Int, p2: Reader, p3: Long): Unit = ???

  override def setNCharacterStream(p1: Int, p2: Reader): Unit = ???

  override def setURL(p1: Int, p2: URL): Unit = ???

  override def setRowId(p1: Int, p2: RowId): Unit = ???

  override def setSQLXML(p1: Int, p2: SQLXML): Unit = ???

  override def setString(p1: Int, p2: String): Unit = bindString(p1, p2)

  override def setFloat(p1: Int, p2: Float): Unit = bindDouble(p1, p2)

  override def setNString(p1: Int, p2: String): Unit = ???

  override def setBoolean(p1: Int, p2: Boolean): Unit = bindLong(p1, if (p2) 1 else 0)

  override def setDouble(p1: Int, p2: Double): Unit = bindDouble(p1, p2)

  override def setBigDecimal(p1: Int, p2: BigDecimal): Unit = bindString(p1, p2.toString)

  override def setTime(p1: Int, p2: Time): Unit = bindLong(p1, p2.getTime)

  override def setTime(p1: Int, p2: Time, p3: Calendar): Unit = ???

  override def setLong(p1: Int, p2: Long): Unit = bindLong(p1, p2)

  override def setShort(p1: Int, p2: Short): Unit =  bindLong(p1, p2)

  override def setCharacterStream(p1: Int, p2: Reader, p3: Int): Unit = ???

  override def setCharacterStream(p1: Int, p2: Reader, p3: Long): Unit = ???

  override def setCharacterStream(p1: Int, p2: Reader): Unit = ???

  override def setClob(p1: Int, p2: Clob): Unit = ???

  override def setClob(p1: Int, p2: Reader, p3: Long): Unit = ???

  override def setClob(p1: Int, p2: Reader): Unit = ???

  override def setNull(p1: Int, p2: Int): Unit = bindNull(p1)

  override def setNull(p1: Int, p2: Int, p3: String): Unit = ???

  override def setInt(p1: Int, p2: Int): Unit =  bindLong(p1, p2)

  override def setMaxFieldSize(p1: Int): Unit = ???

  override def getMoreResults: Boolean = false

  override def getMoreResults(p1: Int): Boolean = ???

  override def clearWarnings(): Unit = ???

  override def getGeneratedKeys: ResultSet = new CursorResultSet(IdReturnCursor(rowId, generatedKeyName))

  override def closeOnCompletion(): Unit = ???

  override def cancel(): Unit = ???

  override def getResultSet: ResultSet = result

  override def isPoolable: Boolean = ???

  override def setPoolable(p1: Boolean): Unit = ???

  override def setCursorName(p1: String): Unit = ???

  override def getUpdateCount: Int = rowId.toInt

  override def addBatch(p1: String): Unit = ???

  override def getMaxRows: Int = ???

  override def execute(p1: String): Boolean = ???

  override def execute(p1: String, p2: Int): Boolean = ???

  override def execute(p1: String, p2: Array[Int]): Boolean = ???

  override def execute(p1: String, p2: Array[String]): Boolean = ???

  override def executeQuery(p1: String): ResultSet = ???

  override def getResultSetType: Int = ???

  override def setMaxRows(p1: Int): Unit = {
    this.maxRows = p1
  }

  override def getFetchSize: Int = ???

  override def getResultSetHoldability: Int = ???

  override def setFetchDirection(p1: Int): Unit = ???

  override def getFetchDirection: Int = ???

  override def getResultSetConcurrency: Int = ???

  override def clearBatch(): Unit = ???

  override def isClosed: Boolean = ???

  override def executeUpdate(p1: String): Int = ???

  override def executeUpdate(p1: String, p2: Int): Int = ???

  override def executeUpdate(p1: String, p2: Array[Int]): Int = ???

  override def executeUpdate(p1: String, p2: Array[String]): Int = ???

  override def getQueryTimeout: Int = ???

  override def getWarnings: SQLWarning = ???

  override def setFetchSize(p1: Int): Unit = ???

  override def setQueryTimeout(p1: Int): Unit = ???

  override def executeBatch(): Array[Int] = ???

  override def setEscapeProcessing(p1: Boolean): Unit = ???

  override def getConnection: Connection = ???

  override def getMaxFieldSize: Int = ???

  override def isCloseOnCompletion: Boolean = ???

  override def unwrap[T](p1: Class[T]): T = ???

  override def isWrapperFor(p1: Class[_]): Boolean = ???
}
