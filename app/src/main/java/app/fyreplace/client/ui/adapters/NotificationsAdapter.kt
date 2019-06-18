package app.fyreplace.client.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import app.fyreplace.client.FyreplaceApplication
import app.fyreplace.client.R
import app.fyreplace.client.data.models.Notification

/**
 * Adapter for displaying notifications in [app.fyreplace.client.ui.fragments.NotificationsFragment].
 */
class NotificationsAdapter : ItemsAdapter<Notification>(NotificationCallback(), true) {
    override fun getItemData(item: Notification): ItemDataHolder = ItemDataHolder(
        item.post.text,
        null,
        item.post.author,
        FyreplaceApplication.context.resources.getQuantityString(
            R.plurals.notifications_item_comment_count,
            item.comments.size,
            item.comments.size
        )
    )

    companion object {
        class NotificationCallback : DiffUtil.ItemCallback<Notification>() {
            override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean =
                oldItem.area == newItem.area && oldItem.post.id == newItem.post.id

            override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean =
                areItemsTheSame(oldItem, newItem) && oldItem.comments == newItem.comments
        }
    }
}
