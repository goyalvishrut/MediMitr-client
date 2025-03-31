package org.example.medimitr.common

expect fun formatReadableDate(timeInMilliSec: Long): String

expect fun Any.formatToTwoDecimal(): String
