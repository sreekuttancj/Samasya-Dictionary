package malayalamdictionary.samasya.di

import dagger.Component
import malayalamdictionary.samasya.MyApplication
import malayalamdictionary.samasya.di.module.ApplicationModule
import malayalamdictionary.samasya.view.*
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    fun inject(myApplication: MyApplication)

    fun inject(mainActivity: MainActivity)
    fun inject(historyEnglishFragment: HistoryEnglishFragment)
    fun inject(historyMalayalamFragment: HistoryMalayalamFragment)
    fun inject(favouriteEnglishFragment: FavouriteEnglishFragment)
    fun inject(favouriteMalayalamFragment: FavouriteMalayalamFragment)
}