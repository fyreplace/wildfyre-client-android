package net.wildfyre.client.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import net.wildfyre.client.data.Post
import java.text.SimpleDateFormat

/**
 * Adapter for displaying posts with [net.wildfyre.client.ui.PostsFragment] implementations.
 */
open class PostsAdapter(showAuthors: Boolean) : ItemsAdapter<Post>(PostCallback(), showAuthors) {
    override fun getItemData(item: Post): ItemDataHolder = ItemDataHolder(
        item.text,
        item.image,
        item.author,
        item.created?.let { dateFormat.format(it) }.orEmpty()
    )

    companion object {
        private val dateFormat = SimpleDateFormat.getDateTimeInstance()

        class PostCallback : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
                oldItem.text == newItem.text && oldItem.image == newItem.image && areItemsTheSame(oldItem, newItem)
        }
    }
}
