package scala.slick.android

import scala.slick.driver.SQLiteDriver
import scala.slick.jdbc.JdbcBackend
import android.database.sqlite.SQLiteOpenHelper
import java.sql.{Driver, Connection}
import java.util.Properties
import javax.sql.DataSource


trait SlickDroidBackend extends JdbcBackend {
  val SlickDroidDatabase = new DatabaseFactoryDef {}
  override val Database = SlickDroidDatabase
  override val backend = this

  trait SlickDroidDatabaseDef extends super.DatabaseDef {
    override def createSession(): Session = new SlickDroidSession(this)
  }

  trait DatabaseFactoryDef extends super.DatabaseFactoryDef {

    def apply(helper: SQLiteOpenHelper): DatabaseDef = new SlickDroidDatabaseDef {
      override def createConnection(): Connection = new SlickDroidConnection(helper.getWritableDatabase)
    }
  }

  class SlickDroidSession(database: Database) extends super.BaseSession(database) {

    val db = conn.asInstanceOf[SlickDroidConnection].db

    override def withTransaction[T](f: => T): T = if(inTransaction) f else {
      inTransaction = true
      db.beginTransaction()
      try {
        doRollback = false
        val res = f
        if(!doRollback) db.setTransactionSuccessful()
        res
      } finally {
        db.endTransaction()
        inTransaction = false
      }
    }
  }
}

object SlickDroidBackend extends SlickDroidBackend

/**
  */
trait SlickDroidDriver extends SQLiteDriver { driver =>

//  override val backend = SlickDroidBackend
  override val simple = new SimpleQL()

  class SimpleQL extends super.SimpleQL {
    val SlickDatabase = SlickDroidBackend.SlickDroidDatabase
  }
}

object SlickDroidDriver extends SlickDroidDriver