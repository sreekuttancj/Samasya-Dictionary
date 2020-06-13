package malayalamdictionary.samasya.adapter

import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.autocompletion_list.view.*
import malayalamdictionary.samasya.R
import malayalamdictionary.samasya.helper.Common

class SuggestionListAdapter: ListAdapter<String, SuggestionListAdapter.SuggestionViewHolder>(diffUtil) {

    private val suggestionLiveData = MutableLiveData<String>()

    fun getSuggestionLiveData():LiveData<String> = suggestionLiveData
    companion object{
        val diffUtil = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return (oldItem == newItem).also {
                }
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return (oldItem == newItem).also {
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.autocompletion_list,parent, false)
        return SuggestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        val data = getItem(position)
        val itemView = holder.itemView

        val type = Typeface.createFromAsset(itemView.context.assets, "fonts/mal.ttf")
        if (Common.englishToMayalayam) {
            itemView.textView_autocomplete.typeface = Typeface.DEFAULT
        } else {
            itemView.textView_autocomplete.typeface = type
        }
        itemView.textView_autocomplete.text = data
        itemView.setOnClickListener{
            suggestionLiveData.value = getItem(position)
        }
    }

    inner class SuggestionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

}