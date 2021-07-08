package com.github.wnebyte.workoutapp.util

import android.app.DatePickerDialog
import android.content.Context
import android.view.View
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateUtil {

    companion object {

        fun fromString(date: String): Date? {
            val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
            return try {
                sdf.parse(date)
            } catch (ex: ParseException) {
                null
            }
        }

        fun fromDate(date: Date): String? {
            val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
            return sdf.format(date)
        }

        fun normalize(num: Int): String =
            when (num.toString().length) {
                1 -> {
                    ("0$num")
                }
                else -> {
                    num.toString()
                }
            }

        fun showDatePickerOnClick(context: Context, dateSetListener: DatePickerDialog.OnDateSetListener)
        : View.OnClickListener = View.OnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val picker = DatePickerDialog(context, dateSetListener, year, month, day)
            picker.show()
            }
    }
}