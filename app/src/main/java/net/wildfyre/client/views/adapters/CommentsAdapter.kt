package net.wildfyre.client.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
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
        holder.text.setTextColor(
            ContextCompat.getColor(
                holder.itemView.context,
                if (wrapper.isNew) R.color.colorPrimary else R.color.foreground
            )
        )
    }

    fun setComments(comments: List<Comment>, newIds: List<Long>?) {
        val newOnes = newIds?.toMutableList()
        data = comments.map {
            CommentWrapper(it).apply {
                newOnes?.firstOrNull { id -> comment.id == id }?.run {
                    newOnes.remove(this)
                    isNew = true
                }
            }
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
