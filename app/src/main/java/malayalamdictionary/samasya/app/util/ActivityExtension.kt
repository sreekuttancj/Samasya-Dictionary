package malayalamdictionary.samasya.app.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun Activity.hideKeyBoard(view: View? = null) {
    var mView = view
    if(view == null){
        mView = currentFocus
    }
    if (mView != null) {
        val imm = getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(mView.windowToken, 0)
    }
}

fun Activity.showKeyboard(view: View? = null) {
    var mView = view
    if(view == null){
        mView = currentFocus
    }
    if (mView != null) {
        val imm = getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.showSoftInput(mView, InputMethodManager.SHOW_FORCED)
    }
}

fun Activity.showKeyboardInFragments(){
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}


