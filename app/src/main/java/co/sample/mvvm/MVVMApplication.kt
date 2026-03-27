package co.sample.mvvm

import android.app.Application
import co.sample.mvvm.di.component.ApplicationComponent
import co.sample.mvvm.di.component.DaggerApplicationComponent
import co.sample.mvvm.di.module.ApplicationModule

class MVVMApplication : Application() {

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        injectDependencies()
    }

    private fun injectDependencies() {
        applicationComponent = DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(this))
            .build()
        applicationComponent.inject(this)
    }

}