package net.wildfyre.client.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import net.wildfyre.client.AppGlide
import net.wildfyre.client.R
import net.wildfyre.client.data.Notification

class NotificationsAdapter : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {
    var data: List<Notification> = listOf()

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.notifications_item, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val notification = data[position]

        notification.post?.let {
            holder.text.text = it.text
            holder.authorContainer.isVisible = it.author != null

            if (holder.authorContainer.isVisible) {
                holder.authorName.text = it.author!!.name
                AppGlide.with(context)
                    .load(it.author!!.avatar ?: R.drawable.ic_launcher)
                    .transform(
                        CenterCrop(),
                        RoundedCorners(context.resources.getDimension(R.dimen.post_author_picture_rounding).toInt())
                    )
                    .into(holder.authorPicture)
            }

            val commentCount = notification.comments?.size ?: 0
            holder.commentCount.text = context.resources.getQuantityString(
                R.plurals.notifications_item_comment_count,
                commentCount,
                commentCount
            )

            holder.container.setOnClickListener {
                Toast.makeText(holder.itemView.context, R.string.main_nav_fragment_notifications, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val container: ViewGroup = itemView.findViewById(R.id.container)
        val text: TextView = itemView.findViewById(R.id.text)
        val authorContainer: ViewGroup = itemView.findViewById(R.id.author_container)
        val authorName: TextView = itemView.findViewById(R.id.author_name)
        val authorPicture: ImageView = itemView.findViewById(R.id.author_picture)
        val commentCount: TextView = itemView.findViewById(R.id.comment_count)
    }
}