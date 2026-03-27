package co.sample.mvvm.ui.topheadline

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

/**
 * ViewModel for Top Headlines screen.
 * Manages UI state and data fetching with proper error handling and retry mechanism.
 */
class TopHeadlineViewModel(private val topHeadlineRepository: TopHeadlineRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Article>>>(UiState.Loading)

    val uiState: StateFlow<UiState<List<Article>>> = _uiState

    init {
        fetchTopHeadlines()
    }

    /**
     * Fetches top headlines from the repository.
     * Uses proper coroutine scope and error handling.
     */
    private fun fetchTopHeadlines() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            topHeadlineRepository.getTopHeadlines(COUNTRY)
                .catch { e ->
                    val errorMessage = when {
                        e.message?.contains("Unable to resolve host") == true ->
                            "Network error. Please check your internet connection."
                        e.message?.contains("timeout") == true ->
                            "Request timed out. Please try again."
                        else ->
                            "Failed to load headlines: ${e.message ?: "Unknown error"}"
                    }
                    _uiState.value = UiState.Error(errorMessage)
                }
                .collect { articles ->
                    _uiState.value = UiState.Success(articles)
                }
        }
    }

    /**
     * Retry fetching headlines after an error.
     * Can be called from UI when user taps retry button.
     */
    fun retry() {
        fetchTopHeadlines()
    }

}