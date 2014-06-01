package slickdroid.example.tests

import scala.collection.mutable.ArrayBuffer
import scala.slick.util.CloseableIterator
import scala.slick.android.SlickDroidDriver.simple._

/**
  */
class InvokerSpec extends AndroidBackendSpec {

  scenario("testCollections") {
    class T(tag: Tag) extends Table[Int](tag, "t") {
      def a = column[Int]("a")
      def * = a
    }
    val ts = TableQuery[T]

    ts.ddl.create
    ts.insertAll(2, 3, 1, 5, 4)

    val q = ts.map(_.a).sorted

    val r1 = q.list
    val r1t: List[Int] = r1
    r1 shouldEqual List(1, 2, 3, 4, 5)

    val r2 = q.buildColl[List]
    val r2t: List[Int] = r2
    r2 shouldEqual List(1, 2, 3, 4, 5)

    val r3 = q.buildColl[Set]
    val r3t: Set[Int] = r3
    r3 shouldEqual Set(3, 4, 2, 1, 5)

    val r4 = q.buildColl[IndexedSeq]
    val r4t: IndexedSeq[Int] = r4
    r4 shouldEqual IndexedSeq(1, 2, 3, 4, 5)

    val r5 = q.buildColl[ArrayBuffer]
    val r5t: ArrayBuffer[Int] = r5
    r5 shouldEqual ArrayBuffer(1, 2, 3, 4, 5)

    val r6 = q.buildColl[Array]
    val r6t: Array[Int] = r6
    r6.toList shouldEqual Array(1, 2, 3, 4, 5).toList

    val it = q.iterator
    val sum = try {
      it.reduceLeft(_ + _)
    } finally it.close()
    sum shouldEqual 15
  }

  scenario("testMap") {
    class T(tag: Tag) extends Table[(Int, String)](tag, "t2") {
      def k = column[Int]("k")
      def v = column[String]("v")
      def * = (k, v)
    }
    val ts = TableQuery[T]

    ts.ddl.create
    ts.insertAll(2 -> "b", 3 -> "c", 1 -> "a")

    val r1 = ts.toMap
    val r1t: Map[Int, String] = r1
    r1 shouldEqual Map(1 -> "a", 2 -> "b", 3 -> "c")
  }

  scenario("testLazy") { // if shared
    class T(tag: Tag) extends Table[Int](tag, "t3") {
      def a = column[Int]("a")
      def * = a
    }
    val ts = TableQuery[T]

    val q = ts.sortBy(_.a)

    def setUp(session: Session) {
      ts.ddl.create(session)
      for(g <- 1 to 1000 grouped 100)
        ts.insertAll(g:_*)(session)
    }

    def f() = CloseableIterator close db.createSession after { session =>
      setUp(session)
      q.iterator(session)
    }

    def g() = CloseableIterator close db.createSession after { session =>
      setUp(session)
      throw new Exception("make sure it gets closed")
    }

    val it = f()
    it.use { it.toStream.toList shouldEqual (1 to 1000).toList }
    intercept[Exception](g())
    db.withSession(ts.ddl.drop(_))
    val it2 = f()
    it2.use { it2.toStream.toList shouldEqual (1 to 1000).toList }
  }
}
