package scala.slick.android

import android.database.sqlite._
import scala.slick.backend.DatabaseComponent
import scala.slick.SlickException
import scala.slick.util.SlickLogger
import org.slf4j.LoggerFactory

trait AndroidBackend extends DatabaseComponent {
//  protected[this] lazy val statementLogger = new SlickLogger(LoggerFactory.getLogger(classOf[AndroidBackend].getName+".statement"))
  protected[this] lazy val statementLogger = new AndroidSlickLogger(classOf[AndroidBackend].getName+".statement")

  type Database = DatabaseDef
  type Session = SessionDef
  type DatabaseFactory = DatabaseFactoryDef

  val Database = new DatabaseFactoryDef {}
  val backend: AndroidBackend = this

  trait DatabaseDef extends super.DatabaseDef {
    
    /** The DatabaseCapabilities, accessed through a Session and created by the
      * first Session that needs them. Access does not need to be synchronized
      * because, in the worst case, capabilities will be determined multiple
      * times by different concurrent sessions but the result should always be
      * the same. */
    @volatile
    protected[AndroidBackend] var capabilities: DatabaseCapabilities = null

    def createSession(): Session = new BaseSession(this)

    def openDatabase(): SQLiteDatabase
  }

  trait DatabaseFactoryDef extends super.DatabaseFactoryDef {

    def apply(helper: SQLiteOpenHelper): DatabaseDef = new DatabaseDef {
      override def openDatabase(): SQLiteDatabase = helper.getWritableDatabase
    }
  }

  trait SessionDef extends super.SessionDef { self =>

    def database: Database
    def db: SQLiteDatabase
    def capabilities: DatabaseCapabilities

    final def prepareStatement(sql: String): PreparedStatement = {
      statementLogger.debug("Preparing statement: "+sql)
      PreparedStatement(sql)(db)
    }

    /** A wrapper around the JDBC Connection's prepareStatement method, that automatically closes the statement. */
    final def withPreparedStatement[T](sql: String)(f: (PreparedStatement => T)): T = {
      val st = prepareStatement(sql)
      try f(st) finally st.close()
    }

    def close(): Unit

    /**
     * Call this method within a <em>withTransaction</em> call to roll back the current
     * transaction after <em>withTransaction</em> returns.
     */
    def rollback(): Unit

    def force() { db }

    /**
     * Run the supplied function within a transaction. If the function throws an Exception
     * or the session's rollback() method is called, the transaction is rolled back,
     * otherwise it is commited when the function returns.
     */
    def withTransaction[T](f: => T): T
  }

  class BaseSession(val database: Database) extends SessionDef {
    protected var open = false
    protected var doRollback = false
    protected var inTransaction = false

    lazy val db = { open = true; database.openDatabase() }

    def capabilities = {
      val dc = database.capabilities
      if(dc ne null) dc
      else {
        val newDC = new DatabaseCapabilities(this)
        database.capabilities = newDC
        newDC
      }
    }

    def close() {
      if(open) db.close()
    }

    def rollback() {
      if(!db.inTransaction()) throw new SlickException("Cannot roll back session in auto-commit mode")
      doRollback = true
    }

    def withTransaction[T](f: => T): T = if(inTransaction) f else {
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

  /**
   * Describes capabilities of the database which can be determined from a
   * DatabaseMetaData object and then cached and reused for all sessions.
   */
  class DatabaseCapabilities(session: Session) {
    val supportsBatchUpdates = true
  }
}

object AndroidBackend extends AndroidBackend {}
