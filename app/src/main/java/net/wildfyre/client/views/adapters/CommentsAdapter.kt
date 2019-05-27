package net.wildfyre.client.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import net.wildfyre.client.AppGlide
import net.wildfyre.client.R
import net.wildfyre.client.data.Comment
import ru.noties.markwon.Markwon
import java.text.SimpleDateFormat

class CommentsAdapter(private val markdown: Markwon) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {
    private val dateFormat = SimpleDateFormat.getDateTimeInstance()
    private var data: List<CommentWrapper> = listOf()
    private val recyclers: MutableList<RecyclerView> = mutableListOf()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclers.add(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclers.remove(recyclerView)
    }

    override fun getItemCount(): Int = data.size

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
                .load(it.avatar)
                .transform(
                    CenterCrop(),
                    RoundedCorners(
                        holder.itemView.resources
                            .getDimension(R.dimen.comment_author_picture_rounding)
                            .toInt()
                    )
                )
                .into(holder.authorPicture)
        }

        val markdownContent = StringBuilder()
        comment.image?.let { markdownContent.append("![]($it)") }
        comment.text?.let { markdownContent.append(it) }
        markdown.setMarkdown(holder.text, markdownContent.toString())
        holder.itemView.setBackgroundResource(
            if (wrapper.isNew) R.color.backgroundHighlight else android.R.color.transparent
        )
    }

    fun setComments(comments: List<Comment>, newIds: List<Long>?) {
        val newOnes = newIds?.toMutableList()
        var scrollPosition = -1

        data = comments.mapIndexed { index, comment ->
            CommentWrapper(comment).also { wrapper ->
                newOnes?.firstOrNull { id -> wrapper.comment.id == id }?.run {
                    if (scrollPosition == -1) {
                        scrollPosition = index
                    }

                    newOnes.remove(this)
                    wrapper.isNew = true
                }
            }
        }

        if (scrollPosition > -1) {
            recyclers.forEach { it.scrollToPosition(scrollPosition) }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val authorName: TextView = itemView.findViewById(R.id.author_name)
        val authorPicture: ImageView = itemView.findViewById(R.id.author_picture)
        val date: TextView = itemView.findViewById(R.id.date)
        val text: TextView = itemView.findViewById(R.id.text)
    }

    private data class CommentWrapper(val comment: Comment) {
        var isNew: Boolean = false
    }
}
