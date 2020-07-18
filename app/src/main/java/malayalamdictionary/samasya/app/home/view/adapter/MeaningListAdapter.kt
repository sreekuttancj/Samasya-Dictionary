package malayalamdictionary.samasya.app.home.view.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.database.SQLException
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rowlayout.view.*
import malayalamdictionary.samasya.R
import malayalamdictionary.samasya.app.helper.Common

class MeaningListAdapter: ListAdapter<String, MeaningListAdapter.MeaningViewHolder>(diffUtil) {

    lateinit var clipboard: ClipboardManager

    companion object{
        val diffUtil = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
               return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeaningViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rowlayout, parent,false)
        return MeaningViewHolder(view)
    }

    override fun onBindViewHolder(holder: MeaningViewHolder, position: Int) {
        val itemView = holder.itemView
        val data = getItem(position)

        clipboard = itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val tf = Typeface.createFromAsset(itemView.context.resources.assets, "fonts/mal.ttf")

        itemView.setOnLongClickListener {
            if (Common.englishToMayalayam) {
                Toast.makeText(itemView.context, "Long press to copy not available", Toast.LENGTH_SHORT).show()
            } else {

                val clip = ClipData.newPlainText("item", data)

                Toast.makeText(itemView.context, data.toLowerCase() + " copied to clip board", Toast.LENGTH_SHORT).show()
                clipboard.setPrimaryClip(clip)
            }

            true
        }
        if (Common.englishToMayalayam) {
            itemView.label.typeface = tf
            itemView.label.text = data
        } else {
            itemView.label.text = data.toLowerCase()
        }
        try {
            itemView.label.setTextColor(Color.parseColor("#000000"))
        } catch (sqlite: SQLException) {
            throw sqlite
        }
    }

    class MeaningViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

}