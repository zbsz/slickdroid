package slickdroid.example

import android.app.ListActivity
import android.os.Bundle
import android.widget.ListView
import android.view.View
import android.util.Log

/**
  */
class MainActivity extends ListActivity {

  val adapter = new TestResultsAdapter()

  /**
    * Called when the activity is first created.
   */
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    setListAdapter(adapter)

    TestRunner.runAll(getApplicationContext, adapter)
  }

  override def onListItemClick(l: ListView, v: View, position: Int, id: Long): Unit = adapter.getItem(position) match {
    case Suite(_, _, _, Some(runner)) => runner.start()
    case Test(name, Suite(_, _, _, Some(runner)), _) => runner.start(Some(name))
    case item => Log.w("MainActivity", s"unexpected item: $item")
  }
}
