package ru.practicum.android.diploma.favorite.recycle_view

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class TopSpaceItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        if (position == 0) {
            outRect.top = spaceHeight
        }
    }
}