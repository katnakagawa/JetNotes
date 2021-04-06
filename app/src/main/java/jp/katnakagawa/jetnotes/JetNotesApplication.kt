package jp.katnakagawa.jetnotes

import android.app.Application
import jp.katnakagawa.jetnotes.di.DependencyInjector

/**
 * Application class responsible for initializing the dependency injector.
 */
class JetNotesApplication : Application() {

    lateinit var dependencyInjector: DependencyInjector

    override fun onCreate() {
        super.onCreate()

        initDependencyInjector()
    }

    private fun initDependencyInjector() {
        dependencyInjector = DependencyInjector(this)
    }
}