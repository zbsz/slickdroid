package slickdroid.example.tests

import scala.slick.android.SlickDroidDriver.simple._

/**
  */
class AggregateSpec extends AndroidBackendSpec {

  scenario("testAggregates") {
    class T(tag: Tag) extends Table[(Int, Option[Int])](tag, "t2") {
      def a = column[Int]("a")
      def b = column[Option[Int]]("b")
      def * = (a, b)
    }
    val ts = TableQuery[T]
    ts.ddl.create
    ts ++= Seq((1, Some(1)), (1, Some(2)), (1, Some(3)))
    def q1(i: Int) = for { t <- ts if t.a === i } yield t
    def q2(i: Int) = (q1(i).length, q1(i).map(_.a).sum, q1(i).map(_.b).sum, q1(i).map(_.b).avg)
    val q2_0 = q2(0).shaped
    val q2_1 = q2(1).shaped
    println(q2_0.run)
    println(q2_1.run)
    q2_0.run shouldEqual (0, None, None, None)
    q2_1.run shouldEqual (3, Some(3), Some(6), Some(2))
  }

  scenario("testGroupBy") {
    class T(tag: Tag) extends Table[(Int, Option[Int])](tag, "t3") {
      def a = column[Int]("a")
      def b = column[Option[Int]]("b")
      def * = (a, b)
    }
    val ts = TableQuery[T]
    ts.ddl.create
    ts ++= Seq((1, Some(1)), (1, Some(2)), (1, Some(3)))
    ts ++= Seq((2, Some(1)), (2, Some(2)), (2, Some(5)))
    ts ++= Seq((3, Some(1)), (3, Some(9)))

    println("=========================================================== q0")
    val q0 = ts.groupBy(_.a)
    val q1 = q0.map(_._2.length).sortBy(identity)
    val r0 = q1.run
    val r0t: Seq[Int] = r0
    r0t shouldEqual Vector(2, 3, 3)

    println("=========================================================== q")
    val q = (for {
      (k, v) <- ts.groupBy(t => t.a)
    } yield (k, v.length, v.map(_.a).sum, v.map(_.b).sum)).sortBy(_._1)
    val r = q.run
    val rt = r: Seq[(Int, Int, Option[Int], Option[Int])]
    println(r)
    rt shouldEqual Vector((1, 3, Some(3), Some(6)), (2, 3, Some(6), Some(8)), (3, 2, Some(6), Some(10)))

    class U(tag: Tag) extends Table[Int](tag, "u") {
      def id = column[Int]("id")
      def * = id
    }
    val us = TableQuery[U]
    us.ddl.create
    us ++= Seq(1, 2, 3)

    println("=========================================================== q2")
    val q2 = (for {
      u <- us
      t <- ts if t.a === u.id
    } yield (u, t)).groupBy(_._1.id).map {
      case (id, q) => (id, q.length, q.map(_._2.a).sum, q.map(_._2.b).sum)
    }
    val r2 = q2.run
    val r2t = r2: Seq[(Int, Int, Option[Int], Option[Int])]
    println(r2)
    r2.toSet shouldEqual Set((1, 3, Some(3), Some(6)), (2, 3, Some(6), Some(8)), (3, 2, Some(6), Some(10)))

    println("=========================================================== q3")
    val q3 = (for {
      (x, q) <- ts.map(t => (t.a + 10, t.b)).groupBy(_._1)
    } yield (x, q.map(_._2).sum)).sortBy(_._1)
    val r3 = q3.run
    val r3t = r3: Seq[(Int, Option[Int])]
    println(r3)
    r3 shouldEqual Vector((11, Some(6)), (12, Some(8)), (13, Some(10)))

    println("=========================================================== q4")
    val q4 = (for {
      (x, q) <- ts.groupBy(t => (t.a, t.b))
    } yield (x, q.length)).sortBy(_._1)
    val r4 = q4.run
    val r4t = r4: Seq[((Int, Option[Int]), Int)]
    println(r4)
    r4 shouldEqual Vector(((1,Some(1)),1), ((1,Some(2)),1), ((1,Some(3)),1),
      ((2,Some(1)),1), ((2,Some(2)),1), ((2,Some(5)),1),
      ((3,Some(1)),1), ((3,Some(9)),1))

    println("=========================================================== q5")
    val q5 = ts
      .filter(_.a === 1)
      .map(t => (t.a, t.b))
      .sortBy(_._2)
      .groupBy(x => (x._1, x._2))
      .map { case (a, _) => (a._1, a._2) }
    q5.run.toSet shouldEqual Set((1, Some(1)), (1, Some(2)), (1, Some(3)))
    us += 4

    println("=========================================================== q6")
    val q6 = (for {
      (u, t) <- us leftJoin ts on (_.id === _.a)
    } yield (u, t)).groupBy(_._1.id).map {
      case (id, q) => (id, q.length, q.map(_._1).length, q.map(_._2).length)
    }
    q6.run.toSet shouldEqual Set((1, 3, 3, 3), (2, 3, 3, 3), (3, 2, 2, 2), (4, 1, 1, 0))

    println("=========================================================== q7")
    val q7 = ts.groupBy(_.a).map { case (a, ts) =>
      (a, ts.map(_.b).sum, ts.map(_.b).min, ts.map(_.b).max, ts.map(_.b).avg)
    }
    q7.run.toSet shouldEqual Set(
      (1, Some(6), Some(1), Some(3), Some(2)),
      (2, Some(8), Some(1), Some(5), Some(2)),
      (3, Some(10), Some(1), Some(9), Some(5)))

    println("=========================================================== q8")
    val q8 = us.map( _ => "test").groupBy(x => x).map(_._2.max)
    q8.run shouldEqual (Seq(Some("test")))
    val q8b = for( (key, group) <- us.map(_ => "x").groupBy(co => co) )
    yield (key, group.map(co => co).max )
    q8b.run shouldEqual (Seq(("x", Some("x"))))
    val q8c = for( (key, group) <- us.map(_ => 5).groupBy(co => co) )
    yield (key, group.map(co => co + co).sum )
    q8c.run shouldEqual (Seq((5, Some(40))))

    println("=========================================================== q9")
    val res9 = Set(
      (1, Some(1)), (1, Some(2)), (1, Some(3)),
      (2, Some(1)), (2, Some(2)), (2, Some(5)),
      (3, Some(1)), (3, Some(9))
    )
    val q9 = ts.groupBy(x => x).map(_._1)
    q9.run.toSet shouldEqual res9
    val q9b = ts.map(x => x).groupBy(_.*).map(_._1)
    q9b.run.toSet shouldEqual res9
    val q9c = ts.map(x => x).groupBy(x => x).map(_._1)
    q9c.run.toSet shouldEqual res9

    println("=========================================================== q10")
    val q10 = (for {
      m <- ts
    } yield m) groupBy (_.a) map {
      case (id, data) => (id, data.map(_.b.asColumnOf[Option[Double]]).max)
    }
    q10.run.toSet shouldEqual Set((2,Some(5.0)), (1,Some(3.0)), (3,Some(9.0)))

    case class Pair(a:Int,b:Option[Int])
    class T4(tag: Tag) extends Table[Pair](tag, "t4") {
      def a = column[Int]("a")
      def b = column[Option[Int]]("b")
      def * = (a, b) <> (Pair.tupled,Pair.unapply)
    }
    val t4s = TableQuery[T4]
    t4s.ddl.create
    t4s ++= Seq(Pair(1, Some(1)), Pair(1, Some(2)))
    t4s ++= Seq(Pair(1, Some(1)), Pair(1, Some(2)))
    t4s ++= Seq(Pair(1, Some(1)), Pair(1, Some(2)))

    println("=========================================================== q11")
    val expected11 = Set(
      Pair(1, Some(1)), Pair(1, Some(2))
    )
    val q12 = t4s
    val res12 = q12.run
    res12.size shouldEqual 6
    res12.toSet shouldEqual expected11
    val q13 = t4s.map(identity)
    val res13 = q13.run
    res13.size shouldEqual 6
    res13.toSet shouldEqual expected11
    val q11 = t4s.groupBy(identity).map(_._1)
    val res11 = q11.run
    res11.toSet shouldEqual expected11
    res11.size shouldEqual 2
  }

