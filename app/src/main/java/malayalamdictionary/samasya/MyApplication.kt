package malayalamdictionary.samasya

import android.app.Application
import malayalamdictionary.samasya.di.ApplicationComponent
import malayalamdictionary.samasya.di.DaggerApplicationComponent
import malayalamdictionary.samasya.di.module.ApplicationModule


class MyApplication: Application() {

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(ApplicationModule(this))
                .build()
        applicationComponent.inject(this)
    }

}