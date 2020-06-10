package malayalamdictionary.samasya.util

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FireBaseHandler @Inject constructor(private val context: Context) {
    private var firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    fun logFirebaseEvents(event: String, bundle: Bundle?){
        Log.i("check_event","event get called $event")
        firebaseAnalytics.logEvent(event, bundle)
    }
}