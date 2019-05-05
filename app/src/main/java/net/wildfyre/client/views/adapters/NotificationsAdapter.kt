package net.wildfyre.client.views.adapters

import android.widget.Toast
import net.wildfyre.client.Application
import net.wildfyre.client.R
import net.wildfyre.client.data.Author
import net.wildfyre.client.data.Notification

/**
 * Adapter for displaying notifications in [net.wildfyre.client.views.NotificationsFragment].
 */
class NotificationsAdapter : ItemsAdapter<Notification>() {
    override var data: List<Notification> = listOf()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        // TODO: replace this with switching to the post
        holder.container.setOnClickListener {
            Toast.makeText(holder.itemView.context, R.string.main_nav_fragment_notifications, Toast.LENGTH_SHORT)
                .show()
        }
    }

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
}