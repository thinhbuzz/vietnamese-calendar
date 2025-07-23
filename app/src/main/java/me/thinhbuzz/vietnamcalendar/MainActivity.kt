package me.thinhbuzz.vietnamcalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import me.thinhbuzz.vietnamcalendar.screens.CalendarScreen
import me.thinhbuzz.vietnamcalendar.ui.theme.VietNamCalendarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VietNamCalendarTheme {
                CalendarScreen()
            }
        }
    }
}