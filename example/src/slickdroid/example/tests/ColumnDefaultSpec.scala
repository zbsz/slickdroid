package slickdroid.example.tests

import scala.slick.android.SlickDroidDriver.simple._
/**
  */
class ColumnDefaultSpec extends AndroidBackendSpec {

  class A(tag: Tag) extends Table[(Int, String, Option[Boolean])](tag, "a") {
    def id = column[Int]("id")
    def a = column[String]("a", O Default "foo")
    def b = column[Option[Boolean]]("b", O Default Some(true))
    def * = (id, a, b)
  }
  lazy val as = TableQuery[A]

  scenario("test") {
    as.ddl.create
    as.map(_.id) += 42
    as.run shouldEqual List((42, "foo", Some(true)))
  }
}
