package net.wildfyre.client.views.adapters

import net.wildfyre.client.Application
import net.wildfyre.client.R
import net.wildfyre.client.data.Author
import net.wildfyre.client.data.Notification

/**
 * Adapter for displaying notifications in [net.wildfyre.client.views.NotificationsFragment].
 */
class NotificationsAdapter : ItemsAdapter<Notification>(true) {
    override fun getText(position: Int): String? = data[position].post?.text

    override fun getImage(position: Int): String? = null

    override fun getAuthor(position: Int): Author? = data[position].post?.author

    override fun getSubtitle(position: Int): String {
        val commentCount = data[position].comments?.size ?: 0
        return Application.context.resources.getQuantityString(
            R.plurals.notifications_item_comment_count,
            commentCount,
            commentCount
        )
    }

    override fun getId(position: Int): Long = data[position].post?.id ?: -1
}
