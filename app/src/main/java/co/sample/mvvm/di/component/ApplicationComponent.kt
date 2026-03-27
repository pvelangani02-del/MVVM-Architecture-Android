package co.sample.mvvm.di.component

import android.content.Context
import dagger.Component
import co.sample.mvvm.MVVMApplication
import co.sample.mvvm.data.api.NetworkService
import co.sample.mvvm.data.repository.TopHeadlineRepository
import co.sample.mvvm.di.ApplicationContext
import co.sample.mvvm.di.module.ApplicationModule
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    fun inject(application: MVVMApplication)

    @ApplicationContext
    fun getContext(): Context

    fun getNetworkService(): NetworkService

    fun getTopHeadlineRepository(): TopHeadlineRepository

}