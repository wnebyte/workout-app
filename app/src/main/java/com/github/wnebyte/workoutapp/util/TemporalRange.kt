package com.github.wnebyte.workoutapp.util

import java.util.*
import java.lang.IllegalStateException
import java.text.DateFormatSymbols
import android.os.Parcelable
import com.github.wnebyte.workoutapp.util.Extensions.Companion.addDays
import com.github.wnebyte.workoutapp.util.Extensions.Companion.format
import kotlinx.parcelize.Parcelize
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toDate
import com.github.wnebyte.workoutapp.util.Extensions.Companion.month
import com.github.wnebyte.workoutapp.util.Extensions.Companion.subtractDays
import com.github.wnebyte.workoutapp.util.Extensions.Companion.year
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toFirstOfLastYear
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toFirstOfNextMonth
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toFirstOfLastMonth
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toFirstOfNextYear
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toLastOfLastMonth
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toLastOfLastYear
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toLastOfThisYear
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toLastOfNextMonth
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toLastOfNextYear
import com.github.wnebyte.workoutapp.util.Extensions.Companion.toLastOfThisMonth

@Parcelize
data class TemporalRange(
    val flag: Int,
    val amount: Int = 1,
    val lower: Long,
    val upper: Long
): Parcelable, ITemporalRange {

    override fun adjustDown(): TemporalRange {
        return when (flag) {
            MONTH -> {
                copy(
                    flag = flag,
                    lower = lower.toDate().toFirstOfLastMonth().time,
                    upper = upper.toDate().toLastOfLastMonth().time
                )
            }
            YEAR -> {
                copy(
                    flag = flag,
                    lower = lower.toDate().toFirstOfLastYear().time,
                    upper = upper.toDate().toLastOfLastYear().time
                )
            }
            DAYS -> {
                copy(
                    flag = flag,
                    amount = amount,
                    lower = lower.toDate().subtractDays(amount).time,
                    upper = upper.toDate().subtractDays(amount).time
                )
            }
            else -> this
        }
    }

    override fun adjustUp(): TemporalRange {
        return when (flag) {
            MONTH -> {
                copy(
                    flag = flag,
                    // 01/01 -> 01/02
                    lower = lower.toDate().toFirstOfNextMonth().time,
                    // 30/02 -> 31/03
                    upper = upper.toDate().toLastOfNextMonth().time
                )
            }
            YEAR -> {
                copy(
                    flag = flag,
                    // 01/01/20 -> 01/01/21
                    lower = lower.toDate().toFirstOfNextYear().time,
                    // 31/12/21 -> 31/12/22
                    upper = upper.toDate().toLastOfNextYear().time
                )
            }
            DAYS -> {
                copy(
                    flag = flag,
                    amount = amount,
                    lower = lower.toDate().addDays(amount).time,
                    upper = upper.toDate().addDays(amount).time
                )
            }
            else -> throw IllegalStateException(
                "Error (TemporalRange): does not recognize flag: '$flag'."
            )
        }
    }

    fun shard(l: Long): Boolean {
        return when (flag) {
            MONTH -> {
                l.toDate().month() == upper.toDate().month()
            }
            YEAR -> {
                l.toDate().year() == upper.toDate().year()
            }
            DAYS -> {
                l >= lower.toDate().addDays(amount).time
            }
            else -> throw IllegalStateException(
                "Error (Range): does not recognize flag: '$flag'."
            )
        }
    }

    override fun toString(): String {
        val date = upper.toDate()
        return when (flag) {
            MONTH -> {
                "${DateFormatSymbols().months[date.month()]} ${date.year()}"
            }
            YEAR -> {
                "${date.year()}"
            }
            // Todo: Should describe the midway-point of the temporal range
            DAYS -> {
                "${date.subtractDays(amount).format("yyyy/MM/dd")} - ${date.format("yyyy/MM/dd")}"
            }
            else -> throw IllegalStateException(
                "Error (Range): does not recognize flag: '$flag'."
            )
        }
    }

    companion object {

        const val MONTH: Int = 1

        const val YEAR: Int = 2

        const val DAYS: Int = 3

        fun newInstance(flag: Int, amount: Int = 1): TemporalRange {
            return when (flag) {
                MONTH -> {
                    val date = Date()
                    TemporalRange(
                        MONTH,
                        amount = 1,
                        date.toFirstOfLastMonth().time,
                        date.toLastOfThisMonth().time
                    )
                }
                YEAR -> {
                    val date = Date()
                    TemporalRange(
                        YEAR,
                        amount = 1,
                        date.toFirstOfLastYear().time,
                        date.toLastOfThisYear().time
                    )
                }
                DAYS -> {
                    val date = Date()
                    TemporalRange(
                        DAYS,
                        amount,
                        date.subtractDays(2 * amount).time,
                        date.time
                    )
                }
                else -> throw IllegalStateException(
                    "Error (Range): does not recognize flag: '$flag'."
                )
            }
        }
    }
}