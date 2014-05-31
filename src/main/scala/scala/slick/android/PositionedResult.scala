package scala.slick.android

import scala.language.higherKinds
import scala.collection.generic.CanBuildFrom
import java.sql.{Timestamp, Time, Date}
import java.io.Closeable
import android.database.Cursor
import scala.slick.util.{ReadAheadIterator, CloseableIterator}

/**
 * A database result positioned at a row and column.
 */
abstract class PositionedResult(val rs: Cursor) extends Closeable { outer =>
  protected[this] var pos = Int.MaxValue
  protected[this] val startPos = 0

  lazy val numColumns = rs.getColumnCount

  final def currentPos = pos
  final def hasMoreColumns = pos < numColumns

  final def skip = { pos += 1; this }
  final def restart = { pos = startPos; this }
  final def rewind = { pos = Int.MinValue; this }

  def nextRow = {
    val ret = (pos == Int.MinValue) || rs.moveToNext()
    pos = startPos
    ret
  }

  def getAny(pos: Int) = rs.getType(pos) match {
    case Cursor.FIELD_TYPE_NULL => null
    case Cursor.FIELD_TYPE_BLOB => rs.getBlob(pos)
    case Cursor.FIELD_TYPE_FLOAT => rs.getDouble(pos)
    case Cursor.FIELD_TYPE_INTEGER => rs.getLong(pos)
    case Cursor.FIELD_TYPE_STRING => rs.getString(pos)
  }
  
  final def << [T](implicit f: GetResult[T]): T = f(this)
  final def <<? [T](implicit f: GetResult[Option[T]]): Option[T] = if(hasMoreColumns) this.<< else None

  final def nextBoolean()    = { val npos = pos + 1; val r = rs getInt        npos; pos = npos; r == 1}
  final def nextBigDecimal() = { val npos = pos + 1; val r = rs getString     npos; pos = npos; BigDecimal(r) }
  final def nextBlob()       = { val npos = pos + 1; val r = rs getBlob       npos; pos = npos; r }
  final def nextByte()       = { val npos = pos + 1; val r = rs getInt        npos; pos = npos; r.toByte }
  final def nextBytes()      = { val npos = pos + 1; val r = rs getBlob       npos; pos = npos; r }
  final def nextDouble()     = { val npos = pos + 1; val r = rs getDouble     npos; pos = npos; r }
  final def nextFloat()      = { val npos = pos + 1; val r = rs getFloat      npos; pos = npos; r }
  final def nextInt()        = { val npos = pos + 1; val r = rs getInt        npos; pos = npos; r }
  final def nextLong()       = { val npos = pos + 1; val r = rs getLong       npos; pos = npos; r }
  final def nextShort()      = { val npos = pos + 1; val r = rs getShort      npos; pos = npos; r }
  final def nextString()     = { val npos = pos + 1; val r = rs getString     npos; pos = npos; r }
  final def nextObject()     = { val npos = pos + 1; val r = getAny(npos)         ; pos = npos; r }
  final def nextDate()       = { val npos = pos + 1; val r = new Date(rs getLong npos); pos = npos; r }
  final def nextTime()       = { val npos = pos + 1; val r = new Time(rs getLong npos); pos = npos; r }
  final def nextTimestamp()  = { val npos = pos + 1; val r = new Timestamp(rs getLong npos); pos = npos; r }

  final def wasNull() = rs.isNull(pos)

