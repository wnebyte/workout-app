package com.github.wnebyte.workoutapp.ext

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateExt {

    companion object {

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
    }
}