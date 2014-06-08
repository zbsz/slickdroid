package com.slicktroid.tests

import org.scalatest._
import android.database.sqlite.{SQLiteDatabase, SQLiteOpenHelper}
import java.util.UUID
import scala.slick.android.SlickDroidDriver.simple._
import org.robolectric.Robolectric

/**
  */
trait SlickDroidSpec extends FeatureSpec with Matchers with BeforeAndAfter with RobolectricSuite {

  val dbName = UUID.randomUUID().toString

  var dbHelper: SQLiteOpenHelper = _
  var db: Database = _

  implicit var session: Session = null

  before {
    dbHelper = new SQLiteOpenHelper(Robolectric.application, dbName, null, 1) {
      override def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int): Unit = {}
      override def onCreate(db: SQLiteDatabase): Unit = {}
    }
    db = SlickDatabase(dbHelper)
    session = db.createSession()
    session should not be null
  }

  after {
    session.close()
    dbHelper.close()
    Robolectric.application.getDatabasePath(dbName).delete()
  }

  override def useInstrumentation(name: String): Option[Boolean] = {
    if (name.startsWith("scala.slick")) Some(true)
    else super.useInstrumentation(name)
  }

  def assertTablesExist(tables: String*)(implicit session: Session) {
//    for(t <- tables) {
//      try { session.db.rawQuery("select 1 from " + AndroidDriver.quoteIdentifier(t) + " where 1 < 0", Array()) } catch { case e: Exception =>
//        fail(s"Table $t should exist")
//      }
//    }
  }

  def assertNotTablesExist(tables: String*)(implicit session: Session) {
//    for(t <- tables) {
//      try {
//        session.db.rawQuery("select 1 from " + AndroidDriver.quoteIdentifier(t) + " where 1 < 0", Array())
//        fail(s"Table $t should not exist")
//      } catch { case e: Exception => }
//    }
  }

  def assertAllMatch[T](t: TraversableOnce[T])(f: PartialFunction[T, _]) = t.foreach { x =>
    if(!f.isDefinedAt(x)) fail("Expected shape not matched by: "+x)
  }
}
