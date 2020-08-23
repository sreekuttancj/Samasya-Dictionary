package malayalamdictionary.samasya.domain.firebase

import androidx.lifecycle.LiveData

interface RemoteConfig {
    companion object{
        const val KEY_APP_UPDATE = "app_update"
    }

    fun initRemoteConfig()
    fun getAppUpdateLiveData(): LiveData<AppUpdate>
}