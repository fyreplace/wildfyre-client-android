package net.wildfyre.client.views.adapters

import androidx.recyclerview.widget.RecyclerView

/**
 * Standard adapter using a list of items as a data source.
 */
abstract class ItemsAdapter<T : RecyclerView.ViewHolder, I> : RecyclerView.Adapter<T>() {
    abstract var data: List<I>

    override fun getItemCount(): Int = data.size
}