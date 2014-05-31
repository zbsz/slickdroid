package slickdroid.example.tests

import scala.slick.android.SlickDroidDriver.simple._

/**
  */
class InsertSpec extends AndroidBackendSpec {

  scenario("simple insert") {

    class TestTable(tag: Tag, tname: String) extends Table[(Int, String)](tag, tname) {
      def id = column[Int]("id")
      def name = column[String]("name")
      def * = (id, name)
      def ins = (id, name)
    }

    val src1 = TableQuery(new TestTable(_, "src1_q"))
    val dst1 = TableQuery(new TestTable(_, "dst1_q"))
    val dst2 = TableQuery(new TestTable(_, "dst2_q"))
    val dst3 = TableQuery(new TestTable(_, "dst3_q"))

    (src1.ddl ++ dst1.ddl ++ dst2.ddl ++ dst3.ddl).create

    src1.insert(1, "A")
    src1.map(_.ins).insertAll((2, "B"), (3, "C"))

    dst1.insert(src1)
    dst1.list.toSet shouldEqual Set((1,"A"), (2,"B"), (3,"C"))

    val q2 = for(s <- src1 if s.id <= 2) yield s
    println("Insert 2: "+dst2.insertStatementFor(q2))
    dst2.insert(q2)
    dst2.list.toSet shouldEqual Set((1,"A"), (2,"B"))

    val q3 = (42, "X".bind)
    println("Insert 3: "+dst2.insertStatementFor(q3))
    dst2.insertExpr(q3)
    dst2.list.toSet shouldEqual Set((1,"A"), (2,"B"), (42,"X"))

    val q4comp = Compiled { dst2.filter(_.id < 10) }
    val dst3comp = Compiled { dst3 }
    dst3comp.insert(q4comp)
    dst3comp.run.toSet shouldEqual Set((1,"A"), (2,"B"))

    /*val q4 = (43, "Y".bind)
    println("Insert 4: "+Dst2.shaped.insertStatementFor(q4))
    Dst2.shaped.insertExpr(q4)
    Query(Dst2).list.toSet shouldEqual Set((1,"A"), (2,"B"), (42,"X"), (43,"Y"))*/
  }

  scenario("insert returning") {

    class A(tag: Tag) extends Table[(Int, String, String)](tag, "A") {
      def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
      def s1 = column[String]("S1")
      def s2 = column[String]("S2")
      def * = (id, s1, s2)
    }
    val as = TableQuery[A]
    def ins1 = as.map(a => (a.s1, a.s2)) returning as.map(_.id)
    def ins2 = as.map(a => (a.s1, a.s2)) returning as.map(_.id) into ((v, i) => (i, v._1, v._2))

    as.ddl.create

    ins1.insert("a", "b") shouldEqual 1
    ins1.insertAll(("e", "f"), ("g", "h")) shouldEqual Seq(2, 3)
    ins2.insert("i", "j") shouldEqual (4, "i", "j")
  }

  scenario("insert forced") {
    class T(tag: Tag) extends Table[(Int, String)](tag, "t_forced") {
      def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
      def name = column[String]("name")
      def * = (id, name)
      def ins = (id, name)
    }
    val ts = TableQuery[T]

    ts.ddl.create

    ts.insert(101, "A")
    ts.map(_.ins).insertAll((102, "B"), (103, "C"))
    ts.filter(_.id > 100).length.run shouldEqual 0

    ts.forceInsert(104, "A")
    ts.map(_.ins).forceInsertAll((105, "B"), (106, "C"))
    ts.filter(_.id > 100).length.run shouldEqual 3
  }

  scenario("insert or update plain") {
    class T(tag: Tag) extends Table[(Int, String)](tag, "t_merge") {
      def id = column[Int]("id", O.PrimaryKey)
      def name = column[String]("name")
      def * = (id, name)
      def ins = (id, name)
    }
    val ts = TableQuery[T]

    ts.ddl.create

    ts ++= Seq((1, "a"), (2, "b"))
    ts.insertOrUpdate((3, "c")) shouldEqual 1
    ts.insertOrUpdate((1, "d")) shouldEqual 1
    ts.sortBy(_.id).run shouldEqual Seq((1, "d"), (2, "b"), (3, "c"))
  }

  scenario("insert or update auto inc") {
    class T(tag: Tag) extends Table[(Int, String)](tag, "t_merge2") {
      def id = column[Int]("id", O.AutoInc, O.PrimaryKey)
      def name = column[String]("name")
      def * = (id, name)
      def ins = (id, name)
    }
    val ts = TableQuery[T]

    ts.ddl.create

    ts ++= Seq((1, "a"), (2, "b"))
    ts.insertOrUpdate((0, "c")) shouldEqual 1
    ts.insertOrUpdate((1, "d")) shouldEqual 1
    ts.sortBy(_.id).run shouldEqual Seq((1, "d"), (2, "b"), (3, "c"))

    val q = ts returning ts.map(_.id)
    q.insertOrUpdate((0, "e")) shouldEqual Some(4)
    q.insertOrUpdate((1, "f")) shouldEqual None
    ts.sortBy(_.id).run shouldEqual Seq((1, "f"), (2, "b"), (3, "c"), (4, "e"))
  }
}
