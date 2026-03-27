package co.sample.mvvm.ui.topheadline

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import co.sample.mvvm.data.model.Article
import co.sample.mvvm.data.repository.TopHeadlineRepository
import co.sample.mvvm.ui.base.UiState
import co.sample.mvvm.utils.AppConstant.COUNTRY

class TopHeadlineViewModel(private val topHeadlineRepository: TopHeadlineRepository) : ViewModel() {

    companion object {
        private const val TAG = "TopHeadlineViewModel"
    }

    private val _uiState = MutableStateFlow<UiState<List<Article>>>(UiState.Loading)

    val uiState: StateFlow<UiState<List<Article>>> = _uiState

    init {
        fetchTopHeadlines()
    }

    private fun fetchTopHeadlines() {
        Log.d(TAG, "Fetching top headlines for country: $COUNTRY")
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            topHeadlineRepository.getTopHeadlines(COUNTRY)
                .catch { e ->
                    Log.e(TAG, "Error fetching headlines", e)
                    _uiState.value = UiState.Error(e.message ?: "Unknown error occurred")
                }
                .collect { articles ->
                    Log.d(TAG, "Successfully fetched ${articles.size} articles")
                    _uiState.value = UiState.Success(articles)
                }
        }
    }

    fun refreshHeadlines() {
        Log.d(TAG, "Manual refresh triggered")
        fetchTopHeadlines()
    }

}