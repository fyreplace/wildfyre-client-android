package net.wildfyre.client.views.adapters

import androidx.recyclerview.widget.DiffUtil
import net.wildfyre.client.data.Author
import net.wildfyre.client.data.Post
import java.text.SimpleDateFormat

/**
 * Adapter for displaying posts with [net.wildfyre.client.views.PostsFragment] implementations.
 */
open class PostsAdapter(showAuthors: Boolean) : ItemsAdapter<Post>(PostCallback(), showAuthors) {
    override fun getItemId(position: Int): Long = getItem(position)?.id ?: -1

    override fun getText(position: Int): String? = getItem(position)?.text

    override fun getImage(position: Int): String? = getItem(position)?.image

    override fun getAuthor(position: Int): Author? = getItem(position)?.author

    override fun getSubtitle(position: Int): String = dateFormat.format(getItem(position)?.created)

    override fun getAreaName(position: Int): String? = null

    companion object {
        private val dateFormat = SimpleDateFormat.getDateTimeInstance()

        class PostCallback : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
                oldItem.text == newItem.text && oldItem.image == newItem.image && areItemsTheSame(oldItem, newItem)
        }
    }
}
