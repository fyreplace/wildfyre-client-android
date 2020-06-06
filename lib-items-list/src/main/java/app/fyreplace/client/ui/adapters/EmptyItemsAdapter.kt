package app.fyreplace.client.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class EmptyItemsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    init {
        setHasStableIds(true)
    }

    override fun getItemCount() = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        throw IllegalStateException()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        throw IllegalStateException()
}
