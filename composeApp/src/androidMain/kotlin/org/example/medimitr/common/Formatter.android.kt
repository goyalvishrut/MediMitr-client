package org.example.medimitr.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

actual fun formatReadableDate(timeInMilliSec: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timeInMilliSec))
}

actual fun Any.formatToTwoDecimal(): String = "%.2f".format(this)
