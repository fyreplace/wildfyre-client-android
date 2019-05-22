package net.wildfyre.client.views.adapters

import net.wildfyre.client.data.Author
import net.wildfyre.client.data.Post

/**
 * Adapter for displaying posts with [net.wildfyre.client.views.PostsFragment] implementations.
 */
open class PostsAdapter(showAuthors: Boolean) : ItemsAdapter<Post>(showAuthors) {
    override fun getText(position: Int): String? = data[position].text

    override fun getImage(position: Int): String? = data[position].image

    override fun getAuthor(position: Int): Author? = data[position].author

    override fun getSubtitle(position: Int): String = data[position].created.toString()

    override fun getId(position: Int): Long = data[position].id ?: -1
}
