package com.slicktroid.tests

import scala.slick.android.SlickDroidDriver.simple._
import scala.slick.util.iter._
import scala.slick.util.iter.Done
import scala.slick.util.iter.El

/**
  */
class IterateeSpec extends SlickDroidSpec {

  class A(tag: Tag) extends Table[(String, Int)](tag, "a") {
    def s = column[String]("s", O.PrimaryKey)
    def i = column[Int]("i")
    def * = (s, i)
  }
  lazy val as = TableQuery[A]

  scenario("testIteratee") {
    as.ddl.create
    as.insertAll(("a", 1), ("b", 2), ("c", 3), ("d", 4))

    val q1 = as.sortBy(_.s)

    /* Sum i values until > 5 with foldLeft().
     * There is no way to stop early when the limit has been reached */
    var seen1 = ""
    val r1 = q1.foldLeft(0) { case (z, (s, i)) =>
      seen1 += s
      if(z > 5) z else z + i
    }
    r1 shouldEqual 6
    seen1 shouldEqual "abcd"

    /* Do the same with enumerate() and terminate when done */
    var seen2 = ""
    def step(z: Int): Input[(String, Int)] => IterV[(String, Int), Int] = {
      case El((s, i)) =>
        seen2 += s
        if(z+i > 5) Done(z+i, Empty) else Cont(step(z+i))
      case Empty =>
        seen2 += "_"
        Cont(step(z))
      case EOF =>
        seen2 += "."
        Done(z, EOF)
    }
    val r2 = q1.enumerate(Cont(step(0))).run
    r2 shouldEqual 6
    seen2 shouldEqual "abc"

    /* Using a fold on the Input and some syntactic sugar */
    def step2(z: Int): Cont.K[(String, Int), Int] = _.fold({ case (s, i) =>
      if(z+i > 5) Done(z+i) else Cont(step2(z+i))
    }, Cont(step2(z)), Done(z, EOF))
    val r3 = q1.enumerate(Cont(step2(0))).run
    r3 shouldEqual 6
  }
}