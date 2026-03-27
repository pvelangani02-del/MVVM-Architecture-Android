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
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * ViewModel for Top Headlines screen.
 * Manages UI state and data fetching with proper error handling and retry mechanism.
 */
class TopHeadlineViewModel(private val topHeadlineRepository: TopHeadlineRepository) : ViewModel() {

    companion object {
        const val ERROR_NETWORK = "error_network"
        const val ERROR_TIMEOUT = "error_timeout"
        const val ERROR_GENERIC = "error_generic"
    }

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
                    val errorCode = when (e) {
                        is UnknownHostException, is ConnectException -> ERROR_NETWORK
                        is SocketTimeoutException -> ERROR_TIMEOUT
                        else -> ERROR_GENERIC
                    }
                    _uiState.value = UiState.Error(errorCode)
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