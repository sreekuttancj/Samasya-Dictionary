package malayalamdictionary.samasya.app.util

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FireBaseHandler @Inject constructor(context: Context) {

    companion object{
        const val SAVE_WORD_ENGLISH ="save_word_english"
        const val SAVE_WORD_MALAYALAM ="save_word_malayalam"
        const val PRONUNCIATION = "pronunciation"
        const val SEARCH_ENGLISH = "search_english"
        const val SEARCH_MALAYALAM = "search_malayalam"
        const val SEARCH_MALAYALAM_IMAGE_BUTTON = "search_mal_img_button"
        const val HISTORY_ENGLISH = "history_english"
        const val HISTORY_MALAYALAM = "history_malayalam"
        const val FAVORITE_ENGLISH = "favorite_english"
        const val FAVORITE_MALAYALAM = "favorite_malayalam"
        const val GOOGLE_TRANSLATE_CLICK ="click_google_translate"
        const val USE_GOOGLE_TRANSLATE ="use_google_translate"
        const val SEND_TO_DB ="send_to_db"
        const val FAB_E = "fab_e"
        const val FAB_M ="fab_m"
        const val SEARCH_USING_MIC = "search_mic"
        const val SEARCH_USING_MIC_NOT_SUPPORT = "search_mic_not_support"
        const val SHARE_APP ="share_app"
        const val RATE_US ="rate_us"
        const val ABOUT = "about"
        const val RATE_US_DIALOG ="rate_us_dialog"

        const val SAVED_WORD ="word"
        const val PRONUNCIATION_WORD = "pronunciation_word"
        const val SEARCH_ENGLISH_WORD = "search_english_word"
        const val SEARCH_MALAYALAM_WORD = "search_malayalam_word"
        const val SEARCH_MALAYALAM_IMAGE_BUTTON_WORD = "search_mal_img_button_word"
        const val HISTORY_ENGLISH_COUNT = "history_english_count"
        const val HISTORY_MALAYALAM_COUNT = "history_malayalam_count"
        const val FAVORITE_ENGLISH_COUNT ="favorite_english_count"
        const val FAVORITE_MALAYALAM_COUNT ="favorite_malayalam_count"
    }
    private var firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    fun initFireBase(){
        Log.i("check_fb_init","enter init FireBase")
        setUserId()
    }

    private fun setUserId() {
        firebaseAnalytics.appInstanceId.addOnCompleteListener{
            it.let {
                if (it.isSuccessful){
                    val user_pseudo_id = it.result
                    Log.i("check_fb","user id: $user_pseudo_id")
                    firebaseAnalytics.setUserId(user_pseudo_id)
                    sharedPreferences.edit().putString(Constant.USER_ID,user_pseudo_id).apply()
                }
            }
        }
    }

    fun logFirebaseEvents(event: String, bundle: Bundle?){
        Log.i("check_event","event get called $event bundle: $bundle")
        firebaseAnalytics.logEvent(event, bundle)
    }

}