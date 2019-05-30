package net.wildfyre.client.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import net.wildfyre.client.AppGlide
import net.wildfyre.client.R
import net.wildfyre.client.data.Comment
import ru.noties.markwon.Markwon
import java.text.SimpleDateFormat

class CommentsAdapter(private val markdown: Markwon, private val onCommentActionSelected: OnCommentDeleted) :
    RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {
    private val dateFormat = SimpleDateFormat.getDateTimeInstance()
    private var data: MutableList<CommentWrapper> = mutableListOf()
    private val recyclers: MutableList<RecyclerView> = mutableListOf()
    var selfId: Long = -1L

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

    override fun getItemId(position: Int): Long = data[position].comment.id ?: -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.comment, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val wrapper = data[position]
        val comment = wrapper.comment
        holder.date.text = dateFormat.format(comment.created)
        comment.author?.let {
            holder.authorName.text = it.name
            AppGlide.with(holder.itemView.context)
                .load(it.avatar ?: R.drawable.ic_launcher)
                .transform(
                    CenterCrop(),
                    RoundedCorners(
                        holder.itemView.resources.getDimensionPixelOffset(R.dimen.comment_author_picture_rounding)
                    )
                )
                .into(holder.authorPicture)
        }

        val markdownContent = StringBuilder()
        comment.image?.let { markdownContent.append("![]($it)") }
        comment.text?.let { markdownContent.append(it) }
        markdown.setMarkdown(holder.text, markdownContent.toString())

        holder.text.setOnLongClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setAdapter(
                    getMenuAdapter(
                        holder.itemView.context,
                        comment.author?.user ?: -1
                    )
                ) { _, i ->
                    when (i) {
                        0 -> copyComment(position)
                        1 -> shareComment(position)
                        2 -> deleteComment(position)
                    }
                }
                .show()

            return@setOnLongClickListener true
        }

        holder.itemView.setBackgroundResource(
            if (wrapper.isNew)
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
                    wrapper.isNew = true
                }
            }
        }.toMutableList()

        if (scrollPosition > -1) {
            recyclers.forEach { it.scrollToPosition(scrollPosition) }
        }
    }

    fun addComment(comment: Comment) {
        data.add(CommentWrapper(comment))
    }

    fun removeComment(position: Int) {
        data.removeAt(position)
    }

    private fun getMenuAdapter(context: Context, id: Long): CommentMenuAdapter = CommentMenuAdapter(context,
        mutableListOf(
            R.drawable.ic_content_copy_daynight_24dp to R.string.post_comment_menu_copy,
            R.drawable.ic_share_daynight_24dp to R.string.post_comment_menu_share
        ).apply {
            if (id == selfId) {
                add(R.drawable.ic_delete_daynight_24dp to R.string.post_comment_menu_delete)
            }
        }
    )

    private fun copyComment(position: Int) {
        // TODO
    }

    private fun shareComment(position: Int) {
        // TODO
    }

    private fun deleteComment(position: Int) =
        onCommentActionSelected.onCommentDeleted(position, data[position].comment)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val authorName: TextView = itemView.findViewById(R.id.author_name)
        val authorPicture: ImageView = itemView.findViewById(R.id.author_picture)
        val date: TextView = itemView.findViewById(R.id.date)
        val text: TextView = itemView.findViewById(R.id.text)
    }

    private data class CommentWrapper(val comment: Comment) {
        var isNew: Boolean = false
    }

    private class CommentMenuAdapter(context: Context, private val items: List<Pair<Int, Int>>) :
        ArrayAdapter<Pair<Int, Int>>(context, R.layout.comment_menu_item, items) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
            super.getView(position, convertView, parent).apply {
                val textView = this as TextView
                textView.setText(items[position].second)
                textView.setCompoundDrawablesWithIntrinsicBounds(items[position].first, 0, 0, 0)
            }
    }

    interface OnCommentDeleted {
        fun onCommentDeleted(position: Int, comment: Comment)
    }
}
