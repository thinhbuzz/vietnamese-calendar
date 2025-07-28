package me.thinhbuzz.vietnamcalendar.utils

import kotlinx.datetime.LocalDate

object LunarDateCache {
    private val cache = mutableMapOf<LocalDate, LunarDate>()
    
    fun getLunarDate(date: LocalDate): LunarDate {
        return cache.getOrPut(date) {
            LunarCalendarConverter.convertSolar2Lunar(
                date.dayOfMonth,
                date.monthNumber,
                date.year
            )
        }
    }
    
    fun clearCache() {
        cache.clear()
    }
}