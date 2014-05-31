package slickdroid.example.tests

import scala.slick.android.AndroidDriver.simple._

/**
  */
class PrimaryKeySpec extends AndroidBackendSpec {

  scenario("testPrimaryKey") {

    class A(tag: Tag) extends Table[(Int, Int, String)](tag, "a") {
      def k1 = column[Int]("k1")
      def k2 = column[Int]("k2")
      def s = column[String]("s")
      def * = (k1, k2, s)
      def pk = primaryKey("pk_a", (k1, k2))
    }
    val as = TableQuery[A]

    as.baseTableRow.primaryKeys.foreach(println)
    as.baseTableRow.primaryKeys.map(_.name).toSet shouldEqual Set("pk_a")

    as.ddl.create

    as ++= Seq(
      (1, 1, "a11"),
      (1, 2, "a12"),
      (2, 1, "a21"),
      (2, 2, "a22")
    )

    intercept[Exception] {
      as += (1, 1, "a11-conflict")
    }
  }
}

