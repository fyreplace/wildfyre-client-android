package app.fyreplace.client.ui.adapters

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import app.fyreplace.client.data.models.Notification
import app.fyreplace.client.lib.notifications.R

/**
 * Adapter for displaying notifications in [app.fyreplace.client.ui.fragments.NotificationsFragment].
 */
class NotificationsAdapter(private val context: Context) :
    ItemsAdapter<Notification>(NotificationCallback(), true) {
    override fun getItemData(item: Notification) = ItemDataHolder(
        item.post.text,
        null,
        item.post.author,
        context.resources.getQuantityString(
            R.plurals.notifications_item_comment_count,
            item.comments.size,
            item.comments.size
        )
    )

    override fun getItemId(position: Int) = getItem(position)?.post?.id ?: RecyclerView.NO_ID

    companion object {
        class NotificationCallback : DiffUtil.ItemCallback<Notification>() {
            override fun areItemsTheSame(oldItem: Notification, newItem: Notification) =
                oldItem.area == newItem.area && oldItem.post.id == newItem.post.id

            override fun areContentsTheSame(oldItem: Notification, newItem: Notification) =
                oldItem.comments == newItem.comments
        }
    }
}
