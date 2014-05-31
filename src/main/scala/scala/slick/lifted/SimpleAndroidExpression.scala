package scala.slick.lifted

import scala.slick.ast._
import scala.slick.android.AndroidStatementBuilderComponent

/** A SimpleExpression allows arbitrary SQL code to be generated. */
trait SimpleAndroidExpression extends Node {
  def toSQL(qb: AndroidStatementBuilderComponent#QueryBuilder): Unit
}

object SimpleAndroidExpression {
  def apply[T : TypedType](f: (Seq[Node], AndroidStatementBuilderComponent#QueryBuilder) => Unit): (Seq[Column[_]] => Column[T]) = {
    def build(params: IndexedSeq[Node]): SimpleFeatureNode[T] = new SimpleFeatureNode[T] with SimpleAndroidExpression {
      def toSQL(qb: AndroidStatementBuilderComponent#QueryBuilder) = f(nodeChildren, qb)
      def nodeChildren = params
      protected[this] def nodeRebuild(ch: IndexedSeq[Node]) = build(ch)
    }
    { paramsC: Seq[Column[_] ] => Column.forNode(build(paramsC.map(_.toNode)(collection.breakOut))) }
  }

  def nullary[R : TypedType](f: AndroidStatementBuilderComponent#QueryBuilder => Unit): Column[R] = {
    val g = apply({ (ch: Seq[Node], qb: AndroidStatementBuilderComponent#QueryBuilder) => f(qb) });
    g.apply(Seq())
  }

  def unary[T1, R : TypedType](f: (Node, AndroidStatementBuilderComponent#QueryBuilder) => Unit): (Column[T1] => Column[R]) = {
    val g = apply({ (ch: Seq[Node], qb: AndroidStatementBuilderComponent#QueryBuilder) => f(ch(0), qb) });
    { t1: Column[T1] => g(Seq(t1)) }
  }

  def binary[T1, T2, R : TypedType](f: (Node, Node, AndroidStatementBuilderComponent#QueryBuilder) => Unit): ((Column[T1], Column[T2]) => Column[R]) = {
    val g = apply({ (ch: Seq[Node], qb: AndroidStatementBuilderComponent#QueryBuilder) => f(ch(0), ch(1), qb) });
    { (t1: Column[T1], t2: Column[T2]) => g(Seq(t1, t2)) }
  }

  def ternary[T1, T2, T3, R : TypedType](f: (Node, Node, Node, AndroidStatementBuilderComponent#QueryBuilder) => Unit): ((Column[T1], Column[T2], Column[T3]) => Column[R]) = {
    val g = apply({ (ch: Seq[Node], qb: AndroidStatementBuilderComponent#QueryBuilder) => f(ch(0), ch(1), ch(2), qb) });
    { (t1: Column[T1], t2: Column[T2], t3: Column[T3]) => g(Seq(t1, t2, t3)) }
  }
}