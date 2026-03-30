package co.sample.mvvm.ui.topheadline

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import co.sample.mvvm.BuildConfig
import co.sample.mvvm.MVVMApplication
import co.sample.mvvm.R
import co.sample.mvvm.data.model.Article
import co.sample.mvvm.databinding.ActivityTopHeadlineBinding
import co.sample.mvvm.di.component.DaggerActivityComponent
import co.sample.mvvm.di.module.ActivityModule
import co.sample.mvvm.ui.base.UiState
import javax.inject.Inject

class TopHeadlineActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "TopHeadlineActivity"
    }

    @Inject
    lateinit var topHeadlineViewModel: TopHeadlineViewModel

    @Inject
    lateinit var adapter: TopHeadlineAdapter

    private lateinit var binding: ActivityTopHeadlineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies()
        super.onCreate(savedInstanceState)
        binding = ActivityTopHeadlineBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (BuildConfig.DEBUG) Log.d(TAG, "Activity created")
        setupUI()
        setupObserver()
    }

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

        // Setup swipe-to-refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (BuildConfig.DEBUG) Log.d(TAG, "Swipe refresh triggered")
            topHeadlineViewModel.refreshHeadlines()
        }
    }

    private fun setupObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                topHeadlineViewModel.uiState.collect {
                    when (it) {
                        is UiState.Success -> {
                            if (BuildConfig.DEBUG) Log.d(TAG, "UI State: Success with ${it.data.size} articles")
                            binding.progressBar.visibility = View.GONE
                            binding.swipeRefreshLayout.isRefreshing = false
                            renderList(it.data)
                            binding.recyclerView.visibility = View.VISIBLE
                        }
                        is UiState.Loading -> {
                            if (BuildConfig.DEBUG) Log.d(TAG, "UI State: Loading")
                            binding.swipeRefreshLayout.isRefreshing = false
                            binding.progressBar.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                        }
                        is UiState.Error -> {
                            if (BuildConfig.DEBUG) Log.e(TAG, "UI State: Error - ${it.message}")
                            binding.progressBar.visibility = View.GONE
                            binding.swipeRefreshLayout.isRefreshing = false
                            val errorMsg = it.message.ifEmpty { getString(R.string.error_unknown) }
                            Toast.makeText(this@TopHeadlineActivity, errorMsg, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            }
        }
    }

    private fun renderList(articleList: List<Article>) {
        adapter.addData(articleList)
        adapter.notifyDataSetChanged()
    }

    private fun injectDependencies() {
        DaggerActivityComponent.builder()
            .applicationComponent((application as MVVMApplication).applicationComponent)
            .activityModule(ActivityModule(this)).build().inject(this)
    }

}
