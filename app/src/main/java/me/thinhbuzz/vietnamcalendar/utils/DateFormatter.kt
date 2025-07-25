package me.thinhbuzz.vietnamcalendar.utils

import kotlinx.datetime.LocalDate
import me.thinhbuzz.vietnamcalendar.data.DayMonthFormat
import me.thinhbuzz.vietnamcalendar.data.DayMonthYearFormat
import me.thinhbuzz.vietnamcalendar.data.MonthYearFormat
import me.thinhbuzz.vietnamcalendar.utils.LunarDate

object DateFormatter {
    
    fun formatMonthYear(date: LocalDate, format: MonthYearFormat, addLeadingZero: Boolean = true): String {
        val month = if (addLeadingZero) date.monthNumber.toString().padStart(2, '0') else date.monthNumber.toString()
        return when (format) {
            MonthYearFormat.MONTH_YEAR -> "Tháng $month ${date.year}"
            MonthYearFormat.YEAR_MONTH -> "${date.year} Tháng $month"
            MonthYearFormat.SHORT_MONTH_YEAR -> "$month/${date.year}"
            MonthYearFormat.SHORT_YEAR_MONTH -> "${date.year}/$month"
        }
    }
    
    fun formatMonthYear(year: Int, month: Int, format: MonthYearFormat, addLeadingZero: Boolean = true): String {
        val monthStr = if (addLeadingZero) month.toString().padStart(2, '0') else month.toString()
        return when (format) {
            MonthYearFormat.MONTH_YEAR -> "Tháng $monthStr $year"
            MonthYearFormat.YEAR_MONTH -> "$year Tháng $monthStr"
            MonthYearFormat.SHORT_MONTH_YEAR -> "$monthStr/$year"
            MonthYearFormat.SHORT_YEAR_MONTH -> "$year/$monthStr"
        }
    }
    
    fun formatDayMonthYear(date: LocalDate, format: DayMonthYearFormat, addLeadingZero: Boolean = true): String {
        val day = if (addLeadingZero) date.dayOfMonth.toString().padStart(2, '0') else date.dayOfMonth.toString()
        val month = if (addLeadingZero) date.monthNumber.toString().padStart(2, '0') else date.monthNumber.toString()
        val year = date.year
        
        return when (format) {
            DayMonthYearFormat.DMY_SLASH -> "$day/$month/$year"
            DayMonthYearFormat.DMY_DASH -> "$day-$month-$year"
            DayMonthYearFormat.DMY_DOT -> "$day.$month.$year"
            DayMonthYearFormat.DMY_FULL -> "$day tháng $month năm $year"
            DayMonthYearFormat.YMD_DASH -> "$year-$month-$day"
        }
    }
    
    fun formatDayMonth(date: LocalDate, format: DayMonthFormat, addLeadingZero: Boolean = true): String {
        val day = if (addLeadingZero) date.dayOfMonth.toString().padStart(2, '0') else date.dayOfMonth.toString()
        val month = if (addLeadingZero) date.monthNumber.toString().padStart(2, '0') else date.monthNumber.toString()
        
        return when (format) {
            DayMonthFormat.DM_SLASH -> "$day/$month"
            DayMonthFormat.DM_DASH -> "$day-$month"
            DayMonthFormat.DM_DOT -> "$day.$month"
            DayMonthFormat.DM_FULL -> "$day tháng $month"
            DayMonthFormat.DM_SHORT -> "$day Th$month"
        }
    }
    
    fun formatWeekRange(weekStart: LocalDate, weekEnd: LocalDate, format: DayMonthFormat, addLeadingZero: Boolean = true): String {
        val start = formatDayMonth(weekStart, format, addLeadingZero)
        val end = formatDayMonth(weekEnd, format, addLeadingZero)
        return "$start - $end"
    }
    
    fun formatLunarDate(lunarDate: LunarDate, format: DayMonthYearFormat, addLeadingZero: Boolean = true): String {
        val day = if (addLeadingZero) lunarDate.day.toString().padStart(2, '0') else lunarDate.day.toString()
        val month = if (addLeadingZero) lunarDate.month.toString().padStart(2, '0') else lunarDate.month.toString()
        val year = lunarDate.year
        
        return when (format) {
            DayMonthYearFormat.DMY_SLASH -> "$day/$month/$year"
            DayMonthYearFormat.DMY_DASH -> "$day-$month-$year"
            DayMonthYearFormat.DMY_DOT -> "$day.$month.$year"
            DayMonthYearFormat.DMY_FULL -> {
                val monthPrefix = if (lunarDate.isLeapMonth) "tháng nhuận" else "tháng"
                "$day $monthPrefix $month năm $year"
            }
            DayMonthYearFormat.YMD_DASH -> "$year-$month-$day"
        }
    }
    
    fun formatLunarDayMonth(lunarDate: LunarDate, format: DayMonthFormat, addLeadingZero: Boolean = true): String {
        val day = if (addLeadingZero) lunarDate.day.toString().padStart(2, '0') else lunarDate.day.toString()
        val month = if (addLeadingZero) lunarDate.month.toString().padStart(2, '0') else lunarDate.month.toString()
        
        return when (format) {
            DayMonthFormat.DM_SLASH -> "$day/$month"
            DayMonthFormat.DM_DASH -> "$day-$month"
            DayMonthFormat.DM_DOT -> "$day.$month"
            DayMonthFormat.DM_FULL -> {
                val monthPrefix = if (lunarDate.isLeapMonth) "tháng nhuận" else "tháng"
                "$day $monthPrefix $month"
            }
            DayMonthFormat.DM_SHORT -> "$day ${if (lunarDate.isLeapMonth) "N" else ""}Th$month"
        }
    }
}