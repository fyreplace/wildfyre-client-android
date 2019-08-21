package app.fyreplace.client.ui.widgets

import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView

class ItemIdKeyProvider(private val recycler: RecyclerView) : ItemKeyProvider<Long>(SCOPE_MAPPED) {
    override fun getKey(position: Int) = recycler.findViewHolderForAdapterPosition(position)?.itemId

    override fun getPosition(key: Long) = recycler.findViewHolderForItemId(key).adapterPosition
}
