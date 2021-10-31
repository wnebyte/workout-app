package com.github.wnebyte.workoutapp.ext

import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Extensions {

    companion object {

        // DATE
        fun Date.format(
            sdf: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        ): String {
            return sdf.format(this)
        }

        fun fromString(
            date: String,
            sdf: SimpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        ): Date? {
            return try {
                sdf.parse(date)
            } catch (ex: ParseException) {
                null
            }
        }

        // AutoCompleteTextView
        fun <T> AutoCompleteTextView.showDropdown(adapter: ArrayAdapter<T>?) {
            if (!TextUtils.isEmpty(this.text.toString())) {
                adapter?.filter?.filter(null)
            }
        }
    }
}