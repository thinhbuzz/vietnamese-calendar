package me.thinhbuzz.vietnamcalendar.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.datetime.LocalDate
import me.thinhbuzz.vietnamcalendar.R
import me.thinhbuzz.vietnamcalendar.utils.CalendarUtils
import me.thinhbuzz.vietnamcalendar.utils.YearMonth
import me.thinhbuzz.vietnamcalendar.viewmodel.CalendarView
import me.thinhbuzz.vietnamcalendar.viewmodel.CalendarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = viewModel(),
    onNavigateToSettings: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            CalendarTopBar(
                onNavigateToday = { viewModel.navigateToToday() },
                onNavigateToSettings = onNavigateToSettings
            )
        },
        bottomBar = {
            CalendarBottomBar(
                currentView = uiState.currentView,
                onViewSelected = viewModel::setCalendarView
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Navigation controls
            CalendarNavigationBar(
                currentView = uiState.currentView,
                currentDate = uiState.selectedDate,
                currentYearMonth = uiState.currentYearMonth,
                currentYear = uiState.currentYear,
                onNavigatePrevious = {
                    when (uiState.currentView) {
                        CalendarView.WEEK -> viewModel.navigateToPreviousWeek()
                        CalendarView.MONTH -> viewModel.navigateToPreviousMonth()
                        CalendarView.YEAR -> viewModel.navigateToPreviousYear()
                    }
                },
                onNavigateNext = {
                    when (uiState.currentView) {
                        CalendarView.WEEK -> viewModel.navigateToNextWeek()
                        CalendarView.MONTH -> viewModel.navigateToNextMonth()
                        CalendarView.YEAR -> viewModel.navigateToNextYear()
                    }
                }
            )
            
            // Calendar content with weight to take available space
            Box(
                modifier = Modifier.weight(1f)
            ) {
                when (uiState.currentView) {
                    CalendarView.WEEK -> WeekView(
                        selectedDate = uiState.selectedDate,
                        onDateSelected = viewModel::selectDate,
                        onSwipeLeft = viewModel::navigateToNextWeek,
                        onSwipeRight = viewModel::navigateToPreviousWeek
                    )
                    CalendarView.MONTH -> MonthView(
                        yearMonth = uiState.currentYearMonth,
                        selectedDate = uiState.selectedDate,
                        onDateSelected = viewModel::selectDate,
                        onSwipeLeft = viewModel::navigateToNextMonth,
                        onSwipeRight = viewModel::navigateToPreviousMonth
                    )
                    CalendarView.YEAR -> YearView(
                        year = uiState.currentYear,
                        selectedDate = uiState.selectedDate,
                        onMonthSelected = { yearMonth ->
                            viewModel.selectDate(LocalDate(yearMonth.year, yearMonth.month, 1))
                            viewModel.setCalendarView(CalendarView.MONTH)
                        },
                        onSwipeLeft = viewModel::navigateToNextYear,
                        onSwipeRight = viewModel::navigateToPreviousYear
                    )
                }
            }
            
            // Selected date information (not shown in year view)
            if (uiState.currentView != CalendarView.YEAR) {
                SelectedDateInfo(
                    selectedDate = uiState.selectedDate,
                    lunarDate = viewModel.getLunarDate(uiState.selectedDate),
                    holiday = viewModel.getHoliday(uiState.selectedDate)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarTopBar(
    onNavigateToday: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    TopAppBar(
        title = { 
            Text(
                text = "Lịch Việt Nam",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            IconButton(onClick = onNavigateToSettings) {
                Icon(Icons.Default.Settings, contentDescription = "Cài đặt")
            }
            IconButton(onClick = onNavigateToday) {
                Icon(Icons.Default.DateRange, contentDescription = "Hôm nay")
            }
        }
    )
}

@Composable
private fun CalendarNavigationBar(
    currentView: CalendarView,
    currentDate: LocalDate,
    currentYearMonth: YearMonth,
    currentYear: Int,
    onNavigatePrevious: () -> Unit,
    onNavigateNext: () -> Unit
) {
    val settings = me.thinhbuzz.vietnamcalendar.ui.LocalCalendarSettings.current
    
    val title = when (currentView) {
        CalendarView.WEEK -> {
            val startWithMonday = settings.firstDayOfWeek == me.thinhbuzz.vietnamcalendar.data.FirstDayOfWeek.MONDAY
            val weekStart = CalendarUtils.getWeekDays(currentDate, startWithMonday).first()
            val weekEnd = CalendarUtils.getWeekDays(currentDate, startWithMonday).last()
            me.thinhbuzz.vietnamcalendar.utils.DateFormatter.formatWeekRange(weekStart, weekEnd, settings.dayMonthFormat, settings.addLeadingZero)
        }
        CalendarView.MONTH -> me.thinhbuzz.vietnamcalendar.utils.DateFormatter.formatMonthYear(
            currentYearMonth.year, 
            currentYearMonth.month.value, 
            settings.monthYearFormat,
            settings.addLeadingZero
        )
        CalendarView.YEAR -> "Năm $currentYear"
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigatePrevious) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Trước")
        }
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        IconButton(onClick = onNavigateNext) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Sau")
        }
    }
}

@Composable
private fun CalendarBottomBar(
    currentView: CalendarView,
    onViewSelected: (CalendarView) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentView == CalendarView.WEEK,
            onClick = { onViewSelected(CalendarView.WEEK) },
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Week") },
            label = { Text("Tuần") }
        )
        NavigationBarItem(
            selected = currentView == CalendarView.MONTH,
            onClick = { onViewSelected(CalendarView.MONTH) },
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Month") },
            label = { Text("Tháng") }
        )
        NavigationBarItem(
            selected = currentView == CalendarView.YEAR,
            onClick = { onViewSelected(CalendarView.YEAR) },
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Year") },
            label = { Text("Năm") }
        )
    }
}

@Composable
private fun SelectedDateInfo(
    selectedDate: LocalDate,
    lunarDate: me.thinhbuzz.vietnamcalendar.utils.LunarDate,
    holiday: me.thinhbuzz.vietnamcalendar.utils.Holiday?
) {
    val settings = me.thinhbuzz.vietnamcalendar.ui.LocalCalendarSettings.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Gregorian calendar
            Text(
                text = "${CalendarUtils.getVietnameseDayOfWeek(selectedDate.dayOfWeek)}, ${me.thinhbuzz.vietnamcalendar.utils.DateFormatter.formatDayMonthYear(selectedDate, settings.dayMonthYearFormat, settings.addLeadingZero)}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            
            // Lunar calendar
            Text(
                text = "Âm lịch: ${me.thinhbuzz.vietnamcalendar.utils.DateFormatter.formatLunarDate(lunarDate, settings.dayMonthYearFormat, settings.addLeadingZero)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Holiday information
            if (holiday != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = holiday.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
