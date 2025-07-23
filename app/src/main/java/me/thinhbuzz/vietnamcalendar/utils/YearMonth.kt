package me.thinhbuzz.vietnamcalendar.utils

import kotlinx.datetime.Month

data class YearMonth(
    val year: Int,
    val month: Month
) {
    constructor(year: Int, monthNumber: Int) : this(year, Month(monthNumber))
}