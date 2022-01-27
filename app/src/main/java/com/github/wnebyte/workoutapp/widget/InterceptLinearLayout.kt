package com.github.wnebyte.workoutapp.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout

class InterceptLinearLayout(context: Context, attrs: AttributeSet? = null)
    : LinearLayout(context, attrs) {

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return true
    }
}