package malayalamdictionary.samasya.util

import android.content.Context
import android.util.Log
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import javax.inject.Inject

class AppUpdateHandler @Inject constructor(context: Context) {

    // Creates instance of the manager.
    val appUpdateManager = AppUpdateManagerFactory.create(context)

    // Returns an intent object that you use to check for an update.
    val appUpdateInfoTask = appUpdateManager.appUpdateInfo

    fun checkForUpdate(updateType: UpdateType){
        Log.i("check_update_type","called")
        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
               when(updateType){
                   UpdateType.FLEXIBLE -> {
                       Log.i("check_update_type","FLEXIBLE")
                       if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)){
                           Log.i("check_update_type","FLEXIBLE true")
                           // Request the update.
                       }else{
                           Log.i("check_update_type","FLEXIBLE false")
                       }
                   }

                   UpdateType.IMMEDIATE -> {
                       if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                           Log.i("check_update_type","IMMEDIATE true")
                           // Request the update.
                       }
                   }
               }
            }else{
                Log.i("check_update_type","Update not available")
            }
        }
    }
}

enum class UpdateType{
    FLEXIBLE,
    IMMEDIATE
}