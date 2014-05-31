package scala.slick.android

import scala.slick.util.CloseableIterator
import android.util.Log

/** An invoker which executes an SQL statement through JDBC. */
abstract class StatementInvoker[+R] extends Invoker[R] { self =>

  protected def getStatement: String
  protected def setParam(st: PreparedStatement): Unit

  def iteratorTo(maxRows: Int)(implicit session: AndroidBackend#Session): CloseableIterator[R] =
    results(maxRows).fold(r => new CloseableIterator.Single[R](r.asInstanceOf[R]), identity)

  /** Invoke the statement and return the raw results. */
  def results(maxRows: Int)(implicit session: AndroidBackend#Session): Either[Int, PositionedResultIterator[R]] = {
    val st = session.prepareStatement(getStatement)
    setParam(st)
    Log.d("QueryInvoker", s"results(maxRows: $maxRows), st: '$st'")
    if (st.sql.trim().startsWith("select")) {
      var doClose = true
      try {
        val cursor = st.executeQuery()
        Log.d("QueryInvoker", s"cursor count: ${cursor.getCount}")
        val pr = new PositionedResult(cursor) {
          override def close() = {
            super.close()
            st.close()
          }
        }
        val rs = new PositionedResultIterator[R](pr, maxRows) {
          def extractValue(pr: PositionedResult) = self.extractValue(pr)
        }
        doClose = false
        Right(rs)
      } finally if(doClose) st.close()
    } else {
      try {
        Left(st.executeUpdateDelete())
      } finally st.close()
    }
  }

  protected def extractValue(pr: PositionedResult): R
}


