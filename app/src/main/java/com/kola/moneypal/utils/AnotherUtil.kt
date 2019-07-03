package com.kola.moneypal.utils

import android.annotation.SuppressLint
import android.text.format.DateFormat
import java.util.*

object AnotherUtil {

    fun convertDate(dateInMilliseconds: String, dateFormat: String): String {
        return DateFormat.format(dateFormat, java.lang.Long.parseLong(dateInMilliseconds)).toString()
    }

    @SuppressLint("SimpleDateFormat")
    fun getdateNow(): String {
        val callForDate = Calendar.getInstance()
        val currentDate = java.text.SimpleDateFormat("dd-MMMM-yyyy")
        val saveCurrentDate = currentDate.format(callForDate.time)
        return saveCurrentDate
    }
}