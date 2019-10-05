package app.fyreplace.client.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class EmptyItemsAdapter<I> : ItemsAdapter<I>(EmptyDiffCallback(), false) {
    override fun getItemCount() = 0

    override fun getItemId(position: Int) = RecyclerView.NO_ID

    override fun getItemData(item: I) =
        ItemDataHolder(null, null, null, "")

    class EmptyDiffCallback<I> : DiffUtil.ItemCallback<I>() {
        override fun areItemsTheSame(oldItem: I, newItem: I) = false

        override fun areContentsTheSame(oldItem: I, newItem: I) = false
    }
}
