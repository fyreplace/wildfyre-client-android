package net.wildfyre.client.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import net.wildfyre.client.R
import net.wildfyre.client.WildFyreApplication
import net.wildfyre.client.data.Author
import net.wildfyre.client.data.Notification

/**
 * Adapter for displaying notifications in [net.wildfyre.client.ui.NotificationsFragment].
 */
class NotificationsAdapter : ItemsAdapter<Notification>(NotificationCallback(), true) {
    override fun getItemId(position: Int): Long = getItem(position)?.post?.id ?: -1

    override fun getText(position: Int): String? = getItem(position)?.post?.text

    override fun getImage(position: Int): String? = null

    override fun getAuthor(position: Int): Author? = getItem(position)?.post?.author

    override fun getSubtitle(position: Int): String {
        val commentCount = getItem(position)?.comments?.size ?: 0
        return WildFyreApplication.context.resources.getQuantityString(
            R.plurals.notifications_item_comment_count,
            commentCount,
            commentCount
        )
    }

    override fun getAreaName(position: Int): String? = getItem(position)?.area

    companion object {
        class NotificationCallback : DiffUtil.ItemCallback<Notification>() {
            override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean =
                oldItem.post?.id == newItem.post?.id

            override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean =
                oldItem.comments?.size == newItem.comments?.size && areItemsTheSame(oldItem, newItem)
        }
    }
}
