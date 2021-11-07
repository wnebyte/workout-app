package com.github.wnebyte.workoutapp.ext

import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import java.lang.IllegalStateException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class Extensions {

    companion object {

        fun Date.format(
            sdf: String = "yyyy/MM/dd HH:mm"
        ): String {
            return SimpleDateFormat(sdf, Locale.getDefault()).format(this)
        }

        fun dateToString(
            date: String,
            sdf: String = "yyyy/MM/dd HH:mm"
        ): Date? {
            return try {
                SimpleDateFormat(sdf, Locale.getDefault()).parse(date)
            } catch (e: ParseException) {
                null
            }
        }

        fun <T> AutoCompleteTextView.showDropdown(adapter: ArrayAdapter<T>?) {
            if (!TextUtils.isEmpty(this.text.toString())) {
                adapter?.filter?.filter(null)
            }
        }

        /**
         * @return this CharSequence as a Char.
         */
        fun CharSequence.toChar(): Char {
            if (this.length != 1) {
                throw IllegalStateException(
                    ""
                )
            }
            return this[0]
        }

        /**
         * @return an empty String if this Int equals 0,
         * else [Any.toString].
         */
        fun Int.toEmptyString(): String {
            return when (this) {
                0 -> {
                    ""
                }
                else -> {
                    this.toString()
                }
            }
        }

        /**
         * @return an empty String if this Double equals 0.0,
         * else [Any.toString].
         */
        fun Double.toEmptyString(): String {
            return when (this) {
                0.0 -> {
                    ""
                }
                else -> {
                    this.toString()
                }
            }
        }

        /**
         * @return `"0".concat(this.toString())` if this Int lies within the inclusive range (0,9),
         * else [Any.toString].
         */
        fun Int.toPaddedString(): String {
            return when (this.toString().length) {
                1 -> {
                    ("0$this")
                }
                else -> {
                    this.toString()
                }
            }
        }
    }
}