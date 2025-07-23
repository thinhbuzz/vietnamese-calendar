package me.thinhbuzz.vietnamcalendar.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import kotlinx.datetime.LocalDate
import me.thinhbuzz.vietnamcalendar.utils.CalendarUtils
import me.thinhbuzz.vietnamcalendar.utils.LunarCalendarConverter
import me.thinhbuzz.vietnamcalendar.utils.VietnameseHolidays

@Composable
fun WeekView(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val weekDays = CalendarUtils.getWeekDays(selectedDate)
    val today = CalendarUtils.getCurrentDate()
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Week header with day names
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weekDays.forEach { date ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = CalendarUtils.getVietnameseDayOfWeek(date.dayOfWeek).take(2),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (date.dayOfWeek.value == 7) Color.Red else MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        // Week days
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weekDays.forEach { date ->
                DayCell(
                    date = date,
                    isSelected = date == selectedDate,
                    isToday = date == today,
                    onDateSelected = onDateSelected,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
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
        isSelected -> MaterialTheme.colorScheme.onPrimary
        holiday != null -> Color.Red
        date.dayOfWeek.value == 7 -> Color.Red
        else -> MaterialTheme.colorScheme.onSurface
    }
    
    Column(
        modifier = modifier
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable { onDateSelected(date) }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Gregorian date (large)
        Text(
            text = date.dayOfMonth.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
        
        // Lunar date (small)
        Text(
            text = "${lunarDate.day}/${lunarDate.month}",
            fontSize = 12.sp,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        
        // Holiday name if exists
        if (holiday != null) {
            Text(
                text = holiday.name,
                fontSize = 8.sp,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}