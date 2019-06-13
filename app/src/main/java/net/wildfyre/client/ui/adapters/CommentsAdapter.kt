package net.wildfyre.client.ui.adapters

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.set
import androidx.core.text.toSpanned
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import net.wildfyre.client.AppGlide
import net.wildfyre.client.R
import net.wildfyre.client.WildFyreApplication
import net.wildfyre.client.data.models.Comment
import ru.noties.markwon.Markwon
import java.text.DateFormat
import java.text.SimpleDateFormat

class CommentsAdapter(
    private val fragment: Fragment,
    private val markdown: Markwon
) :
    RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {
    private var data: MutableList<CommentWrapper> = mutableListOf()
    private val recyclers: MutableList<RecyclerView> = mutableListOf()
    var selfId: Long = -1
    var authorId: Long = -1

    init {
        setHasStableIds(true)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclers.add(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclers.remove(recyclerView)
    }

    override fun getItemCount(): Int = data.size

    override fun getItemId(position: Int): Long = data[position].comment.id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater
            .from(parent.context)
            .inflate(R.layout.comment, parent, false)
    ).also { fragment.registerForContextMenu(it.text) }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wrapper = data[position]
        val comment = wrapper.comment
        val context = holder.itemView.context

        holder.date.text = DATE_FORMAT.format(comment.created)
        comment.author?.let {
            val authorName = SpannableStringBuilder(it.name)
            authorName[0..it.name.length] = StyleSpan(Typeface.BOLD)
            authorName[0..it.name.length] =
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.foreground))

            if (authorId != -1L && authorId == it.user) {
                val authorBadge = context.getString(R.string.post_comment_author)
                authorName.append(" ", authorBadge)
                authorName[it.name.length + 1..authorName.length] = StyleSpan(Typeface.ITALIC)
            }

            holder.authorName.text = authorName.toSpanned()
            AppGlide.with(context)
                .load(it.avatar ?: R.drawable.ic_launcher)
                .placeholder(android.R.color.transparent)
                .transition(AVATAR_TRANSITION)
                .transform(AVATAR_TRANSFORM)
                .into(holder.authorPicture)
        }

        val markdownContent = StringBuilder()
        comment.image?.let { markdownContent.append("![]($it)") }
        comment.text?.let { markdownContent.append(it) }
        markdown.setMarkdown(holder.text, markdownContent.toString())
        holder.text.tag = position
        holder.itemView.setBackgroundResource(
            if (wrapper.isHighlighted)
                R.color.backgroundHighlight
            else
                android.R.color.transparent
        )
    }

    fun setComments(comments: List<Comment>, highlightedIds: List<Long>?) {
        val highlightedOnes = highlightedIds?.toMutableList()
        var scrollPosition = -1

        data = comments.mapIndexed { index, comment ->
            CommentWrapper(comment).also { wrapper ->
                highlightedOnes?.firstOrNull { id -> wrapper.comment.id == id }?.run {
                    if (scrollPosition == -1) {
                        scrollPosition = index
                    }

                    highlightedOnes.remove(this)
                    wrapper.isHighlighted = true
                }
            }
        }.toMutableList()

        if (scrollPosition > -1) {
            recyclers.forEach { it.post { it.scrollToPosition(scrollPosition) } }
        }
    }

    fun addComment(comment: Comment) {
        data.add(CommentWrapper(comment))
    }

    fun removeComment(position: Int) {
        data.removeAt(position)
    }

    fun getComment(position: Int) = data[position].comment

    private companion object {
        val DATE_FORMAT: DateFormat = SimpleDateFormat.getDateTimeInstance()
        val AVATAR_TRANSITION = DrawableTransitionOptions.withCrossFade()
        val AVATAR_TRANSFORM = MultiTransformation(
            CenterCrop(),
            RoundedCorners(WildFyreApplication.context.resources.getDimensionPixelOffset(R.dimen.comment_author_picture_rounding))
        )
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val authorName: TextView = itemView.findViewById(R.id.author_name)
        val authorPicture: ImageView = itemView.findViewById(R.id.author_picture)
        val date: TextView = itemView.findViewById(R.id.date)
        val text: TextView = itemView.findViewById(R.id.text)
    }

    private class CommentWrapper(val comment: Comment) {
        var isHighlighted: Boolean = false
    }
}
