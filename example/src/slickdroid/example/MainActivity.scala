package slickdroid.example

import android.app.ListActivity
import android.os.Bundle


/**
  */
class MainActivity extends ListActivity {

  /**
   * Called when the activity is first created.
   */
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)

    val adapter = new TestResultsAdapter()
    setListAdapter(adapter)

    TestRunner.runAll(getApplicationContext, adapter)
  }
}
