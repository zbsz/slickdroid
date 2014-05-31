package scala.slick.android

import scala.language.{higherKinds, existentials}
import scala.slick.SlickException
import scala.slick.ast.{ColumnOption, Node}
import scala.slick.lifted.{CompiledStreamingExecutable, FlatShapeLevel, Query, Shape}
import scala.slick.profile.BasicInsertInvokerComponent
import android.database.MatrixCursor

/** A slice of the `JdbcProfile` cake which provides the functionality for
  * different kinds of insert operations. */
trait AndroidInsertInvokerComponent extends BasicInsertInvokerComponent{ driver: AndroidDriver =>
  type InsertInvoker[T] = CountingInsertInvokerDef[T]

  def createInsertInvoker[U](tree: CompiledInsert): CountingInsertInvokerDef[U] = createCountingInsertInvoker(tree)

  // Create the different invokers -- these methods should be overridden by drivers as needed
  def createCountingInsertInvoker[U](compiled: CompiledInsert): CountingInsertInvokerDef[U] = new CountingInsertInvoker[U](compiled)
  def createReturningInsertInvoker[U, RU](compiled: CompiledInsert, keys: Node): ReturningInsertInvokerDef[U, RU] = new ReturningInsertInvoker[U, RU](compiled, keys)

  //////////////////////////////////////////////////////////// InsertInvokerDef Traits

  /** The JDBC-specific InsertInvoker with additional methods */
  trait InsertInvokerDef[U] extends super.InsertInvokerDef[U] {
    /** The return type for `insertOrUpdate` operations */
    type SingleInsertOrUpdateResult

    /** Get the SQL statement for a standard (soft) insert */
    def insertStatement: String

    /** Get the SQL statement for a forced insert */
    def forceInsertStatement: String

