package org.example.medimitr.common

expect fun formatReadableDate(timeInMilliSec: Long): String

expect fun String.formatText(vararg args: Any?): String
