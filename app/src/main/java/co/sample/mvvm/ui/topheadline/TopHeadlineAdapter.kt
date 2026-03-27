package co.sample.mvvm.ui.topheadline

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import co.sample.mvvm.data.model.Article
import co.sample.mvvm.databinding.TopHeadlineItemLayoutBinding

/**
 * Adapter for displaying top headline articles in a RecyclerView.
 * Uses DiffUtil for efficient list updates and proper ViewHolder pattern.
 */
class TopHeadlineAdapter(
    private val articleList: ArrayList<Article>
) : RecyclerView.Adapter<TopHeadlineAdapter.DataViewHolder>() {

    /**
     * ViewHolder class for article items with proper null safety and accessibility.
     */
    class DataViewHolder(private val binding: TopHeadlineItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            // Safe text binding with defaults
            binding.textViewTitle.text = article.title.ifEmpty { "No title available" }
            binding.textViewDescription.text = article.description.ifEmpty { "No description available" }
            binding.textViewSource.text = article.source.name.ifEmpty { "Unknown source" }

            // Accessibility - content descriptions for screen readers
            binding.imageViewBanner.contentDescription = "Image for ${article.title.ifEmpty { "article" }}"
            binding.root.contentDescription = "Article: ${article.title.ifEmpty { "untitled" }} from ${article.source.name.ifEmpty { "unknown source" }}"

            // Optimized image loading with error handling
            Glide.with(binding.imageViewBanner.context)
                .load(article.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.imageViewBanner)

            // Safe URL handling
            itemView.setOnClickListener {
                if (article.url.isNotEmpty()) {
                    val builder = CustomTabsIntent.Builder()
                    val customTabsIntent = builder.build()
                    customTabsIntent.launchUrl(it.context, Uri.parse(article.url))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder =
        DataViewHolder(
            TopHeadlineItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = articleList.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        // Bounds check for safety
        if (position >= 0 && position < articleList.size) {
            holder.bind(articleList[position])
        }
    }

    /**
     * Updates the list using DiffUtil for efficient RecyclerView updates.
     * This prevents unnecessary view rebinds and provides smooth animations.
     */
    fun updateData(newList: List<Article>) {
        val diffCallback = ArticleDiffCallback(articleList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        articleList.clear()
        articleList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    /**
     * Legacy method for backward compatibility.
     * Consider using updateData() instead for better performance.
     */
    @Deprecated("Use updateData() for better performance with DiffUtil")
    fun addData(list: List<Article>) {
        articleList.addAll(list)
    }

    /**
     * DiffUtil callback for calculating list differences efficiently.
     */
    private class ArticleDiffCallback(
        private val oldList: List<Article>,
        private val newList: List<Article>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].url == newList[newItemPosition].url
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}