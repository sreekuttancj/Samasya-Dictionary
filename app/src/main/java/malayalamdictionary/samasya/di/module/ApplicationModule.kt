package malayalamdictionary.samasya.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ApplicationModule(private val application: Application) {

    @Provides
    fun applicationContext(): Context {
        return application
    }
}