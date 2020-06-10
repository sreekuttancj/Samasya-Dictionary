package malayalamdictionary.samasya.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import malayalamdictionary.samasya.MyApplication

class BaseActivity: AppCompatActivity() {

    private lateinit var application: MyApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        application = MyApplication.getMyapplicationInstance()
    }

    private fun logFirebaseEvent(eventName: String) {
        logFirebaseEvent(eventName, null)
    }

    private fun logFirebaseEvent(eventName: String, bundle:Bundle?) {
        getFirebaseLog().logEvent(eventName, bundle)
    }

    private fun getFirebaseLog(): FirebaseAnalytics {
        return application.getFirebaseAnalyticsInstance()
    }
}