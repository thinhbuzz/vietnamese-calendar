package me.thinhbuzz.vietnamcalendar.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class FirstDayOfWeek(val value: String, val displayName: String) {
    SUNDAY("sunday", "Chủ Nhật"),
    MONDAY("monday", "Thứ Hai")
}

enum class MonthYearFormat(val value: String, val pattern: String) {
    MONTH_YEAR("month_year", "Tháng M yyyy"),
    YEAR_MONTH("year_month", "yyyy Tháng M"),
    SHORT_MONTH_YEAR("short_month_year", "MM/yyyy"),
    SHORT_YEAR_MONTH("short_year_month", "yyyy/MM");
    
    fun getDisplayName(addLeadingZero: Boolean): String {
        val month = if (addLeadingZero) "07" else "7"
        return when (this) {
            MONTH_YEAR -> "Tháng $month 2025"
            YEAR_MONTH -> "2025 Tháng $month"
            SHORT_MONTH_YEAR -> "$month/2025"
            SHORT_YEAR_MONTH -> "2025/$month"
        }
    }
}

enum class DayMonthYearFormat(val value: String, val pattern: String) {
    DMY_SLASH("dmy_slash", "dd/MM/yyyy"),
    DMY_DASH("dmy_dash", "dd-MM-yyyy"),
    DMY_DOT("dmy_dot", "dd.MM.yyyy"),
    DMY_FULL("dmy_full", "d 'tháng' M 'năm' yyyy"),
    YMD_DASH("ymd_dash", "yyyy-MM-dd");
    
    fun getDisplayName(addLeadingZero: Boolean): String {
        val day = if (addLeadingZero) "05" else "5"
        val month = if (addLeadingZero) "07" else "7"
        return when (this) {
            DMY_SLASH -> "$day/$month/2025"
            DMY_DASH -> "$day-$month-2025"
            DMY_DOT -> "$day.$month.2025"
            DMY_FULL -> "$day tháng $month năm 2025"
            YMD_DASH -> "2025-$month-$day"
        }
    }
}

enum class DayMonthFormat(val value: String, val pattern: String) {
    DM_SLASH("dm_slash", "dd/MM"),
    DM_DASH("dm_dash", "dd-MM"),
    DM_DOT("dm_dot", "dd.MM"),
    DM_FULL("dm_full", "d 'tháng' M"),
    DM_SHORT("dm_short", "d 'Th'M");
    
    fun getDisplayName(addLeadingZero: Boolean): String {
        val day = if (addLeadingZero) "05" else "5"
        val month = if (addLeadingZero) "07" else "7"
        return when (this) {
            DM_SLASH -> "$day/$month"
            DM_DASH -> "$day-$month"
            DM_DOT -> "$day.$month"
            DM_FULL -> "$day tháng $month"
            DM_SHORT -> "$day Th$month"
        }
    }
}

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {
    
    companion object {
        private val FIRST_DAY_OF_WEEK = stringPreferencesKey("first_day_of_week")
        private val MONTH_YEAR_FORMAT = stringPreferencesKey("month_year_format")
        private val DAY_MONTH_YEAR_FORMAT = stringPreferencesKey("day_month_year_format")
        private val DAY_MONTH_FORMAT = stringPreferencesKey("day_month_format")
        private val ADD_LEADING_ZERO = booleanPreferencesKey("add_leading_zero")
    }
    
    val firstDayOfWeek: Flow<FirstDayOfWeek> = context.settingsDataStore.data
        .map { preferences ->
            val value = preferences[FIRST_DAY_OF_WEEK] ?: FirstDayOfWeek.SUNDAY.value
            FirstDayOfWeek.values().find { it.value == value } ?: FirstDayOfWeek.SUNDAY
        }
    
    val monthYearFormat: Flow<MonthYearFormat> = context.settingsDataStore.data
        .map { preferences ->
            val value = preferences[MONTH_YEAR_FORMAT] ?: MonthYearFormat.MONTH_YEAR.value
            MonthYearFormat.values().find { it.value == value } ?: MonthYearFormat.MONTH_YEAR
        }
    
    val dayMonthYearFormat: Flow<DayMonthYearFormat> = context.settingsDataStore.data
        .map { preferences ->
            val value = preferences[DAY_MONTH_YEAR_FORMAT] ?: DayMonthYearFormat.DMY_SLASH.value
            DayMonthYearFormat.values().find { it.value == value } ?: DayMonthYearFormat.DMY_SLASH
        }
    
    val dayMonthFormat: Flow<DayMonthFormat> = context.settingsDataStore.data
        .map { preferences ->
            val value = preferences[DAY_MONTH_FORMAT] ?: DayMonthFormat.DM_SLASH.value
            DayMonthFormat.values().find { it.value == value } ?: DayMonthFormat.DM_SLASH
        }
    
    val addLeadingZero: Flow<Boolean> = context.settingsDataStore.data
        .map { preferences ->
            preferences[ADD_LEADING_ZERO] ?: true
        }
    
    suspend fun setFirstDayOfWeek(firstDayOfWeek: FirstDayOfWeek) {
        context.settingsDataStore.edit { preferences ->
            preferences[FIRST_DAY_OF_WEEK] = firstDayOfWeek.value
        }
    }
    
    suspend fun setMonthYearFormat(format: MonthYearFormat) {
        context.settingsDataStore.edit { preferences ->
            preferences[MONTH_YEAR_FORMAT] = format.value
        }
    }
    
    suspend fun setDayMonthYearFormat(format: DayMonthYearFormat) {
        context.settingsDataStore.edit { preferences ->
            preferences[DAY_MONTH_YEAR_FORMAT] = format.value
        }
    }
    
    suspend fun setDayMonthFormat(format: DayMonthFormat) {
        context.settingsDataStore.edit { preferences ->
            preferences[DAY_MONTH_FORMAT] = format.value
        }
    }
    
    suspend fun setAddLeadingZero(addLeadingZero: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[ADD_LEADING_ZERO] = addLeadingZero
        }
    }
}