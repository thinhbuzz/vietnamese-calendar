package me.thinhbuzz.vietnamcalendar.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.thinhbuzz.vietnamcalendar.data.*

data class CalendarSettings(
    val firstDayOfWeek: FirstDayOfWeek = FirstDayOfWeek.SUNDAY,
    val monthYearFormat: MonthYearFormat = MonthYearFormat.MONTH_YEAR,
    val dayMonthYearFormat: DayMonthYearFormat = DayMonthYearFormat.DMY_SLASH,
    val dayMonthFormat: DayMonthFormat = DayMonthFormat.DM_SLASH,
    val addLeadingZero: Boolean = true
)

val LocalCalendarSettings = compositionLocalOf { CalendarSettings() }

@Composable
fun CalendarSettingsProvider(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val settingsDataStore = SettingsDataStore(context)
    
    val firstDayOfWeek = settingsDataStore.firstDayOfWeek.collectAsStateWithLifecycle(FirstDayOfWeek.SUNDAY).value
    val monthYearFormat = settingsDataStore.monthYearFormat.collectAsStateWithLifecycle(MonthYearFormat.MONTH_YEAR).value
    val dayMonthYearFormat = settingsDataStore.dayMonthYearFormat.collectAsStateWithLifecycle(DayMonthYearFormat.DMY_SLASH).value
    val dayMonthFormat = settingsDataStore.dayMonthFormat.collectAsStateWithLifecycle(DayMonthFormat.DM_SLASH).value
    val addLeadingZero = settingsDataStore.addLeadingZero.collectAsStateWithLifecycle(true).value
    
    val settings = CalendarSettings(
        firstDayOfWeek = firstDayOfWeek,
        monthYearFormat = monthYearFormat,
        dayMonthYearFormat = dayMonthYearFormat,
        dayMonthFormat = dayMonthFormat,
        addLeadingZero = addLeadingZero
    )
    
    CompositionLocalProvider(LocalCalendarSettings provides settings) {
        content()
    }
}