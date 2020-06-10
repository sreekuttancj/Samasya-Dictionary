package malayalamdictionary.samasya.di

import dagger.Component
import malayalamdictionary.samasya.MyApplication
import malayalamdictionary.samasya.di.module.ApplicationModule
import malayalamdictionary.samasya.view.MainActivity

@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    fun inject(myApplication: MyApplication)

    fun inject(mainActivity: MainActivity)
}