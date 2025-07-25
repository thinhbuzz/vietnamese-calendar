package me.thinhbuzz.vietnamcalendar.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import me.thinhbuzz.vietnamcalendar.data.models.HolidayFile
import me.thinhbuzz.vietnamcalendar.utils.LunarCalendarConverter
import java.io.File
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

data class HolidayInfo(
    val name: String,
    val date: LocalDate,
    val isLunar: Boolean = false,
    val description: String = ""
)

class HolidayDataLoader(private val context: Context) {
    
    companion object {
        private const val ASSETS_HOLIDAY_FILE = "holidays/holidays.json"
        private const val LOCAL_HOLIDAY_FILE = "holidays.json"
        private const val REMOTE_HOLIDAY_URL = "https://your-server.com/holidays.json"
        
        @Volatile
        private var instance: HolidayDataLoader? = null
        
        fun getInstance(context: Context): HolidayDataLoader {
            return instance ?: synchronized(this) {
                instance ?: HolidayDataLoader(context.applicationContext).also { instance = it }
            }
        }
    }
    
    private val json = Json { 
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    private val holidayCache = ConcurrentHashMap<Int, List<HolidayInfo>>()
    private var holidayData: HolidayFile? = null
    private var lastLoadTime: Long = 0
    private val cacheTimeout = 24 * 60 * 60 * 1000L // 24 hours
    
    suspend fun getHolidaysForYear(year: Int): List<HolidayInfo> {
        return holidayCache.getOrPut(year) {
            loadHolidaysForYear(year)
        }
    }
    
    suspend fun getHoliday(date: LocalDate): HolidayInfo? {
        val holidays = getHolidaysForYear(date.year)
        return holidays.find { it.date == date }
    }
    
    suspend fun isHoliday(date: LocalDate): Boolean {
        return getHoliday(date) != null
    }
    
    private suspend fun loadHolidaysForYear(year: Int): List<HolidayInfo> = withContext(Dispatchers.IO) {
        ensureDataLoaded()
        
        val holidays = mutableListOf<HolidayInfo>()
        holidayData?.let { data ->
            // Add solar holidays
            data.holidays.solar.forEach { solarHoliday ->
                holidays.add(
                    HolidayInfo(
                        name = solarHoliday.name,
                        date = LocalDate.of(year, solarHoliday.month, solarHoliday.day),
                        isLunar = false,
                        description = solarHoliday.description
                    )
                )
            }
            
            // Add lunar holidays
            data.holidays.lunar.forEach { lunarHoliday ->
                val lunarDate = LunarCalendarConverter.convertLunar2Solar(
                    lunarHoliday.lunarDay,
                    lunarHoliday.lunarMonth,
                    year,
                    false
                )
                
                holidays.add(
                    HolidayInfo(
                        name = lunarHoliday.name,
                        date = LocalDate.of(lunarDate.third, lunarDate.second, lunarDate.first),
                        isLunar = true,
                        description = lunarHoliday.description
                    )
                )
            }
        }
        
        holidays.sortedBy { it.date }
    }
    
    private suspend fun ensureDataLoaded() {
        if (holidayData == null || System.currentTimeMillis() - lastLoadTime > cacheTimeout) {
            loadHolidayData()
        }
    }
    
    private suspend fun loadHolidayData() = withContext(Dispatchers.IO) {
        try {
            // First try to load from local file (updated from remote)
            val localFile = File(context.filesDir, LOCAL_HOLIDAY_FILE)
            if (localFile.exists()) {
                val content = localFile.readText()
                holidayData = json.decodeFromString(HolidayFile.serializer(), content)
                lastLoadTime = System.currentTimeMillis()
                return@withContext
            }
        } catch (e: Exception) {
            // Ignore and fallback to assets
        }
        
        // Fallback to assets
        try {
            val content = context.assets.open(ASSETS_HOLIDAY_FILE).bufferedReader().use { it.readText() }
            holidayData = json.decodeFromString(HolidayFile.serializer(), content)
            lastLoadTime = System.currentTimeMillis()
        } catch (e: Exception) {
            throw RuntimeException("Failed to load holiday data", e)
        }
    }
    
    suspend fun updateFromRemote(url: String = REMOTE_HOLIDAY_URL): Boolean = withContext(Dispatchers.IO) {
        try {
            // This will be implemented with the remote fetching capability
            // For now, return false
            false
        } catch (e: Exception) {
            false
        }
    }
    
    fun clearCache() {
        holidayCache.clear()
        holidayData = null
        lastLoadTime = 0
    }
}