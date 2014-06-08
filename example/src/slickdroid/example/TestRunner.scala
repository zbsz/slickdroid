package slickdroid.example

import android.content.Context
import org.scalatest.Reporter
import scala.util.control.NonFatal
import org.scalatest.events._
import android.util.Log
import android.os.{Handler, Looper}
import org.scalatest.events.TestStarting
import org.scalatest.events.TestCanceled
import org.scalatest.Args
import org.scalatest.events.TestFailed
import android.graphics.Color
import java.util.concurrent.Executors
import slickdroid.example.tests._

/**
  */
object TestRunner {

  val Executor = Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors())
  
  val StateUnknown = Color.GRAY
  val StateError = Color.MAGENTA
  val StateFailed = Color.RED
  val StatePassed = Color.GREEN

  val UiHandler = new Handler(Looper.getMainLooper)

  var context: Context = _

  def runAll(context: Context, adapter: TestResultsAdapter): Unit = {
    this.context = context

    val specs = List(
      new SampleSpec(),
      new MainSpec(),
      new InvokerSpec(),
      new ExecutorSpec(),
      new PrimaryKeySpec(),
      new ColumnDefaultSpec(),
      new InsertSpec(),
      new CountSpec(),
      new ForeignKeySpec(),
      new JoinSpec(),
      new MapperSpec(),
      new TransactionSpec(),
      new UnionSpec(),
      new AggregateSpec(),
      new ScalarFunctionsSpec(),
      new RelationalTypeSpec(),
      new NestingSpec(),
      new PagingSpec(),
      new IterateeSpec(),
      new TemplateSpec(),
      new NewQuerySemanticsSpec(),
      new NullabilitySpec(),
      new PlainSQLSpec()
    )

    val suites = specs.map(spec => Suite(spec.suiteName))

    adapter.setSuites(suites)

    specs.zip(suites) foreach {
      case (spec, suite) =>
        new SuiteRunner(suite, spec, adapter)
    }
  }
}

import TestRunner._

case class Test(name: String, suite: Suite, var state: Int = StateUnknown) {
  override def toString: String = s"Test($name, ${suite.name}, $state)"
}

case class Suite(name: String, var state: Int = StateUnknown, var tests: List[Test] = Nil, var runner: Option[SuiteRunner] = None) {
  override def toString: String = s"Suite($name, $state, ${tests.map(_.name)}"
}

class SuiteRunner(suite: Suite, spec: AndroidBackendSpec, adapter: TestResultsAdapter) extends Reporter {

  Log.d("SuiteRunner", s"init")

  suite.runner = Some(this)

  var current: Test = _
  var failed = false
  var running = false

  start()

  def start(testName: Option[String] = None): Unit = {
    Log.d("SuiteRunner", s"start $suite,  $testName")

    if (!running) {
      running = true
      failed = false
      ui {
        suite.tests = Nil
        suite.state = StateUnknown
        adapter.notifyDataSetChanged()
      }
      Executor.submit(new Runnable() {
        override def run(): Unit = {
          try {
            spec.run(testName, new Args(SuiteRunner.this))

            if (!failed) {
              ui {
                suite.state = StatePassed
                adapter.notifyDataSetChanged()
              }
            }
          } catch {
            case NonFatal(e) =>
              ui { suite.state = StateError; adapter.notifyDataSetChanged() }
              Log.e("SuiteRunner", s"Suite: ${suite.name} aborted", e)
          } finally {
            running = false
          }
        }
      })
    }
  }

  def ui(body: => Unit) = TestRunner.UiHandler.post(new Runnable {
    override def run(): Unit = body
  })

  override def apply(event: Event): Unit = ui {
    Log.d("TestEvent", event.toString)
    event match {
      case e: TestStarting =>
        current = Test(e.testName, suite)
        suite.tests = suite.tests ::: List(current)
      case e: TestCanceled =>
        current.state = StateError
      case e: TestFailed =>
        failed = true
        current.state = StateFailed
        suite.state = StateFailed
        Log.e("TestFailed", e.message, e.throwable.getOrElse(null))
      case e: TestSucceeded =>
        current.state = StatePassed
      case e: SuiteCompleted =>
        if (!failed) suite.state = StatePassed
      case e: SuiteAborted =>
        suite.state = StateError
      case e: RunCompleted =>
        if (!failed) suite.state = StatePassed
      case _ => // ignore
    }
    adapter.notifyDataSetChanged()
  }
}
