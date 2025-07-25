package me.thinhbuzz.vietnamcalendar.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
    onMonthSelected: (YearMonth) -> Unit,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit
) {
    val months = remember(year) { CalendarUtils.getYearMonths(year) }
    val currentMonth = remember { CalendarUtils.getCurrentYearMonth() }
    var dragAmount by remember { mutableStateOf(0f) }
    
    // Months grid without year header
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (dragAmount < -100) {
                            onSwipeLeft()
                        } else if (dragAmount > 100) {
                            onSwipeRight()
                        }
                        dragAmount = 0f
                    }
                ) { _, dragDelta ->
                    dragAmount += dragDelta
                }
            }
    ) {
            items(months.size, key = { months[it].toString() }) { index ->
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
    val monthDays = remember(yearMonth) { CalendarUtils.getMonthDays(yearMonth) }
    
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
                                
                                Box(
                                    modifier = if (isSelected || isToday) {
                                        Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(50))
                                            .background(
                                                if (isToday) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                            )
                                    } else Modifier,
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = date.dayOfMonth.toString(),
                                        fontSize = 10.sp,
                                        color = when {
                                            isToday -> MaterialTheme.colorScheme.onPrimary
                                            date.month != yearMonth.month -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                            isSelected -> MaterialTheme.colorScheme.primary
                                            date.dayOfWeek == DayOfWeek.SUNDAY -> Color.Red
                                            else -> MaterialTheme.colorScheme.onSurface
                                        },
                                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}