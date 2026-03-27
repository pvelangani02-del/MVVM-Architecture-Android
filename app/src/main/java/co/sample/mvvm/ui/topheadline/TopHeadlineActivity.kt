package co.sample.mvvm.ui.topheadline

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import co.sample.mvvm.R
import kotlinx.coroutines.launch
import co.sample.mvvm.MVVMApplication
import co.sample.mvvm.data.model.Article
import co.sample.mvvm.databinding.ActivityTopHeadlineBinding
import co.sample.mvvm.di.component.DaggerActivityComponent
import co.sample.mvvm.di.module.ActivityModule
import co.sample.mvvm.ui.base.UiState
import javax.inject.Inject

/**
 * Main activity displaying top headlines.
 * Implements MVVM architecture with proper lifecycle handling and DI.
 */
class TopHeadlineActivity : AppCompatActivity() {

    @Inject
    lateinit var topHeadlineViewModel: TopHeadlineViewModel

    @Inject
    lateinit var adapter: TopHeadlineAdapter

    private lateinit var binding: ActivityTopHeadlineBinding
    private var lastHandledErrorCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies()
        super.onCreate(savedInstanceState)
        binding = ActivityTopHeadlineBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        setupObserver()
    }

    /**
     * Sets up UI components including RecyclerView with proper layout manager.
     */
    private fun setupUI() {
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                (recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        recyclerView.adapter = adapter
    }

    /**
     * Observes ViewModel state changes and updates UI accordingly.
     * Uses proper lifecycle-aware collection to prevent memory leaks.
     */
    private fun setupObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                topHeadlineViewModel.uiState.collect { state ->
                    handleUiState(state)
                }
            }
        }
    }

    /**
     * Handles different UI states (Success, Loading, Error).
     * Provides clear visual feedback for each state.
     */
    private fun handleUiState(state: UiState<List<Article>>) {
        when (state) {
            is UiState.Success -> {
                lastHandledErrorCode = null
                binding.progressBar.visibility = View.GONE

                // Validate data before rendering
                if (state.data.isNotEmpty()) {
                    binding.recyclerView.visibility = View.VISIBLE
                    renderList(state.data)
                } else {
                    renderList(emptyList())
                    binding.recyclerView.visibility = View.GONE
                    showEmptyState()
                }
            }
            is UiState.Loading -> {
                lastHandledErrorCode = null
                binding.progressBar.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }
            is UiState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                if (lastHandledErrorCode != state.message) {
                    lastHandledErrorCode = state.message
                    showError(state.message)
                }
            }
        }
    }

    /**
     * Renders the list of articles using the adapter.
     * Uses DiffUtil for efficient updates.
     */
    private fun renderList(articleList: List<Article>) {
        adapter.updateData(articleList)
    }

    /**
     * Shows empty state when no articles are available.
     */
    private fun showEmptyState() {
        Toast.makeText(this, getString(R.string.no_articles_to_display), Toast.LENGTH_SHORT).show()
    }

    /**
     * Shows error state with proper user feedback.
     */
    private fun showError(errorCode: String) {
        val message = when (errorCode) {
            TopHeadlineViewModel.ERROR_NETWORK -> getString(R.string.network_error_message)
            TopHeadlineViewModel.ERROR_TIMEOUT -> getString(R.string.timeout_error_message)
            else -> getString(R.string.error_message)
        }

        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(R.string.retry_button) { _, _ ->
                topHeadlineViewModel.retry()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    /**
     * Injects dependencies using Dagger.
     */
    private fun injectDependencies() {
        DaggerActivityComponent.builder()
            .applicationComponent((application as MVVMApplication).applicationComponent)
            .activityModule(ActivityModule(this))
            .build()
            .inject(this)
    }

}


