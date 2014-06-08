package com.slicktroid.tests

import scala.slick.android.SlickDroidDriver.simple._
import java.sql.{Timestamp, Time, Date}
import scala.slick.lifted.ColumnBase


/**
  */
class ScalarFunctionsSpec extends SlickDroidSpec {
  scenario("testScalarFunc") {
    def check[T](q: ColumnBase[T], exp: T) = q.run shouldEqual exp
    def checkLit[T : ColumnType](v: T) = check(LiteralColumn(v), v)

    checkLit(Date.valueOf("2011-07-15"))
    checkLit(Time.valueOf("15:53:21"))
    checkLit(Timestamp.valueOf("2011-07-15 15:53:21"))

    val myExpr = SimpleExpression.binary[Int, Int, Int] { (l, r, qb) =>
      qb.sqlBuilder += '('
      qb.expr(l)
      qb.sqlBuilder += '+'
      qb.expr(r)
      qb.sqlBuilder += "+1)"
    }
    check(myExpr(4, 5), 10)
  }
}
