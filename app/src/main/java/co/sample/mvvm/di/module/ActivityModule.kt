package co.sample.mvvm.di.module

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import co.sample.mvvm.data.repository.TopHeadlineRepository
import co.sample.mvvm.di.ActivityContext
import co.sample.mvvm.ui.base.ViewModelProviderFactory
import co.sample.mvvm.ui.topheadline.TopHeadlineAdapter
import co.sample.mvvm.ui.topheadline.TopHeadlineViewModel

@Module
class ActivityModule(private val activity: AppCompatActivity) {

    @ActivityContext
    @Provides
    fun provideContext(): Context {
        return activity
    }

    @Provides
    fun provideTopHeadlineViewModel(topHeadlineRepository: TopHeadlineRepository): TopHeadlineViewModel {
        return ViewModelProvider(activity,
            ViewModelProviderFactory(TopHeadlineViewModel::class) {
                TopHeadlineViewModel(topHeadlineRepository)
            })[TopHeadlineViewModel::class.java]
    }

    @Provides
    fun provideTopHeadlineAdapter() = TopHeadlineAdapter(ArrayList())

}