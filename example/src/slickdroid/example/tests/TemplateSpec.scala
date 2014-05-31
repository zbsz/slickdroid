package slickdroid.example.tests

import scala.slick.android.SlickDroidDriver.simple._

/**
  */
class TemplateSpec extends AndroidBackendSpec {

  scenario("testParameters") {
    class Users(tag: Tag) extends Table[(Int, String)](tag, "users") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def first = column[String]("first")
      def * = (id, first)
    }
    lazy val users = TableQuery[Users]

    class Orders(tag: Tag) extends Table[(Int, Int, String)](tag, "orders") {
      def userID = column[Int]("userID")
      def orderID = column[Int]("orderID", O.PrimaryKey, O.AutoInc)
      def product = column[String]("product")
      def * = (userID, orderID, product)
    }
    lazy val orders = TableQuery[Orders]

    (users.ddl ++ orders.ddl).create

    users.map(_.first) ++= Seq("Homer", "Marge", "Apu", "Carl", "Lenny")
    for(uid <- users.map(_.id).run)
      orders.map(o => (o.userID, o.product)) += (uid, if(uid < 4) "Product A" else "Product B")

    def userNameByID1(id: Int) = for(u <- users if u.id === id.bind) yield u.first
    def q1 = userNameByID1(3)
    q1.run shouldEqual List("Apu")

    val userNameByID2 = for {
      id <- Parameters[Int]
      u <- users if u.id === id
    } yield u.first
    val q2 = userNameByID2(3)
    q2.run shouldEqual List("Apu")

    val userNameByIDRange = for {
      (min, max) <- Parameters[(Int, Int)]
      u <- users if u.id >= min && u.id <= max
    } yield u.first
    val q3 = userNameByIDRange(2,5)
    q3.run shouldEqual List("Marge","Apu","Carl","Lenny")

    val userNameByIDRangeAndProduct = for {
      (min, (max, product)) <- Parameters[(Int, (Int, String))]
      u <- users if u.id >= min && u.id <= max && orders.filter(o => (u.id === o.userID) && (o.product === product)).exists
    } yield u.first
    val q4 = userNameByIDRangeAndProduct(2,(5,"Product A"))
    q4.run shouldEqual List("Marge","Apu")

    def userNameByIDOrAll(id: Option[Int]) = for(
      u <- users if id.map(u.id === _.bind).getOrElse(LiteralColumn(true))
    ) yield u.first
    val q5a = userNameByIDOrAll(Some(3))
    q5a.run shouldEqual List("Apu")
    val q5b = userNameByIDOrAll(None)
    q5b.run shouldEqual List("Homer","Marge","Apu","Carl","Lenny")
  }

  scenario("testCompiled") {
    class T(tag: Tag) extends Table[(Int, String)](tag, "t_lifted") {
      def id = column[Int]("id", O.PrimaryKey)
      def s = column[String]("s")
      def * = (id, s)
    }
    def ts = TableQuery[T]
    ts.ddl.create
    Compiled(ts.map(identity)) += (1, "a")
    Compiled(ts) ++= Seq((2, "b"), (3, "c"))

    val byIdAndS = { (id: Column[Int], s: ConstColumn[String]) => ts.filter(t => t.id === id && t.s === s) }
    val byIdAndSC = Compiled(byIdAndS)
    val byIdAndFixedSC = byIdAndSC.map(f => f((_: Column[Int]), "b"))
    val byIdC = Compiled { id: Column[Int] => ts.filter(_.id === id) }
    val byId = byIdC.extract
    val byIdC3 = byIdC(3)
    val byId3 = byIdC3.extract
    val countBelow = { (id: Column[Int]) => ts.filter(_.id < id).length }
    val countBelowC = Compiled(countBelow)

    val r0 = byIdAndS(1, "a").run
    r0.toSet shouldEqual Set((1, "a"))

    val r1 = byIdAndSC(1, "a").run
    val r1t: Seq[(Int, String)] = r1
    r1.toSet shouldEqual Set((1, "a"))

    val r2 = byIdAndFixedSC(2).run
    r2.toSet shouldEqual Set((2, "b"))

    val r3a = byIdC3.run
    val r3b = byId3.run
    r3a shouldEqual Seq((3, "c"))
    r3b shouldEqual Seq((3, "c"))

    val r4 = countBelow(3).run
    val r4t: Int = r4
    r4 shouldEqual 2
    val r5 = countBelowC(3).run
    val r5t: Int = r5
    r5 shouldEqual 2

    val joinC = Compiled { id: Column[Int] => ts.filter(_.id === id).innerJoin(ts.filter(_.id === id)) }
    joinC(1).run shouldEqual Seq(((1, "a"), (1, "a")))
  }
}
