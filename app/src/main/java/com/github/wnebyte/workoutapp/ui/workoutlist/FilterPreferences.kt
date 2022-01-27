package com.github.wnebyte.workoutapp.ui.workoutlist

import android.content.Context
import androidx.preference.PreferenceManager

private const val PREF_FILTER = "filter"

object FilterPreferences {

    fun getStoredFilter(context: Context): Filter {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val int = prefs.getInt(PREF_FILTER, 0)
        return Filter.parse(int)
    }

    fun setStoredFilter(context: Context, filter: Filter) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putInt(PREF_FILTER, filter.toInt())
            .apply()
    }
}