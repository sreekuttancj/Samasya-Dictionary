package malayalamdictionary.samasya.app.favorite.view

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.database.SQLException
import android.graphics.Point
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.util.SparseBooleanArray
import android.view.*
import android.widget.AbsListView
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_his_fav.*
import malayalamdictionary.samasya.app.MyApplication
import malayalamdictionary.samasya.R
import malayalamdictionary.samasya.app.favorite.view.adapter.FavouriteAdapter
import malayalamdictionary.samasya.app.database.DatabaseHelper
import malayalamdictionary.samasya.domain.favorite.model.FavouriteItem
import malayalamdictionary.samasya.app.util.FireBaseHandler
import java.util.*
import javax.inject.Inject

class FavouriteMalayalamFragment : Fragment() {

    private lateinit var favouriteItems: MutableList<FavouriteItem>
    private lateinit var meaningArray: ArrayList<String>


    private lateinit var listAdapter: FavouriteAdapter
    private lateinit var listDataHeader: MutableList<FavouriteItem>
    private lateinit var listDataChild: HashMap<String, List<String>>
    private var selectAll: Boolean = true
    private var actionModeEnabled: Boolean = false
    private lateinit var menuItem: MenuItem
    private lateinit var copyText: String
    private lateinit var progress: ProgressDialog
    private lateinit var favouriteTask: FavouriteTask
    private lateinit var expandedItem: SparseBooleanArray
    var expanded = false

    @Inject
    lateinit var fireBaseHandler: FireBaseHandler

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        initDagger()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rowView: View = inflater.inflate(R.layout.fragment_his_fav, container, false)

        return rowView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        listDataHeader = ArrayList()
        listDataChild = HashMap()
        expandedItem = SparseBooleanArray()

        val display = activity!!.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        lvExp.setIndicatorBounds(width - getDipsFromPixel(35f), width - getDipsFromPixel(5f))

        progress = ProgressDialog(activity)
        favouriteTask = FavouriteTask()
        favouriteTask.execute()


        lvExp.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL

        lvExp.setOnGroupClickListener { expandableListView, view, i, l ->
            expanded = !expandableListView.isGroupExpanded(i)
            if (!actionModeEnabled && expanded) {
                expandedItem.put(i, expanded)
            }
            if (!expanded && expandedItem[i]) {
                expandedItem.delete(i)
            }
            if (actionModeEnabled) {
                expandableListView.setItemChecked(i, !expandableListView.isItemChecked(i))
            }

            actionModeEnabled
        }

