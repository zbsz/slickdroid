package scala.slick.android

import android.database.{ContentObserver, CharArrayBuffer, DataSetObserver, Cursor}
import android.os.Bundle
import android.content.ContentResolver
import android.net.Uri

/**
  */
case class IdReturnCursor(var id: Long = 0) extends Cursor {
  private var idx = 0

  override def getCount: Int = 1

  override def moveToFirst(): Boolean = { idx = 0; true }

  override def getType(columnIndex: Int): Int = Cursor.FIELD_TYPE_INTEGER

  override def isBeforeFirst: Boolean = idx < 0

  override def getPosition: Int = idx

  override def move(offset: Int): Boolean = { idx += offset; idx == 0 }

  override def registerContentObserver(observer: ContentObserver): Unit = {}

  override def getExtras: Bundle = null

  override def moveToNext(): Boolean = { idx += 1; idx == 0 }

  override def isAfterLast: Boolean = idx > 0

  override def getWantsAllOnMoveCalls: Boolean = false

  override def getColumnIndex(columnName: String): Int = 0

  override def moveToPrevious(): Boolean = { idx -= 1; idx > -1 }

  override def isLast: Boolean = idx == 0

  override def getDouble(columnIndex: Int): Double = id.toDouble

  override def unregisterContentObserver(observer: ContentObserver): Unit = {}

  override def isFirst: Boolean = idx == 0

  override def getColumnIndexOrThrow(columnName: String): Int = 0

  override def moveToLast(): Boolean = { idx = 0; true }

  override def getColumnCount: Int = 1

  override def getColumnName(columnIndex: Int): String = "id"

  override def getFloat(columnIndex: Int): Float = id.toFloat

  override def registerDataSetObserver(observer: DataSetObserver): Unit = {}

  override def getLong(columnIndex: Int): Long = id

  override def deactivate(): Unit = {}

  override def copyStringToBuffer(columnIndex: Int, buffer: CharArrayBuffer): Unit = {
    buffer.data = id.toString.toCharArray
    buffer.sizeCopied = buffer.data.length
  }

  override def requery(): Boolean = true

  override def moveToPosition(position: Int): Boolean = { idx = position; idx == 0 }

  override def setNotificationUri(cr: ContentResolver, uri: Uri): Unit = {}

  override def unregisterDataSetObserver(observer: DataSetObserver): Unit = {}

  override def getShort(columnIndex: Int): Short = id.toShort

  override def isNull(columnIndex: Int): Boolean = false

  override def respond(extras: Bundle): Bundle = null

  override def close(): Unit = {}

  override def isClosed: Boolean = false

  override def getColumnNames: Array[String] = Array("id")

  override def getInt(columnIndex: Int): Int = id.toInt

  override def getBlob(columnIndex: Int): Array[Byte] = null

  override def getString(columnIndex: Int): String = id.toString
}
