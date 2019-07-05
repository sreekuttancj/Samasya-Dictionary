package malayalamdictionary.samasya.helper

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

 open class OnSwipeTouchListener(ctx: Context) : View.OnTouchListener {

    var gestureDetector: GestureDetector = GestureDetector(ctx, GestureListener())

     override fun onTouch(v: View, event: MotionEvent): Boolean {
         return false
     }
    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            var result = false
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > Common.SWIPE_THRESHOLD && Math.abs(velocityX) > Common.SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight()
                        } else {
                            onSwipeLeft()
                        }
                    }
                    result = true
                } else if (Math.abs(diffY) > Common.SWIPE_THRESHOLD && Math.abs(velocityY) > Common.SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom()
                    } else {
                        onSwipeTop()
                    }
                }
                result = true

            } catch (exception: Exception) {
                exception.printStackTrace()
            }

            return result
        }

    }

    open fun onSwipeRight() {}

    open fun onSwipeLeft() {}

    open fun onSwipeTop() {}

    open fun onSwipeBottom() {}

}