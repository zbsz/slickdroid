package scala.slick.android

import scala.slick.util.CloseableIterator

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
    try {
      Left(st.executeUpdateDelete())
    } finally st.close()
  }

  protected def extractValue(pr: PositionedResult): R
}