  scenario("testIntLength") {
    class A(tag: Tag) extends Table[Int](tag, "A_testIntLength") {
      def id = column[Int]("ID")
      def * = id
    }
    val as = TableQuery[A]
    as.ddl.create
    as += 1

    val q1 = as.groupBy(_.id).map {
      case (_, q) => (q.map(_.id).min, q.length)
    }
    q1.run
  }

  scenario("testGroup3") {
    case class Tab(col1: String, col2: String, col3: String, col4: Int)

    class Tabs(tag: Tag) extends Table[Tab](tag, "TAB_group3") {
      def col1 = column[String]("COL1")
      def col2 = column[String]("COL2")
      def col3 = column[String]("COL3")
      def col4 = column[Int]("COL4")

      def * = (col1, col2, col3, col4) <> (Tab.tupled, Tab.unapply)
    }
    val Tabs = TableQuery[Tabs]

    Tabs.ddl.create
    Tabs ++= Seq(
      Tab("foo", "bar",  "bat", 1),
      Tab("foo", "bar",  "bat", 2),
      Tab("foo", "quux", "bat", 3),
      Tab("baz", "quux", "bat", 4)
    )

    val q1 = Tabs.groupBy(t => (t.col1, t.col2, t.col3)).map {
      case (grp, t) => (grp._1, grp._2, t.map(_.col4).sum)
    }
    q1.run.toSet shouldEqual Set(("baz","quux",Some(4)), ("foo","quux",Some(3)), ("foo","bar",Some(3)))

    val q2 = Tabs.groupBy(t => ((t.col1, t.col2), t.col3)).map {
      case (grp, t) => (grp._1._1, grp._1._2, t.map(_.col4).sum)
    }
    q2.run.toSet shouldEqual Set(("baz","quux",Some(4)), ("foo","quux",Some(3)), ("foo","bar",Some(3)))
  }