    /** Insert a single row, skipping AutoInc columns. */
    def insert(value: U)(implicit session: Backend#Session): SingleInsertResult

    /** Insert a single row, including AutoInc columns. This is not supported
      * by all database engines (see
      * [[scala.slick.driver.JdbcProfile.capabilities.forceInsert]]). */
    def forceInsert(value: U)(implicit session: Backend#Session): SingleInsertResult

    /** Insert multiple rows, skipping AutoInc columns.
      * Uses JDBC's batch update feature if supported by the JDBC driver.
      * Returns Some(rowsAffected), or None if the database returned no row
      * count for some part of the batch. If any part of the batch fails, an
      * exception is thrown. */
    def insertAll(values: U*)(implicit session: Backend#Session): MultiInsertResult

    /** Insert multiple rows, including AutoInc columns.
      * This is not supported by all database engines (see
      * [[scala.slick.driver.JdbcProfile.capabilities.forceInsert]]).
      * Uses JDBC's batch update feature if supported by the JDBC driver.
      * Returns Some(rowsAffected), or None if the database returned no row
      * count for some part of the batch. If any part of the batch fails, an
      * exception is thrown. */
    def forceInsertAll(values: U*)(implicit session: Backend#Session): MultiInsertResult

    /** Insert a single row if its primary key does not exist in the table,
      * otherwise update the existing record. */
    def insertOrUpdate(value: U)(implicit session: Backend#Session): SingleInsertOrUpdateResult

    final def += (value: U)(implicit session: Backend#Session): SingleInsertResult = insert(value)
    final def ++= (values: Iterable[U])(implicit session: Backend#Session): MultiInsertResult = insertAll(values.toSeq: _*)
  }

  /** An InsertInvoker that can also insert from another query. This is supported for
    * inserts which return the row count or the generated keys but not mappings which
    * involve the original data that has been inserted (because it is not available on
    * the client side). */
  trait FullInsertInvokerDef[U] extends InsertInvokerDef[U] {
    /** The result type of operations that insert data produced by another query */
    type QueryInsertResult

    /** Get the SQL statement for inserting a single row from a scalar expression */
    def insertStatementFor[TT](c: TT)(implicit shape: Shape[_ <: FlatShapeLevel, TT, U, _]): String

    /** Get the SQL statement for inserting data produced by another query */
    def insertStatementFor[TT, C[_]](query: Query[TT, U, C]): String

    /** Get the SQL statement for inserting data produced by another query */
    def insertStatementFor[TT, C[_]](compiledQuery: CompiledStreamingExecutable[Query[TT, U, C], _, _]): String

    /** Insert a single row from a scakar expression */
    def insertExpr[TT](c: TT)(implicit shape: Shape[_ <: FlatShapeLevel, TT, U, _], session: Backend#Session): QueryInsertResult

    /** Insert data produced by another query */
    def insert[TT, C[_]](query: Query[TT, U, C])(implicit session: Backend#Session): QueryInsertResult

    /** Insert data produced by another query */
    def insert[TT, C[_]](compiledQuery: CompiledStreamingExecutable[Query[TT, U, C], _, _])(implicit session: Backend#Session): QueryInsertResult
  }

  /** An InsertInvoker that returns the number of affected rows. */
  trait CountingInsertInvokerDef[U] extends FullInsertInvokerDef[U] {
    type SingleInsertResult = Int
    type MultiInsertResult = Option[Int]
    type SingleInsertOrUpdateResult = Int
    type QueryInsertResult = Int

    /** Add a mapping from the inserted values and the generated key to compute a new return value. */
    def returning[RT, RU, C[_]](value: Query[RT, RU, C]): ReturningInsertInvokerDef[U, RU]
  }

  /** In InsertInvoker that returns generated keys or other columns. */
  trait ReturningInsertInvokerDef[U, RU] extends FullInsertInvokerDef[U] { self =>
    type SingleInsertResult = RU
    type MultiInsertResult = Seq[RU]
    type SingleInsertOrUpdateResult = Option[RU]
    type QueryInsertResult = Seq[RU]

    /** Specifies a mapping from inserted values and generated keys to a desired value.
      * @param f Function that maps inserted values and generated keys to a desired value.
      * @tparam R target type of the mapping */
    def into[R](f: (U, RU) => R): IntoInsertInvokerDef[U, R] = new IntoInsertInvokerDef[U, R] {
      def forceInsert(value: U)(implicit session: Backend#Session): R = f(value, self.forceInsert(value))
      def forceInsertAll(values: U*)(implicit session: Backend#Session): Seq[R] = (values, self.forceInsertAll(values: _*)).zipped.map(f)
      def forceInsertStatement: String = self.forceInsertStatement
      def insert(value: U)(implicit session: Backend#Session): R = f(value, self.insert(value))
      def insertAll(values: U*)(implicit session: Backend#Session): Seq[R] = (values, self.insertAll(values: _*)).zipped.map(f)
      def insertOrUpdate(value: U)(implicit session: Backend#Session): Option[R] = self.insertOrUpdate(value).map(ru => f(value, ru))
      def insertStatement: String = self.insertStatement
      def insertStatementFor[TT, C[_]](query: Query[TT, U, C]): String = self.insertStatementFor[TT, C](query)
      def insertStatementFor[TT, C[_]](compiledQuery: CompiledStreamingExecutable[Query[TT, U, C], _, _]): String = self.insertStatementFor[TT, C](compiledQuery)
      def insertStatementFor[TT](c: TT)(implicit shape: Shape[_ <: FlatShapeLevel, TT, U, _]): String = self.insertStatementFor[TT](c)(shape)
    }
  }

  /** An InsertInvoker that returns a mapping of inserted values and generated keys. */
  trait IntoInsertInvokerDef[U, R] extends InsertInvokerDef[U] {
    type SingleInsertResult = R
    type MultiInsertResult = Seq[R]
    type SingleInsertOrUpdateResult = Option[R]
  }

  //////////////////////////////////////////////////////////// InsertInvoker Implementations

  protected abstract class BaseInsertInvoker[U](protected val compiled: CompiledInsert) extends FullInsertInvokerDef[U] {
    protected def useServerSideUpsert = false
    protected def useTransactionForUpsert = true
    protected def useBatchUpdates(implicit session: Backend#Session) = session.capabilities.supportsBatchUpdates

    protected def retOne(st: PreparedStatement, value: U, rowId: Long): SingleInsertResult
    protected def retMany(values: Seq[U], individual: Seq[SingleInsertResult]): MultiInsertResult
    protected def retOneInsertOrUpdate(st: PreparedStatement, value: U, updateCount: Int): SingleInsertOrUpdateResult
    protected def retOneInsertOrUpdateFromInsert(st: PreparedStatement, value: U, rowId: Long): SingleInsertOrUpdateResult
    protected def retOneInsertOrUpdateFromUpdate(updateCount: Int): SingleInsertOrUpdateResult

    lazy val insertStatement = compiled.standardInsert.sql
    lazy val forceInsertStatement = compiled.forceInsert.sql
    def insertStatementFor[TT, C[_]](query: Query[TT, U, C]): String = buildSubquery(query).sql
    def insertStatementFor[TT, C[_]](compiledQuery: CompiledStreamingExecutable[Query[TT, U, C], _, _]): String = buildSubquery(compiledQuery).sql
    def insertStatementFor[TT](c: TT)(implicit shape: Shape[_ <: FlatShapeLevel, TT, U, _]): String = insertStatementFor(Query(c)(shape))

    protected def buildSubquery[TT, C[_]](query: Query[TT, U, C]): SQLBuilder.Result =
      compiled.standardInsert.ibr.buildInsert(queryCompiler.run(query.toNode).tree)

    protected def buildSubquery[TT, C[_]](compiledQuery: CompiledStreamingExecutable[Query[TT, U, C], _, _]): SQLBuilder.Result =
      compiled.standardInsert.ibr.buildInsert(compiledQuery.compiledQuery)

    protected def preparedInsert[T](sql: String)(f: PreparedStatement => T)(implicit session: Backend#Session) =
      session.withPreparedStatement(sql)(f)

    protected def preparedOther[T](sql: String)(f: PreparedStatement => T)(implicit session: Backend#Session) =
      session.withPreparedStatement(sql)(f)

    final def insert(value: U)(implicit session: Backend#Session): SingleInsertResult = internalInsert(compiled.standardInsert, value)

    final def forceInsert(value: U)(implicit session: Backend#Session): SingleInsertResult = internalInsert(compiled.forceInsert, value)

    protected def internalInsert(a: compiled.Artifacts, value: U)(implicit session: Backend#Session): SingleInsertResult =
      preparedInsert(a.sql) { st =>
        st.clearBindings()
        a.converter.set(value, st)
        retOne(st, value, st.executeInsert())
      }

    final def insertAll(values: U*)(implicit session: Backend#Session): MultiInsertResult = internalInsertAll(compiled.standardInsert, values: _*)

    final def forceInsertAll(values: U*)(implicit session: Backend#Session): MultiInsertResult = internalInsertAll(compiled.forceInsert, values: _*)

    protected def internalInsertAll(a: compiled.Artifacts, values: U*)(implicit session: Backend#Session): MultiInsertResult = session.withTransaction {
      retMany(values, values.map(insert))
    }

    final def insertOrUpdate(value: U)(implicit session: Backend#Session): SingleInsertOrUpdateResult = {
      def f(): SingleInsertOrUpdateResult = {
        if(useServerSideUpsert) {
          preparedInsert(compiled.upsert.sql) { st =>
            st.clearBindings()
            compiled.upsert.converter.set(value, st)
            retOneInsertOrUpdate(st, value, st.executeUpdateDelete())
          }
        } else internalInsertOrUpdateEmulation(value)
      }
      if(useTransactionForUpsert) session.withTransaction(f()) else f()
    }

    protected def internalInsertOrUpdateEmulation(value: U)(implicit session: Backend#Session): SingleInsertOrUpdateResult = {
      val found = preparedOther(compiled.checkInsert.sql) { st =>
        st.clearBindings()
        compiled.checkInsert.converter.set(value, st)
        val c = st.executeQuery()
        try { c.moveToFirst() } finally { c.close() }
      }
      if(found) preparedOther(compiled.updateInsert.sql) { st =>
        st.clearBindings()
        compiled.updateInsert.converter.set(value, st)
        retOneInsertOrUpdateFromUpdate(st.executeUpdateDelete())
      } else preparedInsert(compiled.standardInsert.sql) { st =>
        st.clearBindings()
        compiled.standardInsert.converter.set(value, st)
        retOneInsertOrUpdateFromInsert(st, value, st.executeInsert())
      }
    }

    protected def retQuery(st: PreparedStatement, updateCount: Int): QueryInsertResult

    def insertExpr[TT](c: TT)(implicit shape: Shape[_ <: FlatShapeLevel, TT, U, _], session: Backend#Session): QueryInsertResult =
      insert(Query(c)(shape))(session)

    def insert[TT, C[_]](query: Query[TT, U, C])(implicit session: Backend#Session): QueryInsertResult =
      internalInsertQuery(buildSubquery(query), null)

    def insert[TT, C[_]](compiledQuery: CompiledStreamingExecutable[Query[TT, U, C], _, _])(implicit session: Backend#Session): QueryInsertResult =
      internalInsertQuery(buildSubquery(compiledQuery), compiledQuery.param)

    protected def internalInsertQuery(sbr: SQLBuilder.Result, param: Any)(implicit session: Backend#Session): QueryInsertResult = {
      preparedInsert(sbr.sql) { st =>
        st.clearBindings()
        sbr.setter(st, 1, param)
        retQuery(st, st.executeUpdateDelete())
      }
    }
  }

  protected class CountingInsertInvoker[U](compiled: CompiledInsert) extends BaseInsertInvoker[U](compiled) with CountingInsertInvokerDef[U] {

    // SQLite cannot perform server-side insert-or-update with soft insert semantics. We don't have to do
    // the same in ReturningInsertInvoker because SQLite does not allow returning non-AutoInc keys anyway.
    override protected val useServerSideUpsert = compiled.upsert.fields.forall(fs => !fs.options.contains(ColumnOption.AutoInc))
    override protected def useTransactionForUpsert = !useServerSideUpsert

    protected def retOne(st: PreparedStatement, value: U, rowId: Long) = if (rowId < 0) 0 else 1

    protected def retMany(values: Seq[U], individual: Seq[SingleInsertResult]) = Some(individual.filter(_ > -1).length)

    protected def retQuery(st: PreparedStatement, updateCount: Int) = updateCount

    protected def retOneInsertOrUpdate(st: PreparedStatement, value: U, updateCount: Int) = updateCount
    protected def retOneInsertOrUpdateFromInsert(st: PreparedStatement, value: U, rowId: Long) = if (rowId < 0) 0 else 1
    protected def retOneInsertOrUpdateFromUpdate(updateCount: Int) = updateCount

    def returning[RT, RU, C[_]](value: Query[RT, RU, C]) = createReturningInsertInvoker[U, RU](compiled, value.toNode)
  }

  protected class ReturningInsertInvoker[U, RU](compiled: CompiledInsert, keys: Node) extends BaseInsertInvoker[U](compiled) with ReturningInsertInvokerDef[U, RU] {

    protected def checkInsertOrUpdateKeys(): Unit =
      if(keyReturnOther) throw new SlickException("Only a single AutoInc column may be returned from an insertOrUpdate call")

    protected def buildKeysResult(rowIds: Long*): Invoker[RU] = {
      val cursor = new MatrixCursor(keyColumns.toArray)
      rowIds foreach { rowId =>
        cursor.addRow(Array[AnyRef](java.lang.Long.valueOf(rowId)))
      }
      cursor.moveToFirst()
      ResultSetInvoker[RU](_ => cursor)(pr => keyConverter.read(pr.rs).asInstanceOf[RU])
    }

    protected lazy val (keyColumns, keyConverter, keyReturnOther) = compiled.buildReturnColumns(keys)

    override protected def preparedInsert[T](sql: String)(f: PreparedStatement => T)(implicit session: Backend#Session) = {
      session.withPreparedStatement(sql)(f)
    }

    protected def retOne(st: PreparedStatement, value: U, rowId: Long) = keyConverter.read(IdReturnCursor(rowId)).asInstanceOf[RU]

    protected def retMany(values: Seq[U], individual: Seq[SingleInsertResult]) = individual

    protected def retManyBatch(st: PreparedStatement, values: Seq[U], rowIds: Array[Long]) =
      rowIds map { rowId =>
        keyConverter.read(IdReturnCursor(rowId)).asInstanceOf[RU]
      }

    protected def retQuery(st: PreparedStatement, updateCount: Int) = ???
//      buildKeysResult(st).buildColl[Vector](null, implicitly)

    protected def retOneInsertOrUpdate(st: PreparedStatement, value: U, updateCount: Int): SingleInsertOrUpdateResult = ??? // will never be used do to useServerSideUpsert = false

    protected def retOneInsertOrUpdateFromInsert(st: PreparedStatement, value: U, rowId: Long): SingleInsertOrUpdateResult =
      if (rowId < 0) None else Some(keyConverter.read(IdReturnCursor(rowId)).asInstanceOf[RU])

    protected def retOneInsertOrUpdateFromUpdate(count: Int): SingleInsertOrUpdateResult = None
  }
}
