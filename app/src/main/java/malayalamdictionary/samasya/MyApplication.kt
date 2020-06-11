package malayalamdictionary.samasya

import android.app.Application
import malayalamdictionary.samasya.di.ApplicationComponent
import malayalamdictionary.samasya.di.DaggerApplicationComponent
import malayalamdictionary.samasya.di.module.ApplicationModule
import malayalamdictionary.samasya.util.FireBaseHandler
import javax.inject.Inject


class MyApplication: Application() {

    lateinit var applicationComponent: ApplicationComponent
    @Inject
    lateinit var fireBaseHandler: FireBaseHandler

    override fun onCreate() {
        super.onCreate()

        initDagger()
        fireBaseHandler.initFireBase()
    }

    private fun initDagger() {
        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(ApplicationModule(this))
                .build()
        applicationComponent.inject(this)
    }
}