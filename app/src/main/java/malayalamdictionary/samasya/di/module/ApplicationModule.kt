package malayalamdictionary.samasya.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import malayalamdictionary.samasya.helper.Common
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
}