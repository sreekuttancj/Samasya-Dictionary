package malayalamdictionary.samasya

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.database.SQLException
import android.graphics.Point
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import android.widget.AbsListView
import android.widget.ExpandableListView
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import malayalamdictionary.samasya.adapter.HistoryAdapter
import malayalamdictionary.samasya.database.DatabaseHelper
import malayalamdictionary.samasya.helper.HistoryItems
import java.util.*

class HistoryEnglishFragment : Fragment() {
    //todo history deletion bug,check favorite deletion also
    private lateinit var listAdapter: HistoryAdapter
    private lateinit var expListView: ExpandableListView
    private lateinit var listDataHeader: MutableList<HistoryItems>
    private lateinit var listDataChild: HashMap<String, List<String>>
    private lateinit var historyItems: MutableList<HistoryItems>
    private lateinit var meaningArray: ArrayList<String>
    private var actionModeEnabled: Boolean = false
    private var selectAll = true
    private lateinit var menuItem: MenuItem
    private lateinit var copyText: String
    private lateinit var progress: ProgressDialog
    private lateinit var historyTask: HistoryTask
    var expanded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rowView: View = inflater.inflate(R.layout.fragment_history, container, false)

        expListView = rowView.findViewById(R.id.lvExp_history)
        listDataHeader = ArrayList()
        listDataChild = HashMap()

        val display: Display? = activity?.windowManager?.defaultDisplay
        val size = Point()
        display?.getSize(size)
        val width = size.x
        expListView.setIndicatorBounds(width - (getDipsFromPixel(35F)), width - getDipsFromPixel(5F))

        progress = ProgressDialog(activity)
        historyTask = HistoryTask()
        historyTask.execute()

        expListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL

        expListView.setOnGroupClickListener { expandableListView, view, i, l ->
            expanded = !expandableListView.isGroupExpanded(i)
            if (actionModeEnabled) {
                expandableListView.setItemChecked(i, !expandableListView.isItemChecked(i))
            }

            actionModeEnabled
        }


        expListView.setMultiChoiceModeListener(object : AbsListView.MultiChoiceModeListener {
            override fun onItemCheckedStateChanged(mode: ActionMode, position: Int, id: Long, checked: Boolean) {
                val checkedCount = expListView.checkedItemCount
                mode.title = checkedCount.toString()
                listAdapter.toggleSelection(position)
                val selected = listAdapter.getSelectedIds()

                if (checkedCount > 1) {
                    menuItem.isVisible = false
                } else {

                    if (selected.valueAt(0)) {
                        copyText = listAdapter.getGroupItem(selected.keyAt(0)).toString()

                    }

                    menuItem.isVisible = true

                }

                selectAll = checkedCount != listAdapter.groupCount
            }

            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
                val menuInflater = activity!!.menuInflater
                menuInflater.inflate(R.menu.favourite_menu, menu)
                menuItem = menu.getItem(0)
                actionModeEnabled = true

                return true
            }

            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {


                when (item.itemId) {

                    R.id.copy -> {
                        if (!expanded) {
                            val label = "copy"
                            val clipboard = activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText(label, copyText)
                            clipboard.primaryClip = clip
                            mode.finish()
                            Toast.makeText(activity, "$copyText copied to clipboard", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(activity, "Please close expanded words before selection", Toast.LENGTH_SHORT).show()

                        }
                        return true
                    }


                    R.id.select_all -> {
                        if (selectAll) {
                            for (i in 0 until expListView.adapter.count) {
                                expListView.setItemChecked(i, true)
                                listAdapter.selectView(i, true)
                            }
                            selectAll = false

                        } else {
                            for (i in 0 until expListView.adapter.count) {

                                expListView.setItemChecked(i, false)
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
                            //                        // Calls getSelectedIds method from ListViewAdapter Class

                            //                        // Captures all selected ids with a loop
                            for (i in selected.size() - 1 downTo 0) {

                                val databaseHelper = DatabaseHelper(requireContext())
                                if (selected.valueAt(i)) {

                                    if (!expanded) {

                                        val historyItems = listAdapter.getGroup(selected.keyAt(i)) as HistoryItems


                                        listAdapter.remove(historyItems)

                                        try {
                                            val cursor = databaseHelper.writableDatabase.rawQuery("delete From samasya_eng_history where ENG like '" + historyItems.name + "'", null)
                                            cursor.moveToFirst()
                                            while (!cursor.isAfterLast) {
                                                cursor.moveToNext()
                                            }
                                            cursor.close()
                                        } catch (sqle: SQLException) {
                                            throw sqle
                                        }

                                    }
                                } else {
                                    Toast.makeText(activity, "Please close expanded words before selection", Toast.LENGTH_SHORT).show()

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
                //                toolbar.setVisibility(View.VISIBLE);
                listAdapter.removeSelection()
                actionModeEnabled = false

            }
        })


        return rowView
    }

    private fun getDipsFromPixel(pixels: Float): Int {
        // Get the screen's density scale
        val scale = resources.displayMetrics.density
        // Convert the dps to pixels, based on density scale
        return (pixels * scale + 0.5f).toInt()
    }


    fun getDataFromDb() {

        val myDbHelper2 = DatabaseHelper(requireContext())
        try {
            myDbHelper2.open()
            val cursor = myDbHelper2.readableDatabase.rawQuery("Select DISTINCT ENG From samasya_eng_history order by ENG asc", null)
            historyItems = ArrayList()
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                historyItems.add(HistoryItems(cursor.getString(0)))
                cursor.moveToNext()
            }
            cursor.close()
            myDbHelper2.close()
        } catch (sqle: SQLException) {
            throw sqle
        }

    }

    fun getMeaningFromDb(word: String?): ArrayList<String> {

        val myDbHelper2 = DatabaseHelper(requireContext())
        try {
            myDbHelper2.open()
            val cursor = myDbHelper2.readableDatabase.rawQuery("Select * From samasya_eng_mal where ENG like '$word'", null)
            meaningArray = ArrayList()
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                meaningArray.add(cursor.getString(1))
                cursor.moveToNext()
            }
            cursor.close()
            myDbHelper2.close()
        } catch (sqle: SQLException) {
            throw sqle
        }

        return meaningArray
    }

    private fun prepareListData() {

        getDataFromDb()
        // Adding child
        for (i in historyItems.indices) {
            listDataHeader.add(historyItems[i])
            val meaning: List<String>
            meaning = getMeaningFromDb(listDataHeader[i].name)
            listDataChild[listDataHeader[i].name.toString()] = meaning
        }
    }

    private inner class HistoryTask : AsyncTask<String, String, String>() {

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
            listAdapter = HistoryAdapter(requireContext(), listDataHeader, listDataChild, true)
            expListView.setAdapter(listAdapter)

        }
    }

}