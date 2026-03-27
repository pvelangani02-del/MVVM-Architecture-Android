package co.sample.mvvm.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import co.sample.mvvm.data.api.NetworkService
import co.sample.mvvm.data.model.Article
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for fetching top headlines from the network.
 * Provides a clean API for data access with proper flow-based reactive streams.
 */
@Singleton
class TopHeadlineRepository @Inject constructor(private val networkService: NetworkService) {

    /**
     * Fetches top headlines for a given country.
     *
     * @param country Country code (e.g., "us", "in", "gb")
     * @return Flow emitting list of articles
     * @throws Exception if network request fails
     */
    fun getTopHeadlines(country: String): Flow<List<Article>> {
        // Input validation
        require(country.isNotBlank()) { "Country code cannot be blank" }
        require(country.length == 2) { "Country code must be 2 characters (ISO 3166-1 alpha-2)" }

        return flow {
            emit(networkService.getTopHeadlines(country))
        }.map { response ->
            response.articles
        }
    }

}

