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
      new NullabilitySpec()
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

case class Test(name: String, var state: Int = StateUnknown)

case class Suite(name: String, var state: Int = StateUnknown, var tests: List[Test] = Nil)

class SuiteRunner(suite: Suite, spec: AndroidBackendSpec, adapter: TestResultsAdapter) extends Reporter {

  var current: Test = _
  var failed = false

  Executor.submit(new Runnable() {
    override def run(): Unit = {
      try {
        spec.run(None, new Args(SuiteRunner.this))

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
      }
    }
  })

  def ui(body: => Unit) = TestRunner.UiHandler.post(new Runnable {
    override def run(): Unit = body
  })

  override def apply(event: Event): Unit = ui {
    Log.d("Event", event.toString)
    event match {
      case e: TestStarting =>
        current = Test(e.testName)
        suite.tests = suite.tests ::: List(current)
      case e: TestCanceled =>
        current.state = StateError
      case e: TestFailed =>
        failed = true
        current.state = StateFailed
        suite.state = StateFailed
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
