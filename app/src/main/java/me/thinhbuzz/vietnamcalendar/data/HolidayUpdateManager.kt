package me.thinhbuzz.vietnamcalendar.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import me.thinhbuzz.vietnamcalendar.data.models.HolidayFile
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit

class HolidayUpdateManager(private val context: Context) {
    
    companion object {
        private const val LOCAL_HOLIDAY_FILE = "holidays.json"
        private const val PREFS_NAME = "holiday_update_prefs"
        private const val PREF_LAST_UPDATE = "last_update_time"
        private const val PREF_VERSION = "holiday_version"
        
        // Update check interval: 7 days
        private const val UPDATE_INTERVAL = 7 * 24 * 60 * 60 * 1000L
    }
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    suspend fun checkAndUpdateIfNeeded(remoteUrl: String): UpdateResult = withContext(Dispatchers.IO) {
        try {
            // Check if update is needed based on time
            val lastUpdate = prefs.getLong(PREF_LAST_UPDATE, 0)
            val currentTime = System.currentTimeMillis()
            
            if (currentTime - lastUpdate < UPDATE_INTERVAL) {
                return@withContext UpdateResult.NotNeeded("Update not needed yet")
            }
            
            // Fetch remote holiday data
            val remoteData = fetchRemoteData(remoteUrl)
            
            // Check version
            val currentVersion = prefs.getString(PREF_VERSION, "0.0.0") ?: "0.0.0"
            if (remoteData.version <= currentVersion) {
                // Update check time even if version is same
                prefs.edit().putLong(PREF_LAST_UPDATE, currentTime).apply()
                return@withContext UpdateResult.NotNeeded("Already up to date")
            }
            
            // Save new data
            saveDataLocally(remoteData)
            
            // Update preferences
            prefs.edit()
                .putLong(PREF_LAST_UPDATE, currentTime)
                .putString(PREF_VERSION, remoteData.version)
                .apply()
            
            // Clear cache in HolidayDataLoader
            HolidayDataLoader.getInstance(context).clearCache()
            
            UpdateResult.Success("Updated to version ${remoteData.version}")
        } catch (e: Exception) {
            UpdateResult.Error("Update failed: ${e.message}")
        }
    }
    
    suspend fun forceUpdate(remoteUrl: String): UpdateResult = withContext(Dispatchers.IO) {
        try {
            val remoteData = fetchRemoteData(remoteUrl)
            saveDataLocally(remoteData)
            
            prefs.edit()
                .putLong(PREF_LAST_UPDATE, System.currentTimeMillis())
                .putString(PREF_VERSION, remoteData.version)
                .apply()
            
            HolidayDataLoader.getInstance(context).clearCache()
            
            UpdateResult.Success("Force updated to version ${remoteData.version}")
        } catch (e: Exception) {
            UpdateResult.Error("Force update failed: ${e.message}")
        }
    }
    
    private suspend fun fetchRemoteData(url: String): HolidayFile = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .addHeader("Accept", "application/json")
            .build()
        
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw RuntimeException("Failed to fetch data: ${response.code}")
            }
            
            val body = response.body?.string() ?: throw RuntimeException("Empty response")
            json.decodeFromString(HolidayFile.serializer(), body)
        }
    }
    
    private fun saveDataLocally(data: HolidayFile) {
        val file = File(context.filesDir, LOCAL_HOLIDAY_FILE)
        val jsonString = json.encodeToString(HolidayFile.serializer(), data)
        file.writeText(jsonString)
    }
    
    fun getLastUpdateInfo(): UpdateInfo {
        val lastUpdate = prefs.getLong(PREF_LAST_UPDATE, 0)
        val version = prefs.getString(PREF_VERSION, "Unknown") ?: "Unknown"
        return UpdateInfo(lastUpdate, version)
    }
    
    sealed class UpdateResult {
        data class Success(val message: String) : UpdateResult()
        data class NotNeeded(val message: String) : UpdateResult()
        data class Error(val message: String) : UpdateResult()
    }
    
    data class UpdateInfo(
        val lastUpdateTime: Long,
        val currentVersion: String
    )
}