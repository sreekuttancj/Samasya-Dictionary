package malayalamdictionary.samasya.adapter

import android.content.Context
import android.graphics.Typeface
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import android.widget.Toast
import malayalamdictionary.samasya.helper.HistoryItems


class HistoryAdapter(context: Context,listDataHeader:MutableList<HistoryItems>,
                     listChildData: HashMap<String, List<String>>,englishMalayalam : Boolean) : BaseExpandableListAdapter() {

    private val mContext:Context=context
    private val listDataHeader: MutableList<HistoryItems> =listDataHeader
    private val listChildData: HashMap<String, List<String>> = listChildData;
    private var englishMalayalam: Boolean= englishMalayalam
    private var mSelectedItemsIds: SparseBooleanArray = SparseBooleanArray()

    override fun getGroup(groupPosition: Int): Any {
        try {

            return this.listDataHeader[groupPosition]
        } catch (e: Exception) {
            Toast.makeText(mContext, "Please close expanded words before selection", Toast.LENGTH_SHORT).show()
        }
        return true
    }

    fun getGroupItem(groupPosition: Int): Any? {

        try {
            return this.listDataHeader[groupPosition].name
        } catch (e: Exception) {
            Toast.makeText(mContext, "Please close expanded words before selection", Toast.LENGTH_SHORT).show()
        }

        return true
    }


    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val historyItems = getGroup(groupPosition) as HistoryItems
        val headerTitle = historyItems.name
        var mConvertView: View? = convertView
        if (mConvertView == null) {
            val layoutInflater = mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            mConvertView = layoutInflater.inflate(malayalamdictionary.samasya.R.layout.expandable_listview_layout, null)
        }

        val lblListHeader = mConvertView?.findViewById(malayalamdictionary.samasya.R.id.textView_item) as TextView
        val tf = Typeface.createFromAsset(mContext.resources.assets, "fonts/mal.ttf")

        if (englishMalayalam) {
            lblListHeader.setTypeface(Typeface.DEFAULT, Typeface.BOLD)
        } else {
            lblListHeader.setTypeface(tf, Typeface.BOLD)

        }
        lblListHeader.text = headerTitle


        return mConvertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return this.listChildData[this.listDataHeader[groupPosition].name]!!.size
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return this.listChildData[this.listDataHeader[groupPosition].name]!![childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View? {
        val childText = getChild(groupPosition, childPosition) as String
        var mConvertView=convertView
        if (mConvertView == null) {
            val infalInflater = mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            mConvertView = infalInflater.inflate(malayalamdictionary.samasya.R.layout.expandable_list_view_item, null)
        }

        val tf = Typeface.createFromAsset(mContext.resources.assets, "fonts/mal.ttf")

        val txtListChild = mConvertView?.findViewById(malayalamdictionary.samasya.R.id.textView_list_item) as TextView
        if (englishMalayalam) {
            txtListChild.typeface = tf
            txtListChild.text = childText

        } else {
            txtListChild.typeface = Typeface.DEFAULT
            txtListChild.text = childText.toLowerCase()

        }

        return mConvertView
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return this.listDataHeader.size
    }

    fun remove(historyItems: HistoryItems) {
        listDataHeader.remove(historyItems)
        notifyDataSetChanged()
    }

    fun getSelectedIds(): SparseBooleanArray {
        return mSelectedItemsIds
    }

    fun removeSelection() {
        mSelectedItemsIds = SparseBooleanArray()
        notifyDataSetChanged()
    }

    fun selectView(position: Int, value: Boolean) {
        if (value)
            mSelectedItemsIds.put(position, value)
        else
            mSelectedItemsIds.delete(position)
        notifyDataSetChanged()
    }

    fun toggleSelection(position: Int) {
        selectView(position, !mSelectedItemsIds.get(position))
    }

}