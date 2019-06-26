package malayalamdictionary.samasya.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import malayalamdictionary.samasya.MainActivity
import malayalamdictionary.samasya.R
import malayalamdictionary.samasya.helper.Common
import java.util.ArrayList

class ListItemAdapter(context: Context,items: ArrayList<String>, mainActivity: MainActivity): BaseAdapter(), Filterable{

    private val context=context
    private var items=items
    lateinit var textViewAutoComplete: TextView
    private var filteredItems: List<String> = items
    private var mainActivity: MainActivity =mainActivity
    private val mFilter = ItemFilter()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        var mConvertView=convertView
        if (mConvertView == null) {
            val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            mConvertView = layoutInflater.inflate(R.layout.autocompletion_list, parent, false)
        }
        textViewAutoComplete = mConvertView!!.findViewById(R.id.textView_autocomplete)

        val type = Typeface.createFromAsset(context.getAssets(), "fonts/mal.ttf")
        if (Common.englishToMayalayam) {
            textViewAutoComplete.typeface = Typeface.DEFAULT
        } else {
            textViewAutoComplete.typeface = type
        }
        val location = filteredItems[position]
        if (location.isNotEmpty()) {
            textViewAutoComplete.text = location
        }

        textViewAutoComplete.setOnClickListener { mainActivity.fillData(filteredItems[position]) }
        return mConvertView
    }

    override fun getItem(position: Int): Any {
        return filteredItems[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return filteredItems.size
    }

    private inner class ItemFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): Filter.FilterResults {
            val filterString = constraint.toString().toLowerCase()
            val results = Filter.FilterResults()

            val count = items.size
            val tempItems = ArrayList<String>(count)

            for (i in 0 until count) {
                if (items[i].toLowerCase().contains(filterString)) {
                    tempItems.add(items[i])
                }
            }

            results.values = tempItems
            results.count = tempItems.size

            return results
        }

        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
            filteredItems = results.values as ArrayList<String>
            notifyDataSetChanged()
        }
    }
    override fun getFilter(): Filter {
        return mFilter
    }

}
