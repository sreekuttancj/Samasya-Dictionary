package malayalamdictionary.samasya.data.firebase

import androidx.lifecycle.MutableLiveData
import com.fasterxml.jackson.core.type.TypeReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import malayalamdictionary.samasya.app.util.Mapper
import malayalamdictionary.samasya.domain.firebase.AppUpdate
import malayalamdictionary.samasya.domain.firebase.RemoteConfig
import javax.inject.Inject

class RemoteConfigImp @Inject constructor(): RemoteConfig {
    private val appUpdateMutableLiveData = MutableLiveData<AppUpdate>()

    override fun getAppUpdateLiveData() = appUpdateMutableLiveData

    override fun initRemoteConfig() {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 60L
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if(it.isSuccessful) {
                val appUpdate = Mapper.objectFromType(remoteConfig.getString(RemoteConfig.KEY_APP_UPDATE), object : TypeReference<AppUpdate>() {})
                appUpdateMutableLiveData.value = appUpdate
            }
        }
    }
}