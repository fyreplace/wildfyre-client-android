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
import java.text.SimpleDateFormat

class CommentsAdapter : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {
    private val dateFormat = SimpleDateFormat.getDateTimeInstance()
    var data: List<Comment> = listOf()

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.comment, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = data[position]
        holder.date.text = dateFormat.format(comment.created)
        holder.text.text = comment.text
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
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val authorName: TextView = itemView.findViewById(R.id.author_name)
        val authorPicture: ImageView = itemView.findViewById(R.id.author_picture)
        val date: TextView = itemView.findViewById(R.id.date)
        val text: TextView = itemView.findViewById(R.id.text)
    }
}
