package me.thinhbuzz.vietnamcalendar.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.color.ColorProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.lazy.GridCells
import androidx.glance.appwidget.lazy.LazyVerticalGrid
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import kotlinx.datetime.*
import me.thinhbuzz.vietnamcalendar.utils.CalendarUtils
import me.thinhbuzz.vietnamcalendar.utils.YearMonth
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.currentState
import me.thinhbuzz.vietnamcalendar.utils.LunarCalendarConverter
import me.thinhbuzz.vietnamcalendar.utils.VietnameseHolidays

class CalendarWidget : GlanceAppWidget() {
    
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            CalendarWidgetContent()
        }
    }
}

@Composable
fun CalendarWidgetContent() {
    val currentPrefs = currentState<Preferences>()
    val viewType = currentPrefs[stringPreferencesKey("view_type")] ?: "month"
    
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.background)
            .padding(8.dp)
    ) {
        when (viewType) {
            "week" -> WeekWidgetView()
            else -> MonthWidgetView()
        }
    }
}

@Composable
fun MonthWidgetView() {
    val today = CalendarUtils.getCurrentDate()
    val currentYearMonth = CalendarUtils.getCurrentYearMonth()
    val monthDays = CalendarUtils.getMonthDays(currentYearMonth)
    
    Column(
        modifier = GlanceModifier.fillMaxSize()
    ) {
        // Header
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${CalendarUtils.getVietnameseMonth(currentYearMonth.month)} ${currentYearMonth.year}",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlanceTheme.colors.onBackground
                )
            )
        }
        
        // Weekday headers
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            listOf("CN", "T2", "T3", "T4", "T5", "T6", "T7").forEach { day ->
                Box(
                    modifier = GlanceModifier.defaultWeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day,
                        style = TextStyle(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (day == "CN") ColorProvider(day = Color.Red, night = Color.Red) else GlanceTheme.colors.onSurface
                        )
                    )
                }
            }
        }
        
        // Month grid
        LazyVerticalGrid(
            gridCells = GridCells.Fixed(7),
            modifier = GlanceModifier.fillMaxSize()
        ) {
            items(monthDays) { date ->
                if (date != null) {
                    WidgetDayCell(date = date, isToday = date == today)
                } else {
                    Spacer(modifier = GlanceModifier.size(30.dp))
                }
            }
        }
    }
}

@Composable
fun WeekWidgetView() {
    val today = CalendarUtils.getCurrentDate()
    val weekDays = CalendarUtils.getWeekDays(today)
    
    Column(
        modifier = GlanceModifier.fillMaxSize()
    ) {
        // Header
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tuần này",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlanceTheme.colors.onBackground
                )
            )
        }
        
        // Week days
        Row(
            modifier = GlanceModifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            weekDays.forEach { date ->
                Box(
                    modifier = GlanceModifier.defaultWeight(),
                    contentAlignment = Alignment.Center
                ) {
                    WidgetDayCell(date = date, isToday = date == today, isWeekView = true)
                }
            }
        }
    }
}

@Composable
fun WidgetDayCell(
    date: LocalDate,
    isToday: Boolean,
    isWeekView: Boolean = false
) {
    val lunarDate = LunarCalendarConverter.convertSolar2Lunar(
        date.dayOfMonth,
        date.monthNumber,
        date.year
    )
    val holiday = VietnameseHolidays.isHoliday(date)
    
    val backgroundColor = if (isToday) {
        GlanceTheme.colors.primary
    } else {
        GlanceTheme.colors.background
    }
    
    val textColor = when {
        isToday -> GlanceTheme.colors.onPrimary
        holiday != null -> ColorProvider(day = Color.Red, night = Color.Red)
        date.dayOfWeek == DayOfWeek.SUNDAY -> ColorProvider(day = Color.Red, night = Color.Red)
        else -> GlanceTheme.colors.onBackground
    }
    
    Column(
        modifier = GlanceModifier
            .padding(2.dp)
            .background(backgroundColor)
            .clickable(actionRunCallback<OpenAppAction>()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isWeekView) {
            // Day of week
            Text(
                text = CalendarUtils.getVietnameseDayOfWeek(date.dayOfWeek).take(2),
                style = TextStyle(
                    fontSize = 10.sp,
                    color = textColor
                )
            )
        }
        
        // Gregorian date
        Text(
            text = date.dayOfMonth.toString(),
            style = TextStyle(
                fontSize = if (isWeekView) 16.sp else 12.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )
        
        // Lunar date
        Text(
            text = "${lunarDate.day}",
            style = TextStyle(
                fontSize = if (isWeekView) 10.sp else 8.sp,
                color = if (isToday) GlanceTheme.colors.onPrimary else GlanceTheme.colors.onSurfaceVariant
            )
        )
    }
}

class OpenAppAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}

class CalendarWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CalendarWidget()
}