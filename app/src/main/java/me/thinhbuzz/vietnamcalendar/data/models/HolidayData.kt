package me.thinhbuzz.vietnamcalendar.data.models

import kotlinx.serialization.Serializable

@Serializable
data class HolidayFile(
    val version: String,
    val lastUpdated: String,
    val holidays: HolidayCategories
)

@Serializable
data class HolidayCategories(
    val solar: List<SolarHolidayData>,
    val lunar: List<LunarHolidayData>
)

@Serializable
data class SolarHolidayData(
    val id: String,
    val name: String,
    val month: Int,
    val day: Int,
    val description: String = "",
    val isPublicHoliday: Boolean = false,
    val color: String = "#FF0000"
)

@Serializable
data class LunarHolidayData(
    val id: String,
    val name: String,
    val lunarMonth: Int,
    val lunarDay: Int,
    val description: String = "",
    val isPublicHoliday: Boolean = false,
    val color: String = "#FF0000"
)