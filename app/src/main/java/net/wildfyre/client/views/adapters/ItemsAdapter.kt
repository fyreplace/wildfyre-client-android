package net.wildfyre.client.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Space
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import net.wildfyre.client.AppGlide
import net.wildfyre.client.R
import net.wildfyre.client.data.Author
import net.wildfyre.client.views.markdown.PostPlugin
import net.wildfyre.client.views.markdown.prepareForMarkdown
import ru.noties.markwon.Markwon
import ru.noties.markwon.core.CorePlugin
import ru.noties.markwon.ext.strikethrough.StrikethroughPlugin
import ru.noties.markwon.ext.tables.TablePlugin

/**
 * Standard adapter using a list of items as a data source.
 */
abstract class ItemsAdapter<I>(private val showAuthors: Boolean) : RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {
    private lateinit var markdown: Markwon
    var onItemClickedListener: OnItemClickedListener = OnItemClickedListener.default()
    var data: List<I> = listOf()

    init {
        setHasStableIds(true)
    }

    @CallSuper
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        markdown = Markwon.builder(recyclerView.context)
            .usePlugin(CorePlugin.create())
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(PostPlugin.create(recyclerView.context))
            .usePlugin(TablePlugin.create(recyclerView.context))
            .build()
    }

    final override fun getItemCount(): Int = data.size

    final override fun setHasStableIds(hasStableIds: Boolean) = super.setHasStableIds(hasStableIds)

    final override fun getItemId(position: Int): Long = getId(position)

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
            getText(position)?.let {
                holder.text.text = markdown.toMarkdown(it.prepareForMarkdown(null))
            }
        }

        val author = getAuthor(position)
        holder.authorContainer.isVisible = author != null && showAuthors

        if (holder.authorContainer.isVisible) {
            holder.authorName.text = author!!.name
            AppGlide.with(holder.itemView.context)
                .load(author.avatar ?: R.drawable.ic_launcher)
                .transform(
                    CenterCrop(),
                    RoundedCorners(
                        holder.itemView.resources
                            .getDimension(R.dimen.list_item_author_picture_rounding)
                            .toInt()
                    )
                )
                .into(holder.authorPicture)
        }

        holder.space.isVisible = holder.text.isVisible && !holder.authorContainer.isVisible
        holder.subtitle.text = getSubtitle(position)
        holder.clickable.setOnClickListener {
            onItemClickedListener.onItemClicked(
                getAreaName(position),
                getId(position)
            )
        }
    }

    abstract fun getText(position: Int): String?

    abstract fun getImage(position: Int): String?

    abstract fun getAuthor(position: Int): Author?

    abstract fun getSubtitle(position: Int): String

    abstract fun getAreaName(position: Int): String?

    abstract fun getId(position: Int): Long

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.text)
        val image: ImageView = itemView.findViewById(R.id.image)
        val authorContainer: ViewGroup = itemView.findViewById(R.id.author_container)
        val authorName: TextView = itemView.findViewById(R.id.author_name)
        val authorPicture: ImageView = itemView.findViewById(R.id.author_picture)
        val space: Space = itemView.findViewById(R.id.space)
        val subtitle: TextView = itemView.findViewById(R.id.subtitle)
        val clickable: View = itemView.findViewById(R.id.clickable)
    }

    interface OnItemClickedListener {
        fun onItemClicked(areaName: String?, id: Long)

        companion object {
            fun default() = object : OnItemClickedListener {
                override fun onItemClicked(areaName: String?, id: Long) {
                }
            }
        }
    }
}
