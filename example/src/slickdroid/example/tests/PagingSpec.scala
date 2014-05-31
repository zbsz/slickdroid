package slickdroid.example.tests

import scala.slick.android.SlickDroidDriver.simple._

/**
  */
class PagingSpec extends AndroidBackendSpec {

  class IDs(tag: Tag, name: String) extends Table[Int](tag, name) {
    def id = column[Int]("id", O.PrimaryKey)
    def * = id
  }

  scenario("testRawPagination") {
    lazy val ids = TableQuery(new IDs(_, "ids_raw"))
    ids.ddl.create
    ids ++= (1 to 10)

    val q1 = ids.sortBy(_.id)
    q1.run shouldEqual (1 to 10).toList

    val q2 = q1 take 5
    q2.run shouldEqual (1 to 5).toList

//    ifCap(rcap.pagingDrop) {
    val q3 = q1 drop 5
    q3.run shouldEqual (6 to 10).toList

    val q4 = q1 drop 5 take 3
    q4.run shouldEqual (6 to 8).toList

    val q5 = q1 take 5 drop 3
    q5.run shouldEqual (4 to 5).toList
//    }

    val q6 = q1 take 0
    q6.run shouldEqual List()
  }

  scenario("testCompiledPagination") {
    lazy val ids = TableQuery(new IDs(_, "ids_compiled"))
    ids.ddl.create
    ids ++= (1 to 10)
    val q = Compiled { (offset: ConstColumn[Long], fetch: ConstColumn[Long]) =>
      ids.sortBy(_.id).drop(offset).take(fetch)
    }
    q(0, 5).run shouldEqual (1 to 5).toList
//    ifCap(rcap.pagingDrop) {
    q(5, 1000).run shouldEqual (6 to 10).toList
    q(5, 3).run shouldEqual (6 to 8).toList
//    }
    q(0, 0).run shouldEqual List()
  }
}
