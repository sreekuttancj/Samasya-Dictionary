package malayalamdictionary.samasya.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.database.SQLException
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import malayalamdictionary.samasya.R

class MeaningAdapter(context: Context, private val values: Array<String>, internal var englishToMalayalam: Boolean) : ArrayAdapter<String>(context, R.layout.rowlayout, values) {

    internal lateinit var textView: TextView
    internal lateinit var clipboard: ClipboardManager

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var mConvertView = convertView

        clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        if (mConvertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            mConvertView = inflater.inflate(R.layout.rowlayout, parent, false)
        }
        textView = mConvertView!!.findViewById(R.id.label) as TextView
        val tf = Typeface.createFromAsset(this.context.resources.assets, "fonts/mal.ttf")

        textView.setOnLongClickListener {
            if (englishToMalayalam) {
                Toast.makeText(context, "Long press to copy not available", Toast.LENGTH_SHORT).show()
            } else {

                val clip = ClipData.newPlainText("item", values[position])

                Toast.makeText(context, values[position].toLowerCase() + " copied to clip board", Toast.LENGTH_SHORT).show()
                clipboard.setPrimaryClip(clip)
            }

            true
        }
        if (englishToMalayalam) {
            textView.typeface = tf
            textView.text = this.values[position]
        } else {
            textView.text = this.values[position].toLowerCase()
        }
        try {
            textView.setTextColor(Color.parseColor("#000000"))

            return mConvertView
        } catch (sqlite: SQLException) {
            throw sqlite
        }
    }

}