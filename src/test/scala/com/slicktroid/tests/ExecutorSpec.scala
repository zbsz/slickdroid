package com.slicktroid.tests

import scala.slick.android.SlickDroidDriver.simple._

class ExecutorSpec extends SlickDroidSpec {

  scenario("testExecutor") {
    class T(tag: Tag) extends Table[Int](tag, "t_exe") {
      def a = column[Int]("a")
      def * = a
    }
    val ts = TableQuery[T]

    ts.ddl.create
    ts ++= Seq(2, 3, 1, 5, 4)

    val q = ts.sortBy(_.a).map(_.a)

    val r2 = q.run
    val r2t: Seq[Int] = r2
    r2t shouldEqual List(1, 2, 3, 4, 5)

    val r3 = q.length.run
    val r3t: Int = r3
    r3t shouldEqual 5
  }

  scenario("testCollections") {
    class T(tag: Tag) extends Table[(Int, String)](tag, "t_coll") {
      def a = column[Int]("a")
      def b = column[String]("b")
      def * = (a, b)
    }
    val ts = TableQuery[T]

    ts.ddl.create
    ts ++= Seq(2 -> "a", 3 -> "b", 1 -> "c", 5 -> "d", 4 -> "e")

    val q1 = ts.sortBy(_.a).map(_.a)
    val r1 = q1.run
    val r1t: Seq[Int] = r1
    r1t shouldEqual Seq(1, 2, 3, 4, 5)

    val q2a = ts.sortBy(_.a).map(_.a).to[Set]
    val q2b = ts.sortBy(_.a).to[Set].map(_.a)
    val q2c = ts.to[Set].sortBy(_.a).map(_.a)
    val r2a = q2a.run: Set[Int]
    val r2b = q2b.run: Set[Int]
    val r2c = q2c.run: Set[Int]
    val e2 = Set(1, 2, 3, 4, 5)
    r2a shouldEqual e2
    r2b shouldEqual e2
    r2c shouldEqual e2

    val r3a = ts.to[Array].run
    r3a.isInstanceOf[Array[(Int, String)]] shouldEqual true
    val r3b = ts.to[Array].map(_.a).run
    r3b.isInstanceOf[Array[Int]] shouldEqual true
  }
}
