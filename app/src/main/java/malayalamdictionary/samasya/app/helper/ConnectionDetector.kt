package malayalamdictionary.samasya.app.helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import javax.inject.Inject

class ConnectionDetector @Inject constructor(var context: Context) {

    fun isConnectingToInternet(): Boolean {
        val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivity.allNetworkInfo
        if (info != null)
            for (i in info.indices)
                if (info[i].state == NetworkInfo.State.CONNECTED) {
                    return true
                }
        return false
    }
}