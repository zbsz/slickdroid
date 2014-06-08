package scala.slick.android

import scala.slick.driver.SQLiteDriver
import scala.slick.jdbc.JdbcBackend
import android.database.sqlite.SQLiteOpenHelper
import java.sql.Connection


trait SlickDroidBackend extends JdbcBackend {
  override val Database = new DatabaseFactoryDef {}
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
      conn.setAutoCommit(false)
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
        conn.setAutoCommit(true)
      }
    }
  }
}

object SlickDroidBackend extends SlickDroidBackend

/**
  */
object SlickDroidDriver extends {
  override val backend = SlickDroidBackend // early initializer, BasicProfile uses backend in constructor
} with SQLiteDriver { driver =>

  override val simple = new SimpleQL {}

  trait SimpleQL extends super.SimpleQL {
    override val Database = backend.Database
  }
}