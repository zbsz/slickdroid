package com.slicktroid.tests

import scala.slick.android.SlickDroidDriver.simple._
import scala.Some

/**
  */
class TransactionSpec extends SlickDroidSpec {
  
  scenario("testTransaction") {

    class T(tag: Tag) extends Table[Int](tag, "t") {
      def a = column[Int]("a")
      def * = a
    }
    val ts = TableQuery[T]

    ts.ddl.create

    session withTransaction {
      ts.insert(42)
      ts.firstOption shouldEqual Some(42)
      session.rollback()
    }
    ts.firstOption shouldEqual None

    ts.insert(1)
    session withTransaction {
      ts.delete
      ts.firstOption shouldEqual None
      session.rollback()
    }
    ts.firstOption shouldEqual Some(1)
  }
}
