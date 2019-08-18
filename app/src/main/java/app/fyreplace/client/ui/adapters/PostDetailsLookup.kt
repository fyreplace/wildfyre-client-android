package app.fyreplace.client.ui.adapters

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

class PostDetailsLookup(private val recycler: RecyclerView) : ItemDetailsLookup<Long>() {
    override fun getItemDetails(e: MotionEvent) =
        recycler.findChildViewUnder(e.x, e.y)?.let { PostDetails(recycler.getChildViewHolder(it)) }

    class PostDetails(private val holder: RecyclerView.ViewHolder) : ItemDetailsLookup.ItemDetails<Long>() {
        override fun getSelectionKey() = holder.itemId

        override fun getPosition() = holder.adapterPosition
    }
}
