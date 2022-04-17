package com.github.wnebyte.workoutapp.util

import android.content.Context

object ContextHelper {

    fun prependPackageName(context: Context, value: String): String {
        return context.packageName + value
    }
}