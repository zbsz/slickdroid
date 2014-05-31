package scala.slick.android

import scala.slick.util.CloseableIterator
import android.database.Cursor

/** An invoker which calls a function to retrieve a ResultSet. This can be used
  * for reading information from a java.sql.DatabaseMetaData object which has
  * many methods that return ResultSets.
  *
  * For convenience, if the function returns null, this is treated like an
  * empty ResultSet. */
abstract class ResultSetInvoker[+R] extends Invoker[R] { self =>

  protected def createResultSet(session: AndroidBackend#Session): Cursor

  def iteratorTo(maxRows: Int)(implicit session: AndroidBackend#Session): CloseableIterator[R] = {
    val rs = createResultSet(session)
    if(rs eq null) CloseableIterator.empty
    else {
      val pr = new PositionedResult(rs) {
        override def close() = rs.close()
      }
      new PositionedResultIterator[R](pr, maxRows) {
        def extractValue(pr: PositionedResult) = self.extractValue(pr)
      }
    }
  }

  protected def extractValue(pr: PositionedResult): R
}

object ResultSetInvoker {
  def apply[R](f: AndroidBackend#Session => Cursor)(implicit conv: PositionedResult => R): Invoker[R] = new ResultSetInvoker[R] {
    def createResultSet(session: AndroidBackend#Session) = f(session)
    def extractValue(pr: PositionedResult) = conv (pr)
  }
}
