package app.fyreplace.client.ui.adapters

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.fyreplace.client.AppGlide
import app.fyreplace.client.app.post.R
import app.fyreplace.client.data.models.Comment
import app.fyreplace.client.ui.loadAvatar
import app.fyreplace.client.ui.presenters.PostFragment
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import io.noties.markwon.Markwon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.text.SimpleDateFormat

class CommentsAdapter(
    private val navigator: PostFragment.Navigator,
    private val markdown: Markwon
) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {
    private var data: List<CommentWrapper> = listOf()
    private val recyclers: MutableList<RecyclerView> = mutableListOf()
    private var onCommentsChangedAction: (() -> Unit)? = null
    private var onCommentMoreAction: ((View, Int) -> Unit)? = null
    var authorId: Long = -1

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclers.add(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclers.remove(recyclerView)
    }

    override fun getItemCount() = data.size

    override fun getItemId(position: Int) = data[position].comment.id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater
            .from(parent.context)
            .inflate(R.layout.post_comments_comment, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wrapper = data[position]
        val comment = wrapper.comment
        val context = holder.itemView.context

        holder.date.text = DATE_FORMAT.format(comment.created)
        comment.author?.let { author ->
            val authorName = SpannableStringBuilder(author.name)
            authorName[0..author.name.length] = StyleSpan(Typeface.BOLD)
            authorName[0..author.name.length] =
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.onBackground))

            if (authorId != -1L && authorId == author.user) {
                val authorBadge = context.getString(R.string.post_comment_author)
                authorName.append(" ", authorBadge)
                authorName[author.name.length + 1..authorName.length] = StyleSpan(Typeface.ITALIC)
            }

            holder.authorName.text = authorName.toSpanned()
            AppGlide.with(context)
                .loadAvatar(context, author)
                .transform(
                    CenterCrop(),
                    RoundedCorners(context.resources.getDimensionPixelOffset(R.dimen.comment_author_picture_rounding))
                )
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.authorPicture)

            holder.authorPicture.setOnClickListener { navigator.navigateToUser(author) }
        }

        val markdownContent = StringBuilder()
        comment.image?.let { markdownContent.append("![]($it)") }
        comment.text?.let { markdownContent.append(it) }
        markdown.setMarkdown(holder.text, markdownContent.toString())

        holder.more.setOnClickListener {
            onCommentMoreAction?.invoke(holder.more, position)
        }

        holder.itemView.setBackgroundResource(
            if (wrapper.isHighlighted) R.color.backgroundHighlight
            else android.R.color.transparent
        )
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)
        // TextView's in RecyclerView's are buggy, this makes them selectable again
        holder.text.isEnabled = false
        holder.text.isEnabled = true
    }

    suspend fun setComments(comments: List<Comment>, highlightedIds: List<Long>?) {
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
        }

        if (scrollPosition > -1) withContext(Dispatchers.Main) {
            recyclers.forEach {
                (it.layoutManager as? LinearLayoutManager)
                    ?.scrollToPositionWithOffset(scrollPosition, 0)
                    ?: it.scrollToPosition(scrollPosition)
            }
        }

        onCommentsChangedAction?.run {
            invoke()
            onCommentsChangedAction = null
        }
    }

    fun getComment(position: Int) = data[position].comment

    fun doOnCommentsChanged(action: () -> Unit) {
        onCommentsChangedAction = action
    }

    fun doOnCommentMore(action: (View, Int) -> Unit) {
        onCommentMoreAction = action
    }

    private companion object {
        val DATE_FORMAT: DateFormat = SimpleDateFormat.getDateTimeInstance()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val authorName: TextView = itemView.findViewById(R.id.author_name)
        val authorPicture: ImageView = itemView.findViewById(R.id.author_picture)
        val date: TextView = itemView.findViewById(R.id.date)
        val text: TextView = itemView.findViewById(R.id.text)
        val more: View = itemView.findViewById(R.id.more)
    }

    private class CommentWrapper(val comment: Comment) {
        var isHighlighted: Boolean = false
    }
}
