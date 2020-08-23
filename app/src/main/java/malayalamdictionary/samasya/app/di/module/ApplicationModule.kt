package malayalamdictionary.samasya.app.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import malayalamdictionary.samasya.app.helper.Common
import malayalamdictionary.samasya.data.firebase.RemoteConfigImp
import malayalamdictionary.samasya.domain.firebase.RemoteConfig
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: Application) {

    @Singleton
    @Provides
    fun applicationContext(): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideSharedPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(Common.MyPREFERENCES, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideRemoteConfig(remoteConfigImp: RemoteConfigImp): RemoteConfig = remoteConfigImp

}