package net.wildfyre.client.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import net.wildfyre.client.AppGlide
import net.wildfyre.client.R
import net.wildfyre.client.data.Post

/**
 * Adapter for displaying posts in [net.wildfyre.client.views.PostsFragment].
 */
class PostsAdapter : ItemsAdapter<PostsAdapter.ViewHolder, Post>() {
    override var data: List<Post> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.posts_item, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(data[position]) {
            holder.text.isVisible = image == null
            holder.image.isVisible = !holder.text.isVisible

            if (holder.image.isVisible) {
                AppGlide.with(holder.itemView.context)
                    .load(image)
                    .into(holder.image)
            } else {
                holder.text.text = text
            }

            holder.creationDate.text = created.toString()

            // TODO: replace this with switching to the post
            holder.container.setOnClickListener {
                Toast.makeText(holder.itemView.context, R.string.main_nav_fragment_posts, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val container: ViewGroup = itemView.findViewById(R.id.container)
        val text: TextView = itemView.findViewById(R.id.text)
        val image: ImageView = itemView.findViewById(R.id.image)
        val creationDate: TextView = itemView.findViewById(R.id.creation_date)
    }
}