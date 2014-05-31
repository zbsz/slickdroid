package slickdroid.example.tests

import scala.slick.android.SlickDroidDriver.simple._

/**
  */
class NullabilitySpec extends AndroidBackendSpec {

  scenario("testNullability") {
    class T1(tag: Tag) extends Table[String](tag, "t1") {
      def a = column[String]("a")
      def * = a
    }
    val t1 = TableQuery[T1]

    class T2(tag: Tag) extends Table[String](tag, "t2") {
      def a = column[String]("a", O.Nullable)
      def * = a
    }
    val t2 = TableQuery[T2]

    class T3(tag: Tag) extends Table[Option[String]](tag, "t3") {
      def a = column[Option[String]]("a")
      def * = a
    }
    val t3 = TableQuery[T3]

    class T4(tag: Tag) extends Table[Option[String]](tag, "t4") {
      def a = column[Option[String]]("a", O.NotNull)
      def * = a
    }
    val t4 = TableQuery[T4]

    (t1.ddl ++ t2.ddl ++ t3.ddl ++ t4.ddl).create

    t1.insert("a")
    t2.insert("a")
    t3.insert(Some("a"))
    t4.insert(Some("a"))

    t2.insert(null.asInstanceOf[String])
    t3.insert(None)

    intercept[Exception] { t1.insert(null.asInstanceOf[String]) }
    intercept[Exception] { t4.insert(None) }
  }
}
