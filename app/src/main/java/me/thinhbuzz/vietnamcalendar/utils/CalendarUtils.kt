package me.thinhbuzz.vietnamcalendar.utils

import kotlinx.datetime.*

object CalendarUtils {
    
    fun getWeekDays(date: LocalDate): List<LocalDate> {
        // Find the Sunday of the current week
        val daysFromSunday = if (date.dayOfWeek == DayOfWeek.SUNDAY) 0 else date.dayOfWeek.value
        val startOfWeek = date.minus(DatePeriod(days = daysFromSunday))
        return (0..6).map { startOfWeek.plus(DatePeriod(days = it)) }
    }
    
    fun getMonthDays(yearMonth: YearMonth): List<LocalDate?> {
        val firstDay = LocalDate(yearMonth.year, yearMonth.month, 1)
        val daysInMonth = when (yearMonth.month) {
            Month.JANUARY, Month.MARCH, Month.MAY, Month.JULY, Month.AUGUST, Month.OCTOBER, Month.DECEMBER -> 31
            Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
            Month.FEBRUARY -> if (isLeapYear(yearMonth.year)) 29 else 28
            else -> 30
        }
        val lastDay = LocalDate(yearMonth.year, yearMonth.month, daysInMonth)
        // Sunday = 0, Monday = 1, etc.
        val firstDayOfWeek = if (firstDay.dayOfWeek == DayOfWeek.SUNDAY) 0 else firstDay.dayOfWeek.value
        
        val days = mutableListOf<LocalDate?>()
        
        // Add empty days for the beginning of the month
        repeat(firstDayOfWeek) {
            days.add(null)
        }
        
        // Add all days of the month
        var currentDay = firstDay
        while (currentDay.compareTo(lastDay) <= 0) {
            days.add(currentDay)
            currentDay = currentDay.plus(DatePeriod(days = 1))
        }
        
        // Add empty days to complete the last week
        while (days.size % 7 != 0) {
            days.add(null)
        }
        
        return days
    }
    
    fun getYearMonths(year: Int): List<YearMonth> {
        return (1..12).map { YearMonth(year, it) }
    }
    
    private fun isLeapYear(year: Int): Boolean {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
    }
    
    fun getVietnameseDayOfWeek(dayOfWeek: DayOfWeek): String {
        return when (dayOfWeek) {
            DayOfWeek.MONDAY -> "Thứ Hai"
            DayOfWeek.TUESDAY -> "Thứ Ba"
            DayOfWeek.WEDNESDAY -> "Thứ Tư"
            DayOfWeek.THURSDAY -> "Thứ Năm"
            DayOfWeek.FRIDAY -> "Thứ Sáu"
            DayOfWeek.SATURDAY -> "Thứ Bảy"
            DayOfWeek.SUNDAY -> "Chủ Nhật"
            else -> ""
        }
    }
    
    fun getVietnameseMonth(month: Month): String {
        return when (month) {
            Month.JANUARY -> "Tháng Một"
            Month.FEBRUARY -> "Tháng Hai"
            Month.MARCH -> "Tháng Ba"
            Month.APRIL -> "Tháng Tư"
            Month.MAY -> "Tháng Năm"
            Month.JUNE -> "Tháng Sáu"
            Month.JULY -> "Tháng Bảy"
            Month.AUGUST -> "Tháng Tám"
            Month.SEPTEMBER -> "Tháng Chín"
            Month.OCTOBER -> "Tháng Mười"
            Month.NOVEMBER -> "Tháng Mười Một"
            Month.DECEMBER -> "Tháng Mười Hai"
            else -> ""
        }
    }
    
    fun getLunarMonthName(month: Int): String {
        return when (month) {
            1 -> "Giêng"
            2 -> "Hai"
            3 -> "Ba"
            4 -> "Tư"
            5 -> "Năm"
            6 -> "Sáu"
            7 -> "Bảy"
            8 -> "Tám"
            9 -> "Chín"
            10 -> "Mười"
            11 -> "Mười Một"
            12 -> "Chạp"
            else -> month.toString()
        }
    }
    
    fun formatLunarDate(lunarDate: LunarDate): String {
        val monthStr = if (lunarDate.isLeapMonth) "Nhuận ${getLunarMonthName(lunarDate.month)}" else getLunarMonthName(lunarDate.month)
        return "${lunarDate.day} $monthStr"
    }
    
    fun getCurrentDate(): LocalDate {
        return Clock.System.now().toLocalDateTime(TimeZone.of("Asia/Ho_Chi_Minh")).date
    }
    
    fun getCurrentYearMonth(): YearMonth {
        val now = getCurrentDate()
        return YearMonth(now.year, now.month)
    }
}