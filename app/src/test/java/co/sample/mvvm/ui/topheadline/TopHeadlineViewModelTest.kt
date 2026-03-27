package co.sample.mvvm.ui.topheadline

import co.sample.mvvm.data.model.Article
import co.sample.mvvm.data.model.Source
import co.sample.mvvm.data.repository.TopHeadlineRepository
import co.sample.mvvm.ui.base.UiState
import co.sample.mvvm.utils.AppConstant.COUNTRY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

/**
 * Unit tests for TopHeadlineViewModel.
 * Tests proper state management, error handling, and data flow.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TopHeadlineViewModelTest {

    @Mock
    private lateinit var repository: TopHeadlineRepository

    private lateinit var viewModel: TopHeadlineViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when fetching headlines succeeds, uiState should be Success`() = runTest {
        // Given
        val mockArticles = listOf(
            Article(
                title = "Test Article",
                description = "Test Description",
                url = "https://test.com",
                imageUrl = "https://test.com/image.jpg",
                source = Source(id = "test", name = "Test Source")
            )
        )

        `when`(repository.getTopHeadlines(COUNTRY)).thenReturn(flow {
            emit(mockArticles)
        })

        // When
        viewModel = TopHeadlineViewModel(repository)

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(mockArticles, (state as UiState.Success).data)
    }

    @Test
    fun `when fetching headlines fails, uiState should be Error`() = runTest {
        // Given
        val errorMessage = "Unable to resolve host"
        `when`(repository.getTopHeadlines(COUNTRY)).thenReturn(flow {
            throw Exception(errorMessage)
        })

        // When
        viewModel = TopHeadlineViewModel(repository)

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is UiState.Error)
        assertEquals(TopHeadlineViewModel.ERROR_NETWORK, (state as UiState.Error).message)
    }

    @Test
    fun `retry should fetch headlines again`() = runTest {
        // Given
        val mockArticles = listOf(
            Article(
                title = "Test Article",
                description = "Test Description",
                url = "https://test.com",
                imageUrl = "https://test.com/image.jpg",
                source = Source(id = "test", name = "Test Source")
            )
        )

        `when`(repository.getTopHeadlines(COUNTRY)).thenReturn(flow {
            emit(mockArticles)
        })

        viewModel = TopHeadlineViewModel(repository)

        // When
        viewModel.retry()

        // Then
        verify(repository, times(2)).getTopHeadlines(COUNTRY)
    }
}

