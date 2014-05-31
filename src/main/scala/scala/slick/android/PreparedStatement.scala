package scala.slick.android

import android.database.sqlite.SQLiteDatabase
import android.database.Cursor

case class PreparedStatement(sql: String)(implicit db: SQLiteDatabase) {

  val stmt = db.compileStatement(sql)
  var bindings = Map[Int, String]()

  def close(): Unit = stmt.close()

  def bindString(index: Int, value: String): Unit = {
    bindings += (index -> value)
    stmt.bindString(index, value)
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
    stmt.bindBlob(index, value)
  }

  def bindNull(index: Int): Unit = {
    bindings += (index -> null.asInstanceOf[String])
    stmt.bindNull(index)
  }

  def clearBindings(): Unit = {
    bindings = Map()
    stmt.clearBindings()
  }

  def execute(): Unit = stmt.execute()

  def executeInsert(): Long = stmt.executeInsert()

  def executeUpdateDelete(): Int = stmt.executeUpdateDelete()

  def executeQuery(): Cursor = db.rawQuery(sql, bindings.toArray.sortBy(_._1).map(_._2))
  
  def simpleQueryForLong(): Long = stmt.simpleQueryForLong()
}
