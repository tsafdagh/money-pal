package com.kola.moneypal.utils

import android.text.format.DateFormat

object AnotherUtil {

    fun convertDate(dateInMilliseconds: String, dateFormat: String): String {
        return DateFormat.format(dateFormat, java.lang.Long.parseLong(dateInMilliseconds)).toString()
    }
}