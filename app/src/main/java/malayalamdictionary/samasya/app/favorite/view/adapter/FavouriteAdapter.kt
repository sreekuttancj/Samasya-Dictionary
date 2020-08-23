package malayalamdictionary.samasya.app.favorite.view.adapter

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import android.widget.Toast
import malayalamdictionary.samasya.R
import malayalamdictionary.samasya.domain.favorite.model.FavouriteItem
import kotlin.collections.HashMap

class FavouriteAdapter(context: Context, listDataHeader: MutableList<FavouriteItem>,
                       listDataChild: HashMap<String,List<String>>, englishMalayalam: Boolean): BaseExpandableListAdapter() {

    private var mContext: Context = context
    private val listDataHeader: MutableList<FavouriteItem> = listDataHeader
    private val listDataChild: HashMap<String, List<String>> = listDataChild
    private var englishMalayalam: Boolean = englishMalayalam
    private var mSelectedItemsIds: SparseBooleanArray

    init {
        mSelectedItemsIds = SparseBooleanArray()
    }

    override fun getGroup(groupPosition: Int): Any {
        try {
            return this.listDataHeader[groupPosition]
        }
        catch (e : Exception )
        {
            Toast.makeText(mContext, "Please close expanded words before selection", Toast.LENGTH_SHORT).show()
        }
        return true
    }

    override fun isChildSelectable(p0: Int, p1: Int): Boolean {
        return false
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        var mConvertView: View? = convertView
        val favouriteItem: FavouriteItem = getGroup(groupPosition) as FavouriteItem
        val headerTitle = favouriteItem.name
        if (mConvertView == null) {
            val layoutInflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            mConvertView = layoutInflater.inflate(R.layout.expandable_listview_layout, null)
        }

        val tf = Typeface.createFromAsset(mContext.resources.assets, "fonts/mal.ttf")
        val lblListHeader = mConvertView?.findViewById(R.id.textView_item) as TextView
        if (englishMalayalam) run {
            lblListHeader.setTypeface(Typeface.DEFAULT,Typeface.BOLD)
        } else run {
            lblListHeader.textSize = 20f
            lblListHeader.setTypeface(tf, Typeface.BOLD)
        }
        lblListHeader.text = headerTitle

        return mConvertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return this.listDataChild[this.listDataHeader[groupPosition].name]!!.size
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return this.listDataChild[this.listDataHeader[groupPosition].name]!![childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View? {
        val childText = getChild(groupPosition, childPosition) as String
        var mConvertView=convertView
        if (mConvertView == null) run {
            val layoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            mConvertView = layoutInflater.inflate(R.layout.expandable_list_view_item,null)
        }
        val tf = Typeface.createFromAsset(mContext.resources.assets, "fonts/mal.ttf")
        val txtListChild = mConvertView?.findViewById(R.id.textView_list_item) as TextView
        if (englishMalayalam) run {
            txtListChild.typeface = tf
            txtListChild.text = childText
        }else{
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

    fun getGroupItem(groupPosition: Int): Any? {
        try {
            return this.listDataHeader[groupPosition].name
        } catch (e: Exception) {
            Toast.makeText(mContext, "Please close expanded words before selection", Toast.LENGTH_SHORT).show()
        }

        return true
    }
    fun remove(favouriteItem: FavouriteItem) {
        listDataHeader.remove(favouriteItem)
        notifyDataSetChanged()
    }

    fun getSelectedIds() : SparseBooleanArray? {
        return mSelectedItemsIds
    }
    fun removeSelection() {
        mSelectedItemsIds = SparseBooleanArray()
        notifyDataSetChanged()
    }
    fun selectView(position: Int, value: Boolean) {
        if (value)
            mSelectedItemsIds?.put(position, value)
        else
            mSelectedItemsIds?.delete(position)
        notifyDataSetChanged()
    }
    fun toggleSelection(position: Int) {
        Log.e("sparse_array",""+mSelectedItemsIds)
        selectView(position, !(mSelectedItemsIds.get(position)))
    }
}