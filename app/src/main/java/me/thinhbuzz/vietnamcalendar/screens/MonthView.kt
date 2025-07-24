package me.thinhbuzz.vietnamcalendar.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.*
import me.thinhbuzz.vietnamcalendar.utils.CalendarUtils
import me.thinhbuzz.vietnamcalendar.utils.YearMonth
import me.thinhbuzz.vietnamcalendar.utils.LunarCalendarConverter
import me.thinhbuzz.vietnamcalendar.utils.VietnameseHolidays

@Composable
fun MonthView(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val monthDays = CalendarUtils.getMonthDays(yearMonth)
    val today = CalendarUtils.getCurrentDate()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // Weekday headers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("CN", "T2", "T3", "T4", "T5", "T6", "T7").forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (day == "CN") Color.Red else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Month grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(monthDays) { date ->
                MonthDayCell(
                    date = date,
                    isSelected = date == selectedDate,
                    isToday = date == today,
                    isCurrentMonth = date.month == yearMonth.month,
                    onDateSelected = onDateSelected
                )
            }
        }
    }
}

@Composable
private fun MonthDayCell(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    isCurrentMonth: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    val lunarDate = LunarCalendarConverter.convertSolar2Lunar(
        date.dayOfMonth,
        date.monthNumber,
        date.year
    )
    val holiday = VietnameseHolidays.isHoliday(date)
    
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.primaryContainer
        else -> Color.Transparent
    }
    
    val textColor = when {
        !isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        isSelected -> MaterialTheme.colorScheme.onPrimary
        holiday != null -> Color.Red
        date.dayOfWeek == DayOfWeek.SUNDAY -> Color.Red
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .clickable { onDateSelected(date) }
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Gregorian date
        Text(
            text = date.dayOfMonth.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
        
        // Lunar date
        Text(
            text = "${lunarDate.day}",
            fontSize = 12.sp,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else textColor.copy(alpha = 0.6f)
        )
        
        // First day of lunar month indicator
        if (lunarDate.day == 1) {
            Text(
                text = CalendarUtils.getLunarMonthName(lunarDate.month),
                fontSize = 8.sp,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}