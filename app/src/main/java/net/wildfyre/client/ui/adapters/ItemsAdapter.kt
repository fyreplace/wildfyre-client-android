package net.wildfyre.client.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import net.wildfyre.client.AppGlide
import net.wildfyre.client.R
import net.wildfyre.client.WildFyreApplication
import net.wildfyre.client.data.Author
import net.wildfyre.client.ui.PostPlugin
import ru.noties.markwon.Markwon
import ru.noties.markwon.core.CorePlugin
import ru.noties.markwon.ext.strikethrough.StrikethroughPlugin
import ru.noties.markwon.ext.tables.TablePlugin

/**
 * Standard adapter using a list of items as a data source.
 */
abstract class ItemsAdapter<I>(diffCallback: DiffUtil.ItemCallback<I>, private val showAuthors: Boolean) :
    PagedListAdapter<I, ItemsAdapter.ViewHolder>(diffCallback) {
    private lateinit var markdown: Markwon
    var onItemClickedListener: OnItemClickedListener<I>? = null
    var data: List<I> = emptyList()

    final override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        markdown = Markwon.builder(recyclerView.context)
            .usePlugin(CorePlugin.create())
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(PostPlugin.create(recyclerView.context))
            .usePlugin(TablePlugin.create(recyclerView.context))
            .build()
    }

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val totalWidth = parent.measuredWidth
        val spanCount = parent.resources.getInteger(R.integer.post_preview_span_count)

        return ViewHolder(
            totalWidth / spanCount,
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.list_item, parent, false)
        )
    }

    final override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        if (item == null) {
            holder.hide()
            return
        }

        val itemData = getItemData(item)

        holder.text.isVisible = itemData.image == null
        holder.image.isVisible = !holder.text.isVisible
        holder.authorName.isVisible = showAuthors && itemData.author != null
        holder.authorPicture.isVisible = holder.authorName.isVisible
        holder.subtitle.text = itemData.subtitle
        holder.clickable.setOnClickListener { onItemClickedListener?.onItemClicked(item) }
        holder.loader.isVisible = false

        if (itemData.image != null) {
            holder.image.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin =
                    if (holder.authorName.isVisible)
                        WildFyreApplication.context.resources.getDimensionPixelOffset(R.dimen.margin_vertical_small)
                    else
                        0
            }
        }

        if (holder.authorName.isVisible) {
            holder.authorName.text = itemData.author!!.name
            AppGlide.with(holder.itemView.context)
                .load(itemData.author.avatar ?: R.drawable.ic_launcher)
                .placeholder(android.R.color.transparent)
                .transition(IMAGE_TRANSITION)
                .transform(IMAGE_TRANSFORM)
                .into(holder.authorPicture)
        }

        if (holder.image.isVisible) {
            holder.image.setImageDrawable(null)
            AppGlide.with(holder.itemView.context)
                .load(itemData.image)
                .placeholder(android.R.color.transparent)
                .transition(IMAGE_TRANSITION)
                .into(holder.image)
        } else {
            holder.text.text = markdown.toMarkdown(itemData.text.orEmpty())
        }
    }

    abstract fun getItemData(item: I): ItemDataHolder

    private companion object {
        val IMAGE_TRANSITION = DrawableTransitionOptions.withCrossFade()
        val IMAGE_TRANSFORM = MultiTransformation(
            CenterCrop(),
            RoundedCorners(WildFyreApplication.context.resources.getDimensionPixelOffset(R.dimen.list_item_author_picture_rounding))
        )
    }

    class ViewHolder(approxWidth: Int, itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val container: View = itemView.findViewById(R.id.container)
        val text: TextView = itemView.findViewById(R.id.text)
        val image: ImageView = itemView.findViewById(R.id.image)
        val authorName: TextView = itemView.findViewById(R.id.author_name)
        val authorPicture: ImageView = itemView.findViewById(R.id.author_picture)
        val subtitle: TextView = itemView.findViewById(R.id.subtitle)
        val clickable: View = itemView.findViewById(R.id.clickable)
        val loader: View = itemView.findViewById(R.id.loader)

        init {
            container.layoutParams.height = (approxWidth * HEIGHT_RATIO).toInt()
        }

        fun hide() {
            text.isVisible = false
            image.isVisible = false
            authorName.isVisible = false
            authorPicture.isVisible = false
            subtitle.text = ""
            loader.isVisible = true
        }

        private companion object {
            const val HEIGHT_RATIO = 1.4
        }
    }

    data class ItemDataHolder(
        val text: String?,
        val image: String?,
        val author: Author?,
        val subtitle: String
    )

    interface OnItemClickedListener<I> {
        fun onItemClicked(item: I)
    }
}
