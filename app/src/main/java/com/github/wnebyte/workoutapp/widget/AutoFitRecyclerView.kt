package com.github.wnebyte.workoutapp.widget

import kotlin.math.max
import kotlin.math.roundToInt
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "AutoFitRecyclerView"

class AutoFitRecyclerView(context: Context, attrs: AttributeSet? = null)
    : RecyclerView(context, attrs) {

    private var manager: GridLayoutManager

    private var columnWidth: Int = -1

    init {
        if (attrs != null) {
            val attrsArray = IntArray(1) { android.R.attr.columnWidth }
            val array: TypedArray = context.obtainStyledAttributes(attrs, attrsArray)
            columnWidth = array.getDimensionPixelSize(0, -1)
            array.recycle()
        }
        manager = CenteredGridLayoutManager(context, 1)
        layoutManager = manager
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        if (0 < columnWidth) {
            val spanCount = max(1, measuredWidth / columnWidth)
            manager.spanCount = spanCount
        }
    }

    private inner class CenteredGridLayoutManager: GridLayoutManager {

        constructor(context: Context, spanCount: Int):
                super(context, spanCount)

        constructor(context: Context, spanCount: Int, orientation: Int, reverseLayout: Boolean):
                super(context, spanCount, orientation, reverseLayout)

        constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int):
                super(context, attrs, defStyleAttr, defStyleRes)

        override fun getPaddingLeft(): Int {
            val totalItemWidth = columnWidth * spanCount
            val padding =  if (this.width <= totalItemWidth) {
                super.getPaddingLeft()
            } else {
                ((this.width / (1f + spanCount)) - (totalItemWidth / (1f + spanCount)))
                    .roundToInt()
            }
            Log.i(TAG, "left-padding: $padding")
            return padding
        }

        override fun getPaddingRight(): Int {
            val padding = paddingLeft
            Log.i(TAG, "right-padding: $padding")
            return padding
        }
    }

    private inner class GridSpacingItemDecoration(
        val spanCount: Int, val spacing: Int, val includeEdges: Boolean
    ) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount

            if (includeEdges) {
                outRect.left = spacing - column * spacing / spanCount
                outRect.right = (column + 1) * spacing / spanCount

                if (position < spanCount) {
                    outRect.top = spacing
                }
                outRect.bottom = spacing
            } else {
                outRect.left = column * spacing / spanCount
                outRect.right = spacing - (column + 1) * spacing / spanCount
                if (position >= spanCount) {
                    outRect.top = spacing
                }
            }
        }
    }

}