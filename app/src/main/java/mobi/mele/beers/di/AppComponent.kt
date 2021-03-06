package mobi.mele.beers.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import mobi.mele.beers.ui.detail.DetailComponent
import mobi.mele.beers.ui.detail.DetailModule
import mobi.mele.beers.ui.main.MainComponent
import mobi.mele.beers.ui.main.MainModule
import javax.inject.Singleton

/**
 * Created by Antonio Fernández
 * date   : 26/12/21
 * e-mail : meleappdev@gmail.com
 */
@Singleton
@Component(modules = [AppModule::class, DataModule::class])
interface AppComponent {

    fun plus(module: MainModule): MainComponent
    fun plus(module: DetailModule): DetailComponent

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: Application): AppComponent
    }
}