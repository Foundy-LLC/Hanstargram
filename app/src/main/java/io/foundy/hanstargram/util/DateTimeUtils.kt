package io.foundy.hanstargram.util

import android.text.format.DateUtils
import java.text.ParseException
import java.util.*

fun Date.timeAgoString(): String {
    try {
        val now = System.currentTimeMillis()
        val ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS)
        return ago.toString()
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    throw IllegalStateException()
}