  final def nextBooleanOption()    = { val npos = pos + 1; val rr = if (rs.isNull(npos)) None else Some(rs.getInt(npos) == 1); pos = npos; rr }
  final def nextBigDecimalOption() = { val npos = pos + 1; val rr = if (rs.isNull(npos)) None else Some(BigDecimal(rs getString npos)); pos = npos; rr }
  final def nextBlobOption()       = { val npos = pos + 1; val rr = if (rs.isNull(npos)) None else Some(rs getBlob       npos); pos = npos; rr }
  final def nextByteOption()       = { val npos = pos + 1; val rr = if (rs.isNull(npos)) None else Some(rs.getInt(npos).toByte); pos = npos; rr }
  final def nextBytesOption()      = { val npos = pos + 1; val rr = if (rs.isNull(npos)) None else Some(rs getBlob       npos); pos = npos; rr }
  final def nextDoubleOption()     = { val npos = pos + 1; val rr = if (rs.isNull(npos)) None else Some(rs getDouble     npos); pos = npos; rr }
  final def nextFloatOption()      = { val npos = pos + 1; val rr = if (rs.isNull(npos)) None else Some(rs getFloat      npos); pos = npos; rr }
  final def nextIntOption()        = { val npos = pos + 1; val rr = if (rs.isNull(npos)) None else Some(rs getInt        npos); pos = npos; rr }
  final def nextLongOption()       = { val npos = pos + 1; val rr = if (rs.isNull(npos)) None else Some(rs getLong       npos); pos = npos; rr }
  final def nextShortOption()      = { val npos = pos + 1; val rr = if (rs.isNull(npos)) None else Some(rs getShort      npos); pos = npos; rr }
  final def nextStringOption()     = { val npos = pos + 1; val rr = if (rs.isNull(npos)) None else Some(rs getString     npos); pos = npos; rr }
  final def nextObjectOption()     = { val npos = pos + 1; val rr = if (rs.isNull(npos)) None else Some(getAny(npos))         ; pos = npos; rr }
  final def nextDateOption()       = { val npos = pos + 1; val rr = if (rs.isNull(npos)) None else Some(new Date(rs getLong npos)); pos = npos; rr }
  final def nextTimeOption()       = { val npos = pos + 1; val rr = if (rs.isNull(npos)) None else Some(new Time(rs getLong npos)); pos = npos; rr }
  final def nextTimestampOption()  = { val npos = pos + 1; val rr = if (rs.isNull(npos)) None else Some(new Timestamp(rs getLong npos)); pos = npos; rr }

  /**
   * Close the ResultSet and the statement which created it.
   */
  def close(): Unit = {
    rs.close()
  }

  /**
   * Create an embedded PositionedResult which extends from the given dataPos
   * column until the end of this PositionedResult, starts at the current row
   * and ends when the discriminator predicate (which can read columns starting
   * at discriminatorPos) returns false or when this PositionedResult ends.
   */
  def view(discriminatorPos: Int, dataPos: Int, discriminator: (PositionedResult => Boolean)): PositionedResult = new PositionedResult(rs) {
    override protected[this] val startPos = dataPos
    pos = Int.MinValue
    override def nextRow = {
      def disc: Boolean = {
        pos = discriminatorPos
        val ret = discriminator(this)
        pos = startPos
        ret
      }
      if(pos == Int.MinValue) disc else {
        val outerRet = outer.nextRow
        val ret = outerRet && disc
        pos = startPos
        if(!ret && outerRet) outer.rewind
        ret
      }
    }
  }

  /**
   * Create an embedded PositionedResult with a single discriminator column
   * followed by the embedded data, starting at the current position. The
   * embedded view lasts while the discriminator stays the same. If the first
   * discriminator value is NULL, the view is empty.
   */
  def view1: PositionedResult = {
    val discPos = pos
    val disc = nextObject()
    view(discPos, discPos+1, { r => disc != null && disc == r.nextObject })
  }

  final def build[C[_], R](gr: GetResult[R])(implicit canBuildFrom: CanBuildFrom[Nothing, R, C[R]]): C[R] = {
    val b = canBuildFrom()
    while(nextRow) b += gr(this)
    b.result()
  }

  final def to[C[_]] = new To[C]()

  final class To[C[_]] private[PositionedResult] () {
    def apply[R](gr: GetResult[R])(implicit session: AndroidBackend#Session, canBuildFrom: CanBuildFrom[Nothing, R, C[R]]) =
      build[C, R](gr)
  }
}


/**
 * An CloseableIterator for a PositionedResult.
 */
abstract class PositionedResultIterator[+T](val pr: PositionedResult, maxRows: Int) extends ReadAheadIterator[T] with CloseableIterator[T] {

  private[this] var closed = false
  private[this] var readRows = 0

  def rs = pr.rs

  protected def fetchNext(): T = {
    if((readRows < maxRows || maxRows <= 0) && pr.nextRow) {
      val res = extractValue(pr)
      readRows += 1
      res
    }
    else finished()
  }

  final def close() {
    if(!closed) {
      pr.close()
      closed = true
    }
  }

  protected def extractValue(pr: PositionedResult): T
}

