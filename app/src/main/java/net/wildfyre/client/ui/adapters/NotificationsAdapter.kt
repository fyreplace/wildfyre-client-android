package net.wildfyre.client.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import net.wildfyre.client.R
import net.wildfyre.client.WildFyreApplication
import net.wildfyre.client.data.Notification

/**
 * Adapter for displaying notifications in [net.wildfyre.client.ui.NotificationsFragment].
 */
class NotificationsAdapter : ItemsAdapter<Notification>(NotificationCallback(), true) {
    override fun getItemData(item: Notification): ItemDataHolder = ItemDataHolder(
        item.post?.text,
        null,
        item.post?.author,
        item.comments?.size?.let {
            WildFyreApplication.context.resources.getQuantityString(
                R.plurals.notifications_item_comment_count,
                it,
                it
            )
        }.orEmpty()
    )

    companion object {
        class NotificationCallback : DiffUtil.ItemCallback<Notification>() {
            override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean =
                oldItem.post?.id == newItem.post?.id

            override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean =
                oldItem.comments?.size == newItem.comments?.size && areItemsTheSame(oldItem, newItem)
        }
    }
}
