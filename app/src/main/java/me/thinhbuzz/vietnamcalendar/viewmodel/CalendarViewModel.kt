package me.thinhbuzz.vietnamcalendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import me.thinhbuzz.vietnamcalendar.utils.CalendarUtils
import me.thinhbuzz.vietnamcalendar.utils.YearMonth
import me.thinhbuzz.vietnamcalendar.utils.LunarCalendarConverter
import me.thinhbuzz.vietnamcalendar.utils.VietnameseHolidays

enum class CalendarView {
    WEEK, MONTH, YEAR
}

data class CalendarUiState(
    val currentView: CalendarView = CalendarView.MONTH,
    val selectedDate: LocalDate = CalendarUtils.getCurrentDate(),
    val currentYearMonth: YearMonth = CalendarUtils.getCurrentYearMonth(),
    val currentYear: Int = CalendarUtils.getCurrentDate().year
)

class CalendarViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()
    
    fun setCalendarView(view: CalendarView) {
        _uiState.value = _uiState.value.copy(currentView = view)
    }
    
    fun selectDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            currentYearMonth = YearMonth(date.year, date.month),
            currentYear = date.year
        )
    }
    
    fun navigateToToday() {
        val today = CalendarUtils.getCurrentDate()
        _uiState.value = _uiState.value.copy(
            selectedDate = today,
            currentYearMonth = YearMonth(today.year, today.month),
            currentYear = today.year
        )
    }
    
    fun navigateToPreviousWeek() {
        val newDate = _uiState.value.selectedDate.minus(DatePeriod(days = 7))
        _uiState.value = _uiState.value.copy(
            selectedDate = newDate,
            currentYearMonth = YearMonth(newDate.year, newDate.month)
        )
    }
    
    fun navigateToNextWeek() {
        val newDate = _uiState.value.selectedDate.plus(DatePeriod(days = 7))
        _uiState.value = _uiState.value.copy(
            selectedDate = newDate,
            currentYearMonth = YearMonth(newDate.year, newDate.month)
        )
    }
    
    fun navigateToPreviousMonth() {
        val current = _uiState.value.currentYearMonth
        val newYearMonth = if (current.month == Month.JANUARY) {
            YearMonth(current.year - 1, Month.DECEMBER)
        } else {
            YearMonth(current.year, current.month.number - 1)
        }
        _uiState.value = _uiState.value.copy(currentYearMonth = newYearMonth)
    }
    
    fun navigateToNextMonth() {
        val current = _uiState.value.currentYearMonth
        val newYearMonth = if (current.month == Month.DECEMBER) {
            YearMonth(current.year + 1, Month.JANUARY)
        } else {
            YearMonth(current.year, current.month.number + 1)
        }
        _uiState.value = _uiState.value.copy(currentYearMonth = newYearMonth)
    }
    
    fun navigateToPreviousYear() {
        _uiState.value = _uiState.value.copy(currentYear = _uiState.value.currentYear - 1)
    }
    
    fun navigateToNextYear() {
        _uiState.value = _uiState.value.copy(currentYear = _uiState.value.currentYear + 1)
    }
    
    fun getLunarDate(date: LocalDate) = LunarCalendarConverter.convertSolar2Lunar(
        date.dayOfMonth,
        date.monthNumber,
        date.year
    )
    
    fun getHoliday(date: LocalDate) = VietnameseHolidays.isHoliday(date)
}