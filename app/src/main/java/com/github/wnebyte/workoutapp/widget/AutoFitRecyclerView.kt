package com.github.wnebyte.workoutapp.widget

import kotlin.math.max
import kotlin.math.roundToInt
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
            return if (this.width <= totalItemWidth) {
                super.getPaddingLeft()
            } else {
                ((this.width / (1f + spanCount)) - (totalItemWidth / (1f + spanCount)))
                    .roundToInt()
            }
        }

        override fun getPaddingRight(): Int {
            return paddingLeft
        }
    }

}