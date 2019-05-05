package net.wildfyre.client.views.adapters

import android.widget.Toast
import net.wildfyre.client.R
import net.wildfyre.client.data.Author
import net.wildfyre.client.data.Post

/**
 * Adapter for displaying posts with [net.wildfyre.client.views.PostsFragment] implementations.
 */
open class PostsAdapter(showAuthors: Boolean) : ItemsAdapter<Post>(showAuthors) {
    override var data: List<Post> = listOf()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        // TODO: replace this with switching to the post
        holder.container.setOnClickListener {
            Toast.makeText(holder.itemView.context, R.string.app_name, Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun getText(position: Int): String? = data[position].text

    override fun getImage(position: Int): String? = data[position].image

    override fun getAuthor(position: Int): Author? = data[position].author

    override fun getSubtitle(position: Int): String = data[position].created.toString()
}