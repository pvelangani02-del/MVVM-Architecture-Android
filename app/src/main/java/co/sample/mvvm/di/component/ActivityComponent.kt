package co.sample.mvvm.di.component

import dagger.Component
import co.sample.mvvm.di.ActivityScope
import co.sample.mvvm.di.module.ActivityModule
import co.sample.mvvm.ui.topheadline.TopHeadlineActivity

@ActivityScope
@Component(dependencies = [ApplicationComponent::class], modules = [ActivityModule::class])
interface ActivityComponent {

    fun inject(activity: TopHeadlineActivity)

}