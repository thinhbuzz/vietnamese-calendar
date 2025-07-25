package me.thinhbuzz.vietnamcalendar

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.thinhbuzz.vietnamcalendar.data.HolidayUpdateManager
import me.thinhbuzz.vietnamcalendar.utils.VietnameseHolidays

class VietnamCalendarApplication : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()
        // Initialize WorkManager for Glance widgets
        WorkManager.initialize(this, workManagerConfiguration)
        
        // Initialize holiday data loader
        VietnameseHolidays.initialize(this)
        
        // Check for holiday updates in background
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val updateManager = HolidayUpdateManager(this@VietnamCalendarApplication)
                // Replace with your actual server URL when available
                // updateManager.checkAndUpdateIfNeeded("https://your-server.com/holidays.json")
            } catch (e: Exception) {
                // Ignore update errors, will use local/cached data
            }
        }
    }
    
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}