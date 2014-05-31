package slickdroid.example.tests

import scala.slick.android.{StaticQuery => Q}
import Q.interpolation
import scala.slick.android.GetResult
import scala.slick.android.AndroidDriver.simple._

class PlainSQLSpec extends AndroidBackendSpec {

  case class User(id:Int, name:String)

  implicit val getUserResult = GetResult(r => new User(r.<<, r.<<))

  scenario("testPlainSQL") {
    def getUsers(id: Option[Int]) = {
      val q = Q[User] + "select id, name from USERS "
      id map { q + "where id =" +? _ } getOrElse q
    }

    def InsertUser(id: Int, name: String) = Q.u + "insert into USERS values (" +? id + "," +? name + ")"

    val createTable = Q[Int] + "create table USERS(ID int not null primary key, NAME varchar(255))"
    val populateUsers = List(InsertUser(1, "szeiger"), InsertUser(0, "admin"), InsertUser(2, "guest"), InsertUser(3, "foo"))

    val allIDs = Q[Int] + "select id from USERS"
    val userForID = Q[Int, User] + "select id, name from USERS where id = ?"
    val userForIdAndName = Q[(Int, String), User] + "select id, name from USERS where id = ? and name = ?"

    session.withTransaction {
      println("Creating user table: "+createTable.first)
      println("Inserting users:")
      for(i <- populateUsers) println("  "+i.first)
    }

    println("All IDs:")
    for(s <- allIDs.list) println("  "+s)
    allIDs.list.toSet shouldEqual Set(1,0,2,3)

    println("All IDs with foreach:")
    var s1 = Set[Int]()
    allIDs foreach { s =>
      println("  "+s)
      s1 += s
    }
    s1 shouldEqual Set(1,0,2,3)

    val res = userForID(2).first
    println("User for ID 2: "+res)
    res shouldEqual User(2,"guest")

    userForIdAndName(2, "guest").first shouldEqual User(2,"guest")
    userForIdAndName(2, "foo").firstOption shouldEqual None

    println("User 2 with foreach:")
    var s2 = Set[User]()
    userForID(2) foreach { s =>
      println("  "+s)
      s2 += s
    }
    s2 shouldEqual Set(User(2,"guest"))

    println("User 2 with foreach:")
    var s3 = Set[User]()
    getUsers(Some(2)) foreach { s =>
      println("  "+s)
      s3 += s
    }
    s3 shouldEqual Set(User(2,"guest"))

    println("All users with foreach:")
    var s4 = Set[User]()
    getUsers(None) foreach { s =>
      println("  "+s)
      s4 += s
    }
    s4 shouldEqual Set(User(1,"szeiger"), User(2,"guest"), User(0,"admin"), User(3,"foo"))

    println("All users with iterator.foreach:")
    var s5 = Set[User]()
    for(s <- getUsers(None).iterator) {
      println("  "+s)
      s5 += s
    }
    s5 shouldEqual Set(User(1,"szeiger"), User(2,"guest"), User(0,"admin"), User(3,"foo"))

    assertUnquotedTablesExist("USERS")
  }

  scenario("testInterpolation") {
    def userForID(id: Int) = sql"select id, name from USERS where id = $id".as[User]
    def userForIdAndName(id: Int, name: String) = sql"select id, name from USERS where id = $id and name = $name".as[User]

    sqlu"create table USERS(ID int not null primary key, NAME varchar(255))".execute
    val total = (for {
      (id, name) <- List((1, "szeiger"), (0, "admin"), (2, "guest"), (3, "foo"))
    } yield sqlu"insert into USERS values ($id, $name)".first).sum
    total shouldEqual 4

    sql"select id from USERS".as[Int].buildColl[Set] shouldEqual Set(0,1,2,3)

    val res = userForID(2).first
    println("User for ID 2: "+res)
    res shouldEqual User(2,"guest")

    val s1 = sql"select id from USERS where name = ${"szeiger"}".as[Int]
    val s2 = sql"select id from USERS where name = '#${"guest"}'".as[Int]
    s1.getStatement shouldEqual "select id from USERS where name = ?"
    s2.getStatement shouldEqual "select id from USERS where name = 'guest'"
    s1.list shouldEqual List(1)
    s2.list shouldEqual List(2)

    userForIdAndName(2, "guest").first shouldEqual User(2,"guest")
    userForIdAndName(2, "foo").firstOption shouldEqual None
  }

  def assertUnquotedTablesExist(tables: String*) {
    for(t <- tables) {
      try ((Q[Int]+"select 1 from "+t+" where 1 < 0").list) catch { case _: Exception =>
        fail("Table "+t+" should exist")
      }
    }
  }

  def assertNotUnquotedTablesExist(tables: String*) {
    for(t <- tables) {
      try {
        (Q[Int]+"select 1 from "+t+" where 1 < 0").list
        fail("Table "+t+" should not exist")
      } catch { case _: Exception => }
    }
  }

  scenario("testWideResult") {
    val q = sql"select 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23"
    val r1 = q.as[((Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int),
      (Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int))].first
    r1 shouldEqual ((1,2,3,4,5,6,7,8,9,10,11,12),(13,14,15,16,17,18,19,20,21,22,23))

    class Foo(val v1: Int, val v2: Int, val v3: Int, val v4: Int,
              val v5: Int, val v6: Int, val v7: Int, val v8: Int,
              val v9: Int, val v10: Int, val v11: Int, val v12: Int,
              val v13: Int, val v14: Int, val v15: Int, val v16: Int,
              val v17: Int, val v18: Int, val v19: Int, val v20: Int,
              val v21: Int, val v22: Int, val v23: Int) {
      override def toString =
        s"Foo($v1, $v2, $v3, $v4, $v5, $v6, $v7, $v8, $v9, $v10, $v11, $v12, "+
          s"$v13, $v14, $v15, $v16, $v17, $v18, $v19, $v20, $v21, $v22, $v23)"
      override def equals(o: Any) = o match {
        case f: Foo =>
          f.v1 == v1 && f.v2 == v2 && f.v3 == v3 && f.v4 == v4 &&
            f.v5 == v5 && f.v6 == v6 && f.v7 == v7 && f.v8 == v8 &&
            f.v9 == v9 && f.v10 == v10 && f.v11 == v11 && f.v12 == v12 &&
            f.v13 == v13 && f.v14 == v14 && f.v15 == v15 && f.v16 == v16 &&
            f.v17 == v17 && f.v18 == v18 && f.v19 == v19 && f.v20 == v20 &&
            f.v21 == v21 && f.v22 == v22 && f.v23 == v23
        case _ => false
      }
    }
    implicit val getFooResult = GetResult(r => new Foo(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))
    val r2 = q.as[Foo].first
    r2 shouldEqual new Foo(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23)
  }
}