  scenario("testMultiMapAggregates") {
    class B(tag: Tag) extends Table[(Long, String, String)](tag, "b_multimap") {
      def id = column[Long]("id", O.PrimaryKey)
      def b = column[String]("b")
      def d = column[String]("d")

      def * = (id, b, d)
    }
    val bs = TableQuery[B]
    class A(tag: Tag) extends Table[(Long, String, Long, Long)](tag, "a_multimap") {
      def id = column[Long]("id", O.PrimaryKey)
      def a = column[String]("a")
      def c = column[Long]("c")
      def fkId = column[Long]("fkId")
      def * = (id, a, c, fkId)
    }
    val as = TableQuery[A]
    (as.ddl ++ bs.ddl).create

    val q1 = as.groupBy(_.id).map(_._2.map(x => x).map(x => x.a).min)
    assert(q1.run.toList.isEmpty)

    val q2 =
      (as leftJoin bs on (_.id === _.id)).map { case (c, s) =>
        val name = s.b
        (c, s, name)
      }.groupBy { prop =>
        val c = prop._1
        val s = prop._2
        val name = prop._3
        s.id
      }.map { prop =>
        val supId = prop._1
        val c = prop._2.map(x => x._1)
        val s = prop._2.map(x => x._2)
        val name = prop._2.map(x => x._3)
        (name.min, s.map(_.b).min, supId, c.length)
      }
    assert(q2.run.isEmpty)

    val q4 = as.flatMap { t1 =>
      bs.withFilter { t2 =>
        t1.fkId === t2.id && t2.d === ""
      }.map(t2 => (t1, t2))
    }.groupBy { prop =>
      val t1 = prop._1
      val t2 = prop._2
      (t1.a, t2.b)
    }.map { prop =>
      val a = prop._1._1
      val b = prop._1._2
      val t1 = prop._2.map(_._1)
      val t2 = prop._2.map(_._2)
      val c3 = t1.map(_.c).max
      scala.Tuple3(a, b, c3)
    }
    assert(q4.run.isEmpty)
  }
}
