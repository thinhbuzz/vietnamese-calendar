package me.thinhbuzz.vietnamcalendar.utils

import kotlinx.datetime.*

object CalendarUtils {
    
    fun getWeekDays(date: LocalDate, startWithMonday: Boolean = false): List<LocalDate> {
        return if (startWithMonday) {
            // Find the Monday of the current week
            val daysFromMonday = (date.dayOfWeek.value - 1 + 7) % 7
            val startOfWeek = date.minus(DatePeriod(days = daysFromMonday))
            (0..6).map { startOfWeek.plus(DatePeriod(days = it)) }
        } else {
            // Find the Sunday of the current week
            val daysFromSunday = if (date.dayOfWeek == DayOfWeek.SUNDAY) 0 else date.dayOfWeek.value
            val startOfWeek = date.minus(DatePeriod(days = daysFromSunday))
            (0..6).map { startOfWeek.plus(DatePeriod(days = it)) }
        }
    }
    
    fun getMonthDays(yearMonth: YearMonth, startWithMonday: Boolean = false): List<LocalDate> {
        val firstDay = LocalDate(yearMonth.year, yearMonth.month, 1)
        val daysInMonth = when (yearMonth.month) {
            Month.JANUARY, Month.MARCH, Month.MAY, Month.JULY, Month.AUGUST, Month.OCTOBER, Month.DECEMBER -> 31
            Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
            Month.FEBRUARY -> if (isLeapYear(yearMonth.year)) 29 else 28
            else -> 30
        }
        val lastDay = LocalDate(yearMonth.year, yearMonth.month, daysInMonth)
        
        val firstDayOfWeek = if (startWithMonday) {
            // Monday = 0, Tuesday = 1, etc., Sunday = 6
            (firstDay.dayOfWeek.value - 1 + 7) % 7
        } else {
            // Sunday = 0, Monday = 1, etc.
            if (firstDay.dayOfWeek == DayOfWeek.SUNDAY) 0 else firstDay.dayOfWeek.value
        }
        
        val days = mutableListOf<LocalDate>()
        
        // Add days from previous month for the beginning of the month
        if (firstDayOfWeek > 0) {
            val previousMonthLastDay = firstDay.minus(DatePeriod(days = 1))
            val startDate = previousMonthLastDay.minus(DatePeriod(days = firstDayOfWeek - 1))
            var currentDate = startDate
            repeat(firstDayOfWeek) {
                days.add(currentDate)
                currentDate = currentDate.plus(DatePeriod(days = 1))
            }
        }
        
        // Add all days of the month
        var currentDay = firstDay
        while (currentDay.compareTo(lastDay) <= 0) {
            days.add(currentDay)
            currentDay = currentDay.plus(DatePeriod(days = 1))
        }
        
        // Add days from next month to complete the last week
        var nextMonthDay = lastDay.plus(DatePeriod(days = 1))
        while (days.size % 7 != 0) {
            days.add(nextMonthDay)
            nextMonthDay = nextMonthDay.plus(DatePeriod(days = 1))
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
            DayOfWeek.MONDAY -> "Thứ 2"
            DayOfWeek.TUESDAY -> "Thứ 3"
            DayOfWeek.WEDNESDAY -> "Thứ 4"
            DayOfWeek.THURSDAY -> "Thứ 5"
            DayOfWeek.FRIDAY -> "Thứ 6"
            DayOfWeek.SATURDAY -> "Thứ 7"
            DayOfWeek.SUNDAY -> "Chủ Nhật"
            else -> ""
        }
    }
    
    fun getVietnameseMonth(month: Month): String {
        return "Tháng ${month.value}"
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
        val monthStr = if (lunarDate.isLeapMonth) "tháng nhuận ${lunarDate.month}" else "tháng ${lunarDate.month}"
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