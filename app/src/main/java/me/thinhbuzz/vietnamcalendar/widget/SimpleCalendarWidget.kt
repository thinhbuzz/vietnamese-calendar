package me.thinhbuzz.vietnamcalendar.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import kotlinx.datetime.*
import android.util.Log

// Simplified widget for testing
class SimpleCalendarWidget : GlanceAppWidget() {
    
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        try {
            provideContent {
                SimpleCalendarContent()
            }
        } catch (e: Exception) {
            Log.e("SimpleCalendarWidget", "Error providing content", e)
        }
    }
}

@Composable
fun SimpleCalendarContent() {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.background)
            .padding(16.dp)
            .clickable(actionRunCallback<OpenAppAction>()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val now = try {
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        } catch (e: Exception) {
            null
        }
        
        if (now != null) {
            Text(
                text = "Vietnam Calendar",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlanceTheme.colors.onBackground
                )
            )
            
            Spacer(modifier = GlanceModifier.height(8.dp))
            
            Text(
                text = "${now.date.dayOfMonth}/${now.date.monthNumber}/${now.date.year}",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = GlanceTheme.colors.onBackground
                )
            )
            
            Spacer(modifier = GlanceModifier.height(4.dp))
            
            Text(
                text = "${now.hour}:${now.minute.toString().padStart(2, '0')}",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = GlanceTheme.colors.onSurfaceVariant
                )
            )
        } else {
            Text(
                text = "Tap to open app",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = GlanceTheme.colors.onBackground
                )
            )
        }
    }
}