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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.*
import me.thinhbuzz.vietnamcalendar.utils.CalendarUtils
import me.thinhbuzz.vietnamcalendar.utils.YearMonth

@Composable
fun YearView(
    year: Int,
    selectedDate: LocalDate,
    onMonthSelected: (YearMonth) -> Unit
) {
    val months = CalendarUtils.getYearMonths(year)
    val currentMonth = CalendarUtils.getCurrentYearMonth()
    
    // Months grid without year header
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
            items(months.size) { index ->
                val yearMonth = months[index]
                MiniMonthView(
                    yearMonth = yearMonth,
                    isCurrentMonth = yearMonth == currentMonth,
                    selectedDate = selectedDate,
                    onMonthSelected = onMonthSelected
                )
        }
    }
}

@Composable
private fun MiniMonthView(
    yearMonth: YearMonth,
    isCurrentMonth: Boolean,
    selectedDate: LocalDate,
    onMonthSelected: (YearMonth) -> Unit
) {
    val monthDays = CalendarUtils.getMonthDays(yearMonth)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMonthSelected(yearMonth) },
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentMonth) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(4.dp)
        ) {
            // Month name
            Text(
                text = CalendarUtils.getVietnameseMonth(yearMonth.month),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            
            // Mini weekday headers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("C", "2", "3", "4", "5", "6", "7").forEach { day ->
                    Text(
                        text = day,
                        fontSize = 7.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                        color = if (day == "C") Color.Red else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            // Mini month grid
            val rows = monthDays.chunked(7)
            rows.forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    week.forEach { date ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (date != null) {
                                val isSelected = date == selectedDate
                                val isToday = date == CalendarUtils.getCurrentDate()
                                
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    fontSize = 9.sp,
                                    color = when {
                                        date.month != yearMonth.month -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        isToday -> MaterialTheme.colorScheme.primary
                                        date.dayOfWeek == DayOfWeek.SUNDAY -> Color.Red
                                        else -> MaterialTheme.colorScheme.onSurface
                                    },
                                    fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                    modifier = if (isSelected) {
                                        Modifier
                                            .clip(RoundedCornerShape(50))
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                            .padding(1.dp)
                                    } else Modifier
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}