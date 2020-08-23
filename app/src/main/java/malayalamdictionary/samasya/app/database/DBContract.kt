package malayalamdictionary.samasya.app.database

import android.provider.BaseColumns

object DBContract {
    class UserEntry : BaseColumns {
        companion object{
            const val TABLE_SUGGESTION = "samasya_suggestion"
            const val TABLE_ENG_MAL = "samasya_eng_mal"
            const val TABLE_ENG_HISTORY = "samasya_eng_history"
            const val TABLE_ENG_FAVOURITE = "samasya_eng_favourite"
            const val TABLE_MAL_HISTORY = "samasya_mal_history"
            const val TABLE_MAL_FAVOURITE = "samasya_mal_favourite"
        }
    }
}