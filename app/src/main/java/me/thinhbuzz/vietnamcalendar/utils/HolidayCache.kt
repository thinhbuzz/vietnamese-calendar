package me.thinhbuzz.vietnamcalendar.utils

import kotlinx.datetime.LocalDate

object HolidayCache {
    private val holidayMap = mutableMapOf<Int, Map<LocalDate, Holiday>>()
    private val yearHolidays = mutableMapOf<Int, List<Holiday>>()
    
    fun getHoliday(date: LocalDate): Holiday? {
        val yearMap = holidayMap.getOrPut(date.year) {
            val holidays = VietnameseHolidays.getAllHolidays(date.year)
            yearHolidays[date.year] = holidays
            holidays.associateBy { it.date }
        }
        return yearMap[date]
    }
    
    fun getYearHolidays(year: Int): List<Holiday> {
        return yearHolidays.getOrPut(year) {
            VietnameseHolidays.getAllHolidays(year)
        }
    }
    
    fun clearCache() {
        holidayMap.clear()
        yearHolidays.clear()
    }
}