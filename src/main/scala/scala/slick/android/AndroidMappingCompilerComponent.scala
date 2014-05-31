package scala.slick.android

import scala.slick.compiler.{CompilerState, CodeGen}
import scala.slick.ast._
import scala.slick.relational._
import android.database.Cursor

/** JDBC driver component which contains the mapping compiler and insert compiler */
trait AndroidMappingCompilerComponent { driver: AndroidDriver =>

  /** The `MappingCompiler` for this driver. */
  val mappingCompiler: MappingCompiler = new MappingCompiler

  /** Create a (possibly specialized) `ResultConverter` for the given `JdbcType`. */
  def createBaseResultConverter[T](ti: AndroidType[T], name: String, idx: Int): ResultConverter[AndroidResultConverterDomain, T] =
    SpecializedAndroidResultConverter.base(ti, name, idx)

  /** Create a (possibly specialized) `ResultConverter` for `Option` values of the given `JdbcType`. */
  def createOptionResultConverter[T](ti: AndroidType[T], idx: Int): ResultConverter[AndroidResultConverterDomain, Option[T]] =
    SpecializedAndroidResultConverter.option(ti, idx)

  /** A ResultConverterCompiler that builds JDBC-based converters. Instances of
    * this class use mutable state internally. They are meant to be used for a
    * single conversion only and must not be shared or reused. */
  class MappingCompiler extends ResultConverterCompiler[AndroidResultConverterDomain] {
    def createColumnConverter(n: Node, idx: Int, column: Option[FieldSymbol]): ResultConverter[AndroidResultConverterDomain, _] = {
      val AndroidType(ti, option) = n.nodeType.structural
      if(option) createOptionResultConverter(ti, idx)
      else createBaseResultConverter(ti, column.fold(n.toString)(_.name), idx)
    }

    override def createGetOrElseResultConverter[T](rc: ResultConverter[AndroidResultConverterDomain, Option[T]], default: () => T) = rc match {
      case rc: OptionResultConverter[_] => rc.getOrElse(default)
      case _ => super.createGetOrElseResultConverter[T](rc, default)
    }

    override def createTypeMappingResultConverter(rc: ResultConverter[AndroidResultConverterDomain, Any], mapper: MappedScalaType.Mapper) = {
      val tm = new TypeMappingResultConverter(rc, mapper.toBase, mapper.toMapped)
      mapper.fastPath match {
        case Some(pf) => pf.orElse[Any, Any] { case x => x }.apply(tm).asInstanceOf[ResultConverter[AndroidResultConverterDomain, Any]]
        case None => tm
      }
    }
  }

  /** Code generator phase for queries on JdbcProfile-based drivers. */
  class JdbcCodeGen(f: QueryBuilder => SQLBuilder.Result) extends CodeGen {
    def compileServerSideAndMapping(serverSide: Node, mapping: Option[Node], state: CompilerState) = {
      val sbr = f(driver.createQueryBuilder(serverSide, state))
      (CompiledStatement(sbr.sql, sbr, serverSide.nodeType), mapping.map(mappingCompiler.compileMapping))
    }
  }

  /** Code generator phase for inserts on JdbcProfile-based drivers. */
  class JdbcInsertCodeGen(f: Insert => InsertBuilder) extends CodeGen {
    def compileServerSideAndMapping(serverSide: Node, mapping: Option[Node], state: CompilerState) = {
      val ib = f(serverSide.asInstanceOf[Insert])
      val ibr = ib.buildInsert
      (CompiledStatement(ibr.sql, ibr, serverSide.nodeType), mapping.map(n => mappingCompiler.compileMapping(ib.transformMapping(n))))
    }
  }

//  class JdbcFastPathExtensionMethods[T, P](val mp: MappedProjection[T, P]) {
//    def fastPath(fpf: (TypeMappingResultConverter[AndroidResultConverterDomain, T, _] => JdbcFastPath[T])): MappedProjection[T, P] = mp.genericFastPath {
//      case tm @ TypeMappingResultConverter(_: ProductResultConverter[_, _], _, _) =>
//        fpf(tm.asInstanceOf[TypeMappingResultConverter[AndroidResultConverterDomain, T, _]])
//
//    }
//  }
}

trait AndroidResultConverterDomain extends ResultConverterDomain {
  type Reader = Cursor
  type Writer = PreparedStatement
  type Updater = Unit // android sqlite doesn't support updates on cursor
}
