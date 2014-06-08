package com.slicktroid.tests

import scala.slick.android.SlickDroidDriver.simple._
import scala.slick.ast.NumericTypedType

/**
  */
class RelationalTypeSpec extends SlickDroidSpec {

  scenario("testNumeric") {
    def testStore[T](values: T*)(implicit tm: BaseColumnType[T] with NumericTypedType) {
      class Tbl(tag: Tag) extends Table[(Int, T)](tag, "test_numeric") {
        def id = column[Int]("id")
        def data = column[T]("data")
        def * = (id, data)
      }
      val tbl = TableQuery[Tbl]
      tbl.ddl.create;
      val data = values.zipWithIndex.map { case (d, i) => (i+1, d) }
      tbl ++= data
      val q = tbl.sortBy(_.id)
      q.run shouldEqual data
      tbl.ddl.drop
    }

    testStore[Int](-1, 0, 1, Int.MinValue, Int.MaxValue)
    testStore[Long](-1L, 0L, 1L, Long.MinValue, Long.MaxValue)
    testStore[Short](-1, 0, 1, Short.MinValue, Short.MaxValue)
    testStore[Byte](-1, 0, 1, Byte.MinValue, Byte.MaxValue)
    testStore[Double](-1.0, 0.0, 1.0)
    testStore[Float](-1.0f, 0.0f, 1.0f)
//    ifCap(rcap.typeBigDecimal) {
      testStore[BigDecimal](BigDecimal("-1"), BigDecimal("0"), BigDecimal("1"),
        BigDecimal(Long.MinValue), BigDecimal(Long.MaxValue))
//    }
  }

  private def roundtrip[T : BaseColumnType](tn: String, v: T) {
    class T1(tag: Tag) extends Table[(Int, T)](tag, tn) {
      def id = column[Int]("id")
      def data = column[T]("data")
      def * = (id, data)
    }
    val t1 = TableQuery[T1]

    t1.ddl.create
    t1 += (1, v)
    t1.map(_.data).run.head shouldEqual v
    t1.filter(_.data === v).map(_.id).run.headOption shouldEqual Some(1)
    t1.filter(_.data =!= v).map(_.id).run.headOption shouldEqual None
    t1.filter(_.data === v.bind).map(_.id).run.headOption shouldEqual Some(1)
    t1.filter(_.data =!= v.bind).map(_.id).run.headOption shouldEqual None
  }

  scenario("testBoolean") {
    roundtrip[Boolean]("boolean_true", true)
    roundtrip[Boolean]("boolean_false", false)
  }

  scenario("testUnit") {
    class T(tag: Tag) extends Table[Int](tag, "unit_t") {
      def id = column[Int]("id")
      def * = id
    }
    val ts = TableQuery[T]
    ts.ddl.create
    ts += 42
    ts.map(_ => ()).run shouldEqual Seq(())
    ts.map(a => ((), a)).run shouldEqual Seq(((), 42))
    ts.map(a => (a, ())).run shouldEqual Seq((42, ()))
  }
}
