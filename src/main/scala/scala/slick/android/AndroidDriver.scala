package scala.slick.android

import scala.slick.profile.{Capability, SqlDriver, RelationalProfile, SqlProfile}
import scala.slick.ast.{BaseTypedType, Node}
import scala.slick.compiler.{InsertCompiler, Phase, QueryCompiler}
import scala.slick.lifted.{ColumnBase, RunnableCompiled, Query}
import scala.slick.jdbc.JdbcFastPath

/** A profile for accessing SQL databases via JDBC. All drivers for JDBC-based databases
  * implement this profile. */
trait AndroidProfile extends SqlProfile with AndroidTableComponent
  with AndroidInvokerComponent with AndroidInsertInvokerComponent with AndroidExecutorComponent with AndroidTypesComponent { driver: AndroidDriver =>

  type Backend = AndroidBackend
  val backend: Backend = AndroidBackend
  val simple: SimpleQL = new SimpleQL {}
  lazy val Implicit: Implicits = simple
  type ColumnType[T] = AndroidType[T]
  type BaseColumnType[T] = AndroidType[T] with BaseTypedType[T]
  val columnTypes = new JdbcTypes
  lazy val MappedColumnType = MappedAndroidType

  override protected def computeQueryCompiler = super.computeQueryCompiler ++ QueryCompiler.relationalPhases

  override protected def computeCapabilities: Set[Capability] = (super.computeCapabilities
    - RelationalProfile.capabilities.functionDatabase
    - RelationalProfile.capabilities.functionUser
    - RelationalProfile.capabilities.joinFull
    - RelationalProfile.capabilities.joinRight
    - SqlProfile.capabilities.sequence
    - RelationalProfile.capabilities.typeBigDecimal
    - RelationalProfile.capabilities.typeBlob
    - RelationalProfile.capabilities.zip
    )

  lazy val queryCompiler = compiler + new JdbcCodeGen(_.buildSelect)
  lazy val updateCompiler = compiler + new JdbcCodeGen(_.buildUpdate)
  lazy val deleteCompiler = compiler + new JdbcCodeGen(_.buildDelete)
  lazy val insertCompiler = QueryCompiler(Phase.assignUniqueSymbols, new InsertCompiler(InsertCompiler.NonAutoInc), new JdbcInsertCodeGen(createInsertBuilder))
  lazy val forceInsertCompiler = QueryCompiler(Phase.assignUniqueSymbols, new InsertCompiler(InsertCompiler.AllColumns), new JdbcInsertCodeGen(createInsertBuilder))
  lazy val upsertCompiler = QueryCompiler(Phase.assignUniqueSymbols, new InsertCompiler(InsertCompiler.AllColumns), new JdbcInsertCodeGen(createUpsertBuilder))
  lazy val checkInsertCompiler = QueryCompiler(Phase.assignUniqueSymbols, new InsertCompiler(InsertCompiler.PrimaryKeys), new JdbcInsertCodeGen(createCheckInsertBuilder))
  lazy val updateInsertCompiler = QueryCompiler(Phase.assignUniqueSymbols, new InsertCompiler(InsertCompiler.AllColumns), new JdbcInsertCodeGen(createUpdateInsertBuilder))
  def compileInsert(tree: Node) = new JdbcCompiledInsert(tree)
  type CompiledInsert = JdbcCompiledInsert

  final def buildTableSchemaDescription(table: Table[_]): DDL = createTableDDLBuilder(table).buildDDL
  final def buildSequenceSchemaDescription(seq: Sequence[_]): DDL = createSequenceDDLBuilder(seq).buildDDL

  trait LowPriorityImplicits {
    implicit def queryToAppliedQueryInvoker[U, C[_]](q: Query[_,U, C]): QueryInvoker[U] = createQueryInvoker[U](queryCompiler.run(q.toNode).tree, ())
    implicit def queryToUpdateInvoker[U, C[_]](q: Query[_, U, C]): UpdateInvoker[U] = createUpdateInvoker(updateCompiler.run(q.toNode).tree, ())
  }

  trait Implicits extends LowPriorityImplicits with super.Implicits with ImplicitColumnTypes {
    implicit def ddlToDDLInvoker(d: DDL): DDLInvoker = createDDLInvoker(d)
    implicit def queryToDeleteInvoker[C[_]](q: Query[_ <: Table[_], _, C]): DeleteInvoker = createDeleteInvoker(deleteCompiler.run(q.toNode).tree, ())
    implicit def runnableCompiledToAppliedQueryInvoker[RU, C[_]](c: RunnableCompiled[_ <: Query[_, _, C], C[RU]]): QueryInvoker[RU] = createQueryInvoker[RU](c.compiledQuery, c.param)
    implicit def runnableCompiledToUpdateInvoker[RU, C[_]](c: RunnableCompiled[_ <: Query[_, _, C], C[RU]]): UpdateInvoker[RU] =
      createUpdateInvoker(c.compiledUpdate, c.param)
    implicit def runnableCompiledToDeleteInvoker[RU, C[_]](c: RunnableCompiled[_ <: Query[_, _, C], C[RU]]): DeleteInvoker =
      createDeleteInvoker(c.compiledDelete, c.param)

    // This conversion only works for fully packed types
    implicit def productQueryToUpdateInvoker[T, C[_]](q: Query[_ <: ColumnBase[T], T, C]): UpdateInvoker[T] =
      createUpdateInvoker(updateCompiler.run(q.toNode).tree, ())
  }

  trait SimpleQL extends super.SimpleQL with Implicits {
    type FastPath[T] = JdbcFastPath[T]
  }
}

trait AndroidDriver extends SqlDriver
  with AndroidProfile
  with AndroidStatementBuilderComponent
  with AndroidMappingCompilerComponent { driver =>

  override val profile: AndroidProfile = this
}

object AndroidDriver extends AndroidDriver
