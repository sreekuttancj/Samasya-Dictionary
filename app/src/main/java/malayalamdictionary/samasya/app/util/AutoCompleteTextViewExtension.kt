package malayalamdictionary.samasya.app.util

import android.util.Log
import android.widget.AutoCompleteTextView

fun AutoCompleteTextView.updateText(test:String){
    val focused = hasFocus()
    if (focused){
        clearFocus()
    }

    setText(test)

    if (!focused){
        requestFocus()
    }
}