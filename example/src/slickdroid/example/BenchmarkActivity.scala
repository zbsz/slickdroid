package slickdroid.example

import android.app.Activity
import android.os.Bundle
import scala.slick.android.SlickDroidDriver.simple._
import android.util.Log

/**
  */
class BenchmarkActivity extends Activity {
  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)

    new Thread() {
      override def run(): Unit = {
        Benchmark.main(Array())

        runOnUiThread(new Runnable {
          override def run(): Unit = finish()
        })
      }
    } .start()
  }
}


object Benchmark {

  val COUNT = 20

  def main(args: Array[String]) {
    for(i <- 0 to COUNT) test1(i == 0)
    val t0 = System.nanoTime()
    for(i <- 0 to COUNT) test1(false)
    val t1 = System.nanoTime()
    val total = (t1-t0)/1000000.0
    Log.d("Benchmark", COUNT+" runs tooks "+total+" ms ("+(total*1000.0/COUNT)+" µs per run)")
  }

  class Users(tag: Tag) extends Table[(Int, String, String)](tag, "users") {
    def id = column[Int]("id")
    def first = column[String]("first")
    def last = column[String]("last")
    def * = (id, first, last)
  }
  val users = TableQuery[Users]

  class Orders(tag: Tag) extends Table[(Int, Int)](tag, "orders") {
    def userID = column[Int]("userID")
    def orderID = column[Int]("orderID")
    def * = (userID, orderID)
  }
  val orders = TableQuery[Orders]

  def test1(print: Boolean) {
    val q1 = for(u <- users) yield u
    val q2 = for {
      u <- users
      o <- orders filter { o => u.id === o.userID }
    } yield (u.first, u.last, o.orderID)
    val q3 = for(u <- users filter(_.id === 42)) yield (u.first, u.last)
    val q4 =
      (users innerJoin orders on (_.id === _.userID)).sortBy(_._1.last.asc).map(uo => (uo._1.first, uo._2.orderID))
    val q5 = for (
      o <- orders
        filter { o => o.orderID === (for { o2 <- orders filter(o.userID === _.userID) } yield o2.orderID).max }
    ) yield o.orderID

    val s1 = q1.selectStatement
    val s2 = q2.selectStatement
    val s3 = q3.selectStatement
    val s4 = q4.selectStatement
    val s5 = q5.selectStatement

    if(print) {
      Log.d("Benchmark", "q1: " + s1)
      Log.d("Benchmark", "q2: " + s2)
      Log.d("Benchmark", "q3: " + s3)
      Log.d("Benchmark", "q4: " + s4)
      Log.d("Benchmark", "q5: " + s5)
    }
  }
}
