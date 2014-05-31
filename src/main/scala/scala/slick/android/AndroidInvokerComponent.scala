package scala.slick.android

import scala.language.{higherKinds, existentials}
import scala.slick.ast.{CompiledStatement, ResultSetMapping, Node, ParameterSwitch}
import scala.slick.profile.BasicInvokerComponent
import scala.slick.relational.{ResultConverter, CompiledMapping}
import android.util.Log

trait AndroidInvokerComponent extends BasicInvokerComponent{ driver: AndroidDriver =>

  // Create the different invokers -- these methods should be overridden by drivers as needed
  def createUpdateInvoker[T](tree: Node, param: Any) = new UpdateInvoker[T](tree, param)
  def createDeleteInvoker(tree: Node, param: Any) = new DeleteInvoker(tree, param)
  def createQueryInvoker[R](tree: Node, param: Any): QueryInvoker[R] = new QueryInvoker[R](tree, param)
  def createDDLInvoker(ddl: SchemaDescription) = new DDLInvoker(ddl)

  /** An Invoker for queries. */
  class QueryInvoker[R](tree: Node, param: Any) extends StatementInvoker[R] { self =>

    protected[this] val ResultSetMapping(_, compiled, CompiledMapping(_converter, _)) = tree
    protected[this] val converter = _converter.asInstanceOf[ResultConverter[AndroidResultConverterDomain, R]]
    protected[this] val CompiledStatement(_, sres: SQLBuilder.Result, _) = findCompiledStatement(compiled)

    protected[this] def findCompiledStatement(n: Node): CompiledStatement = n match {
      case c: CompiledStatement => c
      case ParameterSwitch(cases, default) =>
        findCompiledStatement(cases.find { case (f, n) => f(param) }.map(_._2).getOrElse(default))
    }

    /** Invoke the statement and return the raw results. */
    override def results(maxRows: Int)(implicit session: AndroidBackend#Session): Either[Int, PositionedResultIterator[R]] = {
      val st = session.prepareStatement(getStatement)
      setParam(st)
      Log.d("QueryInvoker", s"results(maxRows: $maxRows), st: '$st'")
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
    }

    protected def getStatement = sres.sql
    protected def setParam(st: PreparedStatement): Unit = sres.setter(st, 1, param)
    protected def extractValue(pr: PositionedResult): R = converter.read(pr.rs)
    def invoker: this.type = this
  }

  class DDLInvoker(ddl: DDL) extends super.DDLInvoker {
    def create(implicit session: Backend#Session): Unit = session.withTransaction {
      for(s <- ddl.createStatements)
        session.withPreparedStatement(s)(_.execute)
    }

    def drop(implicit session: Backend#Session): Unit = session.withTransaction {
      for(s <- ddl.dropStatements)
        session.withPreparedStatement(s)(_.execute)
    }
  }

  /** Pseudo-invoker for running DELETE calls. */
  class DeleteInvoker(protected val tree: Node, param: Any) {
    protected[this] val ResultSetMapping(_, CompiledStatement(_, sres: SQLBuilder.Result, _), _) = tree

    def deleteStatement = sres.sql

    def delete(implicit session: Backend#Session): Int = session.withPreparedStatement(deleteStatement) { st =>
      sres.setter(st, 1, param)
      st.executeUpdateDelete()
    }

    def deleteInvoker: this.type = this
  }

  /** Pseudo-invoker for running UPDATE calls. */
  class UpdateInvoker[T](protected val tree: Node, param: Any) {
    protected[this] val ResultSetMapping(_, CompiledStatement(_, sres: SQLBuilder.Result, _), CompiledMapping(_converter, _)) = tree
    protected[this] val converter = _converter.asInstanceOf[ResultConverter[AndroidResultConverterDomain, T]]

    def updateStatement = getStatement

    protected def getStatement = sres.sql

    def update(value: T)(implicit session: Backend#Session): Int = session.withPreparedStatement(updateStatement) { st =>
      st.clearBindings()
      converter.set(value, st)
      sres.setter(st, converter.width+1, param)
      st.executeUpdateDelete()
    }

    def updateInvoker: this.type = this
  }
}