        lvExp.setMultiChoiceModeListener(object : AbsListView.MultiChoiceModeListener {
            override fun onItemCheckedStateChanged(mode: ActionMode, position: Int, id: Long, checked: Boolean) {
                if (expandedItem.size() == 0) {
                    val checkedCount = lvExp.checkedItemCount
                    mode.title = checkedCount.toString()
                    listAdapter.toggleSelection(position)

                    val selected = listAdapter.getSelectedIds()

                    if (checkedCount > 1) {
                        menuItem.isVisible = false
                    } else {

                        if (selected!!.valueAt(0)) {
                            copyText = listAdapter.getGroupItem(selected.keyAt(0)).toString()

                        }
                    }

                    selectAll = checkedCount != listAdapter.groupCount
                } else {

                    //if any of the item is expanded then refresh the adapter

                    Toast.makeText(activity, "Please close expanded words before selection", Toast.LENGTH_SHORT).show()
                    expandedItem.clear()
                    lvExp.setAdapter(listAdapter)
                }

            }

            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                val menuInflater = activity!!.menuInflater
                menuInflater.inflate(R.menu.favourite_menu, menu)
                //                toolbar.setVisibility(View.GONE);
                menuItem = menu.getItem(0)
                menuItem.isVisible = false
                actionModeEnabled = true

                return true
            }

            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {

                when (item.itemId) {


                    R.id.select_all -> {
                        if (selectAll) {
                            for (i in 0 until lvExp.adapter.count) {
                                lvExp.setItemChecked(i, true)
                                listAdapter.selectView(i, true)
                            }
                            selectAll = false

                        } else {
                            for (i in 0 until lvExp.adapter.count) {

                                lvExp.setItemChecked(i, false)
                                listAdapter.selectView(i, false)
                            }
                            selectAll = true
                        }

                        return true
                    }

                    R.id.delete -> {

                        val selected = listAdapter.getSelectedIds()
                        val alertDialogBuilder = AlertDialog.Builder(activity)
                        alertDialogBuilder.setMessage("Are you sure,You wanted to delete this item")
                        alertDialogBuilder.setPositiveButton("yes") { arg0, arg1 ->
                            //                        // Captures all selected ids with a loop
                            for (i in selected!!.size() - 1 downTo 0) {

                                val databaseHelper = DatabaseHelper(requireContext())
                                if (selected.valueAt(i)) {
                                    val favouriteItem = listAdapter.getGroup(selected.keyAt(i)) as FavouriteItem

                                    // Re/move selected items following the ids

                                    listAdapter.remove(favouriteItem)

                                    try {
                                        val cursor = databaseHelper.writableDatabase.rawQuery("delete From samasya_mal_favourite where MAL like '" + favouriteItem.name + "'", null)
                                        cursor.moveToFirst()
                                        while (!cursor.isAfterLast) {
                                            cursor.moveToNext()
                                        }
                                        cursor.close()
                                    } catch (sqle: SQLException) {
                                        throw sqle
                                    }

                                }
                            }
                        }

                        alertDialogBuilder.setNegativeButton("No") { dialog, which -> dialog.dismiss() }

                        val alertDialog = alertDialogBuilder.create()
                        alertDialog.show()

                        // Close CAB
                        mode.finish()
                        return true
                    }
                    else -> return false
                }
            }


            override fun onDestroyActionMode(mode: ActionMode) {
                mode.finish()

                listAdapter.removeSelection()
                actionModeEnabled = false
            }
        })
    }

    private fun initDagger() {
        (activity?.applicationContext as MyApplication)
                .applicationComponent
                .inject(this)
    }

    private fun prepareListData() {

        getDataFromDb()

        for (i in favouriteItems.indices) {
            listDataHeader.add(favouriteItems[i])
            val meaning: List<String>
            meaning = getMeaningFromDb(listDataHeader[i].name)
            listDataChild[listDataHeader[i].name.toString()] = meaning
        }

        //track fire base event for no of favorite items
        Log.i("check_favorite_count","${listDataHeader.size}")
        val bundle = Bundle()
        bundle.putInt(FireBaseHandler.FAVORITE_MALAYALAM_COUNT, listDataHeader.size)
        fireBaseHandler.logFirebaseEvents(FireBaseHandler.FAVORITE_MALAYALAM, bundle)

    }


    private fun getDataFromDb() {

        val myDbHelper2 = DatabaseHelper(requireContext())
        try {
            myDbHelper2.open()
            val cursor = myDbHelper2.readableDatabase.rawQuery("Select DISTINCT MAL From samasya_mal_favourite order by MAL asc", null)
            favouriteItems = ArrayList()
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                favouriteItems.add(FavouriteItem(cursor.getString(0)))
                cursor.moveToNext()
            }
            cursor.close()
            myDbHelper2.close()
        } catch (sqle: SQLException) {
            throw sqle
        }

    }

    private fun getMeaningFromDb(word: String?): ArrayList<String> {

        val myDbHelper2 = DatabaseHelper(requireContext())
        try {
            myDbHelper2.open()
            val cursor = myDbHelper2.readableDatabase.rawQuery("Select * From samasya_eng_mal where MAL like '$word'", null)
            meaningArray = ArrayList()
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                meaningArray.add(cursor.getString(0))
                cursor.moveToNext()
            }
            cursor.close()
            myDbHelper2.close()
        } catch (sqle: SQLException) {
            throw sqle
        }

        return meaningArray
    }

    private fun getDipsFromPixel(pixels: Float): Int {
        // Get the screen's density scale
        val scale = resources.displayMetrics.density
        // Convert the dps to pixels, based on density scale
        return (pixels * scale + 0.5f).toInt()
    }

    private inner class FavouriteTask : AsyncTask<String, String, String>() {


        override fun doInBackground(vararg params: String): String? {
            prepareListData()

            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
            progress.setMessage("Loading...")
            progress.show()

        }

        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)
            progress.dismiss()
            listAdapter = FavouriteAdapter(activity!!, listDataHeader, listDataChild, false)
            lvExp.setAdapter(listAdapter)

        }
    }

}