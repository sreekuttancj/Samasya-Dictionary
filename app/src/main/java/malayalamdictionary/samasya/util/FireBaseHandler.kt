package malayalamdictionary.samasya.util

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import malayalamdictionary.samasya.MyApplication
import javax.inject.Inject

class FireBaseHandler @Inject constructor(private val context: Context) {
    private var firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    fun logFirebaseEvents(event: String, bundle: Bundle?){
        Log.i("check_event","context $context")
        Log.i("check_event","event get called $event")
        Toast.makeText(context, "event get called $event", Toast.LENGTH_SHORT).show()
        firebaseAnalytics.logEvent(event, bundle)
    }
}