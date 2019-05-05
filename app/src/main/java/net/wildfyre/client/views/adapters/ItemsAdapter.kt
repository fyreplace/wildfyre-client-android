package net.wildfyre.client.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import net.wildfyre.client.AppGlide
import net.wildfyre.client.R
import net.wildfyre.client.data.Author

/**
 * Standard adapter using a list of items as a data source.
 */
abstract class ItemsAdapter<I> : RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {
    abstract var data: List<I>

    final override fun getItemCount(): Int = data.size

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.list_item, parent, false)
        )

    @CallSuper
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = getImage(position)
        holder.text.isVisible = image == null
        holder.image.isVisible = !holder.text.isVisible

        if (holder.image.isVisible) {
            AppGlide.with(holder.itemView.context)
                .load(image)
                .into(holder.image)
        } else {
            holder.text.text = getText(position)
        }

        val author = getAuthor(position)
        holder.authorContainer.isVisible = author != null

        if (holder.authorContainer.isVisible) {
            holder.authorName.text = author!!.name
            AppGlide.with(holder.itemView.context)
                .load(author.avatar ?: R.drawable.ic_launcher)
                .transform(
                    CenterCrop(),
                    RoundedCorners(
                        holder.itemView.resources
                            .getDimension(R.dimen.post_author_picture_rounding)
                            .toInt()
                    )
                )
                .into(holder.authorPicture)
        }

        holder.subtitle.text = getSubtitle(position)
    }

    abstract fun getText(position: Int): String?

    abstract fun getImage(position: Int): String?

    abstract fun getAuthor(position: Int): Author?

    abstract fun getSubtitle(position: Int): String

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val container: ViewGroup = itemView.findViewById(R.id.container)
        val text: TextView = itemView.findViewById(R.id.text)
        val image: ImageView = itemView.findViewById(R.id.image)
        val authorContainer: ViewGroup = itemView.findViewById(R.id.author_container)
        val authorName: TextView = itemView.findViewById(R.id.author_name)
        val authorPicture: ImageView = itemView.findViewById(R.id.author_picture)
        val subtitle: TextView = itemView.findViewById(R.id.subtitle)
    }
}