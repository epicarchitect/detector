package kolmachikhin.alexander.detector.ui.extentions

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*

fun Calendar.millisecond() = get(MILLISECOND)
fun Calendar.second() = get(SECOND)
fun Calendar.minute() = get(MINUTE)
fun Calendar.hour() = get(HOUR_OF_DAY)
fun Calendar.day() = get(DAY_OF_MONTH)
fun Calendar.month() = get(MONTH)
fun Calendar.year() = get(YEAR)


fun Calendar.millisecond(value: Int) = set(MILLISECOND, value)
fun Calendar.second(value: Int) = set(SECOND, value)
fun Calendar.minute(value: Int) = set(MINUTE, value)
fun Calendar.hour(value: Int) = set(HOUR_OF_DAY, value)
fun Calendar.day(value: Int) = set(DAY_OF_MONTH, value)
fun Calendar.month(value: Int) = set(MONTH, value)
fun Calendar.year(value: Int) = set(YEAR, value)

@SuppressLint("SimpleDateFormat")
fun Calendar.timeText(): String {
    var is24 = true


    val format = SimpleDateFormat(if (is24) "HH:mm" else "hh:mm:a")
    return format.format(time)
}

@SuppressLint("SimpleDateFormat")
fun Calendar.dateText(): String {
    val format = SimpleDateFormat("dd MMM yyyy")
    return format.format(time)
}

fun Calendar.dateAndTimeText() = dateText() + ", " + timeText()