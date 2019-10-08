package app.fyreplace.client.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import app.fyreplace.client.data.models.Post
import java.text.SimpleDateFormat

/**
 * Adapter for displaying posts with [app.fyreplace.client.ui.presenters.PostsFragment] implementations.
 */
class PostsAdapter(showAuthors: Boolean) : ItemsAdapter<Post>(PostCallback(), showAuthors) {
    override fun getItemId(position: Int) = getItem(position)?.id ?: RecyclerView.NO_ID

    override fun getItemData(item: Post) = ItemDataHolder(
        item.text,
        item.image,
        item.author,
        dateFormat.format(item.created)
    )

    companion object {
        private val dateFormat = SimpleDateFormat.getDateTimeInstance()
    }

    class PostCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Post, newItem: Post) =
            oldItem.created == newItem.created &&
                oldItem.text == newItem.text &&
                oldItem.image == newItem.image &&
                oldItem.author == newItem.author
    }
}
