package me.thinhbuzz.vietnamcalendar

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager

class VietnamCalendarApplication : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()
        // Initialize WorkManager for Glance widgets
        WorkManager.initialize(this, workManagerConfiguration)
    }
    
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}