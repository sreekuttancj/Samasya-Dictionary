package malayalamdictionary.samasya

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import malayalamdictionary.samasya.di.ApplicationComponent
import malayalamdictionary.samasya.di.DaggerApplicationComponent
import malayalamdictionary.samasya.di.module.ApplicationModule


class MyApplication: Application() {

    lateinit var applicationComponent: ApplicationComponent

    companion object{
        private var myApplicationInstance: MyApplication? = null

        fun getMyapplicationInstance(): MyApplication{
             if (myApplicationInstance == null) {
                 myApplicationInstance = MyApplication()
            } else {
                 myApplicationInstance
            }
            return myApplicationInstance as MyApplication
        }
    }

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(ApplicationModule(this))
                .build()
        applicationComponent.inject(this)
    }

}