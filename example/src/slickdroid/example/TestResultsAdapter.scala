package slickdroid.example

import android.widget.{TextView, BaseAdapter}
import android.view.{ViewGroup, View}
import android.view.View.OnClickListener

/**
  */
class TestResultsAdapter extends BaseAdapter with OnClickListener {

  var suites = List[Suite]()
  var items = List[AnyRef]()

  def setSuites(suites: List[Suite]) = this.suites = suites


  override def notifyDataSetChanged(): Unit = {
    items = suites.map { suite => suite :: suite.tests }.flatten.toList
    super.notifyDataSetChanged()
  }

  override def getCount: Int = items.length

  override def getItemId(position: Int): Long = position

  override def getItem(position: Int): AnyRef = items(position)

  override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
    val item = items(position)
    val view = if (convertView == null) new TextView(parent.getContext) else convertView.asInstanceOf[TextView]
    view.setPadding(8, 8, 8, 8)
    view.setTag(item)
    item match {
      case Suite(name, state, _, _) =>
        view.setTextSize(22f)
        view.setTextColor(state)
        view.setText(name)
      case Test(name, _, state) =>
        view.setTextSize(20f)
        view.setTextColor(state)
        view.setText(s"\t  $name")
      case _ => //ignore
    }
    view
  }

  override def onClick(v: View): Unit = v.getTag match {
    case Suite(_, _, _, Some(runner)) => runner.start()
    case Test(name, Suite(_, _, _, Some(runner)), _) => runner.start(Some(name))
    case _ => // ignore
  }
}
