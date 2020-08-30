package malayalamdictionary.samasya.app

import android.app.Application
import malayalamdictionary.samasya.app.di.ApplicationComponent
import malayalamdictionary.samasya.app.di.DaggerApplicationComponent
import malayalamdictionary.samasya.app.di.module.ApplicationModule
import malayalamdictionary.samasya.app.util.AppUpdateHandler
import malayalamdictionary.samasya.app.util.FireBaseHandler
import malayalamdictionary.samasya.app.util.UpdateType
import malayalamdictionary.samasya.domain.firebase.RemoteConfig
import javax.inject.Inject


class MyApplication: Application() {

    lateinit var applicationComponent: ApplicationComponent
    @Inject
    lateinit var fireBaseHandler: FireBaseHandler

    @Inject
    lateinit var appUpdateHandler: AppUpdateHandler

    @Inject
    lateinit var firebaseRemoteConfig: RemoteConfig

    override fun onCreate() {
        super.onCreate()

        initDagger()
        fireBaseHandler.initFireBase()
        appUpdateHandler.checkForUpdate(UpdateType.FLEXIBLE)
        firebaseRemoteConfig.initRemoteConfig()
    }

    private fun initDagger() {
        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(ApplicationModule(this))
                .build()
        applicationComponent.inject(this)
    }
}