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
    viewModel: CalendarViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.navigateToToday() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Today")
                    }
                }
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
                        onDateSelected = viewModel::selectDate
                    )
                    CalendarView.MONTH -> MonthView(
                        yearMonth = uiState.currentYearMonth,
                        selectedDate = uiState.selectedDate,
                        onDateSelected = viewModel::selectDate
                    )
                    CalendarView.YEAR -> YearView(
                        year = uiState.currentYear,
                        selectedDate = uiState.selectedDate,
                        onMonthSelected = { yearMonth ->
                            viewModel.selectDate(LocalDate(yearMonth.year, yearMonth.month, 1))
                            viewModel.setCalendarView(CalendarView.MONTH)
                        }
                    )
                }
            }
            
            // Selected date info
            SelectedDateInfo(
                selectedDate = uiState.selectedDate,
                lunarDate = viewModel.getLunarDate(uiState.selectedDate),
                holiday = viewModel.getHoliday(uiState.selectedDate)
            )
        }
    }
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
    val title = when (currentView) {
        CalendarView.WEEK -> {
            val weekStart = CalendarUtils.getWeekDays(currentDate).first()
            val weekEnd = CalendarUtils.getWeekDays(currentDate).last()
            "${weekStart.dayOfMonth}/${weekStart.monthNumber} - ${weekEnd.dayOfMonth}/${weekEnd.monthNumber}/${weekEnd.year}"
        }
        CalendarView.MONTH -> "${CalendarUtils.getVietnameseMonth(currentYearMonth.month)} ${currentYearMonth.year}"
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
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
        }
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        IconButton(onClick = onNavigateNext) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "${CalendarUtils.getVietnameseDayOfWeek(selectedDate.dayOfWeek)}, ${selectedDate.dayOfMonth}/${selectedDate.monthNumber}/${selectedDate.year}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Âm lịch: ${CalendarUtils.formatLunarDate(lunarDate)}, ${lunarDate.year}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            if (holiday != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = holiday.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                if (holiday.description.isNotEmpty()) {
                    Text(
                        text = holiday.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}