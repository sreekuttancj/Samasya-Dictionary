package malayalamdictionary.samasya.app.di

import dagger.Component
import malayalamdictionary.samasya.app.favorite.view.FavouriteEnglishFragment
import malayalamdictionary.samasya.app.favorite.view.FavouriteMalayalamFragment
import malayalamdictionary.samasya.app.MyApplication
import malayalamdictionary.samasya.app.adblocker.view.AdblockActivity
import malayalamdictionary.samasya.app.di.module.ApplicationModule
import malayalamdictionary.samasya.app.history.view.HistoryEnglishFragment
import malayalamdictionary.samasya.app.history.view.HistoryMalayalamFragment
import malayalamdictionary.samasya.app.home.view.MainActivity
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
    fun inject(adblockActivity: AdblockActivity)
}