package co.sample.mvvm.ui.topheadline

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import co.sample.mvvm.R
import co.sample.mvvm.data.model.Article
import co.sample.mvvm.databinding.TopHeadlineItemLayoutBinding

/**
 * Adapter for displaying top headline articles in a RecyclerView.
 * Uses DiffUtil for efficient list updates and proper ViewHolder pattern.
 */
class TopHeadlineAdapter : RecyclerView.Adapter<TopHeadlineAdapter.DataViewHolder>() {

    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    /**
     * ViewHolder class for article items with proper null safety and accessibility.
     */
    class DataViewHolder(private val binding: TopHeadlineItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(article: Article) {
            val context = binding.root.context
            val safeTitle = article.title.orEmpty().ifEmpty { context.getString(R.string.no_title_available) }
            val safeDescription = article.description.orEmpty().ifEmpty { context.getString(R.string.no_description_available) }
            val safeSource = article.source.name.orEmpty().ifEmpty { context.getString(R.string.unknown_source) }

            // Safe text binding with defaults
            binding.textViewTitle.text = safeTitle
            binding.textViewDescription.text = safeDescription
            binding.textViewSource.text = safeSource

            // Accessibility - content descriptions for screen readers
            binding.imageViewBanner.contentDescription =
                context.getString(R.string.image_for_article, safeTitle)
            binding.root.contentDescription =
                context.getString(R.string.article_item_description, safeTitle, safeSource)

            // Optimized image loading with error handling
            Glide.with(binding.imageViewBanner.context)
                .load(article.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(android.R.color.darker_gray)
                .error(android.R.color.darker_gray)
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

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    /**
     * Updates the list using DiffUtil for efficient RecyclerView updates.
     * This prevents unnecessary view rebinds and provides smooth animations.
     */
    fun updateData(newList: List<Article>) {
        differ.submitList(newList)
    }

    /**
     * Legacy method for backward compatibility.
     * Consider using updateData() instead for better performance.
     */
    @Deprecated("Use updateData() for better performance with DiffUtil")
    fun addData(list: List<Article>) {
        updateData(list)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean =
                oldItem.url == newItem.url

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean =
                oldItem == newItem
        }
    }
}