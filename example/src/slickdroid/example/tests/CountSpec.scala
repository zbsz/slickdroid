package slickdroid.example.tests

import scala.slick.android.AndroidDriver.simple._
/**
  */
class CountSpec extends AndroidBackendSpec {

  scenario("testCount") {
    class TestTable(tag: Tag) extends Table[Int](tag, "TEST") {
      def id = column[Int]("ID")
      def * = id
    }
    val testTable = TableQuery(new TestTable(_))

    testTable.ddl.create
    testTable ++= Seq(1, 2, 3, 4, 5)

    val q1 = Query(testTable.length)
    q1.run shouldEqual Vector(5)

    val q2 = testTable.length
    q2.run shouldEqual 5

    val q3 = testTable.filter(_.id < 3).length
    q3.run shouldEqual 2

    val q4 = testTable.take(2).length
    q4.run shouldEqual 2
  }

  scenario("testJoin") {
    class Categories(tag: Tag) extends Table[(Int, String)](tag, "cat_j") {
      def id = column[Int]("id")
      def name = column[String]("name")
      def * = (id, name)
    }
    val categories = TableQuery[Categories]

    class Posts(tag: Tag) extends Table[(Int, String, Int)](tag, "posts_j") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def title = column[String]("title")
      def category = column[Int]("category")
      def * = (id, title, category)
    }
    val posts = TableQuery[Posts]

    (categories.ddl ++ posts.ddl).create
    categories ++= Seq((1, "Scala"), (2, "JVM"), (3, "Java"), (4, "Erlang"), (5, "Haskell"))
    posts ++= Seq((1, "Shiny features", 1), (2, "HotSpot", 2))

    val joinedQuery = for {
      c <- categories
      p <- posts
      if p.category === c.id
    } yield (c, p)

    val q1 = joinedQuery.length
    q1.run shouldEqual 2

    val q2 = Query(joinedQuery.length)
    q2.run shouldEqual Vector(2)
  }

  scenario("testJoinCount") {
    class A(tag: Tag) extends Table[Long](tag, "a_j") {
      def id = column[Long]("id", O.PrimaryKey)
      def * = id
    }
    lazy val as = TableQuery[A]
    class B(tag: Tag) extends Table[(Long, String)](tag, "b_j") {
      def aId = column[Long]("a_id")
      def data = column[String]("data")
      def * = (aId, data)
    }
    lazy val bs = TableQuery[B]
    (as.ddl ++ bs.ddl).create
    as ++= Seq(1L, 2L)
    bs ++= Seq((1L, "1a"), (1L, "1b"), (2L, "2"))
    val qDirectLength = for {
      a <- as if a.id === 1L
    } yield (a, (for {
        b <- bs if b.aId === a.id
      } yield b).length)
    qDirectLength.run shouldEqual Seq((1L, 2))
    val qJoinLength = for {
      a <- as if a.id === 1L
      l <- Query((for {
        b <- bs if b.aId === a.id
      } yield b).length)
    } yield (a, l)
    qJoinLength.run shouldEqual Seq((1L, 2))
  }
}
