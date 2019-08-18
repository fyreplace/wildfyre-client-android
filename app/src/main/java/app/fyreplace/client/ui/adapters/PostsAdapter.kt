package app.fyreplace.client.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import app.fyreplace.client.data.models.Post
import java.text.SimpleDateFormat

/**
 * Adapter for displaying posts with [app.fyreplace.client.ui.fragments.PostsFragment] implementations.
 */
open class PostsAdapter(showAuthors: Boolean) : ItemsAdapter<Post>(PostCallback(), showAuthors) {
    override fun getItemId(position: Int): Long = getItem(position)?.id ?: RecyclerView.NO_ID

    override fun getItemData(item: Post): ItemDataHolder = ItemDataHolder(
        item.text,
        item.image,
        item.author,
        dateFormat.format(item.created)
    )

    companion object {
        private val dateFormat = SimpleDateFormat.getDateTimeInstance()

        class PostCallback : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean =
                areItemsTheSame(oldItem, newItem) &&
                    oldItem.text == newItem.text &&
                    oldItem.image == newItem.image &&
                    oldItem.author == newItem.author
        }
    }
}
