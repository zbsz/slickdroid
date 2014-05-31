package slickdroid.example.tests

import scala.slick.android.AndroidDriver.simple._
import org.scalatest.{Matchers, FeatureSpec, BeforeAndAfter}
import android.database.sqlite.{SQLiteDatabase, SQLiteOpenHelper}
import java.util.UUID
import slickdroid.example.TestRunner
import scala.slick.android.AndroidDriver

/**
  */
class AndroidBackendSpec extends FeatureSpec with Matchers with BeforeAndAfter {

  val dbName = UUID.randomUUID().toString

  var dbHelper: SQLiteOpenHelper = _
  var db: Database = _

  implicit var session: Session = null

  before {
    dbHelper = new SQLiteOpenHelper(TestRunner.context, dbName, null, 1) {
      override def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int): Unit = {}
      override def onCreate(db: SQLiteDatabase): Unit = {}
    }
    db = Database(dbHelper)
    session = db.createSession()
  }

  after {
    session.close()
    dbHelper.close()
    TestRunner.context.getDatabasePath(dbName).delete()
  }

  def assertTablesExist(tables: String*)(implicit session: Session) {
    for(t <- tables) {
      try { session.db.rawQuery("select 1 from " + AndroidDriver.quoteIdentifier(t) + " where 1 < 0", Array()) } catch { case e: Exception =>
        fail(s"Table $t should exist")
      }
    }
  }

  def assertNotTablesExist(tables: String*)(implicit session: Session) {
    for(t <- tables) {
      try {
        session.db.rawQuery("select 1 from " + AndroidDriver.quoteIdentifier(t) + " where 1 < 0", Array())
        fail(s"Table $t should not exist")
      } catch { case e: Exception => }
    }
  }

  def assertAllMatch[T](t: TraversableOnce[T])(f: PartialFunction[T, _]) = t.foreach { x =>
    if(!f.isDefinedAt(x)) fail("Expected shape not matched by: "+x)
  }
}
