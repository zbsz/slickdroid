package slickdroid.example

import android.widget.{TextView, BaseAdapter}
import android.view.{ViewGroup, View}

/**
  */
class TestResultsAdapter extends BaseAdapter {

  var suites = List[Suite]()
  var items = List[(String, Int, Int)]()

  def setSuites(suites: List[Suite]) = this.suites = suites


  override def notifyDataSetChanged(): Unit = {
    items = suites.map { suite =>
      (suite.name, 0, suite.state) :: suite.tests.map(t => (t.name, 1, t.state))
    }.flatten.toList
    super.notifyDataSetChanged()
  }

  override def getCount: Int = items.length

  override def getItemId(position: Int): Long = position

  override def getItem(position: Int): AnyRef = items(position)

  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    val item = items(position)
    val view = if (convertView == null) new TextView(parent.getContext) else convertView.asInstanceOf[TextView]
    view.setTextSize(if (item._2 == 0) 22f else 20f)
    view.setTextColor(item._3)
    view.setText(if (item._2 == 0) item._1 else s"\t ${item._1}")
    view
  }
}
